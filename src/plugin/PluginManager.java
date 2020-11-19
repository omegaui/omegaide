package plugin;
import java.net.*;
import java.util.*;
import java.io.*;
public class PluginManager {
	public class Plug {
		public String name;
		public boolean enabled;
		public Plug(String name, boolean enabled){
			this.name = name;
			this.enabled = enabled;
		}

		@Override
		public String toString(){
			return name + " " + enabled;
		}
	}
	public static LinkedList<Plugin> plugins = new LinkedList<>();
	public static LinkedList<Plug> plugs = new LinkedList<>();
	public static LinkedList<Plug> init_plugs = new LinkedList<>();
	public static final String BASEDIR = "omega-ide-plugins";
	public static final File plugFile = new File(BASEDIR + "/.plugs");

	public PluginManager(){
          refresh();
	}

     public void refresh(){
          plugins.clear();
          plugs.clear();
          init_plugs.clear();
          load();
          read();
     }

	public void read(){
		try{
			if(!plugFile.exists()) return;
			Scanner reader = new Scanner(plugFile);
			while(reader.hasNextLine()){
				String line = reader.nextLine();
				String enabled = line.substring(line.lastIndexOf(' ') + 1);
				String name = line.substring(0, line.lastIndexOf(' '));
				setPlug(name, Boolean.valueOf(enabled));
			}
			reader.close();
		}catch(Exception e){ System.err.println(e); }
	}

	public void setPlug(String name, boolean enabled){
		for(Plug p : plugs){
			if(p.name.equals(name)){
				p.enabled = enabled;
				if(p.enabled && getPlugin(p.name) != null) {
					Plugin plugin = getPlugin(p.name);
					plugin.init();
					plugin.enable();
					init_plugs.add(p);
				}
				return;
			}
		}
	}
	
	public void initPlug(String name) {
		for(Plug p : init_plugs) {
			if(p.name.equals(name)) return;
		}
		Plugin plugin = getPlugin(name);
		if(plugin == null) return;
		plugin.init();
		plugin.enable();
		init_plugs.add(getPlug(name));
	}

	public void save(){
		try{
			if(plugs.isEmpty()) return;
			final PrintWriter writer = new PrintWriter(plugFile);
			plugs.forEach(writer::println);
			writer.close();
		}catch(Exception e){ System.err.println(e); }
	}

	public void offer(Plugin p){
		for(Plug px : plugs){
			if(px.name.equals(p.getName())) return;
		}
		plugs.add(new Plug(p.getName(), false));
	}

	public Plug getPlug(String name){
		for(Plug plug : plugs){
			if(plug.name.equals(name)) return plug;
		}
		return new Plug(name, false);
	}
	
	public Plugin getPlugin(String name){
		for(Plugin p : plugins){
			if(p.getName().equals(name)) return p;
		}
		return null;
	}

	public static boolean enabled(String name){
		for(Plug plug : plugs){
			if(plug.name.equals(name)) return plug.enabled;
		}
		return false;
	}

	public void load(){
		try{
			File base = new File(BASEDIR);
			if(!base.exists()) {
				base.mkdir();
				return;
			}
			File[] files = base.listFiles();
			if(files == null || files.length == 0) return;
			for(File f : files){
				if(f.getName().endsWith("-OMEGAIDE.jar")){
					String className = f.getName();
					className = className.substring(className.indexOf('-') + 1);
					className = className.substring(0, className.lastIndexOf('-'));
					ClassLoader loader = URLClassLoader.newInstance(new URL[]{ f.toURL() });
					Plugin p = (Plugin)loader.loadClass(className).newInstance();
					plugins.add(p);
					offer(p);
				}
			}
		}catch(Exception e){System.err.println(e);}
	}
}
