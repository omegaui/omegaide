package omega.plugin.management;
import omega.plugin.Plugin;

import java.util.zip.ZipFile;

import java.io.File;

import java.util.LinkedList;

import java.net.URLClassLoader;
import java.net.URL;
public class PluginLoader {
	public PluginManager pluginManager;
	
	public URLClassLoader classLoader;
	
	public LinkedList<String> pluginClassNames = new LinkedList<>();
	
	public PluginLoader(PluginManager pluginManager){
		this.pluginManager = pluginManager;
		putPluginsOnClasspath();
	}
	
	public void putPluginsOnClasspath(){
		try{
			File[] plugins = PluginManager.PLUGINS_DIRECTORY.listFiles((e)->e.getName().endsWith(".jar"));
			if(plugins == null || plugins.length == 0)
				return;
			for(int i = 0; i < plugins.length; i++){
				for(int j = 0; j < plugins.length - 1 - i; j++){
					String name1 = plugins[j].getName();
					String name2 = plugins[j + 1].getName();
					if(name1.compareTo(name2) > 0){
						File fx = plugins[j];
						plugins[j] = plugins[j + 1];
						plugins[j + 1] = fx;
					}
				}
			}
			URL[] urls = new URL[plugins.length];
			boolean empty = true;
			for(int i = 0; i < plugins.length; i++){
				try{
					new ZipFile(plugins[i]).close();
				}
				catch(Exception e){
					System.err.println("Corrupt Plugin File \"" + plugins[i].getAbsolutePath() + "\"");
					System.err.println("Attempting to Delete it ...");
					System.err.println(plugins[i].delete() ? "Deleted Successfully!" : "Unable to Delete Corrupt Plugin File!");
					e.printStackTrace();
					continue;
				}
				urls[i] = plugins[i].toURL();
				pluginClassNames.add(plugins[i].getName().substring(0, plugins[i].getName().lastIndexOf(".")));
				empty = false;
			}
			if(!empty)
				classLoader = URLClassLoader.newInstance(urls);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public Plugin loadPlugin(String pluginClassName){
		try{
			return (Plugin)classLoader.loadClass(pluginClassName).newInstance();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	
}
