/**
 * Created with IntelliJ IDEA.
 *
 * @author : Horizon~muu
 * @Date: 2020/10/16/17:57
 * @Description:
 */
public class CsmacdMain {
    public static void main(String[] args) {
        int pcNumber = 20; //主机个数
        int delayTime = 6;    //冲突窗口
        pcNumber += 1;

        CsmacdThread[] pcNumbers = new CsmacdThread[pcNumber];
        //创建每个主机的线程
        for (int i = 0; i < pcNumber; i++) {
            pcNumbers[i] = new CsmacdThread(i, delayTime);
        }
        //start线程
        for (int j = 0; j < pcNumber; j++) {
            if (j == 0) //剔除0号主机避免与后面判断信道发生冲突
                continue;
            pcNumbers[j].start();
            System.out.println("分支栈空间开辟，执行run方法" + j);
        }
    }
}
