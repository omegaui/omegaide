package omega.utils;
/*
    Copyright (C) 2021 Omega UI. All Rights Reserved.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.LinkedList;

import javax.swing.JMenuItem;

import omega.Screen;
import omega.tabPane.IconManager;

public class RecentsManager {

	public final static LinkedList<String> RECENTS = new LinkedList<>();
	private static final String EXT = ".omega-ide" + File.separator + ".recents";
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
                         ToolMenu.recentFilePopup.createItem(r.substring(r.lastIndexOf(File.separatorChar)+1), IconManager.fileImage, ()->{
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
                         ToolMenu.recentProjectPopup.createItem(r.substring(r.lastIndexOf(File.separator)+1), IconManager.projectImage, ()->{
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
               File home = new File(DataManager.getProjectsHome());
               if(home.exists()){
                    ToolMenu.allProjectsPopup.trash();
                    File[] files = home.listFiles();
                    for(int i = 0; i < files.length; i++){
                         for(int j = 0; j < files.length - i - 1; j++){
                         	if(files[j].getName().compareTo(files[j + 1].getName()) > 0){
                                   File f = files[j];
                                   files[j] = files[j + 1];
                                   files[j + 1] = f;
                         	}
                         }
                    }
                    for(File fileZ : files){
                         if(fileZ.isDirectory()){
                              ToolMenu.allProjectsPopup.createItem(fileZ.getName(), IconManager.projectImage, ()->{
                                   ToolMenu.recentProjectPopup.setVisible(false);
                                   Screen.getScreen().getToolMenu().projectPopup.setVisible(false);
                                   screen.loadProject(fileZ);
                              });
                         }
                    }
               }
               
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
