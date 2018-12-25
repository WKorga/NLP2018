

import java.io.*;
import java.util.*;

public class Main {
    public static final String prus_file = "D:\\Programowanie\\Java\\NLP2018\\Lista_2\\dane_pozytywistyczne\\korpus_prusa.txt";
    public static final String orzeszkowa_file = "D:\\Programowanie\\Java\\NLP2018\\Lista_2\\dane_pozytywistyczne\\korpus_orzeszkowej.txt";
    public static final String sienkiewcz_file = "D:\\Programowanie\\Java\\NLP2018\\Lista_2\\dane_pozytywistyczne\\korpus_sienkiewicza.txt";
    public static final String SUPERTAGS_FILE = "D:\\Programowanie\\Java\\NLP2018\\Lista_2\\supertags.txt";
    public static final String TESTS_DIRECTORY = "D:\\Programowanie\\Java\\NLP2018\\Lista_2\\dane_pozytywistyczne\\testy1";
    private static HashMap<String,String> supertags = null;
    private static HashMap<Integer,Stats> lengths = new HashMap<>();
    private static HashMap<String,Stats> tags = new HashMap<>();
    private static HashMap<String,Stats> words = new HashMap<>();
    public static void main(String[] args) throws IOException {
        prepareData();
        File directory = new File(TESTS_DIRECTORY);
        for (File f:directory.listFiles()){
            validateForFile(f);
        }
    }
    private static void validateForFile(File file) throws IOException {
        Scanner scanner = new Scanner(file);
        double orzeszkowa=1, sienkiewicz=1, prus=1;
        while (scanner.hasNextLine()){
            String[] sentences = scanner.nextLine().split("[.?!] (?=[A-Z0-9])");
            for (String sentence:sentences){
                if (sentence.isEmpty())
                    continue;
                int length = sentence.split(" ").length;
                Stats lengthStats = lengths.get(length);
                if (lengthStats!=null){
                    orzeszkowa *= ((double)lengthStats.getOrzeszkowa())/((double) lengthStats.getAll());
                    sienkiewicz *= ((double)lengthStats.getSienkiewicz())/((double) lengthStats.getAll());
                    prus *= ((double)lengthStats.getPrus())/((double) lengthStats.getAll());
                }
                for (String word:sentence.split(" ")){
                    if (word.isEmpty())
                        continue;
                    word=word.toLowerCase();
                    while (word.endsWith(".")||word.endsWith("?")||word.endsWith(",")||word.endsWith("!")
                            ||word.endsWith(";")||word.endsWith(":")||word.endsWith("\"")||word.endsWith("…")
                            ||word.endsWith("”")||word.endsWith("*")||word.endsWith("“")){
                        word=word.substring(0,word.length()-1);
                    }
                    Stats wordStats = words.get(word);
                    if (wordStats!=null){
                        orzeszkowa *= ((double)wordStats.getOrzeszkowa())/((double) wordStats.getAll());
                        sienkiewicz *= ((double)wordStats.getSienkiewicz())/((double) wordStats.getAll());
                        prus *= ((double)wordStats.getPrus())/((double) wordStats.getAll());
                    }
                    String tag = supertags.get(word);
                    if (tag==null){
                        if(word.length()>=3){
                            tag = word.substring(word.length()-3);
                        }else{
                            tag="^"+word;
                        }
                    }
                    Stats tagsStats = tags.get(tag);
                    if (tagsStats!=null){
                        orzeszkowa *= ((double)tagsStats.getOrzeszkowa())/((double) tagsStats.getAll());
                        sienkiewicz *= ((double)tagsStats.getSienkiewicz())/((double) tagsStats.getAll());
                        prus *= ((double)tagsStats.getPrus())/((double) tagsStats.getAll());
                    }
                }
            }
        }
        String max;
        if (orzeszkowa>sienkiewicz){
            if (orzeszkowa>prus)
                max = "Orzeszkowa";
            else
                max = "Prus";
        }else {
            if (sienkiewicz>prus)
                max = "Sienkiewicz";
            else
                max = "Prus";
        }
        FileWriter fileWriter = new FileWriter("result.txt",true);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.println(file.getName() +" "+max
                +" (Orzeszkowa: "+orzeszkowa
                +", Sienkiewicz: "+sienkiewicz
                +", Prus: "+prus+")");
        printWriter.close();
    }
    private static void prepareData() throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(SUPERTAGS_FILE));
        supertags = new HashMap<>();
        while (scanner.hasNextLine()){
            String[] line = scanner.nextLine().split(" ");
            supertags.put(line[0],line[1]);
        }
        scanner.close();
        prepareForAuthor(orzeszkowa_file,Author.ORZESZKOWA);
        prepareForAuthor(sienkiewcz_file,Author.SIENKIEWICZ);
        prepareForAuthor(prus_file,Author.PRUS);
        filterData(lengths);
        filterData(tags);
        filterData(words);
        return;
    }
    private static void prepareForAuthor(String file, Author author) throws FileNotFoundException {
        Scanner scanner =  new Scanner(new File(file));
        while (scanner.hasNextLine()){
            String[] sentences = scanner.nextLine().split("[.?!] (?=[A-Z0-9])");
            for (String sentence:sentences){
                if (sentence.isEmpty())
                    continue;
                Stats lengthStats = lengths.get(sentence.split(" ").length);
                if (lengthStats==null){
                    lengthStats = new Stats();
                    lengths.put(sentence.split(" ").length,lengthStats);
                }
                lengthStats.increment(author);

                for (String word:sentence.split(" ")){
                    if (word.isEmpty())
                        continue;
                    word=word.toLowerCase();
                    while (word.endsWith(".")||word.endsWith("?")||word.endsWith(",")||word.endsWith("!")
                            ||word.endsWith(";")||word.endsWith(":")||word.endsWith("\"")||word.endsWith("…")
                            ||word.endsWith("”")||word.endsWith("*")||word.endsWith("“")){
                        word=word.substring(0,word.length()-1);
                    }
                    Stats wordStats = words.get(word);
                    if (wordStats==null){
                        wordStats = new Stats();
                        words.put(word,lengthStats);
                    }
                    wordStats.increment(author);

                    String tag = supertags.get(word);
                    if (tag==null){
                        if(word.length()>=3){
                            tag = word.substring(word.length()-3);
                        }else{
                            tag="^"+word;
                        }
                    }
                    Stats tagsStats = tags.get(tag);
                    if (tagsStats==null){
                        tagsStats = new Stats();
                        tags.put(tag,tagsStats);
                    }
                    tagsStats.increment(author);
                }
            }
        }
    }
    private static <T> void filterData (HashMap<T,Stats> map){
        List<T> keysToDelete = new ArrayList<>();
        for (Map.Entry<T,Stats> entry:map.entrySet()){
            Stats stats = entry.getValue();
            if (((double)stats.getOrzeszkowa())/stats.getAll()>0.65d)
                continue;
            if (((double)stats.getPrus())/stats.getAll()>0.65d)
                continue;
            if (((double)stats.getSienkiewicz())/stats.getAll()>0.65d)
                continue;
            keysToDelete.add(entry.getKey());
        }
        for (T key:keysToDelete)
            map.remove(key);
    }
}
