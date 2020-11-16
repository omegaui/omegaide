package ide.utils.systems;

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
		if(importSelector != null) {
			importSelector.setVisible(true);
		}
	}

}
