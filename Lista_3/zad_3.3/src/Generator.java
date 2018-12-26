import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Generator {
    public static List<Throw> generateSequence(){
        List<Throw> sequence = new ArrayList<>();
        boolean cheated = false;
        for (int i=0;i<10000;i++){
            double change = new Random().nextDouble();
            if (cheated&&change<=0.05){
                cheated=false;
            }
            if (!cheated&&change<=0.04){
                cheated=true;
            }
            if (cheated){
                int score = new Random().nextInt(10)+1;
                if (score<6){
                    sequence.add(new Throw(true,score));
                }else {
                    sequence.add(new Throw(true,6));
                }
            }else {
                int score = new Random().nextInt(6)+1;
                sequence.add(new Throw(false,score));
            }
        }
        return sequence;
    }
}
