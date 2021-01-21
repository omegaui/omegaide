package ide.utils.systems.creators;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.swing.JTextField;

import ide.Screen;
import ide.utils.systems.View;

public class RefractionManager extends View {

	private JTextField nameField;
	private Runnable task;
	private Runnable externalTask;
	public static volatile File lastFile;

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

	public static void copy(File oriF, File target) {
		if(oriF.isDirectory()) {
			target.mkdir();
			return;
		}
		try {
			InputStream in = new FileInputStream(oriF);
			OutputStream out = new FileOutputStream(target);
			while(in.available() > 0) {
				out.write(in.read());
			}
			in.close();
			out.close();
		}catch(Exception e) {System.err.println(e);}
	}

	public void rename(File file, String title, Runnable externalTask) {
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
