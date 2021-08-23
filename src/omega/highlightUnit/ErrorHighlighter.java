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

package omega.highlightUnit;
import omega.instant.support.java.JavaSyntaxParser;
import omega.deassembler.CodeTokenizer;
import omega.Screen;
import omega.utils.UIManager;
import omega.utils.Editor;
import java.awt.Color;
import java.io.File;
import java.util.LinkedList;
import java.util.StringTokenizer;

import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;


public class ErrorHighlighter {
	
	private LinkedList<Highlight> highlights;
	public static final Color color = omega.utils.UIManager.isDarkMode() ? Color.decode("#800707") : new Color(255, 0, 0, 30);
	
	public ErrorHighlighter() {
		highlights = new LinkedList<>();
	}

     public String getSimplifiedErrorLog(String errorLog){
          String log = "";
          LinkedList<String> lines = CodeTokenizer.tokenize(errorLog, '\n');
          return log;
     }
     
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
				if(token.contains(".java") && token.contains(":") && !canRecord) {
					int e = token.indexOf(':');
					if(Screen.onWindows())
						e = token.indexOf(':', e + 1);
					path = token.substring(0, e);
					path = JavaSyntaxParser.convertToProjectPath(path);
					line = Integer.parseInt(token.substring(e + 1, token.indexOf(':', e + 1)));
                         if(new File(path).exists())
                              canRecord = true;
				}
				else if(canRecord) {
					code = token.trim();
					File file = new File(path);
					Editor e = Screen.getFileView().getScreen().getTabPanel().findEditor(file);
					if(e == null) {
						e = Screen.getFileView().getScreen().loadFile(file);
					}
					Highlighter h = e.getHighlighter();
					HighlightPainter hp = new DefaultHighlighter.DefaultHighlightPainter(color);
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
					canRecord = false;
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void removeAllHighlights() {
		highlights.forEach(h->{
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

