/**
  * ErrorHighlighters
  * Copyright (C) 2021 Omega UI

  * This program is free software: you can redistribute it and/or modify
  * it under the terms of the GNU General Public License as published by
  * the Free Software Foundation, either version 3 of the License, or
  * (at your option) any later version.

  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.

  * You should have received a copy of the GNU General Public License
  * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package omega.instant.support;
import omega.instant.support.python.PythonErrorHighlighter;

import omega.instant.support.kotlin.KotlinErrorHighlighter;

import omega.instant.support.c.CErrorHighlighter;

import java.util.HashMap;

import omega.Screen;
public final class ErrorHighlighters {
	public static CErrorHighlighter cErrorHighlighter = new CErrorHighlighter();
	public static KotlinErrorHighlighter kotlinErrorHighlighter = new KotlinErrorHighlighter();
	public static PythonErrorHighlighter pythonErrorHighlighter = new PythonErrorHighlighter();
	
	public static HashMap<Integer, AbstractErrorHighlighter> externalErrorHighlighters = new HashMap<>();
	
	public static void resetAllErrors(){
		cErrorHighlighter.removeAllHighlights();
		kotlinErrorHighlighter.removeAllHighlights();
		pythonErrorHighlighter.removeAllHighlights();
		if(!externalErrorHighlighters.keySet().isEmpty())
			externalErrorHighlighters.keySet().forEach(key->externalErrorHighlighters.get(key).removeAllHighlights());
	}
	
	public static void add(int langTag, AbstractErrorHighlighter aeh){
		if(externalErrorHighlighters.get(langTag) != null)
			return;
		externalErrorHighlighters.put(langTag, aeh);
	}
	
	public static void showErrors(String errorLog){
		int tag = Screen.getFileView().getProjectManager().getLanguageTag();
		if(tag == LanguageTagView.LANGUAGE_TAG_C || tag == LanguageTagView.LANGUAGE_TAG_CPLUSPLUS)
			cErrorHighlighter.loadErrors(errorLog);
		else if(tag == LanguageTagView.LANGUAGE_TAG_KOTLIN)
			kotlinErrorHighlighter.loadErrors(errorLog);
		else if(tag == LanguageTagView.LANGUAGE_TAG_PYTHON)
			pythonErrorHighlighter.loadErrors(errorLog);
		else if(externalErrorHighlighters.get(tag) != null)
			externalErrorHighlighters.get(tag).loadErrors(errorLog);
	}
	
	public static boolean isLoggerPresentForCurrentLang(){
		int tag = Screen.getFileView().getProjectManager().getLanguageTag();
		return (tag == LanguageTagView.LANGUAGE_TAG_C) || (tag == LanguageTagView.LANGUAGE_TAG_CPLUSPLUS)
		|| (tag == LanguageTagView.LANGUAGE_TAG_PYTHON) || (tag == LanguageTagView.LANGUAGE_TAG_KOTLIN)
		|| externalErrorHighlighters.get(tag) != null;
	}
}
