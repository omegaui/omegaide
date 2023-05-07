/**
 * FontChooser
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
import omegaui.component.FlexPanel;
import omegaui.component.NoCaretField;
import omegaui.component.SwitchComp;
import omegaui.component.TextComp;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

import static omega.io.UIManager.*;

public class FontChooser extends JDialog {

    private TextComp titleComp;

    private FlexPanel fontFamilyPanel;

    private NoCaretField sampleField;

    private SwitchComp buildComp;

    private NoCaretField searchField;
    private NoCaretField sizeField;

    private TextComp closeComp;
    private TextComp applyComp;

    private FlexPanel fontPanel;
    private JScrollPane fontScrollPane;

    private LinkedList<TextComp> fontList = new LinkedList<>();

    private int block;
    private int fontSize = 16;

    private volatile boolean cancel;

    public FontChooser(Screen screen) {
        super(screen, true);
        setTitle("Omega IDE -- Font Chooser");
        setUndecorated(true);
        JPanel panel = new JPanel(null);
        panel.setBackground(c2);
        setContentPane(panel);
        setSize(560, 300);
        setLocationRelativeTo(null);
        setBackground(c2);
        init();
    }

    public void init() {
        titleComp = new TextComp("Font Chooser", c2, TOOLMENU_GRADIENT, glow, null);
        titleComp.setBounds(0, 0, getWidth(), 30);
        titleComp.setClickable(false);
        titleComp.setFont(PX14);
        titleComp.setArc(0, 0);
        titleComp.attachDragger(this);
        add(titleComp);

        fontFamilyPanel = new FlexPanel(null, TOOLMENU_COLOR6_SHADE, null);
        fontFamilyPanel.setBounds(10, 40, 300, 250);
        fontFamilyPanel.setArc(5, 5);
        add(fontFamilyPanel);

        fontScrollPane = new JScrollPane(fontPanel = new FlexPanel(null, c2, c2));
        fontPanel.setArc(0, 0);
        fontScrollPane.setBounds(10, 40, fontFamilyPanel.getWidth() - 20, fontFamilyPanel.getHeight() - 50);
        fontScrollPane.setBackground(c2);
        fontFamilyPanel.add(fontScrollPane);

        fontPanel.setPreferredSize(new Dimension(fontScrollPane.getWidth(), fontScrollPane.getHeight()));

        searchField = new NoCaretField("", "Search Font", TOOLMENU_COLOR6, c2, TOOLMENU_COLOR2);
        searchField.setOnAction(() -> search(searchField.getText()));
        searchField.setBounds(10, 10, fontFamilyPanel.getWidth() - 20, 25);
        searchField.setFont(PX14);
        fontFamilyPanel.add(searchField);

        sampleField = new NoCaretField("Hello World", "", TOOLMENU_COLOR3, c2, TOOLMENU_COLOR1);
        sampleField.setBounds(320, 40, getWidth() - 340, 40);
        sampleField.setFont(PX16);
        add(sampleField);

        TextComp label1 = new TextComp("Use Bold Style", back3, TOOLMENU_GRADIENT, glow, null);
        label1.setBounds(320, 100, 150, 30);
        label1.setClickable(false);
        label1.setFont(PX14);
        add(label1);

        buildComp = new SwitchComp(true, TOOLMENU_COLOR1, TOOLMENU_COLOR3, TOOLMENU_COLOR2_SHADE, (value) -> {
        });
        buildComp.setBounds(480, 100, 70, 30);
        buildComp.setInBallColor(glow);
        buildComp.setToggleListener((value) -> {
            sampleField.setFont(new Font(sampleField.getFont().getName(), value ? Font.BOLD : Font.PLAIN, fontSize));
        });
        add(buildComp);

        TextComp label2 = new TextComp("Font Size", back3, TOOLMENU_GRADIENT, glow, null);
        label2.setBounds(320, 140, getWidth() - 340, 30);
        label2.setClickable(false);
        label2.setFont(PX14);
        add(label2);

        sizeField = new NoCaretField(fontSize + "", "Enter Font Size", TOOLMENU_COLOR3, c2, TOOLMENU_COLOR1);
        sizeField.setBounds(320, 180, getWidth() - 340, 30);
        sizeField.setFont(PX14);
        sizeField.setOnAction(() -> {
            int s = 16;
            try {
                s = Integer.parseInt(sizeField.getText());
            } catch (Exception e) {
                sizeField.setText("Only Integer Allowed");
                return;
            }
            fontSize = s;
            sampleField.setFont(new Font(sampleField.getFont().getName(), buildComp.isOn() ? Font.BOLD : Font.PLAIN, fontSize));
        });
        add(sizeField);

        closeComp = new TextComp("Close", TOOLMENU_COLOR1_SHADE, TOOLMENU_COLOR2_SHADE, TOOLMENU_COLOR2, () -> {
            cancel = true;
            dispose();
        });
        closeComp.setBounds(320 + ((getWidth() - 320) / 2) - 110, getHeight() - 60, 100, 30);
        closeComp.setFont(PX14);
        closeComp.setArc(5, 5);
        add(closeComp);

        applyComp = new TextComp("Apply", TOOLMENU_COLOR1_SHADE, TOOLMENU_COLOR5_SHADE, TOOLMENU_COLOR5, this::dispose);
        applyComp.setBounds(320 + ((getWidth() - 320) / 2) + 10, getHeight() - 60, 100, 30);
        applyComp.setFont(PX14);
        applyComp.setArc(5, 5);
        add(applyComp);
    }

    public void prepareFonts() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] FONT = ge.getAvailableFontFamilyNames();
        block = 0;
        for (String fontName : FONT) {
            TextComp comp = new TextComp(fontName, TOOLMENU_COLOR3_SHADE, c2, fontName.contains("Mono") ? TOOLMENU_COLOR3 : TOOLMENU_COLOR2, () -> prepareView(fontName));
            comp.setBounds(0, block, fontScrollPane.getWidth(), 25);
            comp.setFont(PX14);
            comp.setArc(0, 0);
            fontPanel.add(comp);
            fontList.add(comp);
            block += 25;
        }
        fontPanel.setPreferredSize(new Dimension(fontScrollPane.getWidth(), block));
        repaint();
    }

    public void search(String text) {
        fontList.forEach(fontPanel::remove);
        block = 0;
        for (TextComp comp : fontList) {
            if (comp.getText().contains(text)) {
                comp.setBounds(0, block, fontScrollPane.getWidth(), 25);
                block += 25;
                fontPanel.add(comp);
            }
        }
        if (block == 0) {
            titleComp.setColors(TOOLMENU_COLOR2, c2, c2);
            titleComp.setText("No Results Found");
        } else {
            titleComp.setColors(c2, TOOLMENU_GRADIENT, glow);
            titleComp.setText("Font Chooser");
        }
        fontPanel.setPreferredSize(new Dimension(fontScrollPane.getWidth(), block));
        fontScrollPane.getVerticalScrollBar().setValue(0);
        repaint();
    }

    public void prepareView(String fontName) {
        int s = 16;
        try {
            s = Integer.parseInt(sizeField.getText());
        } catch (Exception e) {
            sizeField.setText("Only Integer Allowed");
        }
        fontSize = s;
        sampleField.setFont(new Font(fontName, buildComp.isOn() ? Font.BOLD : Font.PLAIN, fontSize));
        titleComp.setText("Font Chooser -- Current Font : " + fontName);
    }

    public Font chooseFont(Font currentFont) {
        cancel = false;
        sizeField.setText(currentFont.getSize() + "");
        buildComp.setOn(currentFont.getStyle() == Font.BOLD);
        prepareView(currentFont.getName());
        setVisible(true);
        return cancel ? currentFont : sampleField.getFont();
    }

    @Override
    public void setVisible(boolean value) {
        if (value) {
            if (fontList.isEmpty())
                prepareFonts();
        }
        super.setVisible(value);
    }
}
