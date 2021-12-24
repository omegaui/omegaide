/**
  * Highlight
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

package omega.highlightUnit;

import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;
import omega.utils.Editor;

public class Highlight {

  public Editor editor;
  public HighlightPainter highlightPainter;
  public int start;
  public int end;
  public boolean warning = false;

  public Highlight(
    Editor e,
    HighlightPainter h,
    int start,
    int end,
    boolean warning
  ) {
    this.editor = e;
    this.highlightPainter = h;
    this.start = start;
    this.end = end;
    this.warning = warning;
  }

  public void apply() {
    try {
      editor.getHighlighter().addHighlight(start, end, highlightPainter);
    } catch (Exception e) {}
  }

  public void remove() {
    Highlighter h = editor.getHighlighter();
    Highlighter.Highlight hs[] = h.getHighlights();
    for (int i = 0; i < hs.length; i++) {
      if (hs[i].getPainter() == highlightPainter) h.removeHighlight(hs[i]);
    }
  }

  public boolean equals(int start, int end) {
    return start == start && end == end;
  }
}
