/**
 * WorkspaceSelector
 * Copyright (C) 2021 Omega UI
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package omega.ui.dialog;

import omega.Screen;
import omega.io.AppDataManager;
import omegaui.component.TextComp;

import javax.swing.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.util.LinkedList;

import static omega.io.UIManager.*;

public class WorkspaceSelector extends JDialog {
    public WorkspaceSelector(Screen screen) {
        super(screen);
        setUndecorated(true);

        JPanel panel = new JPanel(null);
        panel.setBackground(c2);
        setContentPane(panel);

        setLayout(null);
        setBackground(c2);
        setTitle("Select Workspace Directory");
        setModal(true);
        setSize(400, 120);
        setLocationRelativeTo(null);
        setShape(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        init();
    }

    public void init() {
        TextComp closeComp = new TextComp("x", TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR2, () -> dispose());
        closeComp.setBounds(0, 0, 30, 30);
        closeComp.setFont(PX14);
        closeComp.setArc(0, 0);
        add(closeComp);

        TextComp titleComp = new TextComp("Select Workspace Directory", c2, TOOLMENU_GRADIENT, glow, () -> setVisible(false));
        titleComp.setBounds(30, 0, getWidth() - 30, 30);
        titleComp.setClickable(false);
        titleComp.setFont(PX14);
        titleComp.setArc(0, 0);
        titleComp.attachDragger(this);
        add(titleComp);

        JTextField textField = new JTextField(AppDataManager.getWorkspace().equals("") ? "e.g : Documents/Omega Projects" : AppDataManager.getWorkspace());
        textField.setBounds(20, 50, getWidth() - 30, 30);
        textField.setFont(PX14);
        textField.setBackground(c2);
        textField.setForeground(glow);
        textField.setEditable(false);
        add(textField);

        FileSelectionDialog fs = new FileSelectionDialog(this);
        fs.setTitle("Choose only one directory");

        TextComp chooseComp = new TextComp("Browse", TOOLMENU_COLOR1_SHADE, c2, TOOLMENU_COLOR1, () -> {
            LinkedList<File> files = fs.selectDirectories();
            new Thread(() -> {
                if (!files.isEmpty()) {
                    AppDataManager.setWorkspace(files.get(0).getAbsolutePath());
                    textField.setText(AppDataManager.getWorkspace());
                    setTitle("Lets Proceed Forward");
                    titleComp.color1 = TOOLMENU_COLOR1_SHADE;
                    titleComp.setClickable(true);
                    titleComp.setText(getTitle());
                }
            }).start();
        });
        chooseComp.setBounds(getWidth() / 2 - 30, 95, 60, 25);
        chooseComp.setFont(PX14);
        chooseComp.setArc(5, 5);
        add(chooseComp);
    }
}

