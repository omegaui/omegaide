/*
 * The Color Preview Addon for the Default Editor
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
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 */

package omega.ui.panel;

import omega.io.EditorAddon;
import omega.ui.component.Editor;
import omega.ui.component.ToolMenu;
import omegaui.component.FlexPanel;
import omegaui.component.TextComp;
import org.fife.ui.rsyntaxtextarea.Token;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import static omega.io.UIManager.TOOLMENU_GRADIENT;
import static omega.io.UIManager.c2;

public class ColorPreview extends FlexPanel implements EditorAddon, KeyListener, MouseMotionListener {

    public Editor editor;

    public TextComp colorComp;

    public volatile boolean ctrl = false;

    public ColorPreview() {
        super(null, TOOLMENU_GRADIENT, null);
        setArc(10, 10);
        setVisible(false);
        init();
    }

    public void init() {
        colorComp = new TextComp("", c2, c2, c2, this::triggerColorPicker);
        colorComp.setArc(10, 10);
        colorComp.setShowHandCursorOnMouseHover(true);
        add(colorComp);
    }

    public void triggerColorPicker() {
        ToolMenu.colorPicker.pickColor(colorComp.color2);
    }

    public synchronized void triggerPreview(MouseEvent e) {
        Token token = editor.viewToToken(e.getPoint());
        if (token == null)
            return;
        if (isHexColorText(token.getLexeme())) {
            String hexCode = "#" + convertStringToHex(token.getLexeme());
            Color color = Color.decode(hexCode);
            colorComp.setColors(color, color, color);
            if (!isVisible())
                setVisible(true);
            relocate();
        }
    }

    public synchronized static String convertStringToHex(String hexString) {
        if (hexString.startsWith("\"") && hexString.endsWith("\""))
            hexString = hexString.substring(1, hexString.length() - 1).trim();
        if (hexString.startsWith("#"))
            hexString = hexString.substring(1);
        return hexString;
    }

    public synchronized static boolean isHexColorText(String text) {
        text = convertStringToHex(text);
        if (text.length() != 6)
            return false;
        for (int i = 0; i < text.length(); i++) {
            if (!isHex(text.charAt(i)))
                return false;
        }
        return true;
    }

    public synchronized static boolean isHex(char ch) {
        return ((ch >= '0' && ch <= '9')
                || ((ch >= 'A' && ch <= 'F')
                || (ch >= 'a' && ch <= 'f')));
    }

    @Override
    public void install(Editor editor) {
        this.editor = editor;
        editor.add(this);
        editor.addMouseMotionListener(this);
        editor.addKeyListener(this);
        editor.getAttachment().getViewport().addChangeListener((e) -> relocate());
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {

    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        synchronized (editor) {
            if (keyEvent.getKeyCode() == KeyEvent.VK_CONTROL)
                ctrl = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        synchronized (editor) {
            if (keyEvent.getKeyCode() == KeyEvent.VK_CONTROL) {
                ctrl = false;
                setVisible(false);
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        //Invoking Code Navigation
        if (ctrl) {
            try {
                triggerPreview(e);
            } catch (Exception ex) {
                //This small code works perfectly.
                //Nothing needs to be debugged.
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    public void relocate() {
        Rectangle rect = editor.getAttachment().getViewport().getViewRect();
        setBounds((rect.x + rect.width) - 65, rect.y + 10, 60, 60);
        colorComp.setBounds(10, 10, getWidth() - 20, getHeight() - 20);
    }

    @Override
    public void setVisible(boolean value) {
        if (value) {
            relocate();
        }
        super.setVisible(value);
    }
}
