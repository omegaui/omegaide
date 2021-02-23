package omega.deassembler;
import java.util.LinkedList;
public class CodeTokenizer {
     public static LinkedList<String> tokenize(String code, char s){
     	LinkedList<String> tokens = new LinkedList<>();
          String token = "";
          for(int i = 0; i < code.length(); i++){
          	char ch = code.charAt(i);
               if(ch == s){
                    tokens.add(token);
                    token = "";
               }
               else
                    token += ch;
          }
          return tokens;
     }
}
