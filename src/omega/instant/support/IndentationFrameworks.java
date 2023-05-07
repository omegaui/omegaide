/*
 * IndentationFrameworks
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

import omega.instant.support.java.framework.IndentationFramework;
import omega.ui.component.Editor;

import java.util.LinkedList;

public class IndentationFrameworks {
    public static IndentationFramework javaIndentationFramework = new IndentationFramework();

    public static LinkedList<AbstractIndentationFramework> indentationFrameworks = new LinkedList<>();

    static {
        add(javaIndentationFramework);
    }

    public static synchronized void indent(Editor editor) {
        for (AbstractIndentationFramework indentationFramework : indentationFrameworks) {
            if (indentationFramework.canIndent(editor)) {
                indentationFramework.indent(editor);
                break;
            }
        }
    }

    public static synchronized void add(AbstractIndentationFramework indentationFramework) {
        if (!indentationFrameworks.contains(indentationFramework))
            indentationFrameworks.add(indentationFramework);
    }
}
