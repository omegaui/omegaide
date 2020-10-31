package system;

import java.io.File;
import java.util.LinkedList;

public final class Plugin{
	private static LinkedList<Descriptor> plugs = new LinkedList<>();
	
	protected static void install(Descriptor dec) {
		if(!plugs.contains(dec)) {
			//Creating PluginFile
			PluginFile pluginFile = new PluginFile(dec.getPluginPath());
			pluginFile.addAll();
			plugs.add(dec);
		}
	}
}
