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

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;
import omega.Screen;
import omega.database.DataBase;
import omega.utils.IconManager;
import omega.utils.JetRunPanel;

public class ProcessManager extends DataBase {

  public static LinkedList<ProcessData> dataSet = new LinkedList<>();

  public ProcessManager() {
    super(".omega-ide" + File.separator + ".processExecutionData");
    load();
  }

  public void load() {
    getDataSetNames()
      .forEach(set -> {
        LinkedList<String> cmds = new LinkedList<>();
        getEntries(set).forEach(entry -> cmds.add(entry.getValue()));
        dataSet.add(new ProcessData(set, cmds));
      });
  }

  @Override
  public void save() {
    clear();
    dataSet.forEach(data -> {
      data.executionCommand.forEach(cmd -> addEntry(data.fileExt, cmd));
    });
    super.save();
  }

  public void add(String ext, LinkedList<String> cmd) {
    dataSet.add(new ProcessData(ext, cmd));
  }

  public LinkedList<String> getExecutionCommand(File file) {
    String ext = file.getName().substring(file.getName().lastIndexOf('.'));
    for (ProcessData data : dataSet) {
      if (data.fileExt.equals(ext)) return data.executionCommand;
    }
    return new LinkedList<String>();
  }

  public synchronized void launch(File file) {
    new Thread(() -> {
      try {
        Screen.getScreen().saveAllEditors();
        LinkedList<String> cmd = (LinkedList<String>) getExecutionCommand(file)
          .clone();

        cmd.add(file.getName());

        String[] commandsAsArray = new String[cmd.size()];
        for (int i = 0; i < cmd.size(); i++) {
          commandsAsArray[i] = cmd.get(i);
        }

        JetRunPanel printArea = new JetRunPanel(
          false,
          commandsAsArray,
          file.getParentFile().getAbsolutePath()
        );
        printArea.launchAsTerminal(
          () -> launch(file),
          IconManager.fluentlaunchImage,
          "Re-launch"
        );
        printArea.print("# File Launched!");
        printArea.print(
          "-------------------------Execution Begins Here-------------------------"
        );
        printArea.start();

        Screen
          .getScreen()
          .getOperationPanel()
          .addTab(
            "Launch (" + file.getName() + ")",
            IconManager.fluentquickmodeonImage,
            printArea,
            printArea::killProcess
          );

        while (printArea.terminalPanel.process.isAlive());

        printArea.print(
          "-------------------------Execution Ends Here-------------------------"
        );
        printArea.print(
          "Launch finished with Exit Code " +
          printArea.terminalPanel.process.exitValue()
        );
      } catch (Exception e) {
        e.printStackTrace();
      }
    })
      .start();
  }
}
