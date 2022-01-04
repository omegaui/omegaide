package omega.instant.support;
import omega.ui.component.Editor;

import java.util.LinkedList;

import omega.instant.support.java.framework.IndentationFramework;
public class IndentationFrameworks {
	public static IndentationFramework javaIndentationFramework = new IndentationFramework();

	public static LinkedList<AbstractIndentationFramework> indentationFrameworks = new LinkedList<>();

	static{
		add(javaIndentationFramework);
	}

	public static synchronized void indent(Editor editor){
		for(AbstractIndentationFramework indentationFramework : indentationFrameworks){
			if(indentationFramework.canIndent(editor)){
				indentationFramework.indent(editor);
				break;
			}
		}
	}

	public static synchronized void add(AbstractIndentationFramework indentationFramework){
		if(!indentationFrameworks.contains(indentationFramework))
			indentationFrameworks.add(indentationFramework);
	}
}
