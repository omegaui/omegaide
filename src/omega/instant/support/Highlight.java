/*
 * Highlight
 * Copyright (C) 2022 Omega UI

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

import omega.instant.support.java.parser.JavaSyntaxParserGutterIconInfo;
import omega.ui.component.Editor;

import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;
import javax.tools.Diagnostic;
import java.awt.*;

public class Highlight {

    public Editor editor;
    public HighlightPainter highlightPainter;
    public int start;
    public int end;
    public boolean warning = false;
    public Diagnostic diagnosticData;
    public JavaSyntaxParserGutterIconInfo gutterIconInfo;
    public Object tag;

    public volatile boolean applied = false;
    public volatile boolean appliedLineColor = false;

    public Highlight(Editor e, HighlightPainter h, int start, int end, boolean warning) {
        this.editor = e;
        this.highlightPainter = h;
        this.start = start;
        this.end = end;
        this.warning = warning;
    }

    public void apply() {
        if (applied)
            return;
        applied = true;
        try {
            editor.getHighlighter().addHighlight(start, end, highlightPainter);
            if (gutterIconInfo != null)
                gutterIconInfo.apply();
        } catch (Exception e) {

        }
    }

    public void applyLineColor(int line, Color c) {
        if (appliedLineColor)
            return;
        appliedLineColor = true;
        try {
            tag = editor.addLineHighlight(line, c);
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    public javax.tools.Diagnostic getDiagnosticData() {
        return diagnosticData;
    }

    public void setDiagnosticData(javax.tools.Diagnostic diagnosticData) {
        this.diagnosticData = diagnosticData;
    }

    public omega.instant.support.java.parser.JavaSyntaxParserGutterIconInfo getGutterIconInfo() {
        return gutterIconInfo;
    }

    public void setGutterIconInfo(omega.instant.support.java.parser.JavaSyntaxParserGutterIconInfo gutterIconInfo) {
        this.gutterIconInfo = gutterIconInfo;
    }


    public void remove() {
        if (tag != null)
            editor.removeLineHighlight(tag);
        if (gutterIconInfo != null)
            gutterIconInfo.remove();
        Highlighter h = editor.getHighlighter();
        Highlighter.Highlight hs[] = h.getHighlights();
        for (int i = 0; i < hs.length; i++) {
            if (hs[i].getPainter() == highlightPainter)
                h.removeHighlight(hs[i]);
        }
    }

    public boolean equals(int start, int end) {
        return start == start && end == end;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Highlight hx) {
            return hx.editor.currentFile.equals(editor.currentFile) && hx.start == start && hx.end == end && hx.diagnosticData.getLineNumber() == diagnosticData.getLineNumber();
        }
        return super.equals(obj);
    }
}

