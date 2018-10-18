import java.io.*;
import java.util.*;

public class Main {
    public static final String TEXT_FILE = "D:\\Programowanie\\Java\\NLP2018\\Lista_1\\task3_test_segmented.txt";
    public static final String DICTIONARY_FILE = "D:\\Programowanie\\Java\\NLP2018\\Lista_1\\dictionary.txt";
    public static final String NO_SPACES_FILE = "D:\\Programowanie\\Java\\NLP2018\\Lista_1\\no_spaces_lowercase.txt";
    public static void main(String[] args) throws IOException {
        //TextFileUtils.createDictionaryToFile(TEXT_FILE,DICTIONARY_FILE);
        Scanner scanner = new Scanner(new File(TEXT_FILE));
        FileWriter fileWriter = new FileWriter(NO_SPACES_FILE);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        List<String> base = new ArrayList<>();
        for (int i=0;i<100;i++){
            if (scanner.hasNext()){
                String word = scanner.next();
                base.add(word);
                bufferedWriter.write(word.toLowerCase());
            }
        }
        bufferedWriter.close();

        scanner = new Scanner(new File(DICTIONARY_FILE));
        Set<String> dictionary = new HashSet<>();
        int maxLength = -1;
        while(scanner.hasNext()){
            String word = scanner.next();
            if (word.length()>maxLength)
                maxLength=word.length();
            dictionary.add(word);
        }
        scanner.close();
        scanner = new Scanner(new File(NO_SPACES_FILE));
        String text = scanner.next();
        scanner.close();

        List<String> tokenization = MaxMatch.tokenizeTextByMaxMatch(dictionary,maxLength,text);
        System.out.println(rateTokenization(base,tokenization));
    }
    private static double rateTokenization(List<String> base, List<String> tokenization){
        int count = 0;
        for (String s:base){
            if (tokenization.contains(s))
                count++;
        }
        return count/(double)base.size();
    }
}
