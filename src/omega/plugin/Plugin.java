package omega.plugin;
import java.util.LinkedList;

import java.net.URL;
public interface Plugin {
	boolean init();
	boolean enable();
	boolean disable();
	boolean needsRestart();
	String getName();
	String getVersion();
	String getAuthor();
	String getDescription();
	String getSizeInMegaBytes();
	String getLicense();
	URL getImage();
	String getPluginCategory();
	
	default LinkedList<URL> getScreenshots(){
		return new LinkedList<URL>();
	}
}
