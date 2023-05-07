/*
 * ChoiceDialog
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

package omega.ui.dialog;

import omega.Screen;
import omega.io.IconManager;
import omegaui.component.TextComp;

import javax.swing.*;
import java.awt.geom.RoundRectangle2D;

import static omega.io.UIManager.*;

public class ChoiceDialog extends JDialog {

    private TextComp emojiComp;

    private TextComp textComp;
    private TextComp choice1Comp;
    private TextComp choice2Comp;
    private TextComp cancelComp;

    public static final int CHOICE1 = 0;
    public static final int CHOICE2 = 1;
    public static final int CANCEL = 2;
    public int choice = CANCEL;

    public static ChoiceDialog choiceDialog;

    public ChoiceDialog(JFrame frame) {
        super(frame, true);
        setTitle("Choice Dialog");
        setLayout(null);
        setUndecorated(true);
        JPanel panel = new JPanel(null);
        setContentPane(panel);
        panel.setBackground(back3);
        init();
        setSize(500, 300);
    }

    public void init() {
        emojiComp = new TextComp("", c2, c2, c2, null);
        emojiComp.setClickable(false);
        emojiComp.setArc(10, 10);
        emojiComp.attachDragger(this);
        emojiComp.setGifImage(IconManager.fluentneutralemojiGif, 98, 98);
        add(emojiComp);

        textComp = new TextComp("Question?", back1, back1, glow, null);
        textComp.setFont(PX14);
        textComp.setArc(0, 0);
        textComp.setClickable(false);
        textComp.attachDragger(this);
        add(textComp);

        choice1Comp = new TextComp("Choice 1", TOOLMENU_COLOR5_SHADE, back1, TOOLMENU_COLOR3, () -> {
            choice = CHOICE1;
            dispose();
        });
        choice1Comp.setArc(5, 5);
        choice1Comp.setFont(UBUNTU_PX14);
        add(choice1Comp);

        choice2Comp = new TextComp("Choice 2", TOOLMENU_COLOR5_SHADE, back1, TOOLMENU_COLOR3, () -> {
            choice = CHOICE2;
            dispose();
        });
        choice2Comp.setArc(5, 5);
        choice2Comp.setFont(UBUNTU_PX14);
        add(choice2Comp);

        cancelComp = new TextComp("Cancel", TOOLMENU_COLOR3_SHADE, TOOLMENU_COLOR1_SHADE, TOOLMENU_COLOR1, this::dispose);
        cancelComp.setFont(PX14);
        cancelComp.setArc(5, 5);
        add(cancelComp);
    }

    public static int makeChoice(String question, String choice1, String choice2) {
        if (choiceDialog == null)
            choiceDialog = new ChoiceDialog(Screen.getScreen());
        choiceDialog.choice = CANCEL;
        choiceDialog.textComp.setText(question);
        choiceDialog.choice1Comp.setText(choice1);
        choiceDialog.choice2Comp.setText(choice2);

        choiceDialog.choice1Comp.setSize(computeWidth(choice1, UBUNTU_PX14) + 10, 25);
        choiceDialog.choice2Comp.setSize(computeWidth(choice2, UBUNTU_PX14) + 10, 25);

        choiceDialog.choice1Comp.setLocation(choiceDialog.getWidth() - 10 - choiceDialog.choice1Comp.getWidth() - 10 - choiceDialog.choice2Comp.getWidth(), choiceDialog.getHeight() - 40);
        choiceDialog.choice2Comp.setLocation(choiceDialog.getWidth() - 10 - choiceDialog.choice2Comp.getWidth(), choiceDialog.getHeight() - 40);

        choiceDialog.setVisible(true);
        return choiceDialog.choice;
    }

    @Override
    public void setSize(int w, int h) {
        super.setSize(w, h);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
        setLocationRelativeTo(null);
        emojiComp.setBounds(getWidth() / 2 - 100 / 2, 10, 100, 100);
        textComp.setBounds(0, 150, getWidth(), 30);
        cancelComp.setBounds(getWidth() / 2 - 90 / 2, 200, 90, 25);
    }

    public static void main(String[] args) {
        makeChoice("Do you really want to embed this change now?", "Yes, I do want.", "No! Afterwards.");
    }
}
