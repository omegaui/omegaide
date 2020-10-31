package ide.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Scanner;

import ide.Screen;

public class NativesManager {

	public static final String NATIVES_PATH = ".natives";
	public LinkedList<String> natives = new LinkedList<>();
	
	public NativesManager() {
		loadFile();
	}
	
	public void add(String path) {
		if(natives.indexOf(path) < 0) {
			natives.add(path);
		}
	}
	
	public void loadFile() {
		natives.clear();
		String path = Screen.getFileView().getProjectPath() + "/" + NATIVES_PATH;
		File file  = new File(path);
		if(!file.exists()) return;
		try {
			Scanner reader = new Scanner(file);
			while(reader.hasNextLine()) {
				String line = reader.nextLine();
				line = line.substring(line.indexOf('\"')+1, line.lastIndexOf('\"'));
				File d = new File(line);
				if(d.exists()) {
					add(line);
				}
			}
			reader.close();
		}catch(Exception e) {}
	}
	
	public void saveFile() {
		try {
			PrintWriter writer = new PrintWriter(new FileOutputStream(Screen.getFileView().getProjectPath() + "/" + NATIVES_PATH));
			natives.forEach((d)->{
				d = "\""+d+"\"";
				writer.println(d);
			});
			writer.close();
		}catch(Exception e) {}
	}

}
