/*
 * TemplateWizard
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
import java.io.File;
import java.io.PrintWriter;

import omega.io.IconManager;

import omegaui.component.TextComp;

import omega.Screen;

import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.JPanel;

import static omega.io.UIManager.*;
import static omegaui.component.animation.Animations.*;

public class TemplateWizard extends JDialog{

	private TextComp iconComp;
	private TextComp titleComp;
	private TextComp closeComp;

	private JTextField nameField;
	
	public TemplateWizard(Screen screen){
		super(screen, true);
		setUndecorated(true);
		setIconImage(screen.getIconImage());
		setTitle("Template Wizard");
		setLayout(null);
		setSize(300, 65);
		setLocationRelativeTo(null);
		JPanel panel = new JPanel(null);
		panel.setBackground(back2);
		setContentPane(panel);
		setBackground(back2);
		initUI();
	}

	public void initUI(){
		iconComp = new TextComp(IconManager.fluenttemplateImage, 25, 25, back2, back2, back2, null);
		iconComp.setBounds(0, 0, 30, 30);
		iconComp.setClickable(false);
		iconComp.setArc(0, 0);
		add(iconComp);

		titleComp = new TextComp("Create New File Template", back2, back2, glow, null);
		titleComp.setBounds(30, 0, getWidth() - 60, 30);
		titleComp.setFont(PX14);
		titleComp.setArc(0, 0);
		titleComp.setClickable(false);
		titleComp.attachDragger(this);
		add(titleComp);
		
		closeComp = new TextComp(IconManager.fluentcloseImage, 20, 20, back2, back2, back2, this::dispose);
		closeComp.setBounds(getWidth() - 30, 0, 30, 30);
		closeComp.setArc(0, 0);
		add(closeComp);

		nameField = new JTextField(".");
		nameField.setBackground(c2);
		nameField.setCaretColor(TOOLMENU_COLOR2);
		nameField.setForeground(TOOLMENU_COLOR3);
		nameField.setBounds(65, 5, getWidth() - 100, 30);
		nameField.setFont(PX14);
		nameField.addActionListener((e) -> {
			File file = new File(".omega-ide" + File.separator + "file-templates", "template" + nameField.getText());
			if(!file.exists()){
				try{
					try(PrintWriter writer = new PrintWriter(file)){
						writer.println("Templates are very simple to write and easy to manage, ");
						writer.println("Whatever you write in this file, will be copied to a newly created \"" + nameField.getText() + "\" file.");
						writer.println("All of your file templates and this file too are located at " + new File(".omega-ide", "file-templates").getAbsolutePath() + ", ");
						writer.println("Take a look of it, if you want to edit, create or delete templates.");
						writer.println(">> This Feature is available from omegaide-v2.3 and onwards");
					}
					catch(Exception ex) {
						ex.printStackTrace();
					}
				}
				catch(Exception ex){
					nameField.setText("Access Denied");
					ex.printStackTrace();
				}
				try{
					Screen.getScreen().loadFile(file);
					dispose();
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
			}
			else {
				nameField.setText("Template Already Exists");
			}
		});
		nameField.setBounds(10, 35, getWidth() - 20, 25);
		nameField.setToolTipText("Enter file extension only with . prefix");
		add(nameField);

		putAnimationLayer(closeComp, getImageSizeAnimationLayer(20, +5, true), ACTION_MOUSE_ENTERED);
	}
	
}
