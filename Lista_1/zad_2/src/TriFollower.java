public class TriFollower {
    private int occurences;
    private String follower1;
    private String follower2;

    public TriFollower(int occurences, String follower1, String follower2) {
        this.occurences = occurences;
        this.follower1 = follower1;
        this.follower2 = follower2;
    }

    public int getOccurences() {
        return occurences;
    }

    public String getFollower1() {
        return follower1;
    }

    public String getFollower2() {
        return follower2;
    }
}
