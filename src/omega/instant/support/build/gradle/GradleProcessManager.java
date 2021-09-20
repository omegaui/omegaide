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
import java.util.Scanner;

import omega.Screen;

import java.io.File;

import omega.utils.DataManager;
import omega.utils.RunPanel;
import omega.utils.IconManager;
public class GradleProcessManager {
	private static RunPanel printArea;
	public static boolean isGradleProject(){
		File settings = new File(Screen.getFileView().getProjectPath(), "settings.gradle");
		return settings.exists();
	}
	public static void init(){
		new Thread(()->{
			try{
				preparePrintArea();
				Screen.getScreen().getOperationPanel().addTab("Gradle Task", printArea, printArea::killProcess);
				if(!Screen.getFileView().getProjectManager().non_java){
					printArea.print("**Changing Project Type ...**");
					Screen.getFileView().getProjectManager().non_java = true;
					Screen.getScreen().manageTools(Screen.getFileView().getProjectManager());
					Screen.getFileView().getProjectManager().save();
					printArea.print("**Changing Project Type ... Done!**");
				}
				printArea.print("# Launching : gradle init");
				printArea.print("--------------------------------------------------");
				Process p = GradleProcessExecutor.init(new File(Screen.getFileView().getProjectPath()));
				printArea.setProcess(p);
				Scanner inputReader = GradleProcessExecutor.getInputScanner(p);
				Scanner errorReader = GradleProcessExecutor.getErrorScanner(p);
				new Thread(()->{
					boolean errorOccured = false;
					while(p.isAlive()){
						while(errorReader.hasNextLine()){
							printArea.print(errorReader.nextLine());
							errorOccured = true;
						}
					}
					errorReader.close();
					printArea.print("--------------------------------------------------");
					printArea.print(errorOccured ? "Error Occured During : gradle init" : "Completed Successfully : gradle init");
				}).start();
				while(p.isAlive()){
					while(inputReader.hasNextLine())
						printArea.print(inputReader.nextLine());
				}
				inputReader.close();
			}
			catch(Exception e){
				e.printStackTrace();
			}
			Screen.getProjectView().reload();
		}).start();
	}
	public static void run(){
		new Thread(()->{
			try{
                    Screen.getScreen().saveAllEditors();
				RunPanel printArea = new RunPanel();
				printArea.launchAsTerminal(GradleProcessManager::run, IconManager.fluentgradleImage, DataManager.getGradleCommand() + " run");
				Screen.getScreen().getOperationPanel().addTab("Gradle Task", printArea, printArea::killProcess);
				if(!Screen.getFileView().getProjectManager().non_java){
					printArea.print("**Changing Project Type ...**");
					Screen.getFileView().getProjectManager().non_java = true;
					Screen.getScreen().manageTools(Screen.getFileView().getProjectManager());
					Screen.getFileView().getProjectManager().save();
					printArea.print("**Changing Project Type ... Done!**");
				}
				printArea.print("# Launching : " + DataManager.getGradleCommand() +" run");
				printArea.print("--------------------------------------------------");
				Process p = GradleProcessExecutor.run(new File(Screen.getFileView().getProjectPath()));
				printArea.setProcess(p);
				Scanner inputReader = GradleProcessExecutor.getInputScanner(p);
				Scanner errorReader = GradleProcessExecutor.getErrorScanner(p);
				new Thread(()->{
					boolean errorOccured = false;
					while(p.isAlive()){
						while(errorReader.hasNextLine()){
							printArea.print(errorReader.nextLine());
							errorOccured = true;
						}
					}
					errorReader.close();
					printArea.print("--------------------------------------------------");
					printArea.print(errorOccured ? "Error Occured During : " + DataManager.getGradleCommand() + " run" : "Completed Successfully : " + DataManager.getGradleCommand() + " run");
				}).start();
				while(p.isAlive()){
					while(inputReader.hasNextLine())
						printArea.print(inputReader.nextLine());
				}
				inputReader.close();
			}
			catch(Exception e){
				e.printStackTrace();
			}
			Screen.getProjectView().reload();
		}).start();
	}
	public static void build(){
		new Thread(()->{
			try{
                    Screen.getScreen().saveAllEditors();
				RunPanel printArea = new RunPanel();
				printArea.setLogMode(true);
				Screen.getScreen().getOperationPanel().addTab("Gradle Task", printArea, printArea::killProcess);
				if(!Screen.getFileView().getProjectManager().non_java){
					printArea.print("**Changing Project Type ...**");
					Screen.getFileView().getProjectManager().non_java = true;
					Screen.getScreen().manageTools(Screen.getFileView().getProjectManager());
					Screen.getFileView().getProjectManager().save();
					printArea.print("**Changing Project Type ... Done!**");
				}
				printArea.print("# Launching : " + DataManager.getGradleCommand() + " build");
				printArea.print("--------------------------------------------------");
				Process p = GradleProcessExecutor.build(new File(Screen.getFileView().getProjectPath()));
				printArea.setProcess(p);
				Scanner inputReader = GradleProcessExecutor.getInputScanner(p);
				Scanner errorReader = GradleProcessExecutor.getErrorScanner(p);
				new Thread(()->{
					boolean errorOccured = false;
					while(p.isAlive()){
						while(errorReader.hasNextLine()){
							printArea.print(errorReader.nextLine());
							errorOccured = true;
						}
					}
					errorReader.close();
					printArea.print("--------------------------------------------------");
					printArea.print(errorOccured ? "Error Occured During : " + DataManager.getGradleCommand() + " build" : "Completed Successfully : " + DataManager.getGradleCommand() + " build");
				}).start();
				while(p.isAlive()){
					while(inputReader.hasNextLine())
						printArea.print(inputReader.nextLine());
				}
				inputReader.close();
			}
			catch(Exception e){
				e.printStackTrace();
			}
			Screen.getProjectView().reload();
		}).start();
	}
	public static void preparePrintArea(){
		if(printArea == null)
			printArea = new RunPanel();
	}
}

