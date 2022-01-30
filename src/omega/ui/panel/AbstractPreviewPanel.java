/**
 * AbstractPreviewPanel -- Generates Previews for Some files like markdown, html, etc
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
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 */

package omega.ui.panel;
import omega.ui.component.Editor;

import omega.ui.window.EditorPreviewWindow;

import java.io.File;

import javax.swing.JPanel;
public abstract class AbstractPreviewPanel extends JPanel{
	/**
	 * @return true if the preview can be generated else false
	 */
	public abstract boolean canCreatePreview(File file);
	
	/**
	 * Generates preview.
	 * @param editor - the object of the editor
	 * @param previewWindow - the object of the preview window, can be used to set title, icon-image, size, location, etc.
	 */
	public abstract void genPreview(Editor editor, EditorPreviewWindow previewWindow);
}
