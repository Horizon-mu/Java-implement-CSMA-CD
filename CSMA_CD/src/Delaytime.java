/**
 * Created with IntelliJ IDEA.
 *
 * @author : Horizon~muu
 * @Date: 2020/10/16/18:25
 * @Description:
 */
public class Delaytime {
    public int returntime(int n) {
        int random;
        int temp;
        temp=Math.min(n,10);
        random=(int)(Math.random()*(Math.pow(2,temp)-1));
        return random;
    }

}
