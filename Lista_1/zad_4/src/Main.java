import java.io.*;
import java.util.*;

public class Main {
    public static final String BIGRAMS_FILE = "D:\\Programowanie\\Java\\NLP2018\\Lista_1\\poleval_2grams.txt";
    public static final String TRIGRAMS_FILE = "D:\\Programowanie\\Java\\NLP2018\\Lista_1\\poleval_3grams.txt";

    public static void main(String[] args) throws IOException {
        String sentence = "wczoraj wieczorem spotkałem pewną piękną kobietę";
        List<String> words = new ArrayList<>(Arrays.asList(sentence.split(" ")));
        List<List<String>> permutations = permute(new ArrayList<>(words));
        words.add("<BOS>");
        words.add("<EOS>");
        List<ScoredSentence> sentences = scorePermutations(permutations,words);

        FileWriter fileWriter= new FileWriter("results.txt");
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        for (ScoredSentence s:sentences){
            for (int i=1;i<s.getSentence().size()-1;i++){
                bufferedWriter.write(s.getSentence().get(i)+" ");
            }
            bufferedWriter.newLine();
        }
        bufferedWriter.close();
    }
    private static List<ScoredSentence> scorePermutations(List<List<String>> permutations, List<String> words) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(BIGRAMS_FILE));
        HashMap<String,List<BiFollower>> bigrams = new HashMap<>();
        while (scanner.hasNextLine()){
            String[] line = scanner.nextLine().split(" ");
            if (!(words.contains(line[1])&&words.contains(line[2])))
                continue;
            int occurrences = Integer.parseInt(line[0]);
            List<BiFollower> entry = bigrams.get(line[1]);
            if (entry!=null){
                entry.add(new BiFollower(occurrences,line[2]));
            }else {
                List<BiFollower> followers = new ArrayList<>();
                followers.add(new BiFollower(occurrences,line[2]));
                bigrams.put(line[1],followers);
            }
        }
        scanner = new Scanner(new File(TRIGRAMS_FILE));
        HashMap<String,List<TriFollower>> trigrams = new HashMap<>();
        while (scanner.hasNextLine()){
            String[] line = scanner.nextLine().split(" ");
            if (!(words.contains(line[1])&&words.contains(line[2])&&(line.length<4||words.contains(line[3]))))
                continue;
            int occurrences = Integer.parseInt(line[0]);
            List<TriFollower> followers = trigrams.get(line[1]);
            if (followers!=null){
                followers.add(new TriFollower(occurrences,line[2],line.length==4?line[3]:null));
            }else {
                followers = new ArrayList<>();
                followers.add(new TriFollower(occurrences,line[2],line.length==4?line[3]:null));
                trigrams.put(line[1],followers);
            }
        }
        scanner.close();
        List<ScoredSentence> sentences = new ArrayList<>();
        for (List<String> permutation:permutations){
            permutation.add(0,"<BOS>");
            permutation.add("<EOS>");
            ScoredSentence sentence= new ScoredSentence(permutation);
            for (int i=0;i<permutation.size()-1;i++){
                List<BiFollower> biFollowers = bigrams.get(permutation.get(i));
                if(biFollowers!=null){
                    for (BiFollower f:biFollowers){
                        if (f.getFollower().equals(permutation.get(i+1))){
                            sentence.addToScore(f.getOccurrences());
                            break;
                        }
                    }
                }
                if (i>permutation.size()-2)
                    break;

                List<TriFollower> triFollowers = trigrams.get(permutation.get(i));
                if(triFollowers!=null){
                    for (TriFollower t:triFollowers){
                        if (t.getFollower1().equals(permutation.get(i+1))
                                &&(t.getFollower2()==null||t.getFollower2().equals(permutation.get(i+2)))){
                            sentence.addToScore(t.getOccurrences());
                            break;
                        }
                    }
                }
            }
            sentences.add(sentence);
        }
        sentences.sort(Comparator.comparingInt(ScoredSentence::getScore).reversed());
        return sentences;
    }
    private static List<List<String>> permute(List<String> list) {
        if (list.isEmpty()) {
            List<List<String>> result = new ArrayList<>();
            result.add(new ArrayList<>());
            return result;
        }
        List<List<String>> result = new ArrayList<>();
        String firstElement = list.remove(0);
        for (List<String> li : permute(list)) {
            for (int i = 0; i <= li.size(); i++) {
                List<String> temp = new ArrayList<>(li);
                temp.add(i, firstElement);
                result.add(temp);
            }
        }
        return result;
    }
}
