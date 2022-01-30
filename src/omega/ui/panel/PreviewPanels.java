/**
 * PreviewPanels
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
import java.awt.BorderLayout;

import omega.ui.component.Editor;

import java.util.LinkedList;
public final class PreviewPanels {
	public static LinkedList<AbstractPreviewPanel> previewPanels = new LinkedList<>();
	static{
		add(new MarkdownPreviewPanel());
	}

	public static synchronized AbstractPreviewPanel getPreviewPanel(Editor editor){
		for(AbstractPreviewPanel previewPanel : previewPanels){
			if(previewPanel.canCreatePreview(editor.currentFile))
				return previewPanel;
		}
		return null;
	}

	public static synchronized void add(AbstractPreviewPanel previewPanel){
		if(!previewPanels.contains(previewPanel))
			previewPanels.add(previewPanel);
	}

	public static synchronized void remove(AbstractPreviewPanel previewPanel){
		previewPanels.remove(previewPanel);
	}
	
}
