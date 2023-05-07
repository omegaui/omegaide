/*
 * ArgumentWindow
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

package omega.instant.support;

import omega.Screen;
import omega.io.IconManager;
import omegaui.component.FlexPanel;
import omegaui.component.RTextField;
import omegaui.component.TextComp;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.LinkedList;

import static omega.io.UIManager.*;

public class ArgumentWindow extends JDialog {

    private TextComp titleComp;
    private TextComp infoComp;

    private FlexPanel argContainerPanel;
    private JScrollPane scrollPane;
    private JPanel mainPanel;

    private TextComp cancelComp;
    private TextComp addComp;
    private TextComp saveComp;

    private LinkedList<RTextField> fields = new LinkedList<>();

    private LinkedList<String> savedCommand = new LinkedList<>();

    private int blockX = 5;
    private int blockY = 5;

    private volatile boolean canceled = false;

    public ArgumentWindow(Screen screen) {
        super(screen, true);
        setTitle("Argument Window");
        setUndecorated(true);
        JPanel panel = new JPanel(null);
        panel.setBackground(c2);
        setContentPane(panel);
        setResizable(false);
        setLayout(null);
        setSize(400, 350);
        setLocationRelativeTo(null);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
        init();
    }

    public void init() {
        titleComp = new TextComp("Specify Arguments", back3, back3, glow, null);
        titleComp.setBounds(0, 0, getWidth() - 30, 30);
        titleComp.setFont(PX14);
        titleComp.setClickable(false);
        titleComp.setArc(0, 0);
        titleComp.attachDragger(this);
        add(titleComp);

        infoComp = new TextComp(IconManager.fluentinfoImage, 25, 25, "Enter One Argument per Field", back3, back3, TOOLMENU_COLOR3, null);
        infoComp.setBounds(getWidth() - 30, 0, 30, 30);
        infoComp.setArc(0, 0);
        infoComp.setClickable(false);
        add(infoComp);

        argContainerPanel = new FlexPanel(null, back1, null);
        argContainerPanel.setBounds(5, 35, getWidth() - 10, getHeight() - 70);
        argContainerPanel.setArc(10, 10);
        add(argContainerPanel);

        scrollPane = new JScrollPane(mainPanel = new JPanel(null));
        scrollPane.setBounds(5, 5, argContainerPanel.getWidth() - 10, argContainerPanel.getHeight() - 10);
        scrollPane.setBorder(null);
        mainPanel.setBackground(c2);
        argContainerPanel.add(scrollPane);

        cancelComp = new TextComp("Cancel", TOOLMENU_COLOR2_SHADE, back3, TOOLMENU_COLOR2, this::cancel);
        cancelComp.setBounds(5, getHeight() - 30, 100, 25);
        cancelComp.setFont(PX14);
        cancelComp.setArc(5, 5);
        add(cancelComp);

        addComp = new TextComp("Add Field", TOOLMENU_COLOR1_SHADE, back3, TOOLMENU_COLOR1, this::addField);
        addComp.setBounds(getWidth() / 2 - 50, getHeight() - 30, 100, 25);
        addComp.setFont(PX14);
        addComp.setArc(5, 5);
        add(addComp);

        saveComp = new TextComp("Save", TOOLMENU_COLOR2_SHADE, back3, TOOLMENU_COLOR2, this::save);
        saveComp.setBounds(getWidth() - 105, getHeight() - 30, 100, 25);
        saveComp.setFont(PX14);
        saveComp.setArc(5, 5);
        add(saveComp);

        RTextField field = new RTextField("Command Name", "", TOOLMENU_COLOR3, c2, TOOLMENU_COLOR1);
        field.setBounds(blockX, blockY, scrollPane.getWidth() - 10, 25);
        field.setFont(PX14);
        field.setArc(0, 0);
        mainPanel.add(field);
        fields.add(field);

        mainPanel.setPreferredSize(new Dimension(scrollPane.getWidth(), 25));
    }

    public void loadView(LinkedList<String> commands) {
        savedCommand = commands;
        if (commands.isEmpty())
            return;
        resetView();

        RTextField mfield = new RTextField("Command Name", "", TOOLMENU_COLOR3, c2, TOOLMENU_COLOR1);
        mfield.setBounds(blockX, blockY, scrollPane.getWidth() - 10, 25);
        mfield.setFont(PX14);
        mfield.setArc(0, 0);
        mainPanel.add(mfield);
        fields.add(mfield);

        fields.get(0).setText(commands.get(0));
        for (int i = 1; i < commands.size(); i++) {
            blockY += 25;

            RTextField field = new RTextField("Option/Argument", "", TOOLMENU_COLOR2, c2, TOOLMENU_COLOR2);
            field.setText(commands.get(i));
            field.setBounds(blockX, blockY, scrollPane.getWidth() - 10, 25);
            field.setFont(PX14);
            field.setArc(0, 0);
            mainPanel.add(field);
            fields.add(field);
        }

        mainPanel.setPreferredSize(new Dimension(scrollPane.getWidth(), blockY + 25));

        repaint();

        scrollPane.getVerticalScrollBar().setVisible(true);
        scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
    }

    public void resetView() {
        fields.forEach(mainPanel::remove);
        fields.clear();
        blockY = 5;
    }

    public void cancel() {
        canceled = true;
        dispose();
    }

    public void addField() {
        blockY += 25;

        RTextField field = new RTextField("Option/Argument", "", TOOLMENU_COLOR2, c2, TOOLMENU_COLOR1);
        field.setBounds(blockX, blockY, scrollPane.getWidth() - 10, 25);
        field.setFont(PX14);
        field.setArc(0, 0);
        mainPanel.add(field);
        fields.add(field);

        mainPanel.setPreferredSize(new Dimension(scrollPane.getWidth(), blockY + 25));

        repaint();

        scrollPane.getVerticalScrollBar().setVisible(true);
        scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
    }

    public void save() {
        //Save Operations
        canceled = false;
        dispose();
    }

    public LinkedList<String> showArgumentWindow() {
        setVisible(true);
        return getCommand();
    }

    public LinkedList<String> getCommand() {
        LinkedList<String> commands = new LinkedList<>();
        fields.forEach(field -> {
            if (field.hasText())
                commands.add(field.getText());
        });
        return isSaved() ? commands : savedCommand;
    }

    public boolean isSaved() {
        return !canceled;
    }
}
