/**
  * The PluginManager
  * Copyright (C) 2021 Omega UI

  * This program is free software: you can redistribute it and/or modify
  * it under the terms of the GNU General Public License as published by
  * the Free Software Foundation, either version 3 of the License, or
  * (at your option) any later version.

  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.

  * You should have received a copy of the GNU General Public License
  * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package omega.plugin.management;
import omega.ui.dialog.ChoiceDialog;

import omegaui.dynamic.database.DataBase;

import omega.plugin.Plugin;

import omega.plugin.store.PluginStore;

import java.util.LinkedList;
import java.util.HashMap;

import java.io.File;
public class PluginManager extends DataBase{
	public static final String PLUGINS_DATA_BASE = ".omega-ide" + File.separator + ".pluginDB";
	public static final File PLUGINS_DIRECTORY = new File(".omega-ide" + File.separator + "plugins");

	public PluginLoader pluginLoader;
	public LinkedList<Plugin> plugins = new LinkedList<>();
	public HashMap<String, Boolean> initMap = new HashMap();
	
	public PluginManager(){
		super(PLUGINS_DATA_BASE);
		pluginLoader = new PluginLoader(this);
	}

	public void load(){
		pluginLoader.pluginClassNames.forEach((name)->{
			Plugin plugin = pluginLoader.loadPlugin(name);
			add(plugin);
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

	public void add(Plugin plugin){
		if(plugin == null)
			return;
		for(Plugin px : plugins){
			if(px.getName().equals(plugin.getName()))
				return;
		}
		plugins.add(plugin);
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

	public void doPluginReactionRegistration(){
		plugins.forEach(plugin->plugin.registerReactions());
	}
	
	public synchronized boolean isPluginInstalled(String fileName){
		return new File(PLUGINS_DIRECTORY.getAbsolutePath(), fileName).exists();
	}
}
