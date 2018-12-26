public class Throw {
    private boolean cheated;
    private int score;

    public Throw(boolean cheated, int score) {
        this.cheated = cheated;
        this.score = score;
    }

    public boolean isCheated() {
        return cheated;
    }

    public int getScore() {
        return score;
    }
}
