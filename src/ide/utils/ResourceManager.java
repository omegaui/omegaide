package ide.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Scanner;

import ide.Screen;

public class ResourceManager {

	public static volatile LinkedList<String> roots = new LinkedList<>();
	private static File file;
	
	public ResourceManager() {
		roots.clear();
		file = new File(Screen.getFileView().getProjectPath() + File.separator + ".resources");
		loadFile();
	}
	
	public void add(String root) {
		if(!roots.contains(root)) {
			roots.add(root);
		}
	}
	
	public void loadFile() {
		if(!file.exists()) return;
		roots.clear();
		try {
			Scanner reader = new Scanner(file);
			while(reader.hasNextLine()) {
				add(reader.nextLine());
			}
			reader.close();
		}catch(Exception e) {System.out.println(e.getMessage());}
	}
	
	public void saveData() {
		if(roots.isEmpty()) {
			file.delete();
			return;
		}
		try {
			PrintWriter writer = new PrintWriter(new FileOutputStream(file));
			roots.forEach(r->{
				writer.println(r);
			});
			writer.close();
		}catch(Exception e) {System.out.println(e.getMessage());}
	}

}
