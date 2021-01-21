package ide.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Scanner;

import ide.Screen;

public class DependencyManager {

	public static final String DEPENDENCIES_PATH = ".dependencies";
	public LinkedList<String> dependencies = new LinkedList<>();
	public LinkedList<String> natives = new LinkedList<>();

	public DependencyManager() {
		loadFile();
	}

	public void add(String path) {
		if(dependencies.indexOf(path) < 0) {
			dependencies.add(path);
		}
	}

	public void loadFile() {
		dependencies.clear();
		String path = Screen.getFileView().getProjectPath() + File.separator + DEPENDENCIES_PATH;
		File file  = new File(path);
		if(!file.exists()) return;
		try {
			Scanner reader = new Scanner(file);
			while(reader.hasNextLine()) {
				String line = reader.nextLine();
				line = line.substring(line.indexOf('\"')+1, line.lastIndexOf('\"'));
				File d = new File(line);
				if(d.exists() && d.getName().endsWith(".jar")) {
					add(line);
				}
			}
			reader.close();
		}catch(Exception e) {}
	}

	public void saveFile() {
		try {
			PrintWriter writer = new PrintWriter(new FileOutputStream(Screen.getFileView().getProjectPath() + File.separator + DEPENDENCIES_PATH));
			dependencies.forEach((d)->{
				d = "\""+d+"\"";
				writer.println(d);
			});
			writer.close();
		}catch(Exception e) {}
	}

}
