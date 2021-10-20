package omega.plugin.management;
import omega.plugin.Plugin;

import omega.plugin.store.PluginStore;

import omega.utils.ChoiceDialog;

import java.util.LinkedList;
import java.util.HashMap;

import java.io.File;

import omega.database.DataBase;
public class PluginManager extends DataBase{
	public static final String PLUGINS_DATA_BASE = ".omega-ide" + File.separator + ".pluginDB";
	public static final File PLUGINS_DIRECTORY = new File(".omega-ide" + File.separator + "plugins");

	public PluginLoader pluginLoader;
	public LinkedList<Plugin> plugins = new LinkedList<>();
	public HashMap<String, Boolean> initMap = new HashMap();
	
	public PluginManager(){
		super(PLUGINS_DATA_BASE);
		load();
	}

	public void load(){
		pluginLoader = new PluginLoader(this);
		pluginLoader.pluginClassNames.forEach((name)->{
			Plugin plugin = pluginLoader.loadPlugin(name);
			if(plugin != null)
				plugins.add(plugin);
		});
		getDataSetNames().forEach((pluginName)->{
			if(getEntryAt(pluginName, 0).getValueAsBoolean()){
				Plugin plugin = getPluginObject(pluginName);
				if(plugin != null) {
					if(plugin.init()){
						plugin.enable();
						initMap.put(plugin.getName(), true);
					}
				}
			}
		});
	}

	public Plugin getPluginObject(String pluginName){
		for(Plugin plugin : plugins){
			if(plugin.getName().equals(pluginName))
				return plugin;
		}
		return null;
	}

	public void put(Plugin plugin, boolean enabled){
		updateEntry(plugin.getName(), String.valueOf(enabled), 0);
		if(enabled){
			if(!plugin.needsRestart()){
				boolean init = false;
				Object x = initMap.get(plugin.getName());
				if((x != null && (boolean)x) || (init = plugin.init())){
					plugin.enable();
					if(init)
						initMap.put(plugin.getName(), true);
				}
			}
		}
		else{
			plugin.disable();
		}
	}

	public boolean isPluginEnabled(Plugin plugin){
		if(getEntryAt(plugin.getName(), 0) == null){
			updateEntry(plugin.getName(), String.valueOf(false), 0);
			initMap.put(plugin.getName(), false);
		}
		return getEntryAt(plugin.getName(), 0).getValueAsBoolean();
	}

	public synchronized void uninstallPlugin(PluginStore store, String name){
		int choice = ChoiceDialog.makeChoice("Do You Want to Uninstall This Plugin?", "Yes", "No");
		if(choice == ChoiceDialog.CHOICE1){
			store.setStatus("Deleting " + name + " ...");
			Plugin plugin = getPluginObject(name);
			if(plugin == null)
				return;
			File file = new File(PLUGINS_DIRECTORY.getAbsolutePath(), getPluginObject(name).getClass().getName() + ".jar");
			if(file.delete()){
				store.setStatus(null);
				plugins.remove(plugin);
				store.refresh();
			}
			else
				store.setStatus("Unable to Delete Plugin : " + name);
		}
	}

	public synchronized boolean isPluginInstalled(String fileName){
		return new File(PLUGINS_DIRECTORY.getAbsolutePath(), fileName).exists();
	}
}
