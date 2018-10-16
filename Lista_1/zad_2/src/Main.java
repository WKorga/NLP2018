import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {
    public static final String BIGRAMS_FILE = "D:\\Programowanie\\Java\\NLP2018\\Lista_1\\poleval_2grams.txt";
    public static final String TRIGRAMS_FILE = "D:\\Programowanie\\Java\\NLP2018\\Lista_1\\poleval_3grams.txt";
    public static final String DICTIONARY_FILE = "D:\\Programowanie\\Java\\NLP2018\\Lista_1\\dictionary.txt";
    public static void main(String[] args) throws FileNotFoundException {
        runProcedureForTrigrams();
    }
    private static void runProcedureForTrigrams() throws FileNotFoundException{
        Scanner scanner = new Scanner(new File(TRIGRAMS_FILE));
        HashMap<String[],List<Follower>> trigrams = new HashMap<>();
        while (scanner.hasNextLine()){
            String[] line = scanner.nextLine().split(" ");
            if(Integer.parseInt(line[0])<10)
                continue;
            List<Follower> entry = trigrams.get(new String[]{line[1],line[2]});
            if (entry!=null){
                entry.add(new Follower(Integer.parseInt(line[0]),line.length==4?line[3]:null));
            }else {
                List<Follower> followers = new ArrayList<>();
                followers.add(new Follower(Integer.parseInt(line[0]),line.length==4?line[3]:null));

                trigrams.put(new String[]{line[1],line[2]},followers);
            }
        }
        for (int i=0;i<20;i++){
            String[] key = (String[])trigrams.keySet().toArray()[new Random().nextInt(trigrams.size())];
            System.out.print(key[0]+" "+key[1]+" ");
            do{
                List<Follower> followers = trigrams.get(key);
                Follower follower = followers.get(new Random().nextInt(followers.size()));
                if (follower.getFollower()==null)
                    break;
                System.out.print(follower.getFollower()+" ");
                key=new String[]{key[1],follower.getFollower()};
            }
            while (trigrams.containsKey(key));
            System.out.print("\n");
        }
    }
    private static void runProcedureForBigrams() throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(BIGRAMS_FILE));
        HashMap<String,List<Follower>> bigrams = new HashMap<>();
        while (scanner.hasNextLine()){
            String[] line = scanner.nextLine().split(" ");
            if(Integer.parseInt(line[0])<3)
                continue;
            List<Follower> entry = bigrams.get(line[1]);
            if (entry!=null){
                entry.add(new Follower(Integer.parseInt(line[0]),line[2]));
            }else {
                List<Follower> followers = new ArrayList<>();
                followers.add(new Follower(Integer.parseInt(line[0]),line[2]));
                bigrams.put(line[1],followers);
            }
        }
        for (int i=0;i<20;i++){
            String word = (String)bigrams.keySet().toArray()[new Random().nextInt(bigrams.size())];
            System.out.print(word+" ");
            do{
                List<Follower> followers = bigrams.get(word);
                word = followers.get(new Random().nextInt(followers.size())).getFollower();
                System.out.print(word+" ");
            }
            while (bigrams.containsKey(word));
        }
    }
}
