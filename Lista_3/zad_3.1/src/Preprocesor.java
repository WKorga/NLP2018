import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Preprocesor {
    public static List<String> preprocesText(List<String> words) throws FileNotFoundException {
        List<String> tokens = new ArrayList<>();
        for (String word:words){
            word=word.toLowerCase();
            tokens.add(removePolishLetters(word));
        }
        return tokens;
    }
    private static String removePolishLetters(String string){
        String result ="";
        for (char letter:string.toCharArray()){
            switch (letter){
                case 'ą':
                    result+="a";
                    break;
                case 'ć':
                    result+="c";
                    break;
                case 'ę':
                    result+="e";
                    break;
                case 'ł':
                    result+="l";
                    break;
                case 'ń':
                    result+="n";
                    break;
                case 'ó':
                    result+="o";
                    break;
                case 'ś':
                    result+="s";
                    break;
                case 'ż':
                    result+="z";
                    break;
                case 'ź':
                    result+="z";
                    break;
                default:
                    result+=letter;
                    break;
            }
        }
        return result;
    }
}
