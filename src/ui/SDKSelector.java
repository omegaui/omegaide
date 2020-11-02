package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.io.File;
import java.util.LinkedList;
import java.util.Scanner;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ide.utils.DataManager;
import ide.utils.UIManager;

public class SDKSelector extends JDialog {

	private JScrollPane scrollPane;
	private LinkedList<ToggleBox> boxs = new LinkedList<>();
	public static final Font font = new Font("Consolas", Font.BOLD, 12);
	private JPanel panel = new JPanel(null);
	private String selection = null;
	private static Dimension dimension;
	private int y;

	public SDKSelector(JFrame f) {
		super(f, true);
		setTitle("Select A JDK");
		setSize(600, 300);
		setLocationRelativeTo(null);
		setType(Type.UTILITY);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		add((scrollPane = new JScrollPane(panel)), BorderLayout.CENTER);
		UIManager.setData(this);
		UIManager.setData(panel);
		dimension = new Dimension(getWidth(), 30);
	}

	private void resolvePath() {
		boxs.forEach(box->panel.remove(box));
		boxs.clear();
		y = 0;
		selection = null;
		String pathJava = DataManager.getPathToJava();
		if(pathJava == null) return;
		File[] files = new File(pathJava).listFiles();
		for(File file : files) {
			if(file.isFile()) continue;
			String release = getRelease(file.getAbsolutePath());
			if(release != null) {
				ToggleBox box = new ToggleBox(file.getName()+"("+release+")", (selected)->{
					if(!selected) return;
					selection = file.getAbsolutePath();
					SDKSelector.this.setVisible(false);
				});
				box.setBounds(0, y, panel.getWidth(), 30);
				box.setPreferredSize(dimension);
				box.setMinimumSize(box.getPreferredSize());
				panel.add(box);
				boxs.add(box);
				y += 30;
			}
		}
	}
	
	private String getRelease(String path) {
		File releaseFile = new File(path+"/release");
		if(!releaseFile.exists()) return null;
		try{
			Scanner reader = new Scanner(releaseFile);
			while(reader.hasNextLine()){
				String s = reader.nextLine();
				String cmd = "JAVA_VERSION=";
				if(s.startsWith("JAVA_VERSION=")){
					s = s.substring(s.indexOf(cmd) + cmd.length());
					s = s.substring(s.indexOf('\"') + 1, s.lastIndexOf('\"'));
					reader.close();
					return s;
				}
			}
			reader.close();
		}catch(Exception e){}
		return null;
	}
	
	public String getSelection() {
		return selection;
	}
	
	@Override
	public void paint(Graphics g) {
		panel.setPreferredSize(new Dimension(getWidth(), y));
		dimension = new Dimension(getWidth(), 30);
		boxs.forEach(box->{
			box.setPreferredSize(dimension);
			box.setMinimumSize(dimension);
			box.setSize(dimension);
		});
		super.paint(g);
		scrollPane.repaint();
	}
	
	@Override
	public void setVisible(boolean value) {
		if(value) {
			resolvePath();
		}
		super.setVisible(value);
		setSize(getWidth(),getHeight() - 1);
		setSize(getWidth(),getHeight() + 1);
		repaint();
	}
}
