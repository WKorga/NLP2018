import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class MaxMatch {
    public static List<String> tokenizeTextByMaxMatch(Set<String> dictionary, int maxLength, String text){
        List<String> tokens = new ArrayList<>();
        int beginIndex = 0;
        int endIndex = maxLength;
        getNextTokenByMaxMatch(beginIndex,endIndex,text,tokens,dictionary,maxLength);

        //recursion causes wrong order of tokens so we need to invert list
        Collections.reverse(tokens);
        return tokens;
    }
    private static boolean getNextTokenByMaxMatch(int beginIndex,
                                                  int endIndex, String text, List tokens,
                                                  Set<String> dictionary, int maxLength){
        if (beginIndex>=text.length())
            return true;
        if (endIndex>text.length())
            endIndex=text.length();
        while (beginIndex<endIndex){
            String substring = text.substring(beginIndex,endIndex);
            if(dictionary.contains(substring)){
                if(getNextTokenByMaxMatch(beginIndex+substring.length(),
                        beginIndex+maxLength,
                        text,tokens,dictionary,maxLength)){
                    tokens.add(substring);
                    return true;
                }
                int x=1;
            }
            endIndex--;
        }
        return false;
    }
}
