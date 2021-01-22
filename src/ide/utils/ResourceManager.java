package ide.utils;
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
