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
		nameField.setCaretColor(Color.YELLOW);
		nameField.addActionListener((e)->{
			setVisible(false);
			task.run();
			Screen.getProjectView().reload();
			if(externalTask != null) externalTask.run();
		});
		add(nameField, BorderLayout.CENTER);
		comps.add(nameField);
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
		}catch(Exception e) {e.printStackTrace();}
	}

	public void rename(File file, String title, Runnable externalTask) {
		this.externalTask = externalTask;
		setTitle(title);
		nameField.setText(file.getName());
		task = ()->{
			String dir = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf('/'));
			String newName = nameField.getText();
			try {
				Scanner reader = new Scanner(file);
				String text = "";
				while(reader.hasNextLine()) {
					text += reader.nextLine() + "\n"; 
				}
				reader.close();
				file.delete();
				PrintWriter writer = new PrintWriter(new FileOutputStream(dir+"/"+newName));
				writer.print(text);
				writer.close();
				lastFile = new File(dir+"/"+newName);
			}catch(Exception e) {e.printStackTrace();}
		};
		setVisible(true);
	}

}
