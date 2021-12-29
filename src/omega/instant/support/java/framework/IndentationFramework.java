/**
* Manages Java Type Explicit Indentation Invocation - Ctrl + I
*
* Copyright (C) 2021 Omega UI
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package omega.instant.support.java.framework;
import omega.instant.support.java.assist.CodeTokenizer;

import java.util.LinkedList;

import org.fife.ui.rtextarea.RTextArea;
public class IndentationFramework {
	public static void indent(RTextArea textArea){
		LinkedList<String> lines = CodeTokenizer.tokenizeWithoutLoss(textArea.getText(), '\n');
		int caretPos = textArea.getCaretPosition();
		int caretLineNumber = textArea.getCaretLineNumber();
		textArea.setText("");
		int tabs = 0;
		int lineN = 0;
		boolean needsExtraTab = false;
		boolean containsEqual = false;
		for(String token : lines){
			token = token.trim();
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
	* Counts the char 'c' in specified line excluding the strings and characters
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
