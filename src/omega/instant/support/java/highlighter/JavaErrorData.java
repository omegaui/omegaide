/*
 * JavaErrorData
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
package omega.instant.support.java.highlighter;
import omega.ui.component.Editor;

import omega.instant.support.Highlight;
public class JavaErrorData {
	public Editor editor;
	public int errorCount;
	public int warningCount;

	public void add(Highlight h){
		if(h.editor == editor){
			if(h.warning)
				warningCount++;
			else
				errorCount++;
		}
	}

	public void setData(){
		if(editor != null){
			editor.javaErrorPanel.setDiagnosticData(errorCount, warningCount);
		}
	}

	public void resetData(){
		if(editor != null){
			editor.javaErrorPanel.setDiagnosticData(0, 0);
		}
	}
}
