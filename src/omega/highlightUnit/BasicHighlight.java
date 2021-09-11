/**
  * Not Ready Yet.
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
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter.HighlightPainter;

import omega.utils.Editor;

import java.awt.Color;

import java.util.LinkedList;
public class BasicHighlight {
	private static LinkedList<Highlight> highlights = new LinkedList<>();
	private static Editor editor;
	public static void highlightJava(Editor e){
          //removeAllHighlights();
		//BasicHighlight.editor = e;
          //removeAllHighlights();
		//The Logic Begins
		//String code = e.getText();
		//addHighlight(0, 100, ErrorHighlighter.color);
	}
	public static void addHighlight(int start, int end, Color color){
		try{
			HighlightPainter hp = new DefaultHighlighter.DefaultHighlightPainter(color);
			editor.getHighlighter().addHighlight(start, end, hp);
			//highlights.add(new Highlight(editor, hp, start, end));
		}
		catch(Exception e){
			System.err.println(e);
		}
	}
	public static void removeHighlight(Highlight h){
		h.remove();
	}
	public static void removeAllHighlights(){
		highlights.forEach(BasicHighlight::removeHighlight);
          highlights.clear();
	}
	
}

