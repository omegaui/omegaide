/**
  * Adds Additional Arguments for Java RunTime and CompileTime
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

import java.util.LinkedList;

import java.awt.Graphics2D;

import omega.comp.TextComp;

import javax.swing.JDialog;
import javax.swing.JPanel;

import static omega.utils.UIManager.*;
import static omega.comp.Animations.*;
public class ExtendedBuildPathManager extends JDialog {
	private TextComp titleComp;
	private TextComp compileComp;
	private TextComp runComp;
	private ExtendedBuildPanel compilePanel;
	private ExtendedBuildPanel runPanel;
	private int state = 0;
	
	public ExtendedBuildPathManager(Screen screen) {
		super(screen, false);
		setUndecorated(true);
		setTitle("Extended BuildPath Manager");
		setSize(400, 450);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		JPanel panel = new JPanel(null);
		panel.setBackground(c2);
		setContentPane(panel);
		setLayout(null);
		init();
	}

	public void init(){
		titleComp = new TextComp("Add Additional Flags", TOOLMENU_COLOR3, c2, c2, null);
		titleComp.setBounds(0, 0, getWidth() - 30, 30);
		titleComp.setFont(PX14);
		titleComp.setClickable(false);
		titleComp.attachDragger(this);
		titleComp.setArc(0, 0);
		add(titleComp);

		TextComp closeComp = new TextComp("x", TOOLMENU_COLOR2_SHADE, back1, TOOLMENU_COLOR2, this::dispose);
		closeComp.setBounds(getWidth() - 30, 0, 30, 30);
		closeComp.setFont(PX14);
		closeComp.setArc(0, 0);
		add(closeComp);

		compileComp = new TextComp("Compile Time", TOOLMENU_COLOR1_SHADE, back1, TOOLMENU_COLOR1, ()->{
			state = 0;
			compilePanel.setVisible(true);
			runPanel.setVisible(false);
			repaint();
		}){
			@Override
			public void draw(Graphics2D g){
				if(state == 0){
					g.setColor(color3);
					g.fillRect(0, getHeight() - 4, getWidth(), 4);
				}
			}
		};
		compileComp.setBounds(0, 30, getWidth()/2, 40);
		compileComp.setArc(0, 0);
		compileComp.setFont(PX14);
		add(compileComp);

		runComp = new TextComp("Run Time", TOOLMENU_COLOR1_SHADE, back1, TOOLMENU_COLOR1, ()->{
			state = 1;
			runPanel.setVisible(true);
			compilePanel.setVisible(false);
			repaint();
		}){
			@Override
			public void draw(Graphics2D g){
				if(state == 1){
					g.setColor(color3);
					g.fillRect(0, getHeight() - 4, getWidth(), 4);
				}
			}
		};
		runComp.setBounds(getWidth()/2, 30, getWidth()/2, 40);
		runComp.setArc(0, 0);
		runComp.setFont(PX14);
		add(runComp);

		compilePanel = new ExtendedBuildPanel(this);
		compilePanel.setBounds(0, 70, getWidth(), getHeight() - 70);
		add(compilePanel);

		runPanel = new ExtendedBuildPanel(this);
		runPanel.setBounds(0, 70, getWidth(), getHeight() - 70);
		runPanel.setVisible(false);
		add(runPanel);
	}

	public LinkedList<String> getCompileTimeFlags(){
		return compilePanel.getFlags();
	}

	public LinkedList<String> getRunTimeFlags(){
		return runPanel.getFlags();
	}

	@Override
	public void setVisible(boolean value){
		if(value){
	          compilePanel.loadFlags(Screen.getFileView().getProjectManager().compileTimeFlags);
	          runPanel.loadFlags(Screen.getFileView().getProjectManager().runTimeFlags);
		}
	     super.setVisible(value);
	}

	@Override
	public void dispose(){
		super.dispose();
		try{
			Screen.getFileView().getProjectManager().compileTimeFlags.clear();
			Screen.getFileView().getProjectManager().runTimeFlags.clear();
			
			getCompileTimeFlags().forEach(Screen.getFileView().getProjectManager().compileTimeFlags::add);
			getRunTimeFlags().forEach(Screen.getFileView().getProjectManager().runTimeFlags::add);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	
}
