import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static final String UNIGRAMS_FILE = "D:\\Programowanie\\Java\\NLP2018\\poleval_unigrams.txt";
    public static final String SUPERTAGS_FILE = "D:\\Programowanie\\Java\\NLP2018\\Lista_2\\supertags.txt";
    public static void main(String[] args) throws IOException {
        String[] sentence = "Mały Piotruś spotkał w niewielkiej restauracyjce wczoraj poznaną koleżankę"
                .toLowerCase().split(" ");
        runForUnigrams(sentence);
    }
    private static void runForUnigrams(String[] sentence) throws IOException {
        Scanner scanner = new Scanner(new File(SUPERTAGS_FILE));
        HashMap<String,String> tags = new HashMap<>();
        HashMap<String,Integer> unigrams = new HashMap<>();
        while (scanner.hasNextLine()){
            String[] line = scanner.nextLine().split(" ");
            tags.put(line[0],line[1]);
        }
        for (String s: sentence){
            if (!tags.containsKey(s)){
                if(s.length()>=3){
                    tags.put(s,s.substring(s.length()-3));
                }else{
                    tags.put(s,"^"+s);
                }
            }
        }
        scanner.close();
        scanner = new Scanner(new File(UNIGRAMS_FILE));
        while (scanner.hasNextLine()){
            String[] line = scanner.nextLine().split(" ");
            if(tags.containsKey(line[0]))
                unigrams.put(line[0],Integer.valueOf(line[1]));
        }

        FileWriter fileWriter = new FileWriter("unigrams_results.txt");
        PrintWriter printWriter = new PrintWriter(fileWriter);

        for (int i=0;i<20;i++){
            String result = "";
            for (String word : sentence){
                List<Map.Entry<String,Integer>> words = unigrams.entrySet().stream()
                        .filter(e->
                                (!e.getKey().equals(word)) &&
                                        tags.get(word).equals(tags.get(e.getKey()))
                        )
                        .collect(Collectors.toList());
                if (words.isEmpty()){
                    result+=word+" ";
                    continue;
                }
                int occurrences =0;
                for (Map.Entry<String,Integer> entry:words){
                    occurrences+=entry.getValue();
                }
                int selected = new Random().nextInt(occurrences);
                for (Map.Entry<String,Integer> entry:words) {
                    if (entry.getValue() < selected){
                        selected -= entry.getValue();
                        continue;
                    }
                    result+=entry.getKey()+" ";
                    break;
                }
            }
            printWriter.println(result);
        }
        printWriter.close();
    }
}
