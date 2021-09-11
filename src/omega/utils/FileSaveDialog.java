/**
  * FileSaveDialog
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

import java.io.File;

import java.util.LinkedList;

import java.awt.Window;

import omega.comp.NoCaretField;
import omega.comp.TextComp;

import javax.swing.JDialog;
import javax.swing.JPanel;

import static omega.utils.UIManager.*;
public class FileSaveDialog extends JDialog{
	private NoCaretField nameField;
	private TextComp dirComp;
	private boolean canceled;
	public FileSaveDialog(Window window){
		super(window);
		setIconImage(window.getIconImages().get(0));
		setModal(true);
		setUndecorated(true);
		JPanel panel = new JPanel(null);
		panel.setBackground(c2);
		setContentPane(panel);
		setBackground(c2);
		setSize(400, 60);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		init();
	}

	public void init(){
		nameField = new NoCaretField("", "Enter File Name", TOOLMENU_COLOR2, c2, TOOLMENU_COLOR3);
		nameField.setBounds(0, 0, getWidth() - 130, 30);
		nameField.setFont(PX14);
		add(nameField);

		FileSelectionDialog fs = new FileSelectionDialog(Screen.getScreen());
		dirComp = new TextComp("Directory", TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR3, ()->{
			LinkedList<File> selections = fs.selectDirectories();
			if(!selections.isEmpty()){
				File file = selections.get(0);
				dirComp.setText(file.getName());
				dirComp.setToolTipText(file.getAbsolutePath());
			}
		});
		dirComp.setBounds(getWidth() - 130, 0, 130, 30);
		dirComp.setFont(PX14);
		dirComp.setArc(0, 0);
		add(dirComp);

		TextComp saveComp = new TextComp("Save", TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR2, ()->{
			setVisible(false);
		});
		saveComp.setBounds(getWidth()/2, 30, getWidth()/2, 30);
		saveComp.setFont(PX14);
		saveComp.setArc(0, 0);
		add(saveComp);

		TextComp closeComp = new TextComp("Cancel", TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR2, ()->{
			canceled = true;
			setVisible(false);
		});
		closeComp.setBounds(0, 30, getWidth()/2, 30);
		closeComp.setFont(PX14);
		closeComp.setArc(0, 0);
		add(closeComp);
	}

	public String saveFile(){
		setVisible(true);
		if(canceled)
			return null;
		if(nameField.getText().equals(""))
			return null;
		if(dirComp.getToolTipText() == null || dirComp.getToolTipText().equals(""))
			return null;
		return dirComp.getToolTipText() + File.separator + nameField.getText();
	}
}
