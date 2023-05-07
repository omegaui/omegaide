/*
 * DynamicListPanel
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
import omega.ui.dialog.FileSelectionDialog;
import omegaui.component.NoCaretField;
import omegaui.component.SwitchComp;
import omegaui.component.TextComp;

import javax.swing.*;
import java.io.File;
import java.util.LinkedList;

import static omega.io.UIManager.*;

public class DynamicListPanel extends JPanel {

    public NoCaretField extField;
    public NoCaretField containerField;

    public TextComp dirComp;

    public SwitchComp quoteComp;
    public SwitchComp dynamicListComp;

    public DynamicListPanel(String ext, String container, String dir, boolean quoted, boolean dynamicMode) {
        this();
        extField.setText(ext);
        containerField.setText(container);
        dirComp.setToolTipText(dir);
        dirComp.setText(new File(dir).getName());
        quoteComp.setOn(quoted);
        dynamicListComp.setOn(dynamicMode);
        quoteComp.setToolTipText("Surround file paths within double quotes : " + (quoted ? "ON" : "OFF"));
        dynamicListComp.setToolTipText("Create List from ONLY Active Editors : " + (dynamicMode ? "ON" : "OFF"));
    }

    public DynamicListPanel() {
        super(null);
        setBackground(back2);
        init();
    }

    public void init() {
        FileSelectionDialog fc = new FileSelectionDialog(Screen.getUniversalSettingsView());
        fc.setTitle("Select Working Directory");

        extField = new NoCaretField("", "File Extension", TOOLMENU_COLOR2, c2, TOOLMENU_COLOR3);
        extField.setBounds(10, 20, 150, 30);
        extField.setFont(PX14);
        add(extField);

        containerField = new NoCaretField("", "Container Name", TOOLMENU_COLOR3, c2, TOOLMENU_COLOR2);
        containerField.setBounds(10, 60, 150, 30);
        containerField.setFont(PX14);
        add(containerField);

        quoteComp = new SwitchComp(TOOLMENU_COLOR1, TOOLMENU_COLOR3, TOOLMENU_COLOR2_SHADE, (value) -> {
            quoteComp.setToolTipText("Surround file paths within double quotes : " + (value ? "ON" : "OFF"));
        });
        quoteComp.setBounds(200, 20, 70, 30);
        quoteComp.setInBallColor(glow);
        quoteComp.setToolTipText("Surround file paths within double quotes : OFF");
        add(quoteComp);

        dynamicListComp = new SwitchComp(TOOLMENU_COLOR1, TOOLMENU_COLOR3, TOOLMENU_COLOR2_SHADE, (value) -> {
            dynamicListComp.setToolTipText("Create List from ONLY Active Editors : " + (value ? "ON" : "OFF"));
        });
        dynamicListComp.setBounds(200, 60, 70, 30);
        dynamicListComp.setInBallColor(glow);
        dynamicListComp.setToolTipText("Create List from ONLY Active Editors : OFF");
        add(dynamicListComp);

        dirComp = new TextComp(Screen.getProjectFile().getProjectName(), "Directory to be searched recursively for files of provided extension!", TOOLMENU_COLOR6_SHADE, c2, TOOLMENU_COLOR6, () -> {
            fc.setCurrentDirectory(new File(Screen.getProjectFile().getProjectPath()));
            LinkedList<File> selections = fc.selectDirectories();
            if (!selections.isEmpty()) {
                dirComp.setToolTipText(selections.get(0).getAbsolutePath());
                dirComp.setText(dirComp.getToolTipText().substring(dirComp.getToolTipText().lastIndexOf(File.separator) + 1));
            }
        });
        dirComp.setBounds(10, 100, 270, 30);
        dirComp.setFont(PX14);
        dirComp.setToolTipText(Screen.getProjectFile().getProjectPath());
        dirComp.setArc(0, 0);
        add(dirComp);
    }

    public boolean validateListPanel() {
        boolean passed = true;
        if (getFileExtension().equals("")) {
            extField.notify("File Extension Required!");
            passed = false;
        }
        if (getContainerName().equals("")) {
            containerField.notify("Container Required!");
            passed = false;
        }
        if (getWorkingDirectory().equals("")) {
            dirComp.setText("Directory Required");
            passed = false;
        }
        return passed && isEnabled();
    }

    public String getFileExtension() {
        return extField.getText();
    }

    public String getContainerName() {
        return containerField.getText();
    }

    public boolean isQuoted() {
        return quoteComp.isOn();
    }

    public boolean isDynamic() {
        return dynamicListComp.isOn();
    }

    public String getWorkingDirectory() {
        return dirComp.getToolTipText();
    }
}
