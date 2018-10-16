import java.util.List;

public class Follower {
    private int occurences;
    private String follower;

    public Follower(int occurences, String follower) {
        this.occurences = occurences;
        this.follower = follower;
    }

    public int getOccurences() {
        return occurences;
    }

    public String getFollower() {
        return follower;
    }
}
