/**
  * GradleProcessManager
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

package omega.instant.support.build.gradle;
import omega.io.DataManager;
import omega.io.IconManager;

import omega.ui.panel.JetRunPanel;

import java.util.Scanner;

import omega.Screen;

import java.io.File;
public class GradleProcessManager {
	
	private static String ext = File.pathSeparator.equals(":") ? "" : ".bat";
	
	public static boolean isGradleProject(){
		File settings = new File(Screen.getFileView().getProjectPath(), "settings.gradle");
		return settings.exists();
	}
	public static void init(){
		new Thread(()->{
			try{
				JetRunPanel printArea = new JetRunPanel(false, new String[]{"gradle" + ext, "init"}, Screen.getFileView().getProjectPath());
				printArea.print("# Executing : gradle init");
				printArea.print("--------------------------------------------------");
				printArea.start();
				Screen.getScreen().getOperationPanel().addTab("Gradle Task", IconManager.fluentquickmodeonImage, printArea, printArea::killProcess);
				if(!Screen.getFileView().getProjectManager().non_java){
					Screen.getFileView().getProjectManager().non_java = true;
					Screen.getScreen().manageTools(Screen.getFileView().getProjectManager());
					Screen.getFileView().getProjectManager().save();
				}
				while(printArea.terminalPanel.process.isAlive());
				printArea.print("--------------------------------------------------");
				printArea.print("Finished with Exit Code " + printArea.terminalPanel.process.exitValue());
			}
			catch(Exception e){
				e.printStackTrace();
			}
			Screen.getFileView().getFileTreePanel().refresh();
		}).start();
	}
	public static void run(){
		new Thread(()->{
			try{
                Screen.getScreen().saveAllEditors();
				
				if(!Screen.getFileView().getProjectManager().non_java){
					Screen.getFileView().getProjectManager().non_java = true;
					Screen.getScreen().manageTools(Screen.getFileView().getProjectManager());
					Screen.getFileView().getProjectManager().save();
				}
				
				
				JetRunPanel printArea = new JetRunPanel(false, new String[]{DataManager.getGradleCommand() + ext, "run"}, Screen.getFileView().getProjectPath());
				printArea.launchAsTerminal(GradleProcessManager::run, IconManager.fluentgradleImage, DataManager.getGradleCommand() + " run");
				
				printArea.print("# Executing : " + DataManager.getGradleCommand() +" run");
				printArea.print("--------------------------------------------------");

				printArea.start();
				
				Screen.getScreen().getOperationPanel().addTab("Gradle Task", IconManager.fluentquickmodeonImage, printArea, printArea::killProcess);
				
				while(printArea.terminalPanel.process.isAlive());
				printArea.print("--------------------------------------------------");
				printArea.print("Finished with Exit Code " + printArea.terminalPanel.process.exitValue());
				
			}
			catch(Exception e){
				e.printStackTrace();
			}
			Screen.getFileView().getFileTreePanel().refresh();
		}).start();
	}
	public static void build(){
		new Thread(()->{
			try{
                Screen.getScreen().saveAllEditors();
				if(!Screen.getFileView().getProjectManager().non_java){
					Screen.getFileView().getProjectManager().non_java = true;
					Screen.getScreen().manageTools(Screen.getFileView().getProjectManager());
					Screen.getFileView().getProjectManager().save();
				}
				
				JetRunPanel printArea = new JetRunPanel(false, new String[]{DataManager.getGradleCommand() + ext, "build"}, Screen.getFileView().getProjectPath());
				printArea.setLogMode(true);
				printArea.print("# Executing : " + DataManager.getGradleCommand() +" build");
				printArea.print("--------------------------------------------------");
				
				printArea.start();
				
				Screen.getScreen().getOperationPanel().addTab("Gradle Task", IconManager.fluentquickmodeonImage, printArea, printArea::killProcess);
				
				while(printArea.terminalPanel.process.isAlive());
				
				printArea.print("--------------------------------------------------");
				printArea.print("Finished with Exit Code " + printArea.terminalPanel.process.exitValue());
			}
			catch(Exception e){
				e.printStackTrace();
			}
			Screen.getFileView().getFileTreePanel().refresh();
		}).start();
	}
}

