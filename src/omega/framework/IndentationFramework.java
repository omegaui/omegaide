package omega.framework;
import org.fife.ui.rtextarea.RTextArea;
import java.util.StringTokenizer;
public class IndentationFramework {
     public static void indent(RTextArea textArea){
     	StringTokenizer tok = new StringTokenizer(textArea.getText(), "\n");
          int caretPos = textArea.getCaretPosition();
          int caretLineNumber = textArea.getCaretLineNumber();
          textArea.setText("");
          int tabs = 0;
          int lineN = 0;
          boolean needsExtraTab = false;
          boolean containsEqual = false;
          while(tok.hasMoreTokens()){
               String token = tok.nextToken().trim();
               lineN++;
               if(lineN <= caretLineNumber)
                    caretPos += (tabs/textArea.getTabSize());
               
               if(count('{', token) == count('}', token))
                    containsEqual = true;
               else
                    containsEqual = false;
               
               if(!containsEqual)
                    tabs -= count('}', token);
               
               if(!needsExtraTab)
                    textArea.append(getTabs(tabs) + token + "\n");
               else{
                    textArea.append(getTabs(tabs) + getTabs(1) + token + "\n");
                    needsExtraTab = false;
               }

               if(!containsEqual)
                    tabs += count('{', token);
               
               if((token.startsWith("if") && !token.contains(";")) || token.startsWith("while") || token.startsWith("for") || token.startsWith("else")){
                    if(count('{', token) == 0 && count('(', token) == count(')', token))
                         needsExtraTab = true;
               }
          }
          textArea.setCaretPosition(caretPos + 1);
     }
     /**
      * Counts the char 'c' in specified line excluded the strings and characters
     */
     public static int count(char c, String line){
     	int count = 0;
          boolean instr = false;
          for(int i = 0; i < line.length(); i++){
               char ch = line.charAt(i);
               if(!instr && "\"\'".contains(ch + ""))
                    instr = true;
               else if("\"\'".contains(ch + ""))
                    instr = false;
               if(!instr && ch == c)
                    count++;
          }
          return count;
     }

     public static String getTabs(int n){
     	String res = "";
          while(n-- > 0)
               res += '\t';
          return res;
     }
}
