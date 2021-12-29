/**
* Return the suitable color for some target tokens
* Copyright (C) 2021 Omega UI

* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.

* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.

* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package omega.instant.support.java.highlighter;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import java.awt.Color;

import static omega.io.UIManager.*;
public class BasicCodeHighlighter {
	
	public static Color valueKeyColor;
	public static Color returnKeyColor;
	public static Color javaConstantColor;
	
	public static Color valueKeyForeColor = glow;
	public static Color returnKeyForeColor = glow;
	public static Color javaConstantForeColor = glow;
	static{
		valueKeyColor = TOOLMENU_COLOR2_SHADE;
		valueKeyForeColor = TOOLMENU_COLOR2;
		
		returnKeyColor = TOOLMENU_COLOR3_SHADE;
		returnKeyForeColor = TOOLMENU_COLOR3;
		
		javaConstantColor = TOOLMENU_COLOR1_SHADE;
		javaConstantForeColor = TOOLMENU_COLOR1;
	}
	
	public static synchronized boolean canComputeBackground(RSyntaxTextArea textArea, Token t){
		return textArea.getSyntaxEditingStyle() == textArea.SYNTAX_STYLE_JAVA
		&& switch(t.getType()){
			case Token.RESERVED_WORD:
			case Token.RESERVED_WORD_2:
			case Token.LITERAL_BOOLEAN:
			case Token.IDENTIFIER:
				yield true;
			default:
				yield false;
		};
	}
	
	public static synchronized boolean canComputeForeground(RSyntaxTextArea textArea, Token t){
		return textArea.getSyntaxEditingStyle() == textArea.SYNTAX_STYLE_JAVA
		&& switch(t.getType()){
			case Token.RESERVED_WORD:
			case Token.RESERVED_WORD_2:
			case Token.LITERAL_BOOLEAN:
			case Token.IDENTIFIER:
				yield true;
			default:
				yield false;
		};
	}
	
	public static synchronized Color computeForegroundColor(Token t){
		String text = t.getLexeme();
		if(isValueKeyword(text)){
			return valueKeyForeColor;
		}
		else if(isReturnKeyword(text)){
			return returnKeyForeColor;
		}
		else if(isJavaConstant(text)){
			return javaConstantForeColor;
		}
		return null;
	}
	
	public static synchronized Color computeBackgroundColor(Token t){
		String text = t.getLexeme();
		if(isValueKeyword(text)){
			return valueKeyColor;
		}
		else if(isReturnKeyword(text)){
			return returnKeyColor;
		}
		else if(isJavaConstant(text)){
			return javaConstantColor;
		}
		return null;
	}
	
	public static synchronized boolean isValueKeyword(String text){
		return text.equals("null") || text.equals("true") || text.equals("false");
	}
	
	public static synchronized boolean isReturnKeyword(String text){
		return text.equals("yield") || text.equals("return");
	}
	
	public static synchronized boolean isJavaConstant(String text){
		boolean containsLetter = false;
		for(int i = 0; i < text.length(); i++){
			char ch = text.charAt(i);
			if(Character.isLetter(ch)){
				containsLetter = true;
				if(Character.isLowerCase(ch))
					return false;
			}
		}
		return containsLetter;
	}
}
