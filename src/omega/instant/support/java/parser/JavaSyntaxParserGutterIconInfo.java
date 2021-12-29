package omega.instant.support.java.parser;
import omega.ui.component.Editor;

import org.fife.ui.rtextarea.GutterIconInfo;
public class JavaSyntaxParserGutterIconInfo {
	public GutterIconInfo gutterIconInfo;
	public Editor editor;

	public JavaSyntaxParserGutterIconInfo(GutterIconInfo gutterIconInfo, Editor editor){
		this.gutterIconInfo = gutterIconInfo;
		this.editor = editor;
	}
}
