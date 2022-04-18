/*
 * PopupManager
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

package omega.io;
import omega.ui.dialog.ChoiceDialog;

import omega.instant.support.build.gradle.GradleProcessManager;

import omega.ui.component.ToolMenu;
import omega.ui.component.Editor;

import omega.ui.popup.OPopupWindow;

import java.awt.Toolkit;

import omega.Screen;

import java.awt.datatransfer.StringSelection;

import java.io.File;
public class PopupManager {
	public static final byte SOURCE_FILE = 0;
	public static final byte NON_SOURCE_FILE = 1;

	public static OPopupWindow createMenu(Editor editor) {
		if(editor.currentFile != null) {
			if(editor.currentFile.getName().endsWith(".java"))
				return createPopup(SOURCE_FILE, editor, Screen.getScreen());
			else
				return createPopup(NON_SOURCE_FILE, editor, Screen.getScreen());
		}
		return null;
	}

	public static OPopupWindow createPopup(byte type, Editor editor, Screen screen) {
		OPopupWindow popup = new OPopupWindow("Tab Menu", screen, 0, false);

		if(type == SOURCE_FILE) {
			popup.createItem("Run as Main Class", IconManager.runImage, ()->{
				Screen.getProjectRunner().setMainClassPath(editor.currentFile.getAbsolutePath());
				Screen.getProjectRunner().run();
			})
			.createItem("Run Project", IconManager.runImage, ()->Screen.getProjectRunner().run())
			.createItem("Build Project", IconManager.buildImage, ()->Screen.getProjectBuilder().compileProject())
			.createItem("Mark As Main", IconManager.fluentrocketImage, ()->{
				Screen.getProjectRunner().setMainClassPath(editor.currentFile.getAbsolutePath());
			});
		}
		if(editor.currentFile != null && editor.currentFile.getName().contains("."))
			popup.createItem("Launch", IconManager.fluentlaunchImage, ()->ToolMenu.processWizard.launch(editor.currentFile));
		popup.createItem("Save", IconManager.fluentsaveImage, ()->editor.saveCurrentFile())
		.createItem("Save As", IconManager.fluentsaveImage, ()->{
			editor.saveFileAs();
			Screen.getProjectFile().getFileTreePanel().refresh();
		})
		.createItem("Discard", IconManager.closeImage, ()->{
			editor.discardData();
			screen.getTabPanel().removeTab(screen.getTabPanel().getTabData(editor.getAttachment()));
		})
		.createItem("Reload", IconManager.fluentrefreshIcon, ()->editor.reloadFile()).width(200);
		popup.createItem("Show in Tree", IconManager.fluentomegaideprojectImage, ()->{
			new Thread(()->{
				Screen.getProjectFile().getFileTreePanel().navigateTo(editor.currentFile);
			}).start();
		});
		popup.createItem("Copy Path (\"path\")", IconManager.fluentcopyImage, ()->Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection("\""+editor.currentFile.getAbsolutePath()+"\""), null));
		popup.createItem("Copy Path", IconManager.fluentcopyImage, ()->Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(editor.currentFile.getAbsolutePath()), null));
		popup.createItem("Open in Desktop", IconManager.fluentdesktopImage, ()->Screen.openInDesktop(editor.currentFile));
		popup.createItem("Close All Tabs", IconManager.closeImage, Screen.getScreen()::closeAllTabs);
		return popup;
	}

	public static void createTreePopup(OPopupWindow popup, File file) {
		if(file.getAbsolutePath().equals(Screen.getProjectFile().getProjectPath())){
			popup.createItem("Initialize Gradle", IconManager.fluentgradleImage, GradleProcessManager::init);
			popup.createItem("Create Gradle Module", IconManager.fluentgradleImage, ()->ToolMenu.gradleModuleWizard.setVisible(true));
		}
		if(file.isDirectory()) {
			popup.createItem("New Directory", IconManager.projectImage, ()->Screen.getProjectFile().getFileCreator().showDirView(file.getAbsolutePath()))
			.createItem("New File", IconManager.fluentnewfileImage, ()->Screen.getProjectFile().getFileCreator().showFileView(file.getAbsolutePath()))
			.createItem("New Class", IconManager.fluentclassFileImage, ()->Screen.getProjectFile().getFileCreator().showFileView("class", file.getAbsolutePath()))
			.createItem("New Record", IconManager.fluentrecordFileImage, ()->Screen.getProjectFile().getFileCreator().showFileView("record", file.getAbsolutePath()))
			.createItem("New Interface", IconManager.fluentinterfaceFileImage, ()->Screen.getProjectFile().getFileCreator().showFileView("interface", file.getAbsolutePath()))
			.createItem("New Enum", IconManager.fluentenumFileImage, ()->Screen.getProjectFile().getFileCreator().showFileView("enum", file.getAbsolutePath()))
			.createItem("New Annotation", IconManager.fluentannotationFileImage, ()->Screen.getProjectFile().getFileCreator().showFileView("@interface", file.getAbsolutePath()));
		}
		popup.createItem("Open in Desktop (F1)", IconManager.fluentdesktopImage, ()->Screen.openInDesktop(file));
		popup.createItem("Open in Terminal (F3)", IconManager.fluentconsoleImage, ()->Screen.openInTerminal(file));
		if(!file.isDirectory()) {
			popup
			.createItem("Open On Right Tab Panel", IconManager.fluenteditFileImage, ()->Screen.getScreen().loadFileOnRightTabPanel(file))
			.createItem("Open On Bottom Tab Panel", IconManager.fluenteditFileImage, ()->Screen.getScreen().loadFileOnBottomTabPanel(file));
		}
		if(!file.getAbsolutePath().equals(Screen.getProjectFile().getProjectPath())){
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
				Screen.getProjectFile().getFileTreePanel().refresh();
			});
		}
		popup.createItem("Refresh", IconManager.fluentrefreshIcon, Screen.getProjectFile().getFileTreePanel()::refresh);
		if(!file.isDirectory()) {
			popup.createItem("Rename (F2)", IconManager.fluentrenameImage, ()->{
				Screen.getProjectFile().getFileTreePanel().findBranch(file).renameView();
			});
		}

		popup.createItem("Copy Path (\"path\")", IconManager.fluentcopyImage, ()->Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection("\"" + file.getAbsolutePath() + "\""), null));
		popup.createItem("Copy Path", IconManager.fluentcopyImage, ()->Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(file.getAbsolutePath()), null));
	}
}

