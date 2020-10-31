package highlightUnit;

import java.awt.Color;
import java.util.LinkedList;
import java.util.StringTokenizer;

import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;

import ide.utils.Editor;

public class BasicHighlight {

	private LinkedList<Highlight> highlights;
	private static final Color color = new Color(225, 20, 30);
	
	public BasicHighlight() {
		highlights = new LinkedList<>();
	}
	
	public void searchForErrors(Editor e) {
		removeAllHighlights();
		String code = e.getText();
		StringTokenizer tok = new StringTokenizer(code, "\n");
		int lineNum = 0;
		int newLinePos = 0;
		while(tok.hasMoreTokens()) {
			String line = tok.nextToken();
			int lineNumX = 0;
			for(int j = 0; j < e.getText().length() && lineNumX < lineNum; j++) {
				char ch = e.getText().charAt(j);
				if(ch == '\n') {
					newLinePos = j;
					lineNumX++;
				}
			}
			if(lineNum == 0)
				newLinePos = line.length();
			lineNum++;
			//For )
			if(line.contains(")")) {
				LinkedList<Integer> positions = new LinkedList<>();
				for(int i = 0; i < line.length(); i++) {
					if(line.charAt(i) == ')')
						positions.add(i);
				}
				for(int i : positions) {
					char nextChar = '\u0000';
					if(line.length() - 1 == i) 
						nextChar = '\n';
					else
						nextChar = line.charAt(i + 1);
					if(nextChar != '-' && nextChar != ')' && nextChar != ';' && nextChar != ' ' && nextChar != '{') {
						int offSet = newLinePos + i + 1;
						int onSet = offSet + 1;
						addHighlight(e, offSet, onSet);
					}
				}
			}
		}
	}
	
	public void addHighlight(Editor e, int start, int end) {
		Highlighter h = e.getHighlighter();
		HighlightPainter hp = new DefaultHighlighter.DefaultHighlightPainter(color);
		try {
		    h.addHighlight(start, end, hp);
		    highlights.add(new Highlight(e, hp, start, end));
		}catch(Exception e1) {System.out.println(e1.getMessage());}
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

	private class Highlight {
		public Editor editor;
		public HighlightPainter highlightPainter;
		public int start;
		public int end;
		public Highlight(Editor e, HighlightPainter h, int start, int end) {
			this.editor = e;
			this.highlightPainter = h;
			this.start = start;
			this.end = end;
		}
		
		public void remove() {
			Highlighter h = editor.getHighlighter();
			Highlighter.Highlight hs[] = h.getHighlights();
			for(int i = 0; i < hs.length; i++) {
				if(hs[i].getPainter() == highlightPainter)
					h.removeHighlight(hs[i]);
			}
		}
	}


}
