package tabPane;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.File;

import javax.swing.Icon;
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
	public static JPopupMenu createPopup(byte type, Editor editor, Screen screen, Icon icon) {
		JPopupMenu popUp = new JPopupMenu();
		popUp.setInvoker(editor);
		JMenuItem nameItem = new JMenuItem(editor.currentFile.getName(), icon);
		nameItem.setDisabledIcon(icon);
		nameItem.setEnabled(false);
		popUp.add(nameItem);
		
		if(type == SOURCE_FILE) {
			JMenuItem runSingleItem = new JMenuItem("Run as Singlet", IconManager.runSingleIcon);
			JMenuItem runThisItem = new JMenuItem("Run as Main Class", IconManager.runAsMainIcon);
			JMenuItem runProjectItem = new JMenuItem("Run Project", IconManager.runProjectIcon);
			JMenuItem compileProjectItem = new JMenuItem("Build Project", IconManager.compileProjectIcon);
			JMenuItem saveItem = new JMenuItem("Save", IconManager.saveIcon);
			JMenuItem saveAsItem = new JMenuItem("Save As", IconManager.saveAsIcon);
			JMenuItem discardItem = new JMenuItem("Discard", IconManager.discardIcon);
			JMenuItem reloadItem = new JMenuItem("Reload", IconManager.reloadIcon);
			
			runSingleItem.addActionListener(e->{
				Screen.getRunView().runSinglet(editor.currentFile);
			});
			runThisItem.addActionListener(e->{
				Screen.getRunView().setMainClassPath(editor.currentFile.getAbsolutePath());
				Screen.getRunView().run();
			});
			runProjectItem.addActionListener(e->{
				Screen.getRunView().run();
			});
			compileProjectItem.addActionListener(e->{
				Screen.getBuildView().compileProject();
			});
			saveItem.addActionListener(e->{
				editor.saveCurrentFile();
			});
			saveAsItem.addActionListener(e->{
				editor.saveFileAs();
				Screen.getProjectView().reload();
			});
			discardItem.addActionListener(e->{
				editor.reloadFile();
				screen.getTabPanel().remove(editor);
			});
			reloadItem.addActionListener(e->{editor.reloadFile();});
			
			popUp.add(runSingleItem);
			popUp.add(runThisItem);
			popUp.add(runProjectItem);
			popUp.add(compileProjectItem);
			popUp.add(saveItem);
			popUp.add(saveAsItem);
			popUp.add(discardItem);
			popUp.add(reloadItem);
		}
		else {
			JMenuItem saveItem = new JMenuItem("Save", IconManager.saveIcon);
			JMenuItem saveAsItem = new JMenuItem("Save As", IconManager.saveAsIcon);
			JMenuItem discardItem = new JMenuItem("Discard", IconManager.discardIcon);
			JMenuItem reloadItem = new JMenuItem("Reload", IconManager.reloadIcon);

			saveItem.addActionListener(e->{
				editor.saveCurrentFile();
			});
			saveAsItem.addActionListener(e->{
				editor.saveFileAs();
				Screen.getProjectView().reload();
			});
			discardItem.addActionListener(e->{
				editor.reloadFile();
				screen.getTabPanel().remove(editor);
			});
			reloadItem.addActionListener(e->{editor.reloadFile();});

			popUp.add(saveItem);
			popUp.add(saveAsItem);
			popUp.add(discardItem);
			popUp.add(reloadItem);
		}
		JMenuItem copyFullPath = new JMenuItem("Copy Path (\"path\")", IconManager.fileIcon);
		copyFullPath.addActionListener((e)->Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection("\""+editor.currentFile.getAbsolutePath()+"\""), null));
		popUp.add(copyFullPath);
		
		JMenuItem copyRelative = new JMenuItem("Copy Path", IconManager.fileIcon);
		copyRelative.addActionListener((e)->Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(editor.currentFile.getAbsolutePath()), null));
		popUp.add(copyRelative);
		UIManager.setData(popUp);
		return popUp;
	}

	public static void createTreePopup(JPopupMenu popUp, File file) {
		JMenuItem deleteItem = new JMenuItem("Delete", IconManager.deleteIcon);
		JMenuItem refreshItem = new JMenuItem("Refresh", IconManager.refreshIcon);
		JMenuItem renameItem = new JMenuItem("Rename", IconManager.renameIcon);
		JMenuItem packageItem = new JMenuItem("New Package", IconManager.java_20px);
		JMenuItem classItem = new JMenuItem("New Class", IconManager.class_20px);
		JMenuItem annotationItem = new JMenuItem("New Annotation", IconManager.annotation_20px);
		JMenuItem interfaceItem = new JMenuItem("New Interface", IconManager.interface_20px);
		JMenuItem enumItem = new JMenuItem("New Enum", IconManager.enum_20px);
		JMenuItem customItem = new JMenuItem("New File", IconManager.file_20px);
		final Screen screen = Screen.getScreen();
		packageItem.addActionListener(e->Screen.getFileView().getFileCreator().show("Class"));
		classItem.addActionListener(e->Screen.getFileView().getFileCreator().show("Class"));
		interfaceItem.addActionListener(e->Screen.getFileView().getFileCreator().show("interface"));
		enumItem.addActionListener(e->Screen.getFileView().getFileCreator().show("enum"));
		annotationItem.addActionListener(e->Screen.getFileView().getFileCreator().show("@interface"));
		customItem.addActionListener(e->Screen.getFileView().getFileCreator().show("Custom File"));
		refreshItem.addActionListener(e->{
			Screen.getProjectView().reload();
		});
		renameItem.addActionListener(e->{
			Editor editor = screen.getTabPanel().findEditor(file);
			if(editor != null) screen.getTabPanel().remove(editor);
			Screen.getProjectView().getRefractor().rename(file, "Rename -"+file.getName(), ()->{
				Screen.getProjectView().getScreen().loadFile(RefractionManager.lastFile);
			});
			Screen.getProjectView().reload();
			ImportManager.readSource(EditorTools.importManager);
		});
		deleteItem.addActionListener(e->{
			Editor editor = screen.getTabPanel().findEditor(file);
			if(editor != null) {
				screen.getTabPanel().remove(editor);
			}
			Editor.deleteFile(file);
			Screen.getProjectView().reload();
			ImportManager.readSource(EditorTools.importManager);
		});

		popUp.add(packageItem);
		popUp.add(classItem);
		popUp.add(interfaceItem);
		popUp.add(enumItem);
		popUp.add(annotationItem);
		popUp.add(customItem);
		popUp.add(refreshItem);
		popUp.add(renameItem);
		popUp.add(deleteItem);
		
		JMenuItem copyFullPath = new JMenuItem("Copy Path (\"path\")", IconManager.fileIcon);
		copyFullPath.addActionListener((e)->Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection("\""+file.getAbsolutePath()+"\""), null));
		popUp.add(copyFullPath);
		
		JMenuItem copyRelative = new JMenuItem("Copy Path", IconManager.fileIcon);
		copyRelative.addActionListener((e)->Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(file.getAbsolutePath()), null));
		popUp.add(copyRelative);
	}
}
