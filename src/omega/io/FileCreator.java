/**
  * The FileCreator
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

package omega.io;
import omega.instant.support.FileWizard;

import java.io.File;

import omega.Screen;
public class FileCreator {

	private Screen screen;
	private FileWizard fileWizard;
	
	public FileCreator(Screen screen) {
		this.screen = screen;
		fileWizard = new FileWizard(screen);
	}
	
	public void show(String type) {
          fileWizard.parentRoot.setToolTipText(Screen.getProjectFile().getProjectPath() + File.separator + "src");
		if(Screen.getProjectFile().getProjectPath() != null && new File(Screen.getProjectFile().getProjectPath()).exists())
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

