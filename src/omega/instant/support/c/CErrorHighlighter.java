/**
* Highlights Errors
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
package omega.instant.support.c;

import java.awt.Color;
import java.awt.Image;
import java.io.File;
import java.util.LinkedList;
import java.util.Locale;
import java.util.StringTokenizer;
import javax.swing.ImageIcon;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;
import omega.Screen;
import omega.deassembler.CodeTokenizer;
import omega.framework.CodeFramework;
import omega.highlightUnit.Highlight;
import omega.instant.support.AbstractErrorHighlighter;
import omega.instant.support.java.JavaSyntaxParser;
import omega.instant.support.java.JavaSyntaxParserGutterIconInfo;
import omega.utils.Editor;
import omega.utils.IconManager;
import omega.utils.UIManager;
import org.fife.ui.rsyntaxtextarea.SquiggleUnderlineHighlightPainter;

public class CErrorHighlighter implements AbstractErrorHighlighter {

  private LinkedList<Highlight> highlights;
  private LinkedList<JavaSyntaxParserGutterIconInfo> gutterIconInfos;

  public CErrorHighlighter() {
    highlights = new LinkedList<>();
    gutterIconInfos = new LinkedList<>();
  }

  /*
	c_test.c: In function ‘main’:
	c_test.c:6:2: error: expected ‘,’ or ‘;’ before ‘return’
	6 |  return 2;
	|  ^~~~~~
	main.c:3:10: fatal error: utils.c: No such file or directory
	3 | #include <utils.c>
	|          ^~~~~~~~~
	*/
  @Override
  public void loadErrors(String errorLog) {
    removeAllHighlights();
    StringTokenizer tokenizer = new StringTokenizer(errorLog, "\n");
    boolean canRecord = false;
    String path = "";
    String code = "";
    String message = "";
    int line = 0;
    try {
      while (tokenizer.hasMoreTokens()) {
        String token = tokenizer.nextToken();
        if (
          !canRecord &&
          CodeFramework.count(token, ':') >= 4 &&
          !token.startsWith(" ")
        ) {
          int index;
          path = token.substring(0, index = token.indexOf(':')).trim();
          line =
            Integer.parseInt(
              token
                .substring(index + 1, index = token.indexOf(':', index + 1))
                .trim()
            );
          index = token.indexOf(':', index + 1);
          message = token.substring(index + 1).trim();
          canRecord = true;
        } else if (canRecord && token.contains("|")) {
          code = token.substring(token.indexOf('|') + 1).trim();

          if (!path.contains(File.separator)) {
            path =
              Screen.getFileView().getArgumentManager().compileDir +
              File.separator +
              path;
          }

          File file = new File(path);
          if (file.exists()) {
            Editor e = Screen.getFileView().getScreen().loadFile(file);
            try {
              ImageIcon icon = new ImageIcon(
                message.startsWith("warning")
                  ? IconManager.fluentwarningImage
                  : IconManager.fluenterrorImage
              );
              int size = e.getGraphics().getFontMetrics().getHeight();
              icon =
                new ImageIcon(
                  icon
                    .getImage()
                    .getScaledInstance(size, size, Image.SCALE_SMOOTH)
                );
              gutterIconInfos.add(
                new JavaSyntaxParserGutterIconInfo(
                  e
                    .getAttachment()
                    .getGutter()
                    .addLineTrackingIcon(line - 1, icon, message),
                  e
                )
              );
            } catch (Exception ex) {
              ex.printStackTrace();
            }
            Highlighter h = e.getHighlighter();
            SquiggleUnderlineHighlightPainter painter = new SquiggleUnderlineHighlightPainter(
              message.startsWith("warning") ? Color.YELLOW : Color.RED
            );
            String text = e.getText();
            int index = 0;
            int times = 0;
            for (int i = 0; i < text.length(); i++) {
              if (text.charAt(i) == '\n' && times < line - 1) {
                index = i;
                times++;
              }
            }
            int start = text.indexOf(code, line == 1 ? 0 : index + 1);
            int end = start + code.length();
            h.addHighlight(start, end, painter);
            highlights.add(
              new Highlight(
                e,
                painter,
                start,
                end,
                message.startsWith("warning")
              )
            );
          }
          canRecord = false;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    int warningCount = 0;
    int errorCount = 0;
    for (Highlight h : highlights) {
      warningCount = 0;
      errorCount = 0;
      for (Highlight hx : highlights) {
        if (h.editor.equals(hx.editor)) {
          if (hx.warning) warningCount++; else errorCount++;
        }
      }
      h.editor.javaErrorPanel.setDiagnosticData(errorCount, warningCount);
    }
  }

  @Override
  public void removeAllHighlights() {
    highlights.forEach(h -> {
      h.editor.javaErrorPanel.setDiagnosticData(0, 0);
      h.remove();
    });
    highlights.clear();
    gutterIconInfos.forEach(info ->
      info.editor
        .getAttachment()
        .getGutter()
        .removeTrackingIcon(info.gutterIconInfo)
    );
    gutterIconInfos.clear();
  }

  public void remove(Editor e) {
    highlights.forEach(h -> {
      if (h.editor == e) h.remove();
    });
  }

  public void remove(Editor e, int caretPosition) {
    highlights.forEach(h -> {
      if (
        h.editor == e && caretPosition >= h.start && caretPosition <= h.end
      ) h.remove();
    });
  }
}
