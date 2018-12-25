import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static final String UNIGRAMS_FILE = "D:\\Programowanie\\Java\\NLP2018\\poleval_unigrams.txt";
    public static final String BIGRAMS_FILE = "D:\\Programowanie\\Java\\NLP2018\\poleval_2grams.txt";
    private static List<String> originalWords;
    private static List<String> tokens;
    private static HashMap<String,Integer> unigrams;
    private static HashMap<String,HashMap<String,Integer>> bigrams;
    public static void main(String[] args) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File("D:\\Programowanie\\Java\\NLP2018\\Lista_3\\zad_3.1\\text.txt"));
        originalWords=new ArrayList<>();
        while (scanner.hasNextLine()){
            String[] line = scanner.nextLine().split(" ");
            Collections.addAll(originalWords, line);
        }
        tokens = Preprocesor.preprocesText(originalWords);

        scanner = new Scanner(new File(BIGRAMS_FILE));
        bigrams = new HashMap<>();
        while (scanner.hasNextLine()){
            String[] line = scanner.nextLine().split(" ");
            if(Integer.parseInt(line[0])<3)
                continue;
            bigrams.putIfAbsent(line[1],new HashMap<>());
            bigrams.get(line[1]).put(line[2],Integer.parseInt(line[0]));
        }
        scanner = new Scanner(new File(UNIGRAMS_FILE));
        unigrams = new HashMap<>();
        while (scanner.hasNextLine()){
            String[] line = scanner.nextLine().split(" ");
            unigrams.put(line[0],Integer.parseInt(line[1]));
        }

        checkResult(restoreOriginalText(tokens));
    }
    private static void checkResult(List<String> tokens){
        int polskawaCount=0;
        int pelnaCount=0;
        for (int i=0;i<originalWords.size();i++){
            if (originalWords.get(i).equalsIgnoreCase(tokens.get(i)))
                polskawaCount++;
            if (originalWords.get(i).equals(tokens.get(i)))
                pelnaCount++;
        }
        System.out.println(Math.sqrt(
                (pelnaCount/(double)originalWords.size())*(polskawaCount/(double)originalWords.size())
        ));
    }
    private static List<String> restoreOriginalText(List<String> tokens){
        List<String> result = new ArrayList<>();
        String previousToken = "<BOS>";
        for (String token:tokens){
            Map<String,Integer> possibleWords = unigrams.entrySet().stream()
                    .filter(e-> canEqual(token,e.getKey()))
                    .collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue));

            if (possibleWords.keySet().size()==0){
                result.add(token);
                continue;
            }
            if (possibleWords.keySet().size()==1){
                result.add(new ArrayList<>(possibleWords.keySet()).get(0));
                continue;
            }
            Map<String,Integer> possibleBigrams = bigrams.getOrDefault(previousToken,new HashMap<>()).entrySet().stream()
                    .filter(e->{
                                for (String key:possibleWords.keySet()){
                                    if (e.getKey().equalsIgnoreCase(key))
                                        return true;
                                }
                                return false;
                            }
                    )
                    .collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue));
            int maxScore=0;
            String best=token;
            for (Map.Entry<String,Integer> entry:possibleWords.entrySet()){
                int score = entry.getValue()*possibleBigrams.getOrDefault(entry.getKey(),1);
                if (score>maxScore){
                    maxScore=score;
                    best=entry.getKey();
                }
            }
            result.add(best);
            previousToken=best;
        }
        return result;
    }
    private static boolean canEqual(String word1, String word2){
        if (word1.length()!=word2.length())
            return false;
        word2=word2.toLowerCase();
        for (int i=0;i<word1.length();i++){
            switch (word1.charAt(i)){
                case 'a':
                    if (word2.charAt(i)!='a'&&word2.charAt(i)!='ą')
                        return false;
                    break;
                case 'c':
                    if (word2.charAt(i)!='c'&&word2.charAt(i)!='ć')
                        return false;
                    break;
                case 'e':
                    if (word2.charAt(i)!='e'&&word2.charAt(i)!='ę')
                        return false;
                    break;
                case 'l':
                    if (word2.charAt(i)!='l'&&word2.charAt(i)!='ł')
                        return false;
                    break;
                case 'n':
                    if (word2.charAt(i)!='n'&&word2.charAt(i)!='ń')
                        return false;
                    break;
                case 'o':
                    if (word2.charAt(i)!='o'&&word2.charAt(i)!='ó')
                        return false;
                    break;
                case 's':
                    if (word2.charAt(i)!='s'&&word2.charAt(i)!='ś')
                        return false;
                    break;
                case 'z':
                    if (word2.charAt(i)!='z'&&word2.charAt(i)!='ż'&&word2.charAt(i)!='ź')
                        return false;
                    break;
                default:
                    if (word1.charAt(i)!=word2.charAt(i))
                        return false;
                    break;
            }
        }
        return true;
    }
}
