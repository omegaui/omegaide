/*
 * GradleProcessManager
 * Copyright (C) 2022 Omega UI

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
import omega.io.AppDataManager;
import omega.io.IconManager;

import omega.ui.panel.JetRunPanel;

import omega.Screen;

import java.io.File;
public class GradleProcessManager {

	private static String ext = File.pathSeparator.equals(":") ? "" : ".bat";

	public static boolean isGradleProject(){
		File settings = new File(Screen.getProjectFile().getProjectPath(), "settings.gradle");
		return settings.exists() || new File(Screen.getProjectFile().getProjectPath(), "settings.gradle.kts").exists();
	}

	public static void init(){
		new Thread(()->{
			try{
				JetRunPanel printArea = new JetRunPanel(false, new String[]{"gradle" + ext, "init"}, Screen.getProjectFile().getProjectPath());
				printArea.print("# Executing : gradle init");
				printArea.print("--------------------------------------------------");
				printArea.start();
				Screen.getScreen().getOperationPanel().addTab("Gradle Task", IconManager.fluentquickmodeonImage, printArea, printArea::killProcess);
				if(!Screen.getProjectFile().getProjectManager().isLanguageTagNonJava()){
					Screen.getProjectFile().getProjectManager().setLanguageTag(-1);
					Screen.getScreen().manageTools(Screen.getProjectFile().getProjectManager());
					Screen.getProjectFile().getProjectManager().save();
				}
				while(printArea.terminalPanel.process.isAlive());
					printArea.print("--------------------------------------------------");
				printArea.print("Finished with Exit Code " + printArea.terminalPanel.process.exitValue());
			}
			catch(Exception e){
				e.printStackTrace();
			}
			Screen.getProjectFile().getFileTreePanel().refresh();
		}).start();
	}

	public static void run(){
		new Thread(()->{
			try{
				Screen.getScreen().saveAllEditors();

				if(!Screen.getProjectFile().getProjectManager().isLanguageTagNonJava()){
					Screen.getProjectFile().getProjectManager().setLanguageTag(-1);
					Screen.getScreen().manageTools(Screen.getProjectFile().getProjectManager());
					Screen.getProjectFile().getProjectManager().save();
				}


				JetRunPanel printArea = new JetRunPanel(false, new String[]{AppDataManager.getGradleCommand() + ext, "run"}, Screen.getProjectFile().getProjectPath());
				printArea.launchAsTerminal(GradleProcessManager::run, IconManager.fluentgradleImage, AppDataManager.getGradleCommand() + " run");

				printArea.print("# Executing : " + AppDataManager.getGradleCommand() +" run");
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
			Screen.getProjectFile().getFileTreePanel().refresh();
		}).start();
	}

	public static void build(){
		new Thread(()->{
			try{
				Screen.getScreen().saveAllEditors();
				if(!Screen.getProjectFile().getProjectManager().isLanguageTagNonJava()){
					Screen.getProjectFile().getProjectManager().setLanguageTag(-1);
					Screen.getScreen().manageTools(Screen.getProjectFile().getProjectManager());
					Screen.getProjectFile().getProjectManager().save();
				}

				JetRunPanel printArea = new JetRunPanel(false, new String[]{AppDataManager.getGradleCommand() + ext, "build"}, Screen.getProjectFile().getProjectPath());
				printArea.setLogMode(true);
				printArea.print("# Executing : " + AppDataManager.getGradleCommand() +" build");
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
			Screen.getProjectFile().getFileTreePanel().refresh();
		}).start();
	}

}

