/*
 * AbstractCodeHighlighter
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

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Token;

import java.awt.*;

public abstract class AbstractCodeHighlighter {

    public abstract boolean canComputeForeground(RSyntaxTextArea textArea, Token t);

    public abstract Color computeForegroundColor(RSyntaxTextArea textArea, Token t);

    public synchronized boolean canComputeBackground(RSyntaxTextArea textArea, Token t) {
        return false;
    }

    public synchronized Color computeBackgroundColor(RSyntaxTextArea textArea, Token t) {
        return null;
    }
}
