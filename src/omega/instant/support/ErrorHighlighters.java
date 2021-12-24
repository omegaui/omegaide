package omega.instant.support;

import java.util.HashMap;
import omega.Screen;
import omega.instant.support.c.CErrorHighlighter;
import omega.instant.support.kotlin.KotlinErrorHighlighter;
import omega.instant.support.python.PythonErrorHighlighter;

public class ErrorHighlighters {

  public static CErrorHighlighter cErrorHighlighter = new CErrorHighlighter();
  public static KotlinErrorHighlighter kotlinErrorHighlighter = new KotlinErrorHighlighter();
  public static PythonErrorHighlighter pythonErrorHighlighter = new PythonErrorHighlighter();

  public static HashMap<Integer, AbstractErrorHighlighter> externalErrorHighlighters = new HashMap<>();

  public static void resetAllErrors() {
    cErrorHighlighter.removeAllHighlights();
    kotlinErrorHighlighter.removeAllHighlights();
    pythonErrorHighlighter.removeAllHighlights();
    if (!externalErrorHighlighters.keySet().isEmpty()) externalErrorHighlighters
      .keySet()
      .forEach(key -> externalErrorHighlighters.get(key).removeAllHighlights());
  }

  public static void add(int langTag, AbstractErrorHighlighter aeh) {
    if (externalErrorHighlighters.get(langTag) != null) return;
    externalErrorHighlighters.put(langTag, aeh);
  }

  public static void showErrors(String errorLog) {
    int tag = Screen.getFileView().getProjectManager().getLanguageTag();
    if (
      tag == LanguageTagView.LANGUAGE_TAG_C ||
      tag == LanguageTagView.LANGUAGE_TAG_CPLUSPLUS
    ) cErrorHighlighter.loadErrors(errorLog); else if (
      tag == LanguageTagView.LANGUAGE_TAG_KOTLIN
    ) kotlinErrorHighlighter.loadErrors(errorLog); else if (
      tag == LanguageTagView.LANGUAGE_TAG_PYTHON
    ) pythonErrorHighlighter.loadErrors(errorLog); else if (
      externalErrorHighlighters.get(tag) != null
    ) externalErrorHighlighters.get(tag).loadErrors(errorLog);
  }

  public static boolean isLoggerPresentForCurrentLang() {
    int tag = Screen.getFileView().getProjectManager().getLanguageTag();
    return (
      (tag == LanguageTagView.LANGUAGE_TAG_C) ||
      (tag == LanguageTagView.LANGUAGE_TAG_CPLUSPLUS) ||
      (tag == LanguageTagView.LANGUAGE_TAG_PYTHON) ||
      (tag == LanguageTagView.LANGUAGE_TAG_KOTLIN) ||
      externalErrorHighlighters.get(tag) != null
    );
  }
}
