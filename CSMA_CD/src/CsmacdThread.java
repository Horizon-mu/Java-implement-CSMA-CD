/**
 * Created with IntelliJ IDEA.
 *
 * @author : Horizon~muu
 * @Date: 2020/10/16/17:53
 * @Description:
 */
public class CsmacdThread extends Thread{

    private static int bus = 0;      /** 当前所处的状态*/
    private int ID;
    private int delay;

    CsmacdThread(int ID,int delay){
        this.ID = ID;//主机名称
        this.delay = delay;
    }


    @Override
    public void run() {
        int failtime=0;
        //开始循环发送十个数据报
        for (int i=1;i<=10;i++) {
            //每个主机发送每个数据报前都随机休眠，模拟随机抢占信道过程
            try {
                Thread.sleep((int)(Math.random()*36));
                //因为动态退避的时间比较长，避免一个主机连续发送，随机等待的时间长些
            }catch (InterruptedException e) {
                System.err.println("Interrupted: Interrupt exception ");
            }
            int count = 16 ; //允许发送的次数
            int counts = 0; //冲突的次数
            while (count>0) {

                //侦听循环
                if (bus!=0) { //侦听到总线忙
                    System.out.println("pc["+ID+"]:总线忙！！！");
                    try {
                        Thread.sleep(1); //侦听间隔1ms，避免一直侦听
                    } catch (InterruptedException e) {
                        System.err.println("Interrupted: Interrupt exception ");
                    }
                    continue; //侦听使用坚持算法
                }else { //侦听到总线空闲
                    try {
                        Thread.sleep(delay/2);
                        /**
                         * 若传输过程会产生冲突，则必定会在单程内产生，但冲突传送回主机可能总共需要双程
                         * 因此在单程的时候用自己的ID标记总线，在接下来的回程之后再检查总线的标记
                         * 若在前单程未发生冲突，则在回程之后的标记依旧是自己的ID
                         * 若在前单程发生了冲突，则会在对方单程的时候bus被对方更改ID标记
                         * 而此时原主机的回程并未结束
                         * 因此回程后检查bus会跟自己的ID不符，传输过程就有冲突了
                         *
                         * 综上：在单程后标记ID，在回程后检查bus与自己ID是否相符
                         * 相符则未冲突，不相符则发生了冲突
                         * **/
                    } catch (InterruptedException e) {
                        System.err.println("Interrupted: Interrupt exception ");
                    }
                    bus=bus+ID;
                    try {
                        Thread.sleep(delay/2); //回程
                    } catch (InterruptedException e1) {
                        System.err.println("Interrupted: Interrupt exception ");
                    }
                    if (bus!=ID) { //检查bus是否与自身的id相同，判断是否发生冲突
                        bus=999; //停用信道
                        try {
                            Thread.sleep(delay/2); //等待信道上冲突的信息完全传完
                        } catch (InterruptedException e) {
                            System.err.println("Interrupted: Interrupt exception ");
                        }
                        bus=0; //恢复信道
                        count--;
                        counts++;
                        System.out.println("pc["+ID+"]:第 " + i + " 个数据帧第 "
                                + counts + " 次发送发生碰撞!");

                        //动态退避
                        try {
                            Delaytime timer = new Delaytime();
                            Thread.sleep(delay*timer.returntime(counts));
                        } catch (InterruptedException e) {
                            System.err.println("Interrupted: Interrupt exception");
                        }continue;
                    }
                    if(bus==ID) { //未发生冲突
                        try {
                            Thread.sleep((1+(int)(Math.random()*10)));
                            //随机数据报的长短为6+0 to 6+10 ms
                        } catch (InterruptedException e) {
                            System.err.println("Interrupted: Interrupt exception");
                        }
                        System.out.println("pc["+ID+"]:第" + i + "个数据发送成功!");
                        try {
                            Thread.sleep(delay/2);  //主机发送完数据，但是数据的尾部还在路上
                        } catch (InterruptedException e) {
                            System.err.println("Interrupted: Interrupt exception");
                        }
                        bus=0; //恢复信道
                    }
                }break;
            }
            if (count==0) {
                System.out.println("pc[" + ID + "]:第 " + i + " 个数据帧发送失败！");
                failtime++;
            }
        }
        Thread.yield(); //可惜未能实现礼让
        System.out.println("pc["+ID+"]:发送完成，共丢失数据"+failtime+"个");
    }

}
