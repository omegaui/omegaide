package omega.instant.support.java;
import omega.utils.Editor;


import omega.highlightUnit.Highlight;
public class JavaErrorData {
	public Editor editor;
	public int errorCount;
	public int warningCount;

	public void add(Highlight h){
		if(h.editor == editor){
			if(h.warning)
				warningCount++;
			else
				errorCount++;
		}
	}

	public void setData(){
		if(editor != null){
			editor.javaErrorPanel.setDiagnosticData(errorCount, warningCount);
		}
	}

	public void resetData(){
		if(editor != null){
			editor.javaErrorPanel.setDiagnosticData(0, 0);
		}
	}
}
