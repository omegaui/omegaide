package omega.instant.support;
import omega.Screen;

import omega.instant.support.c.CErrorHighlighter;
public class ErrorHighlighters {
	public static CErrorHighlighter cErrorHighlighter = new CErrorHighlighter();
	
	public static void resetAllErrors(){
		cErrorHighlighter.removeAllHighlights();
	}
	
	public static void showErrors(String errorLog){
		if(Screen.getFileView().getProjectManager().getLanguageTag() == LanguageTagView.LANGUAGE_TAG_C)
			cErrorHighlighter.loadErrors(errorLog);
	}
}
