import java.util.List;

public class ScoredSentence {
    private List<String> sentence;
    private int score;

    public ScoredSentence(List<String> sentence) {
        this.sentence = sentence;
        score=0;
    }
    public void addToScore(int add){
        score+=add;
    }
    public List<String> getSentence() {
        return sentence;
    }
    public int getScore() {
        return score;
    }
}
