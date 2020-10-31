package ide.utils;

import ide.utils.systems.EditorTools;

public class ToolPane {

	public EditorTools editorTools;
	
	public void initEditorTools() {
		if(editorTools == null) 
			editorTools = new EditorTools();
		editorTools.initTools();
	}
}
