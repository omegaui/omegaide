package ide.utils.systems.creators;
/*
    The default Refraction Manager.
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

import ide.*;
import ide.utils.systems.*;
import java.util.*;
import java.awt.*;
import ide.utils.*;
import org.fife.ui.rsyntaxtextarea.*;
import java.io.*;
import javax.swing.*;

public class RefractionManager extends View {
	private JTextField nameField;
	private Runnable task;
	private Runnable externalTask;
	public static volatile File lastFile;
     private static JScrollPane scrollPane;
     private static RSyntaxTextArea logArea;
     private static Thread operationThread;
     private static LinkedList<File> files;
     private static volatile boolean duplicateStructures = false;
     
     static {
          logArea  = new RSyntaxTextArea(){
               @Override
               public void append(String x){
               	super.append(x);
                    scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
               }
          };
          scrollPane = new JScrollPane(logArea);
          logArea.setSyntaxEditingStyle(logArea.SYNTAX_STYLE_JAVA);
          Editor.getTheme().apply(logArea);
     }

	public RefractionManager(Screen window) {
		super("Refractor", window);
		setLayout(new BorderLayout());
		setAlwaysOnTop(true);
		init();
		setSize(400, 70);
		setLocationRelativeTo(null);
	}

	private void init() {
		nameField = new JTextField();
		nameField.addActionListener((e)->{
			setVisible(false);
			task.run();
			Screen.getProjectView().reload();
			if(externalTask != null) externalTask.run();
		});
		add(nameField, BorderLayout.CENTER);
		comps.add(nameField);
		nameField.setFont(new java.awt.Font("Ubuntu Mono", java.awt.Font.BOLD, 14));
	}

     public static void move(File target, File destination){
          if(operationThread != null && operationThread.isAlive())
               return;
     	copy(target, destination);
          if(duplicateStructures) return;
          while(operationThread.isAlive());
          if(!target.isDirectory()){
               target.delete();
               Screen.getProjectView().reload();
               return;
          }
          try{
               Editor.deleteDir(target);
          }catch(Exception e){ System.err.println(e); }
          Screen.getProjectView().reload();
     }

     public static void copy(File target, File destination){
          if(operationThread != null && operationThread.isAlive())
               return;
          duplicateStructures = false;
          operationThread = new Thread(()->{
               try{
                    if(!target.isDirectory()){
                         if(destination.isDirectory())
                              copyFileToDir(target, destination);
                         else 
                              copyFile(target, destination);
                         Screen.getProjectView().reload();
                         return;
                    }
                    logArea.setText("");
                    Screen.getScreen().getOperationPanel().addTab("File Operation", scrollPane, ()->operationThread.stop());
                    String xpath = destination.getAbsolutePath() + File.separator + target.getName();
                    new File(xpath).mkdir();
                    files = new LinkedList<>();
                    loadStructure(files, target);
                    files.forEach(file -> {
                         if(file.isDirectory()){
                              final String PATH = file.getAbsolutePath();
                              String path = PATH;
                              path = destination.getAbsolutePath() + File.separator;
                              path += target.getName() + File.separator;
                              path += PATH.substring(PATH.lastIndexOf(target.getName()) + target.getName().length() + 1);
                              createParentDir(path, destination.getAbsolutePath());
                              File x = new File(path);
                              if(!x.exists()){
                                   x.mkdir();
                                   logArea.append("\nCreating Dir : \"" + path + "\"");
                              }
                         }
                    });
                    files.forEach(file -> {
                         if(!file.isDirectory()){
                              final String PATH = file.getAbsolutePath();
                              String path = PATH;
                              path = destination.getAbsolutePath() + File.separator;
                              path += target.getName() + File.separator;
                              path += PATH.substring(PATH.lastIndexOf(target.getName()) + target.getName().length() + 1);
                              logArea.append("\nCreating File : \"" + path + "\"");
                              copyFile(file, new File(path));
                         }
                    });
                    Screen.getProjectView().reload();
               }catch(Exception e){ duplicateStructures = true; logArea.append("\n\nDuplicate Directory Names Found! Process Canceled"); }
          });
          operationThread.start();
     }

     public static void createParentDir(String path, String parent){
          String rpath = path.substring(parent.length() + 1);
          StringTokenizer tok = new StringTokenizer(rpath, File.separator);
          rpath = parent;
          while(tok.hasMoreTokens()){
               rpath += File.separator + tok.nextToken();
               File f = new File(rpath);
               if(!f.exists()){
                    f.mkdir();
                    logArea.append("\nCreating Dir : \"" + rpath + "\"");
               }
          }
     }

     public static void loadStructure(LinkedList<File> files, File dir){
     	File[] F = dir.listFiles();
          if(F == null || F.length == 0) return;
          for(File f : F){
               if(f.isDirectory()){
                    files.add(f);
                    loadStructure(files, f);
               }
               else
                    files.add(f);
          }
     }

     public static void copyFileToDir(File file, File targetDir) {
          try {
               InputStream in = new FileInputStream(file);
               OutputStream out = new FileOutputStream(targetDir.getAbsolutePath() + File.separator + file.getName());
               while(in.available() > 0)
                    out.write(in.read());
               in.close();
               out.close();
          }catch(Exception e) { logArea.append("\n" + e); }
     }
     
     public static void copyFile(File file, File target) {
          try {
               InputStream in = new FileInputStream(file);
               OutputStream out = new FileOutputStream(target);
               while(in.available() > 0)
                    out.write(in.read());
               in.close();
               out.close();
          }catch(Exception e) { e.printStackTrace(); }
     }

	public void rename(File file, String title, Runnable externalTask) {
          if(file.isDirectory()) return;
		this.externalTask = externalTask;
		setTitle(title);
		nameField.setText(file.getName());
		task = ()->{
			String dir = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf(File.separatorChar));
			String newName = nameField.getText();
			try {
				InputStream in = new FileInputStream(file);
				OutputStream out = new FileOutputStream(dir + File.separator + newName);
				while(in.available() > 0)
					out.write(in.read());
				in.close();
				out.close();
				file.delete();
				lastFile = new File(dir + File.separator + newName);
			}catch(Exception e) {System.err.println(e);}
		};
		setVisible(true);
	}
}
