/*
 * JavaSyntaxParserGutterIconInfo
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

package omega.instant.support.java.parser;

import omega.ui.component.Editor;
import org.fife.ui.rtextarea.GutterIconInfo;

import javax.swing.*;
import javax.tools.Diagnostic;
import java.awt.*;
import java.util.Locale;

public class JavaSyntaxParserGutterIconInfo {
    public GutterIconInfo gutterIconInfo;
    public Editor editor;
    public Diagnostic d;
    public volatile boolean applied = false;

    public JavaSyntaxParserGutterIconInfo(GutterIconInfo gutterIconInfo, Editor editor) {
        this.gutterIconInfo = gutterIconInfo;
        this.editor = editor;
    }

    public JavaSyntaxParserGutterIconInfo(Editor editor, Diagnostic d) {
        this.gutterIconInfo = gutterIconInfo;
        this.editor = editor;
        this.d = d;
    }

    public void apply() {
        if (applied)
            return;
        try {
            ImageIcon icon = new ImageIcon(JavaSyntaxParser.getSuitableIcon(d.getKind()));
            icon = new ImageIcon(icon.getImage().getScaledInstance(editor.getFont().getSize(), editor.getFont().getSize(), Image.SCALE_SMOOTH));
            gutterIconInfo = editor.getAttachment().getGutter().addLineTrackingIcon((int) (d.getLineNumber() - 1), icon, d.getMessage(Locale.ROOT));
            applied = true;
        } catch (Exception e) {
            //			e.printStackTrace();

        }
    }

    public void remove() {
        if (gutterIconInfo != null)
            editor.getAttachment().getGutter().removeTrackingIcon(gutterIconInfo);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof JavaSyntaxParserGutterIconInfo info) {
            return info.gutterIconInfo == gutterIconInfo && editor.currentFile.equals(info.editor.currentFile);
        }
        return super.equals(obj);
    }
}
