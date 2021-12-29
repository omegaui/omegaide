/**
  * PluginReactionManager
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

package omega.plugin.event;
import omega.plugin.management.PluginManager;

import omega.plugin.Plugin;

import java.util.LinkedList;
public class PluginReactionManager {
	public PluginManager pluginManager;
	
	public LinkedList<PluginReactionData> reactionDataSet = new LinkedList<>();

	public PluginReactionManager(PluginManager pluginManager){
		this.pluginManager = pluginManager;
		
	}

	public synchronized boolean registerPlugin(Plugin plugin, int pluginReactionType, PluginReactionEventListener listener){
		if(!pluginManager.isPluginEnabled(plugin) || !PluginReactionEvent.isEventTypeValid(pluginReactionType))
			return false;
		reactionDataSet.add(new PluginReactionData(plugin, pluginReactionType, listener));
		return true;
	}

	public synchronized void triggerReaction(PluginReactionEvent event){
		int pluginReactionEventType = event.type();
		for(PluginReactionData data : reactionDataSet){
			if(data.pluginReactionEventType() == pluginReactionEventType)
				data.listener().react(event);
		}
	}
}
