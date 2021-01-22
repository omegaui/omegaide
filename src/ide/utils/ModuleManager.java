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
import java.util.LinkedList;

import ide.Screen;
import omega.database.DataBase;
import omega.database.DataEntry;
public class ModuleManager extends DataBase{
	public LinkedList<String> roots = new LinkedList<>();
	public ModuleManager(){
		super(Screen.getFileView().getProjectPath() + File.separator + ".modules");
		LinkedList<DataEntry> entries = getEntries("Module Roots");
		if(entries != null){
			entries.forEach(e->{
				if(new File(e.getValue()).exists())
					add(e.getValue());
			});
		}
	}

	@Override
	public void save(){
		int i = 0;
		for(String r : roots) {
			updateEntry("Module Roots", r, i++);
		}
		super.save();
	}

	public String getModularPath(){
		if(roots.isEmpty()) return null;
		String command = "";
		for(String r : roots)
			command += r + ide.Screen.PATH_SEPARATOR;
		return command.equals("") ? null : command;
	}

	public String getModularNames() {
		String command = "";
		for(String r : roots){
			for(String name : getModularJars(r)){
				command += "," + name;
			}
		}
		command = command.substring(1);
		return command.equals("") ? null : command;
	}

	public String getModularNamesFor(String r) {
		String command = "";
		for(String name : getModularJars(r)){
			command += "," + name;
		}
		command = command.substring(1);
		return command.equals("") ? null : command;
	}

	public LinkedList<String> getModularJars(String parent){
		LinkedList<String> names = new LinkedList<>();
		if(!new File(parent).exists()) return names;
		File[] files = new File(parent).listFiles();
		outer:
			for(File f : files){
				if(f.getName().endsWith(".jar")){
					for(char c : f.getName().toCharArray()){
						if(!Character.isLetter(c) && c != '.')
							continue outer;
					}
					names.add(f.getName().substring(0, f.getName().lastIndexOf('.')));
				}
			}
		return names;
	}

	public void remove(String root){
		roots.remove(root);
	}

	public boolean add(String root){
		if(!roots.contains(root))
			roots.add(root);
		return roots.contains(root);
	}
}
