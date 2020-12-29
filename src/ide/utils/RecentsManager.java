package ide.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.LinkedList;

import javax.swing.JMenuItem;

import ide.Screen;
import tabPane.IconManager;

public class RecentsManager {

	public final static LinkedList<String> RECENTS = new LinkedList<>();
	private static final String EXT = ".recents";
	private Screen screen;
	
	public RecentsManager(Screen screen) {
		this.screen = screen;
		loadData();
	}
	
	public void loadData() {
		RECENTS.clear();
		File file = new File(EXT);
		if(!file.exists()) return;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String token = reader.readLine();
			while(token != null) {
				add(token);
				token = reader.readLine();
			}
			reader.close();
               
			ToolMenu.recentFilePopup.trash();
			RECENTS.forEach((r)->{
				File fileX = new File(r);
				if(fileX.isFile()) {
                         ToolMenu.recentFilePopup.createItem(r.substring(r.lastIndexOf('/')+1), IconManager.fileImage, ()->{
                              ToolMenu.recentFilePopup.setVisible(false);
                              Screen.getScreen().getToolMenu().filePopup.setVisible(false);
                              screen.loadFile(fileX);
                         });
				}
			});

               ToolMenu.recentFilePopup.createItem("Clear Recent Files list", IconManager.hideImage, ()->{
                    ToolMenu.recentFilePopup.setVisible(false);
                    removeAllFiles();
               });
               
			ToolMenu.recentProjectPopup.trash();
			RECENTS.forEach((r)->{
				File fileX = new File(r);
				if(fileX.isDirectory()) {
                         ToolMenu.recentProjectPopup.createItem(r.substring(r.lastIndexOf('/')+1), IconManager.projectImage, ()->{
                              ToolMenu.recentProjectPopup.setVisible(false);
                              Screen.getScreen().getToolMenu().projectPopup.setVisible(false);
                              screen.loadProject(fileX);
                         });
				}
			});

               ToolMenu.recentProjectPopup.createItem("Clear Recent Projects list", IconManager.hideImage, ()->{
                    ToolMenu.recentProjectPopup.setVisible(false);
                    removeAllProjects();
               });
               
		}catch(Exception e) {e.printStackTrace();}
	}
	
	public synchronized void saveData() {
		try {
			PrintWriter writer = new PrintWriter(new FileOutputStream(EXT));
			RECENTS.forEach((r)->writer.println(r));
			writer.close();
			loadData();
		}catch(Exception e) {e.printStackTrace();}
	}

	public static synchronized void add(String path) {
		if(RECENTS.indexOf(path) < 0) {
			RECENTS.add(path);
			File file = new File(path);
			if(RECENTS.size() > 20) {
				if(file.isDirectory()) {
					if(getNumberOfProjects() > 10)
						removeFirstProject();
					else if(getNumberOfFiles() > 10)
						removeFirstFile();
				}
			}
		}
	}

	public static void removeAllProjects() {
		LinkedList<String> paths = new LinkedList<>();
		File file = null;
		for(String path : RECENTS) {
			file = new File(path);
			if(file.isDirectory())
				paths.add(path);
		}
		for(String path : paths) {
			RECENTS.remove(path);
		}
		Screen.getRecentsManager().saveData();
	}
	
	public static void removeAllFiles() {
		LinkedList<String> paths = new LinkedList<>();
		File file = null;
		for(String path : RECENTS) {
			file = new File(path);
			if(file.isFile())
				paths.add(path);
		}
		for(String path : paths) {
			RECENTS.remove(path);
		}
		Screen.getRecentsManager().saveData();
	}

	private static void removeFirstProject() {
		File file = null;
		for(String path : RECENTS) {
			file = new File(path);
			if(file.isDirectory())
				break;
		}
		if(file == null) return;
		RECENTS.remove(file.getAbsolutePath());
	}
	
	private static void removeFirstFile() {
		File file = null;
		for(String path : RECENTS) {
			file = new File(path);
			if(file.isFile())
				break;
		}
		if(file == null) return;
		RECENTS.remove(file.getAbsolutePath());
	}
	
	public static int getNumberOfProjects() {
		int n = 0;
		File file;
		for(String path : RECENTS) {
			file = new File(path);
			if(file.isDirectory())
				n++;
		}
		return n;
	}
	
	public static int getNumberOfFiles() {
		int n = 0;
		File file;
		for(String path : RECENTS) {
			file = new File(path);
			if(file.isFile())
				n++;
		}
		return n;
	}
	
}
