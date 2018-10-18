public class TriFollower {
    private int occurrences;
    private String follower1;
    private String follower2;

    public TriFollower(int occurrences, String follower1, String follower2) {
        this.occurrences = occurrences;
        this.follower1 = follower1;
        this.follower2 = follower2;
    }

    public int getOccurrences() {
        return occurrences;
    }

    public String getFollower1() {
        return follower1;
    }

    public String getFollower2() {
        return follower2;
    }
}
