import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static final String BIGRAMS_FILE = "D:\\Programowanie\\Java\\NLP2018\\Lista_1\\poleval_2grams.txt";
    public static final String TRIGRAMS_FILE = "D:\\Programowanie\\Java\\NLP2018\\Lista_1\\poleval_3grams.txt";
    public static final String DICTIONARY_FILE = "D:\\Programowanie\\Java\\NLP2018\\Lista_1\\dictionary.txt";
    public static void main(String[] args) throws IOException {
        runProcedureForTrigrams();
    }
    private static void runProcedureForTrigrams() throws IOException {
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
        FileWriter fileWriter= new FileWriter("tri_result.txt");
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        for (int i=0;i<20;i++){
            String firstWord = "<BOS>";
            List<TriFollower> followers = trigrams.get(firstWord);
            TriFollower follower;
            do{
                follower = followers.get(new Random().nextInt(followers.size()));
                bufferedWriter.write(follower.getFollower1()+" ");
                firstWord = follower.getFollower1();
                final String f2 = follower.getFollower2();
                followers = trigrams.get(firstWord)
                        .stream().filter(t->t.getFollower1().equals(f2)).collect(Collectors.toList());
                if (followers.size()==0)
                    break;
            }while (!follower.getFollower2().equals("<EOS>")&&trigrams.containsKey(firstWord));
            bufferedWriter.write((follower.getFollower2().equals("<EOS>")?"":follower.getFollower2()+" "));
        }
        bufferedWriter.close();
    }
    private static void runProcedureForBigrams() throws IOException {
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
        FileWriter fileWriter= new FileWriter("bi_result.txt");
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        for (int i=0;i<20;i++){
            String word = "<BOS>";
            do{
                List<BiFollower> followers = bigrams.get(word);
                word = followers.get(new Random().nextInt(followers.size())).getFollower();
                if(!word.equals("<EOS>"))
                    bufferedWriter.write(word+" ");
            } while (!word.equals("<EOS>")&&bigrams.containsKey(word));
        }
        bufferedWriter.close();
    }
}
