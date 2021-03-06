/**
  * PopupManager
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

package omega.utils;
import omega.instant.support.build.gradle.GradleProcessManager;
import omega.popup.*;
import omega.Screen;
import javax.swing.JOptionPane;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

import java.io.File;
public class PopupManager {
	public static final byte SOURCE_FILE = 0;
	public static final byte NON_SOURCE_FILE = 1;
	public static OPopupWindow createPopup(byte type, Editor editor, Screen screen) {
		OPopupWindow popup = new OPopupWindow("Tab Menu", screen, 0, false);
		
		if(type == SOURCE_FILE) {
               popup.createItem("Run as Main Class", IconManager.runImage, ()->{
                    Screen.getRunView().setMainClassPath(editor.currentFile.getAbsolutePath());
                    Screen.getRunView().run();
               })
               .createItem("Run Project", IconManager.runImage, ()->Screen.getRunView().run())
               .createItem("Build Project", IconManager.buildImage, ()->Screen.getBuildView().compileProject())
               .createItem("Save", IconManager.fluentsaveImage, ()->editor.saveCurrentFile())
               .createItem("Save As", IconManager.fluentsaveImage, ()->{
                    editor.saveFileAs();
                    Screen.getProjectView().reload();
               })
               .createItem("Discard", IconManager.closeImage, ()->{
                    editor.reloadFile();
                    screen.getTabPanel().remove(editor);
               })
               .createItem("Reload", null, ()->editor.reloadFile()).width(200);
		}
		else {
               if(editor.currentFile != null && editor.currentFile.getName().contains("."))
			     popup.createItem("Launch", IconManager.fluentlaunchImage, ()->ToolMenu.processWizard.launch(editor.currentFile));
			popup.createItem("Save", IconManager.fileImage, ()->editor.saveCurrentFile())
               .createItem("Save As", IconManager.fileImage, ()->{
                    editor.saveFileAs();
                    Screen.getProjectView().reload();
               })
               .createItem("Discard", IconManager.closeImage, ()->{
                    editor.reloadFile();
                    screen.getTabPanel().remove(editor);
               })
               .createItem("Reload", null, ()->editor.reloadFile()).width(200);
		}
          popup.createItem("Copy Path (\"path\")", IconManager.fileImage, ()->Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection("\""+editor.currentFile.getAbsolutePath()+"\""), null));
          popup.createItem("Copy Path", IconManager.fileImage, ()->Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(editor.currentFile.getAbsolutePath()), null));
		popup.createItem("Open in Desktop", IconManager.fileImage, ()->Screen.openInDesktop(editor.currentFile));
		return popup;
	}

	public static void createTreePopup(OPopupWindow popup, File file) {
          if(file.getAbsolutePath().equals(Screen.getFileView().getProjectPath())){
               popup.createItem("Initialize Gradle", IconManager.fluentgradleImage, GradleProcessManager::init);
               popup.createItem("Create Gradle Module", IconManager.fluentgradleImage, ()->ToolMenu.gradleModuleWizard.setVisible(true));
          }
          popup.createItem("New Directory", IconManager.projectImage, ()->Screen.getFileView().getFileCreator().showDirView(file.getAbsolutePath()))
          .createItem("New File", IconManager.fileImage, ()->Screen.getFileView().getFileCreator().showFileView(file.getAbsolutePath()))
          .createItem("New Class", IconManager.classImage, ()->Screen.getFileView().getFileCreator().showFileView("class", file.getAbsolutePath()))
          .createItem("New Record", IconManager.classImage, ()->Screen.getFileView().getFileCreator().showFileView("record", file.getAbsolutePath()))
          .createItem("New Interface", IconManager.interImage, ()->Screen.getFileView().getFileCreator().showFileView("interface", file.getAbsolutePath()))
          .createItem("New Enum", IconManager.enumImage, ()->Screen.getFileView().getFileCreator().showFileView("enum", file.getAbsolutePath()))
          .createItem("New Annotation", IconManager.annImage, ()->Screen.getFileView().getFileCreator().showFileView("@interface", file.getAbsolutePath()))
          .createItem("Open in Desktop", IconManager.fileImage, ()->Screen.openInDesktop(file));
          if(!file.isDirectory()) {
               popup
               .createItem("Open On Right Tab Panel", IconManager.fileImage, ()->Screen.getScreen().loadFileOnRightTabPanel(file))
               .createItem("Open On Bottom Tab Panel", IconManager.fileImage, ()->Screen.getScreen().loadFileOnBottomTabPanel(file));
          }
          if(!file.getAbsolutePath().equals(Screen.getFileView().getProjectPath())){
               popup.createItem("Delete", IconManager.closeImage, ()->{
                    if(file.isDirectory()){
                         try{
                              int res0 = ChoiceDialog.makeChoice("Do you want to delete Directory " + file.getName() + "?", "Yes", "No");
                              if(res0 != ChoiceDialog.CHOICE1)
                                   return;
                              Editor.deleteDir(file);
                         }
                         catch(Exception e){ 
                              System.err.println(e);
                         }
                    }
                    else
                         Editor.deleteFile(file);
                    Screen.getProjectView().reload();
               });
          }
          popup.createItem("Refresh", null, ()->Screen.getProjectView().reload());
          if(!file.isDirectory()) {
               popup.createItem("Rename", IconManager.fileImage, ()->{
                    Screen.getProjectView().getFileOperationManager().rename("Rename " + file.getName(), "rename", file);
                    Screen.getProjectView().reload();
               });
          }
		
          popup.createItem("Copy Path (\"path\")", IconManager.fileImage, ()->Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection("\"" + file.getAbsolutePath() + "\""), null));
          popup.createItem("Copy Path", IconManager.fileImage, ()->Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(file.getAbsolutePath()), null));
	}
}

