package omega.plugin.event;
import omega.plugin.Plugin;
public record PluginReactionData(Plugin plugin, int pluginReactionEventType, PluginReactionEventListener listener) {
	public void triggerIfCapable(PluginReactionEvent event, int type){
		if(type != pluginReactionEventType)
			return;
		listener.react(event);
	}
}
