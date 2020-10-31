package ide.utils;

import java.io.File;

import ide.Screen;
import omega.database.DataBase;
import omega.database.DataEntry;

public class DataManager extends DataBase{

	private volatile static String defaultProjectPath = "No Default Project set yet.";
	private volatile static String editorColoringScheme = "idea";
	private volatile static boolean realTimeContentAssist = true;
	private volatile static boolean useStarImports = false;
	private volatile static String pathToJava = "";
	
	public DataManager(Screen screen)
	{
		super(".preferences");
		loadData();
	}
	
	private void loadData()
	{
		try {
			DataEntry e = getEntryAt("Default Project", 0);
			if(e == null) return;
			if(new File(e.getValue()).exists())
				setDefaultProjectPath(e.getValue());
			setEditorColoringScheme(getEntryAt("Editor Coloring Scheme", 0).getValue());
			setContentAssistRealTime(getEntryAt("Content Assist Real-Time", 0).getValueAsBoolean());
			setUseStarImports(getEntryAt("Use Star Imports", 0).getValueAsBoolean());
			setPathToJava(getEntryAt("Folder Containing Java Development Kits and Environments", 0).getValue());
		}catch(Exception e) {e.printStackTrace();}
	}

	public void saveData()
	{
		clear();
		addEntry("Default Project", defaultProjectPath);
		addEntry("Editor Coloring Scheme", editorColoringScheme);
		addEntry("Content Assist Real-Time", isContentAssistRealTime()+"");
		addEntry("Use Star Imports", isUsingStarImports()+"");
		addEntry("Folder Containing Java Development Kits and Environments", getPathToJava());
		save();
	}
	
	public static String getPathToJava() {
		return pathToJava;
	}
	
	public static void setPathToJava(String path) {
		pathToJava = path;
	}
	
	public static String getDefaultProjectPath() {
		return defaultProjectPath;
	}

	public static void setDefaultProjectPath(String defaultProjectPath) {
		DataManager.defaultProjectPath = defaultProjectPath;
	}

	public static void setContentAssistRealTime(boolean value) {
		realTimeContentAssist = value;
	}
	
	public static boolean isContentAssistRealTime() {
		return realTimeContentAssist;
	}

	public static void setUseStarImports(boolean value) {
		useStarImports = value;
	}
	
	public static boolean isUsingStarImports() {
		return useStarImports;
	}
	
	public static String getEditorColoringScheme() {
		return editorColoringScheme;
	}

	public static void setEditorColoringScheme(String editorColoringScheme) {
		DataManager.editorColoringScheme = editorColoringScheme;
	}
}
