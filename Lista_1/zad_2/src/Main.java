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
        HashMap<String,List<TriFollower>> trigrams = new HashMap<>();
        while (scanner.hasNextLine()){
            String[] line = scanner.nextLine().split(" ");
            if(Integer.parseInt(line[0])<6)
                continue;
            List<TriFollower> entry = trigrams.get(line[1]);
            if (entry!=null){
                entry.add(new TriFollower(Integer.parseInt(line[0]),line[2],line.length==4?line[3]:null));
            }else {
                List<TriFollower> followers = new ArrayList<>();
                followers.add(new TriFollower(Integer.parseInt(line[0]),line[2],line.length==4?line[3]:null));

                trigrams.put(line[1],followers);
            }
        }
        for (int i=0;i<20;i++){
            String word = (String)trigrams.keySet().toArray()[new Random().nextInt(trigrams.size())];
            System.out.print(word+" ");
            do{
                List<TriFollower> followers = trigrams.get(word);
                TriFollower follower = followers.get(new Random().nextInt(followers.size()));
                if(follower.getFollower2()==null)
                    break;

                System.out.print(follower.getFollower1()+" "+follower.getFollower2()+" ");
                word=follower.getFollower2();
            }
            while (trigrams.containsKey(word));
        }
    }
    private static void runProcedureForBigrams() throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(BIGRAMS_FILE));
        HashMap<String,List<BiFollower>> bigrams = new HashMap<>();
        while (scanner.hasNextLine()){
            String[] line = scanner.nextLine().split(" ");
            if(Integer.parseInt(line[0])<3)
                continue;
            List<BiFollower> entry = bigrams.get(line[1]);
            if (entry!=null){
                entry.add(new BiFollower(Integer.parseInt(line[0]),line[2]));
            }else {
                List<BiFollower> followers = new ArrayList<>();
                followers.add(new BiFollower(Integer.parseInt(line[0]),line[2]));
                bigrams.put(line[1],followers);
            }
        }
        for (int i=0;i<20;i++){
            String word = (String)bigrams.keySet().toArray()[new Random().nextInt(bigrams.size())];
            System.out.print(word+" ");
            do{
                List<BiFollower> followers = bigrams.get(word);
                word = followers.get(new Random().nextInt(followers.size())).getFollower();
                System.out.print(word+" ");
            }
            while (bigrams.containsKey(word));
        }
    }
}
