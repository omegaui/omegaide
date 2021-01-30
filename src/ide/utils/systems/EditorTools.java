package ide.utils.systems;
/*
    Copyright (C) 2021 Omega UI. All Rights Reserved.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
import ide.Screen;
import importIO.ImportManager;
import importUI.ImportSelector;

public class EditorTools {

	private static ImportSelector importSelector;
	public static ImportManager importManager;

	public EditorTools() {
		importManager = new ImportManager();
		importSelector = new ImportSelector(Screen.getScreen());
	}

	public void initTools() {
		while(ImportManager.readingNatives);
		try {
			if(importManager.readingThread != null)
				importManager.readingThread.stop();
		}catch(Exception e) {}
		importManager.reload(Screen.getFileView().getProjectPath());
	}

	public static void showIS() {
          if(Screen.getFileView().getProjectManager().non_java) return;     
		if(importSelector != null) {
			importSelector.setVisible(true);
		}
	}

}
