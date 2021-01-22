package ide.utils;
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
import settings.comp.*;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JWindow;
import javax.swing.filechooser.FileFilter;

import org.fife.ui.rtextarea.RTextArea;

import ide.Screen;
public class ModuleView extends JDialog{
	private RTextArea textArea;
	private static final Font font = new Font("Ubuntu Mono", Font.BOLD, 18);
	private static final Font fontX = new Font("Ubuntu Mono", Font.BOLD, 16);
	private static JFileChooser fileC;
	private class Dock extends JComponent {
		protected Dock(){
			setLayout(null);
			setPreferredSize(new Dimension(600, 40));
			TextComp iconBtn = new TextComp("x", ide.utils.UIManager.c1, ide.utils.UIManager.c2, ide.utils.UIManager.c3, ()->ModuleView.this.setVisible(false));
			iconBtn.setFocusable(false);
			iconBtn.setBounds(0, 0, 40, 40);
			UIManager.setData(iconBtn);
			iconBtn.setFont(font);
			add(iconBtn);

			TextComp title = new TextComp("Module Manager -Manage Project Modules", ide.utils.UIManager.c1, ide.utils.UIManager.c3, ide.utils.UIManager.c2, ()->{});
			title.setBounds(40, 0, 440, 40);
			UIManager.setData(title);
			title.setFont(font);
			title.setClickable(false);
			title.addMouseMotionListener(new MouseAdapter(){
				@Override
				public void mouseDragged(MouseEvent e){
					ModuleView.this.setLocation(e.getXOnScreen() - 300, e.getYOnScreen() - 20);
				}
			});
			add(title);

			TextComp addBtn = new TextComp("+", ide.utils.UIManager.c1, ide.utils.UIManager.c2, ide.utils.UIManager.c3, ()->addPath());
			addBtn.setBounds(480, 0, 40, 40);
			UIManager.setData(addBtn);
			addBtn.setFont(font);
			add(addBtn);

			TextComp rmBtn = new TextComp("-", ide.utils.UIManager.c1, ide.utils.UIManager.c2, ide.utils.UIManager.c3, ()->removePath(textArea.getCaretLineNumber()));
			rmBtn.setBounds(520, 0, 40, 40);
			UIManager.setData(rmBtn);
			rmBtn.setFont(font);
			add(rmBtn);


			final class Help extends JWindow{
				Help(){
					super(ModuleView.this);
					setLayout(new BorderLayout());
					setSize(780, 200);
					init();
				}

				void init(){
					String s = ">To add external modules to the project :\n"
							+">You can either edit the compile-time or run-time arguments or if not comfortable,\n"
							+">You can use this wizard to add external sdks or runtimes (ex:javafx) to the project.\n"
							+">What you need to do is to just add the path to the parent directory of modules\n"
							+" of the runtime environment to the project\n"
							+" by pressing \'+\' button and the ide will automatically setup the environment for use.\n"
							+">Usually, the modular jars are placed in the \'lib\' directory of the sdk.\n"
							+">So, You just need to add that \'lib\' directory\'s path.";
					RTextArea ins = new RTextArea(s);
					UIManager.setData(ins);
					ins.setFont(font);
					ins.setEditable(false);
                         ins.setCurrentLineHighlightColor(ide.utils.UIManager.c1);
					add(ins, BorderLayout.CENTER);
				}

				@Override
				public void setVisible(boolean value){
					if(value)
						setLocationRelativeTo(ModuleView.this);
					super.setVisible(value);
				}
			}

			final Help help = new Help();

			TextComp howBtn = new TextComp("?", ide.utils.UIManager.c1, ide.utils.UIManager.c2, ide.utils.UIManager.c3, ()->{});
			howBtn.setBounds(560, 0, 40, 40);
			UIManager.setData(howBtn);
			howBtn.setFont(font);
			howBtn.addMouseListener(new MouseAdapter(){
				@Override
				public void mouseEntered(MouseEvent e){
					help.setVisible(true);
				}

				@Override
				public void mouseExited(MouseEvent e){
					help.setVisible(false);
				}
			});
			add(howBtn);
		}
	}

	public ModuleView(Screen screen){
		super(screen, "Module Manager", true);
		setUndecorated(true);
		setSize(600, 600);
		setIconImage(screen.getIconImage());
		setResizable(false);
		setLocationRelativeTo(null);
		init();
		UIManager.setData(this);
	}

	private void removePath(int index){
		StringTokenizer tok = new StringTokenizer(textArea.getText(), "\n");
		int i = 0;
		while(tok.hasMoreTokens()){
			if(i++ == index) {
				String token = tok.nextToken();
				Screen.getFileView().getModuleManager().remove(token);
				String modules = Screen.getFileView().getModuleManager().getModularNamesFor(token);
				if(modules != null && !modules.equals("")) {
					StringTokenizer tokX = new StringTokenizer(modules, ",");
					while(tokX.hasMoreTokens()) {
						String tokenX = tokX.nextToken();
						String path = token + File.separator + tokenX + ".jar";
						Screen.getFileView().getDependencyManager().dependencies.remove(path);
					}
				}
			}
		}
		Screen.getFileView().getModuleManager().save();
		Screen.getFileView().getDependencyManager().saveFile();
		Screen.getScreen().tools.initTools();
		read();
	}

	private void addPath(){
		int res = fileC.showOpenDialog(this);
		if(res == JFileChooser.APPROVE_OPTION){
			for(File f : fileC.getSelectedFiles()){
				if(Screen.getFileView().getModuleManager().add(f.getAbsolutePath())) {
					textArea.append(f.getAbsolutePath() + "\n");
				}
			}
		}
		Screen.getFileView().getModuleManager().save();
		for(String root : Screen.getFileView().getModuleManager().roots) {
			String modules = Screen.getFileView().getModuleManager().getModularNamesFor(root);
			if(modules != null && !modules.equals("")) {
				StringTokenizer tok = new StringTokenizer(modules, ",");
				while(tok.hasMoreTokens()) {
					String token = tok.nextToken();
					String path = root + File.separator + token + ".jar";
					System.out.println(path);
					Screen.getFileView().getDependencyManager().add(path);
				}
			}
		}
		Screen.getFileView().getDependencyManager().saveFile();
		Screen.getScreen().tools.initTools();
	}

	private void init(){
		fileC = new JFileChooser();
		fileC.setDialogTitle("Choose a parent directory");
		fileC.setApproveButtonText("Select");
		fileC.setMultiSelectionEnabled(true);
		fileC.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileC.setFileFilter(new FileFilter(){
			@Override
			public String getDescription(){
				return "Select a directory";
			}

			@Override
			public boolean accept(File file){
				return file.isDirectory();
			}
		});

		textArea = new RTextArea();
		textArea.setEditable(false);
		UIManager.setData(textArea);
		textArea.setFont(fontX);
          textArea.setCurrentLineHighlightColor(ide.utils.UIManager.c1);
		add(new JScrollPane(textArea), BorderLayout.CENTER);

		add(new Dock(), BorderLayout.NORTH);
	}

	private void read() {
		textArea.setText("");
		for(String s : Screen.getFileView().getModuleManager().roots){
			textArea.append(s + "\n");
		}
	}

	public void setVisible(boolean value) {
		if(value && Screen.getFileView().getModuleManager() != null) {
			read();
		}
		super.setVisible(value);
	}
}
