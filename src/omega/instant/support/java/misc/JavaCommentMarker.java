/**
  * JavaCommentMarker
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
package omega.instant.support.java.misc;
import omega.ui.component.Editor;

import javax.swing.text.Document;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
public class JavaCommentMarker {
	public static void markSingleLineComment(Editor editor, int line){
		try{
			int l = 0;
			int pos = 0;
			for(char ch : editor.getText().toCharArray()){
				pos++;
				if(ch == '\n')
					l++;
				if(l == line)
					break;
			}
			String text = editor.getText().substring(pos, editor.getText().indexOf('\n', pos + 1));
			Document doc = editor.getDocument();
			if(!text.startsWith("//")) {
				doc.insertString(pos, "//", new SimpleAttributeSet());
				editor.setCaretPosition(pos + text.length() + 3);
			}
			else {
				doc.remove(pos, 2);
				editor.setCaretPosition(pos + text.length());
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
