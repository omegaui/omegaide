package omega.utils.systems;
import omega.jdk.ImportSelector;
import omega.Screen;

public class EditorTools {

	private static ImportSelector importSelector;

	public void initTools() {
          importSelector = new ImportSelector(Screen.getScreen());
	}

	public static void showIS() {
          if(Screen.getFileView().getProjectManager().non_java) return;     
		if(importSelector != null) {
			importSelector.setVisible(true);
		}
	}

}
