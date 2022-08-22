/*
 * Return the suitable color for some target tokens(Java)
 * Copyright (C) 2022 Omega UI

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
import omega.instant.support.AbstractCodeHighlighter;

import omega.instant.support.java.framework.ImportFramework;

import java.util.LinkedList;

import omega.instant.support.java.assist.SourceReader;

import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import java.awt.Color;

import static omega.io.UIManager.*;
public class BasicCodeHighlighter extends AbstractCodeHighlighter{

	public static Color valueKeyForeColor = glow;
	public static Color returnKeyForeColor = glow;
	public static Color javaConstantForeColor = glow;
	public static Color objectKeywordForeColor = glow;
	public static Color packageColor = glow;

	public static Color classColor;
	public static Color methColor;

	public static LinkedList<String> lastClassList;
	public static String lastText;

	static{
		valueKeyForeColor = TOOLMENU_COLOR2;

		returnKeyForeColor = TOOLMENU_COLOR3;

		javaConstantForeColor = TOOLMENU_COLOR1;
		
		if(isDarkMode()){
			classColor = Color.decode("#B9B9B6");
			methColor = Color.decode("#FFC66D");
			objectKeywordForeColor = Color.decode("#FEA248");
			packageColor = Color.decode("#ca5805");
		}
		else{
			classColor = Color.BLACK;
			methColor = Color.decode("#00425A");
		}
	}

	@Override
	public synchronized boolean canComputeForeground(RSyntaxTextArea textArea, Token t){
		return textArea.getSyntaxEditingStyle() == textArea.SYNTAX_STYLE_JAVA
		&& switch(t.getType()){
			case Token.RESERVED_WORD:
			case Token.RESERVED_WORD_2:
			case Token.LITERAL_BOOLEAN:
			case Token.IDENTIFIER:
			case Token.FUNCTION:
			yield true;
			default:
			yield false;
		};
	}

	@Override
	public synchronized Color computeForegroundColor(RSyntaxTextArea textArea, Token t){
		String text = t.getLexeme();
		try{
			if(isValueKeyword(text)){
				return valueKeyForeColor;
			}
			else if(isObjectKeyword(text)){
				return objectKeywordForeColor;
			}
			else if(isPackageKeyword(text)){
				return packageColor;
			}
			else if(isReturnKeyword(text)){
				return returnKeyForeColor;
			}
			else if(isJavaConstant(text)){
				return javaConstantForeColor;
			}
			else if(isJavaMethodDeclaration(textArea, t)){
				return methColor;
			}
			else if(isJavaClass(textArea, text)){
				return classColor;
			}
		}
		catch(Exception e){}
		return t.getType() == Token.FUNCTION ? classColor : null;
	}

	public static synchronized boolean isValueKeyword(String text){
		return text.equals("null") || text.equals("true") || text.equals("false");
	}

	public static synchronized boolean isObjectKeyword(String text){
		return text.equals("super") || text.equals("this");
	}

	public static synchronized boolean isPackageKeyword(String text){
		return text.equals("package") || text.equals("import");
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

	public static synchronized boolean isJavaClass(RSyntaxTextArea textArea, String text){
		LinkedList<String> clazzez = null;
		if(lastText != null && lastText.equals(textArea.getText()))
			clazzez = lastClassList;
		else
			clazzez = lastClassList = ImportFramework.fastListContainedClasses(lastText = textArea.getText());
		for(String name : clazzez){
			if(name.equals(text))
				return true;
		}
		return false;
	}

	public static synchronized boolean isJavaMethodDeclaration(RSyntaxTextArea textArea, Token t){
		if(!t.isIdentifier())
			return false;
		String text = t.getLexeme();
		if(!Character.isLowerCase(text.charAt(0)))
			return false;
		String code = textArea.getText();
		int endOffset = t.getEndOffset();
		int startOffset = t.getOffset();
		if(code.length() - 1 <= endOffset)
			return false;
		return code.charAt(startOffset - 1) == ' ' && code.charAt(endOffset) == '(' && code.charAt(code.indexOf('\n', endOffset) - 1) != ';';
	}
}
