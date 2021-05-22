package omega.utils.systems.creators;
import java.io.File;

import omega.instant.support.FileWizard;
import omega.Screen;

public class FileCreator {

	private Screen screen;
	private FileWizard fileWizard;
	
	public FileCreator(Screen screen) {
		this.screen = screen;
		fileWizard = new FileWizard(screen);
	}
	
	public void show(String type) {
          fileWizard.parentRoot.setToolTipText(Screen.getFileView().getProjectPath() + File.separator + "src");
		if(Screen.getFileView().getProjectPath() != null && new File(Screen.getFileView().getProjectPath()).exists())
			fileWizard.show(type);
	}

     public void showDirView(String path){
          fileWizard.parentRoot.setToolTipText(path);
          fileWizard.typeBtn.setText("directory");
          fileWizard.setVisible(true);
     }
     
     public void showFileView(String path){
          fileWizard.parentRoot.setToolTipText(path);
          fileWizard.typeBtn.setText("Custom File");
          fileWizard.setVisible(true);
     }
     
     public void showFileView(String type, String path){
          fileWizard.parentRoot.setToolTipText(path);
          fileWizard.typeBtn.setText(type);
          fileWizard.setVisible(true);
     }
}
