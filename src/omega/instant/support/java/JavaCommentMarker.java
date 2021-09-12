package omega.instant.support.java;
import javax.swing.text.Document;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;

import omega.utils.Editor;
public class JavaCommentMarker {
	public static void markSingleLineComment(Editor editor, int line){
		try{
			int l = 0;
			int pos = 0;
			for(char ch : editor.getText().toCharArray()){
				pos++;
				if(ch == '\n')
					l++;
				if(l == line)
					break;
			}
			String text = editor.getText().substring(pos, editor.getText().indexOf('\n', pos + 1));
			Document doc = editor.getDocument();
			if(!text.startsWith("//")) {
				doc.insertString(pos, "//", new SimpleAttributeSet());
				editor.setCaretPosition(pos + text.length() + 3);
			}
			else {
				doc.remove(pos, 2);
				editor.setCaretPosition(pos + text.length());
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
