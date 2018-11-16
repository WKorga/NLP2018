import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeSet;

public class TextFileUtils {
    public static void createDictionaryToFile(String textFilePath, String targetFilePath) throws IOException {
        Scanner scanner = new Scanner(new File(textFilePath));
        TreeSet<String> tree = new TreeSet<>();
        while(scanner.hasNext()){
            tree.add(scanner.next());
        }
        scanner.close();
        FileWriter fileWriter= new FileWriter(targetFilePath);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        for (String s:tree){
            bufferedWriter.write(s);
            bufferedWriter.newLine();
        }
        scanner.close();
        bufferedWriter.close();
    }
//    public static void removeSpacesAndUppercaseToFile(String textFilePath, String targetFilePath) throws IOException {
//        int words = 100;
//        Scanner scanner = new Scanner(new File(textFilePath));
//        FileWriter fileWriter= new FileWriter(targetFilePath);
//        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
//        while(scanner.hasNext()&&words>0){
//            bufferedWriter.write(scanner.next().toLowerCase());
//            words--;
//        }
//        scanner.close();
//        bufferedWriter.close();
//    }
    public static void createUnigramsToFile(String textFilePath, String targetFilePath) throws IOException {
        Scanner scanner = new Scanner(new File(textFilePath));
        HashMap<String,Integer> tree = new HashMap<>();
        while(scanner.hasNext()){
            String word = scanner.next();
            if(tree.containsKey(word)){
                tree.put(word,tree.get(word)+1);
            }else{
                tree.put(word,1);
            }
        }
        scanner.close();
        FileWriter fileWriter= new FileWriter(targetFilePath);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        for (Map.Entry<String,Integer> e :tree.entrySet()){
            bufferedWriter.write(e.getKey()+" "+e.getValue());
            bufferedWriter.newLine();
        }
        scanner.close();
        bufferedWriter.close();
    }
}
