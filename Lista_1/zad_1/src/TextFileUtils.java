import java.io.*;
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
    public static void removeSpacesAndUppercaseToFile(String textFilePath, String targetFilePath) throws IOException {
        int words = 100;
        Scanner scanner = new Scanner(new File(textFilePath));
        FileWriter fileWriter= new FileWriter(targetFilePath);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        while(scanner.hasNext()&&words>0){
            bufferedWriter.write(scanner.next().toLowerCase());
            words--;
        }
        scanner.close();
        bufferedWriter.close();
    }
}
