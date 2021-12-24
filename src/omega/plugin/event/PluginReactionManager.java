package omega.plugin.event;

import java.util.LinkedList;
import omega.plugin.Plugin;
import omega.plugin.management.PluginManager;

public class PluginReactionManager {

  public PluginManager pluginManager;

  public LinkedList<PluginReactionData> reactionDataSet = new LinkedList<>();

  public PluginReactionManager(PluginManager pluginManager) {
    this.pluginManager = pluginManager;
  }

  public synchronized boolean registerPlugin(
    Plugin plugin,
    int pluginReactionType,
    PluginReactionEventListener listener
  ) {
    if (
      !pluginManager.isPluginEnabled(plugin) ||
      !PluginReactionEvent.isEventTypeValid(pluginReactionType)
    ) return false;
    reactionDataSet.add(
      new PluginReactionData(plugin, pluginReactionType, listener)
    );
    return true;
  }

  public synchronized void triggerReaction(PluginReactionEvent event) {
    int pluginReactionEventType = event.type();
    for (PluginReactionData data : reactionDataSet) {
      if (data.pluginReactionEventType() == pluginReactionEventType) data
        .listener()
        .react(event);
    }
  }
}
