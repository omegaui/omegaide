package omega.utils;
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
               .createItem("Save", IconManager.fileImage, ()->editor.saveCurrentFile())
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
		else {
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
          popup.createItem("New Directory", IconManager.projectImage, ()->Screen.getFileView().getFileCreator().showDirView(file.getAbsolutePath()))
          .createItem("New File", IconManager.fileImage, ()->Screen.getFileView().getFileCreator().showFileView(file.getAbsolutePath()))
          .createItem("New Class", IconManager.classImage, ()->Screen.getFileView().getFileCreator().show("class"))
          .createItem("New Record", IconManager.classImage, ()->Screen.getFileView().getFileCreator().show("record"))
          .createItem("New Interface", IconManager.interImage, ()->Screen.getFileView().getFileCreator().show("interface"))
          .createItem("New Enum", IconManager.enumImage, ()->Screen.getFileView().getFileCreator().show("enum"))
          .createItem("New Annotation", IconManager.annImage, ()->Screen.getFileView().getFileCreator().show("@interface"))
          .createItem("Open in Desktop", IconManager.fileImage, ()->Screen.openInDesktop(file))
          .createItem("Open On Right Tab Panel", IconManager.fileImage, ()->Screen.getScreen().loadFileOnRightTabPanel(file))
          .createItem("Open On Bottom Tab Panel", IconManager.fileImage, ()->Screen.getScreen().loadFileOnBottomTabPanel(file))
          .createItem("Delete", IconManager.closeImage, ()->{
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
          })
          .createItem("Refresh", null, ()->Screen.getProjectView().reload())
          .createItem("Rename", IconManager.fileImage, ()->{
               Screen.getProjectView().getFileOperationManager().rename("Rename " + file.getName(), "rename", file);
               Screen.getProjectView().reload();
          });
		
          popup.createItem("Copy Path (\"path\")", IconManager.fileImage, ()->Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection("\"" + file.getAbsolutePath() + "\""), null));
          popup.createItem("Copy Path", IconManager.fileImage, ()->Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(file.getAbsolutePath()), null));
	}
}
