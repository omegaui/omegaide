/*
 * MainWindowSizeController -- Controls Main Window Dimensions!
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
import omega.io.UIManager;
import omegaui.component.SliderComp;
import omegaui.component.TextComp;

import javax.swing.*;
import java.awt.*;

import static omega.io.UIManager.*;
import static omegaui.component.animation.Animations.*;

public class MainWindowSizeController extends JDialog {
    public Screen screen;

    public TextComp iconComp;
    public TextComp titleComp;
    public TextComp closeComp;

    public TextComp defaultWidthLabel;
    public TextComp defaultHeightLabel;

    public SliderComp widthSlider;
    public SliderComp heightSlider;

    public MainWindowSizeController(Screen screen) {
        super(screen, true);
        this.screen = screen;
        setUndecorated(true);
        setSize(400, 170);
        setLocationRelativeTo(null);
        JPanel panel = new JPanel(null);
        panel.setBackground(c2);
        setContentPane(panel);
        init();
    }

    public void init() {
        iconComp = new TextComp(IconManager.fluentsettingsImage, 25, 25, c2, c2, c2, null);
        iconComp.setBounds(0, 0, 30, 30);
        iconComp.setClickable(false);
        iconComp.setArc(0, 0);
        add(iconComp);

        titleComp = new TextComp("Change Main Window Default Size", c2, c2, glow, null);
        titleComp.setBounds(30, 0, getWidth() - 60, 30);
        titleComp.setFont(PX14);
        titleComp.setArc(0, 0);
        titleComp.setClickable(false);
        titleComp.attachDragger(this);
        add(titleComp);

        closeComp = new TextComp(IconManager.fluentcloseImage, 25, 25, c2, c2, c2, this::dispose);
        closeComp.setBounds(getWidth() - 30, 0, 30, 30);
        closeComp.setClickable(false);
        closeComp.setArc(0, 0);
        add(closeComp);

        putAnimationLayer(closeComp, getImageSizeAnimationLayer(20, +5, true), ACTION_MOUSE_ENTERED);

        defaultWidthLabel = new TextComp("Window Width", TOOLMENU_GRADIENT, c2, glow, null);
        defaultWidthLabel.setBounds(10, 70, 120, 40);
        defaultWidthLabel.setFont(PX14);
        defaultWidthLabel.setArc(10, 10);
        defaultWidthLabel.setClickable(false);
        add(defaultWidthLabel);

        defaultHeightLabel = new TextComp("Window Height", TOOLMENU_GRADIENT, c2, glow, null);
        defaultHeightLabel.setBounds(10, 120, 120, 40);
        defaultHeightLabel.setFont(PX14);
        defaultHeightLabel.setArc(10, 10);
        defaultHeightLabel.setClickable(false);
        add(defaultHeightLabel);

        widthSlider = new SliderComp(TOOLMENU_COLOR2_SHADE, TOOLMENU_COLOR4_SHADE, TOOLMENU_COLOR3, glow);
        widthSlider.setSlideListener((value) -> {
            UIManager.setMainWindowWidth(value);
            screen.setSize(value, screen.getHeight());
        });
        widthSlider.setBounds(140, 70, getWidth() - 150, 40);
        widthSlider.setMinMaxValueTextFont(UBUNTU_PX12);
        widthSlider.setValueTextFont(widthSlider.getMinMaxValueTextFont());
        widthSlider.setMinMaxValueTextColor(TOOLMENU_COLOR3);
        widthSlider.setValueTextColor(TOOLMENU_COLOR2);
        widthSlider.setValueUnit(" px");
        widthSlider.setPaintValuesEnabled(true);
        widthSlider.setMinValue(765);
        widthSlider.setMaxValue(Toolkit.getDefaultToolkit().getScreenSize().width);
        add(widthSlider);

        heightSlider = new SliderComp(TOOLMENU_COLOR2_SHADE, TOOLMENU_COLOR4_SHADE, TOOLMENU_COLOR3, glow);
        heightSlider.setSlideListener((value) -> {
            UIManager.setMainWindowHeight(value);
            screen.setSize(screen.getWidth(), value);
        });
        heightSlider.setBounds(140, 120, getWidth() - 150, 40);
        heightSlider.setMinMaxValueTextFont(UBUNTU_PX12);
        heightSlider.setValueTextFont(heightSlider.getMinMaxValueTextFont());
        heightSlider.setMinMaxValueTextColor(TOOLMENU_COLOR3);
        heightSlider.setValueTextColor(TOOLMENU_COLOR2);
        heightSlider.setValueUnit(" px");
        heightSlider.setPaintValuesEnabled(true);
        heightSlider.setMinValue(430);
        heightSlider.setMaxValue(Toolkit.getDefaultToolkit().getScreenSize().height);
        add(heightSlider);
    }

    @Override
    public void setVisible(boolean value) {
        if (value) {
            widthSlider.setValue(screen.getWidth());
            heightSlider.setValue(screen.getHeight());
        }
        super.setVisible(value);
    }
}
