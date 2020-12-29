package tabPane;
import popup.*;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.nio.file.Path;

import java.io.File;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import ide.Screen;
import ide.utils.Editor;
import ide.utils.UIManager;
import ide.utils.systems.EditorTools;
import ide.utils.systems.creators.RefractionManager;
import importIO.ImportManager;

public class PopupManager {
	public static final byte SOURCE_FILE = 0;
	public static final byte NON_SOURCE_FILE = 1;
	public static OPopupWindow createPopup(byte type, Editor editor, Screen screen) {
		OPopupWindow popup = new OPopupWindow("Tab Menu", ide.Screen.getScreen(), 0, false);
		
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
               .createItem("Reload", null, ()->editor.reloadFile()).width(300);
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
               .createItem("Reload", null, ()->editor.reloadFile()).width(300);
		}
          popup.createItem("Copy Path (\"path\")", IconManager.fileImage, ()->Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection("\""+editor.currentFile.getAbsolutePath()+"\""), null));
          popup.createItem("Copy Path", IconManager.fileImage, ()->Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(editor.currentFile.getAbsolutePath()), null));
		return popup;
	}

	public static void createTreePopup(OPopupWindow popup, File file) {
          popup.createItem("New Package", IconManager.projectImage, ()->Screen.getFileView().getFileCreator().show("Class"))
          .createItem("New Class", IconManager.classImage, ()->Screen.getFileView().getFileCreator().show("Class"))
          .createItem("New Interface", IconManager.interImage, ()->Screen.getFileView().getFileCreator().show("interface"))
          .createItem("New Enum", IconManager.enumImage, ()->Screen.getFileView().getFileCreator().show("enum"))
          .createItem("New Annotation", IconManager.annImage, ()->Screen.getFileView().getFileCreator().show("@interface"))
          .createItem("Create Custom File", IconManager.fileImage, ()->Screen.getFileView().getFileCreator().show("Custom File"))
          .createItem("Delete", IconManager.closeImage, ()->{
              Editor editor = ide.Screen.getScreen().getTabPanel().findEditor(file);
               if(editor != null) {
                    ide.Screen.getScreen().getTabPanel().remove(editor);
               }
               Editor.deleteFile(file);
               Screen.getProjectView().reload();
               ImportManager.readSource(EditorTools.importManager);
          })
          .createItem("Refresh", null, ()->Screen.getProjectView().reload())
          .createItem("Rename", IconManager.fileImage, ()->{
               Editor editor = ide.Screen.getScreen().getTabPanel().findEditor(file);
               if(editor != null) ide.Screen.getScreen().getTabPanel().remove(editor);
               Screen.getProjectView().getRefractor().rename(file, "Rename -"+file.getName(), ()->{
                    Screen.getProjectView().getScreen().loadFile(RefractionManager.lastFile);
               });
               Screen.getProjectView().reload();
               ImportManager.readSource(EditorTools.importManager);
          });
		
          popup.createItem("Copy Path (\"path\")", IconManager.fileImage, ()->Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection("\""+file.getAbsolutePath()+"\""), null));
          popup.createItem("Copy Path", IconManager.fileImage, ()->Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(file.getAbsolutePath()), null));
	}
}
