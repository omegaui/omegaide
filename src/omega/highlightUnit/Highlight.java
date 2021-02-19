package omega.highlightUnit;
import omega.utils.Editor;

import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;


public class Highlight {
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
