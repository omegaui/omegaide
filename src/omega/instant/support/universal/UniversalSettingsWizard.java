/**
* UniversalSettingsWizard
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

import static omega.utils.UIManager.*;

import java.awt.Dimension;
import java.awt.Window;
import java.io.File;
import java.util.LinkedList;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import omega.Screen;
import omega.comp.FlexPanel;
import omega.comp.NoCaretField;
import omega.comp.TextComp;
import omega.instant.support.ArgumentWindow;
import omega.utils.FileSelectionDialog;

public class UniversalSettingsWizard extends JDialog {

  private TextComp titleComp;

  private TextComp runField;
  private TextComp compileField;
  private TextComp runWorkDirComp;
  private TextComp compileWorkDirComp;

  private TextComp listMakerComp;
  public LinkedList<ListMaker> lists = new LinkedList<>();

  private JScrollPane scrollPane;
  private FlexPanel panel;
  private int block = 0;

  private ArgumentWindow commandWindow;

  public UniversalSettingsWizard(Window window) {
    super(window, "Universal Settings Wizard");
    setModal(true);
    setUndecorated(true);
    setResizable(false);
    JPanel panel = new JPanel(null);
    panel.setBackground(c2);
    setContentPane(panel);
    setResizable(false);
    setSize(600, 500);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    init();
  }

  public void init() {
    commandWindow = new ArgumentWindow(Screen.getScreen());

    FileSelectionDialog fc = new FileSelectionDialog(this);
    fc.setTitle("Select Working Directory");

    titleComp =
      new TextComp("Universal Settings Wizard", TOOLMENU_COLOR3, c2, c2, null);
    titleComp.setBounds(0, 0, getWidth(), 30);
    titleComp.setFont(PX14);
    titleComp.attachDragger(this);
    titleComp.setArc(0, 0);
    titleComp.setClickable(false);
    add(titleComp);

    TextComp label0 = new TextComp(
      "Run Command",
      TOOLMENU_COLOR3_SHADE,
      c2,
      TOOLMENU_COLOR3,
      null
    );
    label0.setBounds(10, 50, 150, 25);
    label0.setFont(PX14);
    label0.setClickable(false);
    add(label0);

    runField =
      new TextComp(
        "",
        "Click to Set Run Command",
        TOOLMENU_GRADIENT,
        back1,
        TOOLMENU_COLOR2,
        () -> {
          commandWindow.loadView(
            Screen.getFileView().getArgumentManager().run_time_args
          );
          commandWindow.setVisible(true);
          if (commandWindow.isSaved()) {
            Screen.getFileView().getArgumentManager().run_time_args =
              commandWindow.getCommand();
            Screen.getFileView().getArgumentManager().save();
            runField.setText(
              Screen.getFileView().getArgumentManager().getRunCommand()
            );
          }
        }
      );
    runField.setBounds(180, 50, getWidth() - 320, 25);
    runField.setFont(PX14);
    add(runField);

    runWorkDirComp =
      new TextComp(
        "Working Directory",
        "Choose Working Directory When Building",
        TOOLMENU_COLOR1_SHADE,
        back1,
        TOOLMENU_COLOR2,
        () -> {
          fc.setCurrentDirectory(
            new File(omega.Screen.getFileView().getProjectPath())
          );
          LinkedList<File> selections = fc.selectDirectories();
          if (!selections.isEmpty()) {
            runWorkDirComp.setToolTipText(selections.get(0).getAbsolutePath());
            runWorkDirComp.setText(
              runWorkDirComp
                .getToolTipText()
                .substring(
                  runWorkDirComp.getToolTipText().lastIndexOf(File.separator) +
                  1
                )
            );
          }
        }
      );
    runWorkDirComp.setLocation(runField.getX() + runField.getWidth() + 5, 50);
    runWorkDirComp.setSize(getWidth() - runWorkDirComp.getX() - 10, 25);
    runWorkDirComp.setFont(PX14);
    add(runWorkDirComp);

    TextComp label1 = new TextComp(
      "Compile Command",
      TOOLMENU_COLOR3_SHADE,
      c2,
      TOOLMENU_COLOR3,
      null
    );
    label1.setBounds(10, 100, 150, 25);
    label1.setFont(PX14);
    label1.setClickable(false);
    add(label1);

    compileField =
      new TextComp(
        "",
        "Click to Set Compile Command",
        TOOLMENU_GRADIENT,
        back1,
        TOOLMENU_COLOR2,
        () -> {
          commandWindow.loadView(
            Screen.getFileView().getArgumentManager().compile_time_args
          );
          commandWindow.setVisible(true);
          if (commandWindow.isSaved()) {
            Screen.getFileView().getArgumentManager().compile_time_args =
              commandWindow.getCommand();
            Screen.getFileView().getArgumentManager().save();
            compileField.setText(
              Screen.getFileView().getArgumentManager().getCompileCommand()
            );
          }
        }
      );
    compileField.setBounds(180, 100, getWidth() - 320, 25);
    compileField.setFont(PX14);
    add(compileField);

    compileWorkDirComp =
      new TextComp(
        "Working Directory",
        "Choose Working Directory When Running",
        TOOLMENU_COLOR1_SHADE,
        back1,
        TOOLMENU_COLOR2,
        () -> {
          fc.setCurrentDirectory(
            new File(Screen.getFileView().getProjectPath())
          );
          LinkedList<File> selections = fc.selectDirectories();
          if (!selections.isEmpty()) {
            compileWorkDirComp.setToolTipText(
              selections.get(0).getAbsolutePath()
            );
            compileWorkDirComp.setText(
              compileWorkDirComp
                .getToolTipText()
                .substring(
                  compileWorkDirComp
                    .getToolTipText()
                    .lastIndexOf(File.separator) +
                  1
                )
            );
          }
        }
      );
    compileWorkDirComp.setLocation(
      runField.getX() + runField.getWidth() + 5,
      100
    );
    compileWorkDirComp.setSize(getWidth() - compileWorkDirComp.getX() - 10, 25);
    compileWorkDirComp.setFont(PX14);
    add(compileWorkDirComp);

    TextComp closeComp = new TextComp(
      "Close",
      TOOLMENU_COLOR2_SHADE,
      back1,
      TOOLMENU_COLOR2,
      this::dispose
    );
    closeComp.setBounds(getWidth() / 2 - 100 - 110, 150, 100, 25);
    closeComp.setFont(PX14);
    add(closeComp);

    listMakerComp =
      new TextComp(
        "Add a List Maker",
        TOOLMENU_COLOR4_SHADE,
        back2,
        TOOLMENU_COLOR4,
        this::addList
      );
    listMakerComp.setBounds(getWidth() / 2 - 100, 150, 200, 25);
    listMakerComp.setFont(PX14);
    add(listMakerComp);

    TextComp applyComp = new TextComp(
      "Apply",
      TOOLMENU_COLOR2_SHADE,
      back1,
      TOOLMENU_COLOR2,
      this::apply
    );
    applyComp.setBounds(getWidth() / 2 - 100 + 210, 150, 100, 25);
    applyComp.setFont(PX14);
    add(applyComp);

    scrollPane = new JScrollPane(panel = new FlexPanel(null, c2, c2));
    scrollPane.setBounds(0, 200, 600, 300);
    scrollPane.setBackground(c2);
    panel.setArc(0, 0);
    scrollPane.setBorder(null);
    add(scrollPane);
  }

  public void addList() {
    ListMaker listMaker = new ListMaker();
    listMaker.setLocation(0, block);
    panel.add(listMaker);
    lists.add(listMaker);
    block += 30;
    panel.setPreferredSize(new Dimension(600, block));

    setSize(
      600,
      lists.isEmpty()
        ? 180
        : (200 + (lists.size() > 6 ? 300 : (lists.size() * 30)))
    );
    setLocationRelativeTo(null);
    if (getHeight() >= 500) {
      scrollPane
        .getVerticalScrollBar()
        .setValue(scrollPane.getVerticalScrollBar().getMaximum());
      scrollPane.getVerticalScrollBar().setVisible(true);
      panel.repaint();
    }
  }

  public void apply() {
    boolean checkPassed = true;
    for (ListMaker list : lists) {
      if (list.isEnabled()) {
        if (!list.validateListMaker()) checkPassed = false;
      }
    }
    if (!checkPassed) return;
    Screen.getFileView().getArgumentManager().compileDir =
      compileWorkDirComp.getToolTipText();
    Screen.getFileView().getArgumentManager().runDir =
      runWorkDirComp.getToolTipText();
    Screen.getFileView().getArgumentManager().units.clear();
    lists.forEach(list -> {
      if (list.validateListMaker() && list.isEnabled()) {
        Screen.getFileView().getArgumentManager().units.add(list);
      }
    });
    Screen.getFileView().getArgumentManager().save();
  }

  @Override
  public void setVisible(boolean value) {
    if (value) {
      block = 0;
      lists.forEach(panel::remove);
      lists.clear();
      compileField.setText(
        !Screen.isNotNull(
            Screen.getFileView().getArgumentManager().getCompileCommand()
          )
          ? "Click to Enter Compile Command"
          : Screen.getFileView().getArgumentManager().getCompileCommand()
      );
      runField.setText(
        !Screen.isNotNull(
            Screen.getFileView().getArgumentManager().getRunCommand()
          )
          ? "Click to Enter Run Command"
          : Screen.getFileView().getArgumentManager().getRunCommand()
      );
      compileWorkDirComp.setToolTipText(
        Screen.getFileView().getArgumentManager().compileDir.equals("")
          ? "Working Directory"
          : Screen.getFileView().getArgumentManager().compileDir
      );
      runWorkDirComp.setToolTipText(
        Screen.getFileView().getArgumentManager().runDir.equals("")
          ? "Working Directory"
          : Screen.getFileView().getArgumentManager().runDir
      );
      try {
        compileWorkDirComp.setText(
          compileWorkDirComp
            .getToolTipText()
            .substring(
              compileWorkDirComp.getToolTipText().lastIndexOf(File.separator) +
              1
            )
        );
        runWorkDirComp.setText(
          runWorkDirComp
            .getToolTipText()
            .substring(
              runWorkDirComp.getToolTipText().lastIndexOf(File.separator) + 1
            )
        );
      } catch (Exception e) {}
      Screen
        .getFileView()
        .getArgumentManager()
        .units.forEach(unit -> {
          unit.setLocation(0, block);
          panel.add(unit);
          lists.add(unit);
          block += 30;
        });
      setSize(
        600,
        lists.isEmpty()
          ? 180
          : (200 + (lists.size() > 6 ? 500 : (lists.size() * 30)))
      );
      setLocationRelativeTo(null);
    }
    super.setVisible(value);
  }
}
