package ide.utils;
import java.io.File;
import java.util.LinkedList;

import ide.Screen;
import omega.database.DataBase;
import omega.database.DataEntry;
public class ModuleManager extends DataBase{
	public LinkedList<String> roots = new LinkedList<>();
	public ModuleManager(){
		super(Screen.getFileView().getProjectPath() + "/.modules");
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
			command += r + ":";
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
