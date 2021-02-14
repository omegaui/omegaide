package creator;
/*
    This class is responsible for showing the FileWizard.
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
import popup.*;

import static ide.utils.UIManager.*;
import static creator.ProjectWizard.addHoverEffect;
import static creator.ProjectWizard.createSRCFile;
import static creator.ProjectWizard.setData;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import ide.Screen;
import ide.utils.UIManager;
import ide.utils.systems.EditorTools;
import importIO.ImportManager;
import settings.comp.TextComp;
public class FileWizard extends JDialog{
	public TextComp parentRoot;
	public TextComp typeBtn;
	public FileWizard(JFrame f){
		super(f, true);
		setLayout(null);
		setUndecorated(true);
		setSize(400, 120);
		setLocationRelativeTo(null);
		setResizable(false);
		init();
	}

	private void init(){
		JTextField nameField = new JTextField();
		nameField.setForeground(UIManager.c3);
		addHoverEffect(nameField, "Enter name of the File or Source");
		nameField.setBounds(0, 0, getWidth() - 40, 40);
          nameField.setBackground(c2);
		add(nameField);

		final JFileChooser fileC = new JFileChooser();
		fileC.setMultiSelectionEnabled(false);
		fileC.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileC.setApproveButtonText("Select");
		fileC.setDialogTitle("Select a directory as source root");
          
		parentRoot = new TextComp(":", c1, c3, c2, ()->{
			int res = fileC.showOpenDialog(this);
			if(res == JFileChooser.APPROVE_OPTION){
				parentRoot.setToolTipText(fileC.getSelectedFile().getAbsolutePath());
			}
		});
          parentRoot.setArc(0, 0);
		parentRoot.setBounds(nameField.getWidth(), 0, 40, 40);
		add(parentRoot);

		final OPopupWindow popup = new OPopupWindow("File-Type Menu", this, 0, false).width(210);
		typeBtn = new TextComp("class", c1, c2, c3, ()->{});
		typeBtn.setBounds(0, nameField.getHeight(), getWidth(), 40);
          popup.createItem("Directory", tabPane.IconManager.projectImage, ()->typeBtn.setText("directory"))
          .createItem("Class", tabPane.IconManager.classImage, ()->typeBtn.setText("class"))
          .createItem("Interface", tabPane.IconManager.interImage, ()->typeBtn.setText("interface"))
          .createItem("Annotation", tabPane.IconManager.annImage, ()->typeBtn.setText("@interface"))
          .createItem("Enum", tabPane.IconManager.enumImage, ()->typeBtn.setText("enum"))
          .createItem("Custom File", tabPane.IconManager.fileImage, ()->typeBtn.setText("Custom File"));
		typeBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				popup.setLocation(e.getXOnScreen(), e.getYOnScreen());
				popup.setVisible(true);
			}
		});
          typeBtn.setArc(0, 0);
		add(typeBtn);

		TextComp cancelBtn = new TextComp("Close", c1, c2, c3, ()->setVisible(false));
		cancelBtn.setBounds(0, getHeight() - 40, getWidth()/2, 40);
		setData(cancelBtn);
          cancelBtn.setArc(0, 0);
		add(cancelBtn);

		TextComp createBtn = new TextComp("Create", c1, c2, c3, ()->{
			if(parentRoot.getToolTipText() == null) return;
			if(!new File(parentRoot.getToolTipText()).exists()) {
				nameField.setText("The Root Directory Does not exists");
				return;
			}
			String type = typeBtn.getText();
               if(type.equals("directory")){
                    File dir = new File(parentRoot.getToolTipText() + File.separator + nameField.getText());
                    dir.mkdir();
                    Screen.getProjectView().reload();
                    return;
               }
			if(!type.equals("Custom File")){
				String text = nameField.getText();
				if(!text.contains(".")){
					nameField.setText("Specify a package as pack.Class");
					return;
				}
				final String PATH = text.substring(0, text.lastIndexOf('.'));
				String path = PATH;
				StringTokenizer tokenizer = new StringTokenizer(path, ".");
				path = parentRoot.getToolTipText() + File.separator;
				while(tokenizer.hasMoreTokens()){
					path += tokenizer.nextToken() + File.separator;
					File file = new File(path);
					if(!file.exists())
						file.mkdir();
				}
				File src = new File(path+text.substring(text.lastIndexOf('.') + 1)+".java");
				createSRCFile(src, type, PATH, text.substring(text.lastIndexOf('.') + 1));
				ImportManager.readSource(EditorTools.importManager);
			}else{
				File file = new File(parentRoot.getToolTipText() + File.separator + nameField.getText());
				if(!file.exists()){
					try{
						file.createNewFile();
						Screen.getProjectView().reload();
                              Screen.getScreen().loadFile(file);
					}catch(Exception ex){nameField.setText("Access Denied");}
				}
				else
					nameField.setText("File Already Exists");
			}
		});
		createBtn.setBounds(getWidth()/2, getHeight() - 40, getWidth()/2, 40);
		setData(createBtn);
          createBtn.setArc(0, 0);
		add(createBtn);
	}

	public void show(String type){
		typeBtn.setText(type);
		if(Screen.getFileView().getProjectPath() != null && new File(Screen.getFileView().getProjectPath()).exists())
			parentRoot.setToolTipText(Screen.getFileView().getProjectPath() + File.separator + "src");
		setVisible(true);
	}

	@Override
	public Component add(Component c){
		super.add(c);
		setData(c);
		return c;
	}
}
