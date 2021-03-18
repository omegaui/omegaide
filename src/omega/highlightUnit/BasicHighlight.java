package omega.highlightUnit;
import javax.swing.text.*;
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
			highlights.add(new Highlight(editor, hp, start, end));
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
