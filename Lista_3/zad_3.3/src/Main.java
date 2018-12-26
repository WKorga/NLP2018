import java.util.ArrayList;
import java.util.List;

public class Main {
    private static List<Throw> sequence;
    public static void main(String[] args) {
        int tries = 1000;
        double totalHCount=0;
        double totalFbCount=0;
        for (int j=0;j<tries;j++){
            sequence = Generator.generateSequence();
            List<Boolean> heuristicsResults = useHeuristicApproach();
            List<Boolean> forwardBackwardResults = useForwardBackwardAlgorithm();
            int hCount=0;
            int fbCount=0;
            for (int i=0;i<sequence.size();i++){
                if (sequence.get(i).isCheated()==heuristicsResults.get(i))
                    hCount++;
                if (sequence.get(i).isCheated()==forwardBackwardResults.get(i))
                    fbCount++;
            }
            totalHCount+=(double)hCount/10000;
            totalFbCount+=(double)fbCount/10000;
        }
        totalHCount/=tries;
        totalFbCount/=tries;
        return;
    }
    private static List<Boolean> useHeuristicApproach(){
        int windowSize = 60;
        int border = 15;
        List<List<Boolean>> results = new ArrayList<>();
        for (int i=0;i<sequence.size();i++){
            results.add(new ArrayList<>());
        }
        for (int i=0;i<sequence.size()-windowSize+1;i++){
            int sixCount=0;
            for (int j=0;j<windowSize;j++){
                if (sequence.get(i+j).getScore()==6)
                    sixCount++;
            }
            boolean cheated = sixCount>=border;
            for (int j=0;j<windowSize;j++){
                results.get(i+j).add(cheated);
            }
        }
        List<Boolean> averageResults = new ArrayList<>();
        for (List<Boolean> resultForThrow:results){
            int cheatedCount = 0;
            for (Boolean cheated:resultForThrow){
                if (cheated)
                    cheatedCount++;
            }
            averageResults.add(cheatedCount>resultForThrow.size()/2);
        }
        return averageResults;
    }
    private static List<Boolean> useForwardBackwardAlgorithm(){
        double nonCheatedToCheated = 0.04;
        double cheatedToNonCheated = 0.05;
        double stayWithCheated = 1-cheatedToNonCheated;
        double stayWithNonCheated = 1-nonCheatedToCheated;
        double cheated = 0;
        double nonCheated = 1;
        List<Pair> forwardResults = new ArrayList<>();
        forwardResults.add(new Pair(cheated,nonCheated));
        for (int i=1;i<sequence.size();i++){
            int score = sequence.get(i).getScore();
            double newCheated = cheated*stayWithCheated*getScoreProbability(score,true)+
                    nonCheated*nonCheatedToCheated*getScoreProbability(score,true);
            double newNonCheated = nonCheated*stayWithNonCheated*getScoreProbability(score,false)+
                    cheated*cheatedToNonCheated*getScoreProbability(score,false);
            cheated = newCheated/(newCheated+newNonCheated);
            nonCheated = newNonCheated/(newCheated+newNonCheated);
            forwardResults.add(new Pair(cheated,nonCheated));
        }
        cheated = 1;
        nonCheated = 1;
        List<Pair> backwardResults = new ArrayList<>();
        backwardResults.add(new Pair(cheated,nonCheated));
        for (int i=sequence.size()-2;i>=0;i--){
            int score = sequence.get(i).getScore();
            double newCheated = cheated*stayWithCheated*getScoreProbability(score,true)+
                    nonCheated*nonCheatedToCheated*getScoreProbability(score,true);
            double newNonCheated = nonCheated*stayWithNonCheated*getScoreProbability(score,false)+
                    cheated*cheatedToNonCheated*getScoreProbability(score,false);
            cheated = newCheated/(newCheated+newNonCheated);
            nonCheated = newNonCheated/(newCheated+newNonCheated);
            backwardResults.add(0,new Pair(cheated,nonCheated));
        }
        List<Boolean> result = new ArrayList<>();
        for (int i=0;i<sequence.size();i++){
            cheated = forwardResults.get(i).getCheated()*backwardResults.get(i).getCheated();
            nonCheated = forwardResults.get(i).getNonCheated()*backwardResults.get(i).getNonCheated();
            result.add(cheated>=nonCheated);
        }
        return result;
    }
    private static double getScoreProbability(int score, boolean cheated){
        if (cheated){
            return score==6?0.5:0.1;
        }
        return (double)1/6;
    }
}
