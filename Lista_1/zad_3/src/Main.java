import java.io.*;
import java.util.*;

public class Main {
    public static final String BIGRAMS_FILE = "D:\\Programowanie\\Java\\NLP2018\\Lista_1\\poleval_2grams.txt";
    public static final String TRIGRAMS_FILE = "D:\\Programowanie\\Java\\NLP2018\\Lista_1\\poleval_3grams.txt";
    public static final String DICTIONARY_FILE = "D:\\Programowanie\\Java\\NLP2018\\Lista_1\\dictionary.txt";
    public static void main(String[] args) throws IOException {
        runProcedureForBigrams();
    }
    private static void runProcedureForTrigrams() throws IOException {
        Scanner scanner = new Scanner(new File(TRIGRAMS_FILE));
        HashMap<String,List<TriFollower>> trigrams = new HashMap<>();
        while (scanner.hasNextLine()){
            String[] line = scanner.nextLine().split(" ");
            int occurrences = Integer.parseInt(line[0]);
            if(occurrences<10)
                continue;
            List<TriFollower> followers = trigrams.get(line[1]);
            if (followers!=null){
                followers.add(new TriFollower(occurrences,line[2],line.length==4?line[3]:null));
            }else {
                followers = new ArrayList<>();
                followers.add(new TriFollower(occurrences,line[2],line.length==4?line[3]:null));
                trigrams.put(line[1],followers);
            }
        }
        FileWriter fileWriter= new FileWriter("tri_result.txt");
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        for (int i=0;i<20;i++){
            String word = "<BOS>";
            do{
                List<TriFollower> followers = trigrams.get(word);
                int occurences=0;
                for (TriFollower follower:followers){
                    occurences+=follower.getOccurrences();
                }
                int selected = new Random().nextInt(occurences);
                for (TriFollower follower:followers){
                    if (follower.getOccurrences()<selected)
                        selected-=follower.getOccurrences();
                    else {
                        if(follower.getFollower2()==null){
                            word=follower.getFollower1();
                            if(!word.equals("<EOS>"))
                                bufferedWriter.write(word+" ");
                            break;
                        }
                        bufferedWriter.write(follower.getFollower1()+" "
                                        +(follower.getFollower2().equals("<EOS>")?"":follower.getFollower2()+" "));
                        word=follower.getFollower2();
                        break;
                    }
                }
            } while (!word.equals("<EOS>")&&trigrams.containsKey(word));
        }
        bufferedWriter.close();
    }
    private static void runProcedureForBigrams() throws IOException {
        Scanner scanner = new Scanner(new File(BIGRAMS_FILE));
        HashMap<String,List<BiFollower>> bigrams = new HashMap<>();
        while (scanner.hasNextLine()){
            String[] line = scanner.nextLine().split(" ");
            int occurrences = Integer.parseInt(line[0]);
            if(occurrences<3)
                continue;
            List<BiFollower> entry = bigrams.get(line[1]);
            if (entry!=null){
                entry.add(new BiFollower(occurrences,line[2]));
            }else {
                List<BiFollower> followers = new ArrayList<>();
                followers.add(new BiFollower(occurrences,line[2]));
                bigrams.put(line[1],followers);
            }
        }
        FileWriter fileWriter= new FileWriter("bi_result.txt");
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        for (int i=0;i<20;i++){
            String word = "<BOS>";
            do{
                List<BiFollower> followers = bigrams.get(word);
                int occurences=0;
                for (BiFollower follower:followers){
                    occurences+=follower.getOccurrences();
                }
                int selected = new Random().nextInt(occurences);
                for (BiFollower follower:followers){
                    if (follower.getOccurrences()<selected)
                        selected-=follower.getOccurrences();
                    else {
                        word=follower.getFollower();
                        if(!word.equals("<EOS>"))
                            bufferedWriter.write(word+" ");
                        break;
                    }
                }
            } while (!word.equals("<EOS>")&&bigrams.containsKey(word));
        }
        bufferedWriter.close();
    }
}
