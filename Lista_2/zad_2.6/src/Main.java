import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static final String PAN_TADEUSZ_FILE = "D:\\Programowanie\\Java\\NLP2018\\Lista_2\\pan_tadeusz.txt";
    private static Map<String,Integer> syllables = new HashMap<>();
    private static Map<String,Integer> unigrams = new HashMap<>();
    private static Map<String,Map<String,Integer>> bigrams = new HashMap<>();
    private static Map<String,Map<String,Integer>> reversedBigrams = new HashMap<>();
    private static Map<String,String> supertags = new HashMap<>();
    private static Map<String,Integer> syllablesPatterns = new HashMap<>();
    private static List<String> tagsPatterns = new ArrayList<>();
    public static final String SUPERTAGS_FILE = "D:\\Programowanie\\Java\\NLP2018\\Lista_2\\supertags.txt";
    private static final ArrayList<Character> vowels = new ArrayList<Character>(Arrays.asList('a','e','y','i','o','ą','ę','u','ó'));
    public static void main(String[] args) throws FileNotFoundException {
        prepareData();
        List<String> verse2;
        do{
            List<String> verse1 = generateVerse();
            verse2 = generateVerseWithRhyme(verse1.get(verse1.size()-1));
        }while (!(verse2.size()>0));
        //List<String> verse2 = generateVerseWithRhyme("bojąc");
        return;
    }
    private static void prepareData() throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(SUPERTAGS_FILE));
        supertags = new HashMap<>();
        while (scanner.hasNextLine()){
            String[] line = scanner.nextLine().split(" ");
            supertags.put(line[0],line[1]);
        }
        scanner = new Scanner(new File(PAN_TADEUSZ_FILE));
        while (scanner.hasNextLine()){
            String line = scanner.nextLine().toLowerCase();
            String syllablesPattern ="";
            String tagsPattern="";
            //epilog jest pisany 11-zgłoskowcem ale na razie zostawiam tak
            if (line.isEmpty()||countSyllables(line)!=13)
                continue;
            String[] words = line.split(" ");
            String previousWord = "<BOS>";
            for (String word: words){
                word = cleanWord(word);
                if (word.isEmpty())
                    continue;
                int sCount;
                if (syllables.containsKey(word)){
                    unigrams.compute(word,(key,value)->value+1);
                    sCount = syllables.get(word);
                }else {
                    sCount = countSyllables(word);
                    syllables.put(word, sCount);
                    unigrams.put(word,1);
                }
                syllablesPattern+=sCount+" ";

                String tag = supertags.get(word);
                if (tag==null){
                    if(word.length()>=3){
                        tag = word.substring(word.length()-3);
                    }else{
                        tag="^"+word;
                    }
                    supertags.put(word,tag);
                }
                tagsPattern+=tag+" ";
                String finalWord = word;
                bigrams.putIfAbsent(previousWord,new HashMap<>());
                bigrams.compute(previousWord,(key, map)->{
                    map.putIfAbsent(finalWord,0);
                    map.compute(finalWord,(key2,value)->value+1);
                    return map;
                });
                String finalPreviousWord = previousWord;
                reversedBigrams.putIfAbsent(finalWord,new HashMap<>());
                reversedBigrams.compute(finalWord,(key, map)->{
                    map.putIfAbsent(finalPreviousWord,0);
                    map.compute(finalPreviousWord,(key2,value)->value+1);
                    return map;
                });
                previousWord = word;
            }
            syllablesPatterns.putIfAbsent(syllablesPattern,0);
            syllablesPatterns.compute(syllablesPattern,(k,v)->v+1);
            if (!tagsPatterns.contains(tagsPattern))
                tagsPatterns.add(tagsPattern);
        }
    }
    private static int countSyllables(String word){
        int count =0;
        char[] chars = word.toLowerCase().toCharArray();
        for (int i=0;i<word.length();i++){
            if (vowels.contains(chars[i])){
                count++;
                if (chars[i]=='i')
                    i++;
            }
        }
        return count;
    }
    private static String cleanWord(String word){
        if (word.isEmpty())
            return word;
        while (word.endsWith(".")||word.endsWith("?")||word.endsWith(",")||word.endsWith("!")
                ||word.endsWith(";")||word.endsWith(":")||word.endsWith("\"")||word.endsWith("»")
                ||word.endsWith("”")||word.endsWith("*")||word.endsWith("“")||word.endsWith(")")
                ||word.endsWith("«")||word.endsWith("…")||word.endsWith("—")){
            word=word.substring(0,word.length()-1);
        }
        if (word.isEmpty())
            return word;
        if (word.charAt(0)=='«'||word.charAt(0)=='('||word.charAt(0)=='*'){
            word=word.substring(1,word.length());
        }
        return word;
    }
    private static boolean checkIfRhyme(String word1,String word2){
        if(word1.equals(word2))
            return false;
        int v_count=0;
        for (int i=1;i<=word1.length();i++){
            if (i==word2.length())
                return true;
            if (word1.charAt(word1.length()-i)!=word2.charAt(word2.length()-i))
                return false;
            if (vowels.contains(word1.charAt(word1.length()-i))){
                if (i!=word1.length()&&word1.charAt(word1.length()-i-1)!='i')
                    v_count++;
                if (v_count>=2)
                    break;
            }
        }
        return true;
    }
    private static List<String> generateVerse(){
        Map<String,Integer> tSyllablesPatterns = new HashMap<>(syllablesPatterns);
        String pattern=null;
        List<String> verse=new ArrayList<>();
        do {
            pattern = getRandomKey(tSyllablesPatterns);
            tSyllablesPatterns.remove(pattern);
        }while (!generateNextWord(verse,pattern,0,tagsPatterns,"","<BOS>"));
        return verse;

    }
    private static boolean generateNextWord(List<String> verse, String syllablesPattern, int count,
                                         List<String> tagsPatterns, String tagsPattern, String previousWord){
        //take all followers that satisfy the conditions
        if (!bigrams.containsKey(previousWord))
            return false;
        Map<String,Integer> followers = bigrams.get(previousWord).entrySet().stream()
                .filter(e->{
                    return Integer.parseInt(syllablesPattern.split(" ")[count])==syllables.get(e.getKey())
                            && filterPatterns(tagsPatterns,tagsPattern+" "+supertags.get(e.getKey())).size()>0;
                        }
                )
                .collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue));
        //if there are no suitable folowers return false
        if (followers.isEmpty())
            return false;
        String follower=null;
        //as long as there are followers that we havent already checked
        while (!followers.isEmpty()){
            follower=getRandomKey(followers);
            //remove to avoid infinite loop
            followers.remove(follower);
            String nTagsPattern = tagsPattern+" "+supertags.get(follower);
            List<String> nTagsPatterns = filterPatterns(tagsPatterns,nTagsPattern);
            List<String> nVerse = new ArrayList<>(verse);
            nVerse.add(follower);
            if (count==syllablesPattern.split(" ").length-1){
                System.out.println(nVerse);
                verse.clear();
                verse.addAll(nVerse);
                return true;
            }
            if (generateNextWord(nVerse, syllablesPattern, count+1,nTagsPatterns,nTagsPattern,follower)){
                verse.clear();
                verse.addAll(nVerse);
                return true;
            }
        }
        return false;
    }
    private static List<String> generateVerseWithRhyme(String wordToRhyme){
        Map<String,Integer> tSyllablesPatterns = new HashMap<>(syllablesPatterns);
        String pattern;
        String rhyme;
        List<String> verse;
        Map<String,Integer> rhymes = unigrams.entrySet().stream().filter(
                e->checkIfRhyme(e.getKey(),wordToRhyme))
                .collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue));
        do {
            if (tSyllablesPatterns.isEmpty())
                return new ArrayList<>();
            pattern = getRandomKey(tSyllablesPatterns);
            tSyllablesPatterns.remove(pattern);
            String[] patternArray= pattern.split(" ");
            Map<String,Integer> matchingRhymes = rhymes.entrySet().stream().filter(
                    e->Integer.parseInt(patternArray[patternArray.length-1])==syllables.get(e.getKey()))
                    .collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue));
            rhyme = getRandomKey(matchingRhymes);
            verse=new ArrayList<>();
            verse.add(rhyme);
        }while (!generatePreviousWord(verse,pattern.split(" "),1,tagsPatterns, supertags.get(rhyme),rhyme));
        return verse;
    }
    private static boolean generatePreviousWord(List<String> verse, String[] syllablesPattern, int count,
                                           List<String> tagsPatterns, String tagsPattern, String nextWord){
        //take all followers that satisfy the conditions
        if (!reversedBigrams.containsKey(nextWord))
            return false;
        if (count==syllablesPattern.length){
            System.out.println(verse);
            return true;
//            if (reversedBigrams.get(nextWord).containsKey("<BOS>")){
//                System.out.println(verse);
//                return true;
//            } else
//                return false;
        }
        Map<String,Integer> followers = reversedBigrams.get(nextWord).entrySet().stream()
                .filter(e->{
                            return !e.getKey().equals("<BOS>")&&Integer.parseInt(syllablesPattern[syllablesPattern.length-count-1])==syllables.get(e.getKey())
                                    && filterPatterns(tagsPatterns,supertags.get(e.getKey())+" "+tagsPattern).size()>0;
                        }
                )
                .collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue));
        //if there are no suitable folowers return false
        if (followers.isEmpty())
            return false;
        String follower=null;
        //as long as there are followers that we havent already checked
        while (!followers.isEmpty()){
            follower=getRandomKey(followers);
            //remove to avoid infinite loop
            followers.remove(follower);
            String nTagsPattern = supertags.get(follower)+" "+tagsPattern;
            List<String> nTagsPatterns = filterPatterns(tagsPatterns,nTagsPattern);
            List<String> nVerse = new ArrayList<>(verse);
            nVerse.add(0,follower);
            if (generatePreviousWord(nVerse, syllablesPattern, count+1,nTagsPatterns,nTagsPattern,follower)){
                verse.clear();
                verse.addAll(nVerse);
                return true;
            }
        }
        return false;
    }
    private static List<String> filterPatterns(List<String> tagsPatterns, String tagsPattern){
        return tagsPatterns.stream().filter(p->p.contains(tagsPattern)).collect(Collectors.toList());
    }
    private static String getRandomKey(Map<String,Integer> map){
        int occurrences = 0;
        for (Integer value:map.values())
            occurrences+=value;

        int selected = new Random().nextInt(occurrences+1);
        String key = null;
        for(Map.Entry<String,Integer> entry:map.entrySet()){
            if (entry.getValue() < selected){
                selected -= entry.getValue();
                continue;
            }
            key=entry.getKey();
            break;
        }
        return key;
    }
}
