/*
 * UniversalSettingsWizard
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

package omega.instant.support.universal;

import omega.Screen;
import omega.instant.support.ArgumentWindow;
import omega.ui.dialog.FileSelectionDialog;
import omegaui.component.FlexPanel;
import omegaui.component.TextComp;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.LinkedList;

import static omega.io.UIManager.*;

public class UniversalSettingsWizard extends JDialog {
    private TextComp titleComp;

    private TextComp runField;
    private TextComp compileField;
    private TextComp runWorkDirComp;
    private TextComp compileWorkDirComp;

    private TextComp listMakerComp;

    private JScrollPane scrollPane;
    private FlexPanel panel;
    private int block = 0;

    private ArgumentWindow commandWindow;
    private DynamicListMaker listMaker;

    public UniversalSettingsWizard(Window window) {
        super(window, "Universal Settings Wizard");
        setModal(true);
        setUndecorated(true);
        setResizable(false);
        JPanel panel = new JPanel(null);
        panel.setBackground(c2);
        setContentPane(panel);
        setResizable(false);
        setSize(600, 180);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        init();
    }

    public void init() {
        commandWindow = new ArgumentWindow(Screen.getScreen());

        FileSelectionDialog fc = new FileSelectionDialog(this);
        fc.setTitle("Select Working Directory");

        titleComp = new TextComp("Universal Settings Wizard", TOOLMENU_COLOR3, c2, c2, null);
        titleComp.setBounds(0, 0, getWidth(), 30);
        titleComp.setFont(PX14);
        titleComp.attachDragger(this);
        titleComp.setArc(0, 0);
        titleComp.setClickable(false);
        add(titleComp);

        TextComp label0 = new TextComp("Run Command", TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR3, null);
        label0.setBounds(10, 50, 150, 25);
        label0.setFont(PX14);
        label0.setClickable(false);
        add(label0);

        runField = new TextComp("", "Click to Set Run Command", TOOLMENU_GRADIENT, back1, TOOLMENU_COLOR2, () -> {
            commandWindow.loadView(Screen.getProjectFile().getArgumentManager().run_time_args);
            commandWindow.setVisible(true);
            if (commandWindow.isSaved()) {
                Screen.getProjectFile().getArgumentManager().run_time_args = commandWindow.getCommand();
                Screen.getProjectFile().getArgumentManager().save();
                runField.setText(Screen.getProjectFile().getArgumentManager().getRunCommand());
            }
        });
        runField.setBounds(180, 50, getWidth() - 320, 25);
        runField.setFont(PX14);
        add(runField);

        runWorkDirComp = new TextComp("Working Directory", "Choose Working Directory When Running, Default : Project", TOOLMENU_COLOR1_SHADE, back1, TOOLMENU_COLOR2, () -> {
            fc.setCurrentDirectory(new File(omega.Screen.getProjectFile().getProjectPath()));
            LinkedList<File> selections = fc.selectDirectories();
            if (!selections.isEmpty()) {
                runWorkDirComp.setToolTipText(selections.get(0).getAbsolutePath());
                runWorkDirComp.setText(runWorkDirComp.getToolTipText().substring(runWorkDirComp.getToolTipText().lastIndexOf(File.separator) + 1));
            }
        });
        runWorkDirComp.setLocation(runField.getX() + runField.getWidth() + 5, 50);
        runWorkDirComp.setSize(getWidth() - runWorkDirComp.getX() - 10, 25);
        runWorkDirComp.setFont(PX14);
        add(runWorkDirComp);

        TextComp label1 = new TextComp("Compile Command", TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR3, null);
        label1.setBounds(10, 100, 150, 25);
        label1.setFont(PX14);
        label1.setClickable(false);
        add(label1);

        compileField = new TextComp("", "Click to Set Compile Command", TOOLMENU_GRADIENT, back1, TOOLMENU_COLOR2, () -> {
            commandWindow.loadView(Screen.getProjectFile().getArgumentManager().compile_time_args);
            commandWindow.setVisible(true);
            if (commandWindow.isSaved()) {
                Screen.getProjectFile().getArgumentManager().compile_time_args = commandWindow.getCommand();
                Screen.getProjectFile().getArgumentManager().save();
                compileField.setText(Screen.getProjectFile().getArgumentManager().getCompileCommand());
            }
        });
        compileField.setBounds(180, 100, getWidth() - 320, 25);
        compileField.setFont(PX14);
        add(compileField);

        compileWorkDirComp = new TextComp("Working Directory", "Choose Working Directory When Compiling, Default : Project", TOOLMENU_COLOR1_SHADE, back1, TOOLMENU_COLOR2, () -> {
            fc.setCurrentDirectory(new File(Screen.getProjectFile().getProjectPath()));
            LinkedList<File> selections = fc.selectDirectories();
            if (!selections.isEmpty()) {
                compileWorkDirComp.setToolTipText(selections.get(0).getAbsolutePath());
                compileWorkDirComp.setText(compileWorkDirComp.getToolTipText().substring(compileWorkDirComp.getToolTipText().lastIndexOf(File.separator) + 1));
            }
        });
        compileWorkDirComp.setLocation(runField.getX() + runField.getWidth() + 5, 100);
        compileWorkDirComp.setSize(getWidth() - compileWorkDirComp.getX() - 10, 25);
        compileWorkDirComp.setFont(PX14);
        add(compileWorkDirComp);

        TextComp closeComp = new TextComp("Close", TOOLMENU_COLOR2_SHADE, back1, TOOLMENU_COLOR2, this::dispose);
        closeComp.setBounds(getWidth() / 2 - 100 - 110, 150, 100, 25);
        closeComp.setFont(PX14);
        add(closeComp);

        listMakerComp = new TextComp("Add a List Maker", TOOLMENU_COLOR4_SHADE, back2, TOOLMENU_COLOR4, this::addList);
        listMakerComp.setBounds(getWidth() / 2 - 100, 150, 200, 25);
        listMakerComp.setFont(PX14);
        add(listMakerComp);

        TextComp applyComp = new TextComp("Apply", TOOLMENU_COLOR2_SHADE, back1, TOOLMENU_COLOR2, this::apply);
        applyComp.setBounds(getWidth() / 2 - 100 + 210, 150, 100, 25);
        applyComp.setFont(PX14);
        add(applyComp);

        scrollPane = new JScrollPane(panel = new FlexPanel(null, c2, c2));
        scrollPane.setBounds(0, 200, 600, 300);
        scrollPane.setBackground(c2);
        panel.setArc(0, 0);
        scrollPane.setBorder(null);
        add(scrollPane);

        listMaker = new DynamicListMaker();
    }

    public void addList() {
        dispose();
        listMaker.setVisible(true);
    }

    public void apply() {
        Screen.getProjectFile().getArgumentManager().compileDir = compileWorkDirComp.getToolTipText();
        Screen.getProjectFile().getArgumentManager().runDir = runWorkDirComp.getToolTipText();
        listMaker.loadAllToArgsManager();
    }

    @Override
    public void setVisible(boolean value) {
        if (value) {
            block = 0;
            compileField.setText(!Screen.isNotNull(Screen.getProjectFile().getArgumentManager().getCompileCommand()) ? "Click to Enter Compile Command" : Screen.getProjectFile().getArgumentManager().getCompileCommand());
            runField.setText(!Screen.isNotNull(Screen.getProjectFile().getArgumentManager().getRunCommand()) ? "Click to Enter Run Command" : Screen.getProjectFile().getArgumentManager().getRunCommand());
            compileWorkDirComp.setToolTipText(Screen.getProjectFile().getArgumentManager().compileDir.equals("") ? "Working Directory" : Screen.getProjectFile().getArgumentManager().compileDir);
            runWorkDirComp.setToolTipText(Screen.getProjectFile().getArgumentManager().runDir.equals("") ? "Working Directory" : Screen.getProjectFile().getArgumentManager().runDir);
            try {
                compileWorkDirComp.setText(compileWorkDirComp.getToolTipText().substring(compileWorkDirComp.getToolTipText().lastIndexOf(File.separator) + 1));
                runWorkDirComp.setText(runWorkDirComp.getToolTipText().substring(runWorkDirComp.getToolTipText().lastIndexOf(File.separator) + 1));
            } catch (Exception e) {

            }
            setLocationRelativeTo(null);
        }
        super.setVisible(value);
    }
}
