/**
 * Universal ProcessManager
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

package omega.instant.support.universal;
import omega.io.IconManager;
import omega.io.TabData;

import omega.ui.panel.JetRunPanel;
import omega.ui.panel.OperationPane;

import omegaui.dynamic.database.DataBase;

import omega.Screen;

import java.io.File;
import java.io.PrintWriter;

import java.util.LinkedList;
import java.util.Scanner;
import java.util.Arrays;
public class ProcessManager extends DataBase{
	public static LinkedList<ProcessData> dataSet = new LinkedList<>();
	public ProcessManager(){
		super(".omega-ide" + File.separator + ".processExecutionData");
		load();
	}

	public void load(){
		getDataSetNames().forEach(set->{
			LinkedList<String> cmds = new LinkedList<>();
			getEntries(set).forEach(entry->cmds.add(entry.getValue()));
			dataSet.add(new ProcessData(set, cmds));
		});
	}

	@Override
	public void save(){
		clear();
		dataSet.forEach(data->{
			data.executionCommand.forEach(cmd->addEntry(data.fileExt, cmd));
		});
		super.save();
	}

	public void add(String ext, LinkedList<String> cmd){
		dataSet.add(new ProcessData(ext, cmd));
	}

	public LinkedList<String> getExecutionCommand(File file){
		String ext = file.getName().substring(file.getName().lastIndexOf('.'));
		for(ProcessData data : dataSet){
			if(data.fileExt.equals(ext))
				return data.executionCommand;
		}
		return new LinkedList<String>();
	}

	public synchronized void launch(File file){
		new Thread(()->{
			try{
				Screen.getScreen().saveAllEditors();
				LinkedList<String> cmd = (LinkedList<String>)getExecutionCommand(file).clone();

				cmd.add(file.getName());

				String command = "";

				String[] commandsAsArray = new String[cmd.size()];
				for(int i = 0; i < cmd.size(); i++){
					commandsAsArray[i] = cmd.get(i);
					command += cmd.get(i) + " ";
				}

				JetRunPanel printArea = new JetRunPanel(false, commandsAsArray, file.getParentFile().getAbsolutePath());
				printArea.launchAsTerminal(()->launch(file), IconManager.fluentlaunchImage, "Re-launch");
				printArea.print("File Launched!");
				printArea.print("Execution Command : " + command);
				printArea.print("..................................................");

				String name = "Launch (" + file.getName();
				int count = OperationPane.count(name);
				if(count > -1)
					name += " " + count;
				name =  name + ")";

				Screen.getScreen().getOperationPanel().addTab(name, IconManager.fluentquickmodeonImage, printArea, printArea::killProcess);

				TabData currentTabData = Screen.getScreen().getOperationPanel().getTabData(printArea);
				currentTabData.getTabIconComp().setImage(null);
				currentTabData.getTabIconComp().setGifImage(IconManager.fluentloadinginfinityGif);

				printArea.terminalPanel.setOnProcessExited(()->{
					currentTabData.getTabIconComp().setImage(IconManager.fluentquickmodeonImage);
					currentTabData.getTabIconComp().setGifImage(null);
				});

				printArea.start();

			}
			catch(Exception e){
				e.printStackTrace();
			}
		}).start();
	}
}

