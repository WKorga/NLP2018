import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static final String PAN_TADEUSZ_FILE = "D:\\Programowanie\\Java\\NLP2018\\Lista_2\\pan_tadeusz.txt";
    private static Map<String,Integer> syllables = new HashMap<>();
    private static Map<String,Integer> unigrams = new HashMap<>();
    private static Map<String,Map<String,Double>> bigrams = new HashMap<>();
    private static Map<String,Map<String,Double>> reversedBigrams = new HashMap<>();
    private static Map<String,String> supertags = new HashMap<>();
    private static Map<String,Map<String,Integer>> syllablesPatterns = new HashMap<>();
    private static List<String> tagsPatterns = new ArrayList<>();
    public static final String SUPERTAGS_FILE = "D:\\Programowanie\\Java\\NLP2018\\Lista_2\\supertags.txt";
    private static final ArrayList<Character> vowels = new ArrayList<Character>(Arrays.asList('a','e','y','i','o','ą','ę','u','ó'));
    public static void main(String[] args) throws IOException {
        prepareData();
        generateTwoVerses();
        for(int i=0;i<10;i++){
            generateTwoVerses();
        }
        return;
    }
    private static void generateTwoVerses() throws IOException {
        List<String> verse1;
        List<String> fPatterns = new ArrayList<>(syllablesPatterns.keySet());
        List<String> verse2;
        do {
            String firstVersePattern;
            do {
                verse1=new ArrayList<>();
                firstVersePattern = fPatterns.get(new Random().nextInt(fPatterns.size()));
                fPatterns.remove(firstVersePattern);
            }while (!generateNextWord(verse1,firstVersePattern,0,tagsPatterns,"","<BOS>"));

            Map<String,Integer> sPatterns = syllablesPatterns.get(firstVersePattern);
            String rhyme;
            String wordToRhyme = verse1.get(verse1.size()-1);
            Map<String,Integer> rhymes = unigrams.entrySet().stream().filter(
                    e->checkIfRhyme(e.getKey(),wordToRhyme))
                    .collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue));
            String secondVersePattern;
            do {
                if (sPatterns.isEmpty()){
                    verse2=new ArrayList<>();
                    break;
                }
                secondVersePattern = getRandomIntKey(sPatterns);
                sPatterns.remove(secondVersePattern);
                String[] patternArray= secondVersePattern.split(" ");
                Map<String,Integer> matchingRhymes = rhymes.entrySet().stream().filter(
                        e->Integer.parseInt(patternArray[patternArray.length-1])==syllables.get(e.getKey()))
                        .collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue));
                rhyme = getRandomIntKey(matchingRhymes);
                matchingRhymes.remove(rhyme);
                verse2=new ArrayList<>();
                verse2.add(rhyme);
            }while (!generatePreviousWord(verse2,secondVersePattern.split(" "),1,tagsPatterns, supertags.get(rhyme),rhyme));
        }while (!(verse2.size()>0));

        FileWriter fileWriter= new FileWriter("results.txt",true);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.println("\n");
        verse1.forEach(s->printWriter.print(s+" "));
        printWriter.write("\n");
        verse2.forEach(s->printWriter.print(s+" "));
        printWriter.close();
    }
    private static void prepareData() throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(SUPERTAGS_FILE));
        supertags = new HashMap<>();
        while (scanner.hasNextLine()){
            String[] line = scanner.nextLine().split(" ");
            supertags.put(line[0],line[1]);
        }
        scanner = new Scanner(new File(PAN_TADEUSZ_FILE));
        String firstLinePattern="";
        boolean second=false;
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
                    map.putIfAbsent(finalWord,0.0);
                    map.compute(finalWord,(key2,value)->value+1);
                    return map;
                });
                String finalPreviousWord = previousWord;
                reversedBigrams.putIfAbsent(finalWord,new HashMap<>());
                reversedBigrams.compute(finalWord,(key, map)->{
                    map.putIfAbsent(finalPreviousWord,0.0);
                    map.compute(finalPreviousWord,(key2,value)->value+1);
                    return map;
                });
                previousWord = word;
            }
            if (second){
                syllablesPatterns.putIfAbsent(firstLinePattern,new HashMap<>());
                String finalSyllablesPattern = syllablesPattern;
                syllablesPatterns.compute(firstLinePattern,(key, map)->{
                    map.putIfAbsent(finalSyllablesPattern,0);
                    map.compute(finalSyllablesPattern,(key2,value)->value+1);
                    return map;
                });
                second=false;
            }else {
                firstLinePattern=syllablesPattern;
                second=true;
            }
            if (!tagsPatterns.contains(tagsPattern))
                tagsPatterns.add(tagsPattern);
        }
        countPPMIValues(bigrams);
        countPPMIValues(reversedBigrams);
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

    private static boolean generateNextWord(List<String> verse, String syllablesPattern, int count,
                                         List<String> tagsPatterns, String tagsPattern, String previousWord){
        //take all followers that satisfy the conditions
        if (!bigrams.containsKey(previousWord))
            return false;
        Map<String,Double> followers = bigrams.get(previousWord).entrySet().stream()
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
            follower=getRandomDoubleKey(followers);
            //remove to avoid infinite loop
            followers.remove(follower);
            String nTagsPattern = tagsPattern+" "+supertags.get(follower);
            List<String> nTagsPatterns = filterPatterns(tagsPatterns,nTagsPattern);
            List<String> nVerse = new ArrayList<>(verse);
            nVerse.add(follower);
            if (count==syllablesPattern.split(" ").length-1 ||
                    generateNextWord(nVerse, syllablesPattern, count+1,nTagsPatterns,nTagsPattern,follower)){
                verse.clear();
                verse.addAll(nVerse);
                return true;
            }
        }
        return false;
    }
    private static boolean generatePreviousWord(List<String> verse, String[] syllablesPattern, int count,
                                           List<String> tagsPatterns, String tagsPattern, String nextWord){
        //take all followers that satisfy the conditions
        if (!reversedBigrams.containsKey(nextWord))
            return false;
        if (count==syllablesPattern.length){
            return true;
        }
        Map<String,Double> followers = reversedBigrams.get(nextWord).entrySet().stream()
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
            follower=getRandomDoubleKey(followers);
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
    private static String getRandomIntKey(Map<String,Integer> map){
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
    private static String getRandomDoubleKey(Map<String,Double> map){
        double occurrences = 0;
        for (Double value:map.values())
            occurrences+=value;

        double selected = new Random().nextDouble()*occurrences;
        String key = null;
        for(Map.Entry<String,Double> entry:map.entrySet()){
            if (entry.getValue() < selected){
                selected -= entry.getValue();
                continue;
            }
            key=entry.getKey();
            break;
        }
        return key;
    }
    private static void countPPMIValues(Map<String,Map<String,Double>> map){
        int occurrences = 0;
        for (Integer i:unigrams.values()){
            occurrences+=i;
        }
        for (Map.Entry<String,Map<String,Double>> entry1:map.entrySet()){
            if (entry1.getKey().equals("<BOS>"))
                continue;
            for (Map.Entry<String,Double> entry2:entry1.getValue().entrySet()){
                if (entry2.getKey().equals("<BOS>"))
                    continue;
                double value = Math.max(0,Math.log(
                        (entry2.getValue()*(double)occurrences)/
                                (unigrams.get(entry1.getKey())*unigrams.get(entry2.getKey()))
                ));
                entry1.getValue().put(entry2.getKey(),value);
            }
        }
    }
}
