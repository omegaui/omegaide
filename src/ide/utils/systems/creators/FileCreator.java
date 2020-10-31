package ide.utils.systems.creators;

import java.io.File;

import creator.FileWizard;
import ide.Screen;

public class FileCreator {

	private Screen screen;
	private FileWizard fileWizard;
	
	public FileCreator(Screen screen) {
		this.screen = screen;
		fileWizard = new FileWizard(screen);
	}	
	
	public void show(String type) {
		if(Screen.getFileView().getProjectPath() != null && new File(Screen.getFileView().getProjectPath()).exists())
			fileWizard.show(type);
	}

}
