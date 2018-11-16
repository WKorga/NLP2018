import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static final String BIGRAMS_FILE = "D:\\Programowanie\\Java\\NLP2018\\poleval_2grams.txt";
    public static final String UNIGRAMS_FILE = "D:\\Programowanie\\Java\\NLP2018\\poleval_unigrams.txt";
    public static final String SUPERTAGS_FILE = "D:\\Programowanie\\Java\\NLP2018\\Lista_2\\supertags.txt";
    private static HashMap<String,String> tags = null;
    private static HashMap<String,Integer> unigrams = null;
    private static Connection connection = null;
    public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
        String[] sentence = "Mały Piotruś spotkał w niewielkiej restauracyjce wczoraj poznaną koleżankę"
                .toLowerCase().split(" ");
        Class.forName("com.mysql.cj.jdbc.Driver");

        String url ="jdbc:mysql://nlp18-mysql.mysql.database.azure.com:3306/nlp18?"+
                "useSSL=true&requireSSL=false&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        connection= DriverManager.getConnection(
                url,"nlp-user@nlp18-mysql","");
        connection.createStatement().execute("ALTER DATABASE nlp18 DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;");

        runForBigrams(sentence);
    }
    private static void runForBigrams(String[] sentence) throws IOException, SQLException {
        Scanner scanner = new Scanner(new File(SUPERTAGS_FILE));
        tags = new HashMap<>();
        unigrams = new HashMap<>();
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

        FileWriter fileWriter = new FileWriter("bigrams_result.txt");
        PrintWriter printWriter = new PrintWriter(fileWriter);

        for (int i=0;i<10;i++){
            String result = "";
            String word = randomUnigram(sentence[0]);
            result+=word+" ";
            for (int j =1;j<sentence.length;j++){
                int finalJ = j;
                List<BiFollower> words = getFollowers(word).stream()
                        .filter(f->tags.get(sentence[finalJ]).equals(tags.get(f.getFollower())))
                        .collect(Collectors.toList());
                if (words.isEmpty()){
                    word = randomUnigram(sentence[j]);
                    result+="|"+word+" ";
                    continue;
                }
                int occurrences =0;
                for (BiFollower follower:words){
                    occurrences+=follower.getOccurrences();
                }
                int selected = new Random().nextInt(occurrences);
                for (BiFollower follower:words) {
                    if (follower.getOccurrences() < selected){
                        selected -= follower.getOccurrences();
                        continue;
                    }
                    word = follower.getFollower();
                    result+=word+" ";
                    break;
                }
            }
            printWriter.println(result);
        }
        printWriter.close();
    }
    private static String randomUnigram(String word){
        List<Map.Entry<String,Integer>> words = unigrams.entrySet().stream()
                .filter(e->
                        (!e.getKey().equals(word)) &&
                                tags.get(word).equals(tags.get(e.getKey()))
                )
                .collect(Collectors.toList());
        if (words.isEmpty()){
            return word;
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
            return entry.getKey();
        }
        return null;
    }
    private static List<BiFollower> getFollowers(String word) throws SQLException {
        Statement statement = connection.createStatement();
        System.out.println("Select for "+word);
        ResultSet resultSet = statement.executeQuery("select occurrences, word2 from bigrams where occurrences > 2 and word1='"+word+"';");
        List<BiFollower> followers = new ArrayList<>();
        while (resultSet.next()){
            followers.add(new BiFollower(resultSet.getInt("occurrences"),resultSet.getString("word2")));
        }
        return followers;
    }
}
