/**
* Return the suitable color for some token types
*/

package omega.instant.support;
import omega.jdk.JDKManager;

import org.fife.ui.rsyntaxtextarea.Token;

import java.awt.Color;

import static omega.utils.UIManager.*;
public class BasicCodeHighlighter {
	
	public static Color varColor = Color.decode("#486d71");
	public static Color classColor = Color.decode("#8d8b60");
	public static Color sourceClassColor = Color.decode("#71935c");
	public static Color operatorColor = Color.decode("#83605f");
	public static Color constantColor = Color.decode("#FFCD22");
	
	public static synchronized boolean isComputable(Token t){
		return t.getType() != Token.RESERVED_WORD && t.getType() != Token.RESERVED_WORD_2 && (
			switch(t.getType()){
				case Token.IDENTIFIER:
				case Token.OPERATOR:
					yield true;
				default:
					yield false;
			}
		);
	}
	
	public static synchronized Color computeColor(Token t){
		String text = t.getLexeme();
		if(Character.isLowerCase(text.charAt(0)))
			return varColor;
		if(Character.isUpperCase(text.charAt(0))){
			if(JDKManager.isSourceClass(text))
				return sourceClassColor;
			boolean isConstant = true;
			for(int i = 1; i < text.length(); i++){
				if(Character.isLetter(text.charAt(i)) && !Character.isUpperCase(text.charAt(i))){
					isConstant = false;
				}
			}
			if(isConstant)
				return constantColor;
			return classColor;
		}
		if(t.getType() == Token.OPERATOR || t.getType() == Token.SEPARATOR)
			return operatorColor;
		return glow;
	}
}
