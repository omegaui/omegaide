/**
* ChoiceDialog
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

package omega.utils;
import omega.Screen;

import java.awt.Graphics;

import omega.comp.TextComp;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JFrame;


import static omega.utils.UIManager.*;
public class ChoiceDialog extends JDialog{
	private TextComp headerComp;
	private TextComp choice1Comp;
	private TextComp choice2Comp;
	private TextComp cancelComp;
	
	public static int CHOICE1 = 0;
	public static int CHOICE2 = 1;
	public static int CANCEL = 2;
	public int choice = CANCEL;
	
	private static ChoiceDialog choiceDialog;
	
	private JPanel panel = new JPanel(null);
	
	public ChoiceDialog(JFrame frame){
		super(frame, true);
		setUndecorated(true);
		setContentPane(panel);
		setTitle("Choice Dialog");
		setBackground(c2);
		setLayout(null);
		init();
	}
	
	public void init(){
		headerComp = new TextComp("", c2, c2, TOOLMENU_COLOR3, null);
		headerComp.setFont(PX14);
		headerComp.setClickable(false);
		headerComp.setArc(0, 0);
		headerComp.setLayout(null);
		add(headerComp);
		
		choice1Comp = new TextComp("", TOOLMENU_COLOR1_SHADE, c2, TOOLMENU_COLOR1, ()->{
			choice = CHOICE1;
			dispose();
		});
		choice1Comp.setFont(PX14);
		choice1Comp.setArc(0, 0);
		add(choice1Comp);
		
		choice2Comp = new TextComp("", TOOLMENU_COLOR1_SHADE, c2, TOOLMENU_COLOR1, ()->{
			choice = CHOICE2;
			setVisible(false);
		});
		choice2Comp.setFont(PX14);
		choice2Comp.setArc(0, 0);
		add(choice2Comp);
		
		cancelComp = new TextComp("Cancel", TOOLMENU_COLOR3, TOOLMENU_COLOR2, c2, ()->{
			choice = CANCEL;
			setVisible(false);
		});
		cancelComp.setFont(PX14);
		cancelComp.setArc(0, 0);
		headerComp.add(cancelComp);
	}
	
	public void plotComps(){
		headerComp.setBounds(0, 0, getWidth(), getHeight() - 30);
		choice1Comp.setBounds(0, getHeight() - 30, getWidth()/2, 30);
		choice2Comp.setBounds(getWidth()/2, getHeight() - 30, getWidth()/2, 30);
		cancelComp.setBounds(getWidth() - 100, 0, 100, 30);
	}
	
	public static int makeChoice(String text, String choice1, String choice2){
		if(choiceDialog == null)
			choiceDialog = new ChoiceDialog(Screen.getScreen());
		choiceDialog.choice = CANCEL;
		choiceDialog.headerComp.setText(text);
		choiceDialog.choice1Comp.setText(choice1);
		choiceDialog.choice2Comp.setText(choice2);
		
		Graphics g = Screen.getScreen().getGraphics();
		g.setFont(PX14);
		if(text.length() > 100)
			text = text.substring(0, 95) + "..." + text.charAt(text.length() - 1);
		int w = g.getFontMetrics().stringWidth(text);
		if(w < 400)
			w = 400;
		choiceDialog.setSize(w + 10, 150);
		choiceDialog.setLocationRelativeTo(null);
		choiceDialog.plotComps();
		choiceDialog.setVisible(true);
		return choiceDialog.choice;
	}
	
	@Override
	public void paint(Graphics g){
		super.paint(g);
		panel.repaint();
	}
}

