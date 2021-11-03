package omega.tabPane;
import java.awt.BorderLayout;

import omega.utils.Editor;
public class EditorTabHolder extends TabHolder{
	public EditorTabHolder(Editor editor){
		super(editor);
		getMainPanel().add(editor.getAttachment(), BorderLayout.CENTER);
		getMainPanel().add(editor.getFAndR(), BorderLayout.NORTH);
	}
}
