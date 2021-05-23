/**
  * <one line to give the program's name and a brief idea of what it does.>
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
import omega.utils.IconManager;
import java.io.PrintWriter;
import java.util.Scanner;
import omega.Screen;
import omega.utils.PrintArea;
import java.io.File;
import java.util.LinkedList;
import omega.database.DataBase;
public class ProcessManager extends DataBase{
     public static LinkedList<ProcessData> dataSet = new LinkedList<>();
     public ProcessManager(){
     	super(".omega-ide" + File.separator + ".processExecutionData");
          load();
     }

     public void load(){
     	getDataSetNames().forEach(set->{
               dataSet.add(new ProcessData(set, getEntryAt(set, 0).getValue()));
	     });
     }

     @Override
     public void save(){
     	clear();
          dataSet.forEach(data->{
               addEntry(data.fileExt, data.executionCommand);
          });
          super.save();
     }

     public void add(String ext, String cmd){
     	dataSet.add(new ProcessData(ext, cmd));
     }

     public String getExecutionCommand(File file){
          String ext = file.getName().substring(file.getName().lastIndexOf('.'));
          for(ProcessData data : dataSet){
               if(data.fileExt.equals(ext))
                    return data.executionCommand;
          }
     	return null;
     }

     public synchronized void launch(File file){
          new Thread(()->{
          	try{
                    Screen.getScreen().saveAllEditors();
          		PrintArea printArea = new PrintArea();
                    printArea.launchAsTerminal(()->launch(file), IconManager.fluentlaunchImage, "Re-launch");
                    Screen.getScreen().getOperationPanel().addTab("Launch (" + file.getName() + ")", printArea, ()->printArea.stopProcess());
                    printArea.print("# Starting Shell ... ");
                    String shell = "sh";
                    if(File.pathSeparator.equals(";"))
                         shell = "cmd";
                    Process launchInShellProcess = new ProcessBuilder(shell).directory(file.getParentFile()).start();
                    printArea.print("# Shell Started!");
                    printArea.setProcess(launchInShellProcess);
                    printArea.print("-------------------------Execution Begins Here-------------------------");
                    
                    Scanner errorReader = new Scanner(launchInShellProcess.getErrorStream());
                    Scanner inputReader = new Scanner(launchInShellProcess.getInputStream());
                    
                    PrintWriter writer = new PrintWriter(launchInShellProcess.getOutputStream());
                    writer.println(getExecutionCommand(file) + " " + file.getName());
                    writer.println("exit");
                    writer.flush();
                    
                    new Thread(()->{
                         String statusX = "No Errors";
                         while(launchInShellProcess.isAlive()) {
                              while(errorReader.hasNextLine()) {
                                   statusX = "Errors";
                                   printArea.print(errorReader.nextLine());
                              }
                         }
                         printArea.print("-------------------------Execution Ends Here-------------------------");
                         printArea.print("Launched finished with \"" + statusX + "\"");
                         errorReader.close();
                    }).start();

                    while(launchInShellProcess.isAlive()) {
                         while(inputReader.hasNextLine()) {
                              printArea.print(inputReader.nextLine());
                         }
                    }
                    inputReader.close();
          	}
          	catch(Exception e){ 
          		e.printStackTrace();
          	}
          }).start();
     }
}

