/**
  * DataManager
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

package omega.utils;

import java.io.File;

import omega.Screen;
import omega.database.DataBase;
import omega.database.DataEntry;

public class DataManager extends DataBase {

	public static final String INSTANT_MODE_SPEED = "instant-mode-speed";
	public static final String INSTANT_MODE_ACCURACY = "instant-mode-accuracy";

	private static String defaultProjectPath = "No Default Project set yet.";
	private static String pathToJava = "";
     private static String projectsHome = "";
     private static String theme = "light";
	private static String consoleCommand = "";
	private static String gradleCommand = "gradlew";
	private static String instantMode = INSTANT_MODE_SPEED;
	
	private volatile static boolean realTimeContentAssist = false;
     private volatile static boolean contentModeJava = true;
	private volatile static boolean useStarImports = false;
	private volatile static boolean sourceDefenderEnabled;
	
	public DataManager(Screen screen) {
		super(".omega-ide" + File.separator + ".preferences");
		loadData();
	}
	
	private void loadData() {
		try {
			DataEntry e = getEntryAt("Default Project", 0);
			if(e == null) return;
			if(new File(e.getValue()).exists())
				setDefaultProjectPath(e.getValue());
			setContentAssistRealTime(getEntryAt("Content Assist Real-Time", 0).getValueAsBoolean());
			setUseStarImports(getEntryAt("Use Star Imports", 0).getValueAsBoolean());
               setPathToJava(getEntryAt("Folder Containing Java Development Kits and Environments", 0).getValue());
               setWorkspace(getEntryAt("Projects Home", 0).getValue());
               setTheme(getEntryAt("Theme", 0).getValue());
               setContentModeJava(getEntryAt("Content Mode Java", 0).getValueAsBoolean());
               setSourceDefenderEnabled(getEntryAt("Source Defender Enabled", 0).getValueAsBoolean());
               setConsoleCommand(getEntryAt("System Console Launch Command", 0).getValue());
               setGradleCommand(getEntryAt("Gradle Build Script", 0).getValue());
               setInstantMode(getEntryAt("Instant Mode", 0).getValue());
		}
		catch(Exception e) { 
		     e.printStackTrace();
	     }
	}

	public void saveData() {
		clear();
		addEntry("Default Project", defaultProjectPath);
          addEntry("Content Assist Real-Time", isContentAssistRealTime() + "");
          addEntry("Content Mode Java", isContentModeJava() + "");
		addEntry("Use Star Imports", isUsingStarImports() + "");
          addEntry("Folder Containing Java Development Kits and Environments", getPathToJava());
          addEntry("Projects Home", getWorkspace());
          addEntry("Theme", getTheme());
          addEntry("Source Defender Enabled", isSourceDefenderEnabled() + "");
          addEntry("System Console Launch Command", getConsoleCommand());
          addEntry("Gradle Build Script", getGradleCommand());
          addEntry("Instant Mode", getInstantMode());
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

     public static void setWorkspace(String home){
          projectsHome = home;
     }

     public static String getWorkspace(){
     	return projectsHome;
     }

     public static void setTheme(String t){
     	theme = t;
     }

     public static String getTheme(){
     	return theme;
     }

     public static boolean isContentModeJava() {
          return contentModeJava;
     }
     
     public static void setContentModeJava(boolean contentModeJava) {
          DataManager.contentModeJava = contentModeJava;
     }

     public static boolean isSourceDefenderEnabled() {
          return sourceDefenderEnabled;
     }
     
     public static void setSourceDefenderEnabled(boolean sourceDefenderEnabled) {
          DataManager.sourceDefenderEnabled = sourceDefenderEnabled;
     }

     public static java.lang.String getConsoleCommand() {
          return consoleCommand;
     }
     
     public static void setConsoleCommand(java.lang.String consoleCommand) {
          DataManager.consoleCommand = consoleCommand;
     }

     public static java.lang.String getGradleCommand() {
          return gradleCommand;
     }
     
     public static void setGradleCommand(java.lang.String gradleCommand) {
          DataManager.gradleCommand = gradleCommand;
     }

     public static java.lang.String getInstantMode() {
          return instantMode;
     }
     
     public static void setInstantMode(java.lang.String instantMode) {
     	if(instantMode.equals(INSTANT_MODE_SPEED) || instantMode.equals(INSTANT_MODE_ACCURACY))
          	DataManager.instantMode = instantMode;
     	else{
     		System.err.println("Invalid \"Instant Mode\" value : " + instantMode);
     		System.err.println("Must be either \"" + INSTANT_MODE_SPEED + "\" or \"" + INSTANT_MODE_ACCURACY + "\"");
     	}
     }
     
}

