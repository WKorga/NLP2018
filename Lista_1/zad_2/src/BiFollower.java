import java.util.List;

public class BiFollower {
    private int occurences;
    private String follower;

    public BiFollower(int occurences, String follower) {
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
