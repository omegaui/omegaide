package snippet;
/*
    Copyright (C) 2021 Omega UI. All Rights Reserved.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
import tabPane.IconManager;
import ide.utils.UIManager;
import ide.Screen;
import java.io.File;
import Omega.IDE;
import settings.comp.TextComp;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;

import ide.utils.Editor;
import launcher.Door;
public class SnippetView extends JDialog {
	private static final Font FONT = new Font("Ubuntu Mono", Font.BOLD, 16);
	private RSyntaxTextArea textArea;
	private JTextField textField;
	private LinkedList<Door> doors = new LinkedList<>();
	private BufferedImage image;
	private JPanel leftPanel;
	private int block;
	private volatile Snippet snip;
	private JScrollPane pane;
	public SnippetView(ide.Screen screen){
		super(screen);
		setUndecorated(true);
		setIconImage(screen.getIconImage());
		setModal(false);
		setTitle("Snippet Manager");
		setSize(700, 605);
		setLocationRelativeTo(null);
		setResizable(false);
		setLayout(null);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
				saveView();
			}
		});
		init();
	}

	private void init(){
		//Door System
          image = tabPane.IconManager.buildImage;
		leftPanel = new JPanel(null);
		pane = new JScrollPane(leftPanel);
		pane.setBounds(0, 0, 250, getHeight());
		add(pane);

		//View System
		textField = new JTextField();
		textField.setBounds(250, 0, getWidth() - 250 - 120, 40);
		textField.setToolTipText("Enter Snippet Name with alphabets, numbers and symbols(except \';\') without whitespaces");
		add(textField);

		TextComp add = new TextComp("+", ide.utils.UIManager.c1, ide.utils.UIManager.c2, ide.utils.UIManager.c3, ()->{
               if(textField.getText().contains(" ") || textField.getText().equals("")) {
                    textField.setText("See Tooltip for Naming the Snippets");
                    return;
               }
               SnippetBase.add(textField.getText(), textArea.getText(), textArea.getCaretPosition(), textArea.getCaretLineNumber());
               setView(SnippetBase.getAll().getLast());
               loadDoors();
	     });
		add.setBounds(getWidth() - 120, 0, 40, 40);
		add.setFont(FONT);
          add.setArc(0, 0);
		add(add);

		TextComp rem = new TextComp("-", ide.utils.UIManager.c1, ide.utils.UIManager.c2, ide.utils.UIManager.c3, ()->{
               SnippetBase.remove(textField.getText());
               loadDoors();
               textField.setText("");
               textArea.setText("");
               this.snip = null;
	     });
		rem.setBounds(getWidth() - 80, 0, 40, 40);
		rem.setFont(FONT);
          rem.setArc(0, 0);
		add(rem);

		TextComp close = new TextComp("x", ide.utils.UIManager.c1, ide.utils.UIManager.c2, ide.utils.UIManager.c3, ()->{
               dispose();
               saveView();
	     });
		close.setBounds(getWidth() - 40, 0, 40, 40);
		close.setFont(FONT);
          close.setArc(0, 0);
		add(close);

		textArea = new RSyntaxTextArea();
		textArea.setSyntaxEditingStyle(RSyntaxTextArea.SYNTAX_STYLE_JAVA);
		textArea.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				saveView();
			}
		});
		if(!ide.utils.UIManager.isDarkMode()) {
			textField.setBackground(Color.WHITE);
			textField.setForeground(Color.BLACK);
			leftPanel.setBackground(Color.WHITE);
			try {
			     Theme.load(Editor.class.getResourceAsStream("/idea.xml")).apply(textArea);
			}catch(Exception e) {}
		}
		else {
			ide.utils.UIManager.setData(textField);
			ide.utils.UIManager.setData(leftPanel);
			try {
			     Theme.load(Editor.class.getResourceAsStream("/dark.xml")).apply(textArea);
			}catch(Exception e) {}
		}
		textField.setFont(new Font(UIManager.fontName, Font.BOLD, UIManager.fontSize));
		RTextScrollPane scrollPane = new RTextScrollPane(textArea);
		scrollPane.setBounds(250, 40, getWidth() - 250, getHeight() - 40);
		add(scrollPane);
	}
	
	public void setView(Snippet snip) {
		saveView();
		this.snip = snip;
		textField.setText(snip.base);
		textArea.setText(snip.code);
		textArea.setCaretPosition(snip.caret);
	}
	

	public void saveView() {
		if(snip == null) return;
		if(!snip.base.equals(textField.getText())) return;
		snip.base = textField.getText();
		snip.code = textArea.getText();
		snip.caret = textArea.getCaretPosition();
		snip.line = textArea.getCaretLineNumber();
		SnippetBase.save();
	}

	private void loadDoors() {
		doors.forEach(leftPanel::remove);
		doors.clear();
		block = -40;
		for(Snippet snip : SnippetBase.getAll()) {
			Door door = new Door(File.separator + "\\" + File.separator + snip.base, image, ()->setView(snip));
			door.setBounds(0, block += 40, 250, 40);
			doors.add(door);
			leftPanel.add(door);
		}
		if(block == -40) return;
		leftPanel.setPreferredSize(new Dimension(250, block + 40));
		pane.getVerticalScrollBar().setVisible(true);
		pane.getVerticalScrollBar().setValue(0);
		repaint();
	}
	
	@Override
	public void setVisible(boolean value) {
		super.setVisible(value);
		if(value) {
			loadDoors();
		}
	}
}