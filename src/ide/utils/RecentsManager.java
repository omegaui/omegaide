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
			ToolMenu.openFileMenu.removeAll();
			ToolMenu.openProjectMenu.removeAll();
			ToolMenu.openFileMenu.setIcon(IconManager.open_file);
			RECENTS.forEach((r)->{
				File fileX = new File(r);
				if(fileX.isFile()) {
					JMenuItem fileItem = new JMenuItem(r.substring(r.lastIndexOf('/')+1), IconManager.open_file);
					fileItem.addActionListener(e->screen.loadFile(new File(r)));
					ToolMenu.openFileMenu.add(fileItem);
				}
			});
			JMenuItem fileItem = new JMenuItem("Clear Recent Files list", IconManager.hide);
			fileItem.addActionListener(e->removeAllFiles());
			ToolMenu.openFileMenu.add(fileItem);
			
			ToolMenu.openProjectMenu.setIcon(IconManager.project);
			RECENTS.forEach((r)->{
				File fileX = new File(r);
				if(fileX.isDirectory()) {
					JMenuItem projectItem = new JMenuItem(r.substring(r.lastIndexOf('/')+1), IconManager.project);
					projectItem.addActionListener(e->screen.loadProject(new File(r)));
					ToolMenu.openProjectMenu.add(projectItem);
				}
			});
			JMenuItem projectItem = new JMenuItem("Clear Recent Projects list", IconManager.hide);
			projectItem.addActionListener(e->removeAllProjects());
			ToolMenu.openProjectMenu.add(projectItem);
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
