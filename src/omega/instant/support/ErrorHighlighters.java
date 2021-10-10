package omega.instant.support;
import omega.instant.support.c.CErrorHighlighter;

import omega.instant.support.python.PythonErrorHighlighter;

import omega.instant.support.kotlin.KotlinErrorHighlighter;

import omega.Screen;
public class ErrorHighlighters {
	public static CErrorHighlighter cErrorHighlighter = new CErrorHighlighter();
	public static KotlinErrorHighlighter kotlinErrorHighlighter = new KotlinErrorHighlighter();
	public static PythonErrorHighlighter pythonErrorHighlighter = new PythonErrorHighlighter();
	
	public static void resetAllErrors(){
		cErrorHighlighter.removeAllHighlights();
		kotlinErrorHighlighter.removeAllHighlights();
		pythonErrorHighlighter.removeAllHighlights();
	}
	
	public static void showErrors(String errorLog){
		int tag = Screen.getFileView().getProjectManager().getLanguageTag();
		if(tag == LanguageTagView.LANGUAGE_TAG_C || tag == LanguageTagView.LANGUAGE_TAG_CPLUSPLUS)
			cErrorHighlighter.loadErrors(errorLog);
		else if(tag == LanguageTagView.LANGUAGE_TAG_KOTLIN)
			kotlinErrorHighlighter.loadErrors(errorLog);
		else if(tag == LanguageTagView.LANGUAGE_TAG_PYTHON)
			pythonErrorHighlighter.loadErrors(errorLog);
	}
}
