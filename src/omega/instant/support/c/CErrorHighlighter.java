/**
  * Highlights Errors
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
package omega.instant.support.c;
import omega.framework.CodeFramework;

import omega.highlightUnit.Highlight;

import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;
import javax.swing.text.DefaultHighlighter;

import omega.Screen;

import omega.utils.UIManager;
import omega.utils.Editor;

import java.io.File;

import omega.instant.support.java.JavaSyntaxParser;

import omega.deassembler.CodeTokenizer;

import java.awt.Color;

import java.util.LinkedList;
import java.util.StringTokenizer;
public class CErrorHighlighter {
	
	private LinkedList<Highlight> highlights;
	public static final Color color = omega.utils.UIManager.isDarkMode() ? Color.decode("#800707") : new Color(255, 0, 0, 30);
	public static final HighlightPainter hp = new DefaultHighlighter.DefaultHighlightPainter(color);
	
	public CErrorHighlighter() {
		highlights = new LinkedList<>();
	}

     public String getSimplifiedErrorLog(String errorLog){
          String log = "";
          LinkedList<String> lines = CodeTokenizer.tokenize(errorLog, '\n');
          return log;
     }
     /*
      	c_test.c: In function ‘main’:
		c_test.c:6:2: error: expected ‘,’ or ‘;’ before ‘return’
		    6 |  return 2;
		      |  ^~~~~~
      */
	public void loadErrors(String errorLog) {
		removeAllHighlights();
		StringTokenizer tokenizer = new StringTokenizer(errorLog, "\n");
		boolean canRecord = false;
		String path = "";
		String code = "";
		int line = 0;
		try {
			while(tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken();
				if(!canRecord && CodeFramework.count(token, ':') == 4 && !token.startsWith(" ")){
					int index;
					path = token.substring(0, index = token.indexOf(':')).trim();
					line = Integer.parseInt(token.substring(index + 1, token.indexOf(':', index + 1)).trim());
					canRecord = true;
				}
				else if(canRecord && token.contains("|")){
					code = token.substring(token.indexOf('|') + 1).trim();

					if(!path.contains(File.separator)){
						path = Screen.getFileView().getArgumentManager().compileDir + File.separator + path;
					}
					
					File file = new File(path);
					if(file.exists()){
						Editor e = Screen.getFileView().getScreen().loadFile(file);
						Highlighter h = e.getHighlighter();
						String text = e.getText();
						int index = 0;
						int times = 0;
						for(int i = 0; i < text.length(); i++) {
							if(text.charAt(i) == '\n' && times < line-1) {
								index = i;
								times++;
							}
						}
						int start = text.indexOf(code, index+1);
						int end = start + code.length();
					     h.addHighlight(start, end, hp);
					     highlights.add(new Highlight(e, hp, start, end, false));
					}
					canRecord = false;
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		int count = 0;
		for(Highlight h : highlights){
			count = 0;
			for(Highlight hx : highlights){
				if(h.editor.equals(hx.editor))
					count++;
			}
			h.editor.javaErrorPanel.setDiagnosticData(count, 0);
		}
	}

	public void removeAllHighlights() {
		highlights.forEach(h->{
			h.editor.javaErrorPanel.setDiagnosticData(0, 0);
			h.remove();
		});
		highlights.clear();
	}
	
	public void remove(Editor e) {
		highlights.forEach(h->{
			if(h.editor == e) h.remove();
		});
	}
	
	public void remove(Editor e, int caretPosition) {
		highlights.forEach(h->{
			if(h.editor == e && caretPosition >= h.start && caretPosition <= h.end) h.remove();
		});
	}
}

