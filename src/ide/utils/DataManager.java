package ide.utils;
/*
    Copyright (C) 2021 Omega UI. All Rights Reserved.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
import java.io.File;

import ide.Screen;
import omega.database.DataBase;
import omega.database.DataEntry;

public class DataManager extends DataBase {

	private static String defaultProjectPath = "No Default Project set yet.";
	private volatile static boolean realTimeContentAssist = false;
	private static boolean useStarImports = false;
	private static String pathToJava = "";
     private static String projectsHome = "";
     private static String theme = "light";
	
	public DataManager(Screen screen) {
		super(".omega-ide" + File.separator + ".preferences");
		loadData();
	}
	
	private void loadData()
	{
		try {
			DataEntry e = getEntryAt("Default Project", 0);
			if(e == null) return;
			if(new File(e.getValue()).exists())
				setDefaultProjectPath(e.getValue());
			setContentAssistRealTime(getEntryAt("Content Assist Real-Time", 0).getValueAsBoolean());
			setUseStarImports(getEntryAt("Use Star Imports", 0).getValueAsBoolean());
               setPathToJava(getEntryAt("Folder Containing Java Development Kits and Environments", 0).getValue());
               setProjectsHome(getEntryAt("Projects Home", 0).getValue());
               setTheme(getEntryAt("Theme", 0).getValue());
		}catch(Exception e) { 
		     System.err.println(e);
	     }
	}

	public void saveData()
	{
		clear();
		addEntry("Default Project", defaultProjectPath);
		addEntry("Content Assist Real-Time", isContentAssistRealTime()+"");
		addEntry("Use Star Imports", isUsingStarImports()+"");
          addEntry("Folder Containing Java Development Kits and Environments", getPathToJava());
          addEntry("Projects Home", getProjectsHome());
          addEntry("Theme", getTheme());
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

     public static void setProjectsHome(String home){
          projectsHome = home;
     }

     public static String getProjectsHome(){
     	return projectsHome;
     }

     public static void setTheme(String t){
     	theme = t;
     }

     public static String getTheme(){
     	return theme;
     }
}
