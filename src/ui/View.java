package ui;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JPanel;

import ide.Screen;
import ide.utils.DataManager;
public class View extends JDialog{
	public static final Font font = new Font("Consolas", Font.BOLD, 20);
	private TabPanel tabPanel;
	private JPanel cardPanel;
	public View(Screen screen){
		super(screen,true);
		setUndecorated(true);
		setSize(720, 300);
		setLocationRelativeTo(null);
		setIconImage(screen.getIconImage());
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        ide.utils.UIManager.setData(this);

		cardPanel = new JPanel(new CardLayout());
		add(cardPanel, BorderLayout.CENTER);

		tabPanel = new TabPanel(this);
		add(tabPanel, BorderLayout.WEST);
		try {
			if(((Color)javax.swing.UIManager.get("Button.background")).getRed() <= 53)
				setIconImage((BufferedImage)ImageIO.read(getClass().getResourceAsStream("/omega_ide_icon64_dark.png")));
			else
				setIconImage((BufferedImage)ImageIO.read(getClass().getResourceAsStream("/omega_ide_icon64_pref.png")));
		}catch(Exception e) {}
	}

	public void addPanel(String name, Component c){
		cardPanel.add(name, c);
	}

	public void showPanel(String name){
		((CardLayout)cardPanel.getLayout()).show(cardPanel, name);
	}

	public static void refresh() {
		//Code_Assist
		CodeAssistPanel.showHintsBox.selected = DataManager.isContentAssistRealTime();
		CodeAssistPanel.ctrlSpBox.selected = !DataManager.isContentAssistRealTime();
		CodeAssistPanel.starImports.selected = DataManager.isUsingStarImports();
		//Project Panel
		if(Screen.getFileView().getProjectManager() != null) {
			try {
				String sdk = String.copyValueOf(Screen.getFileView().getProjectManager().jdkPath.toCharArray());
				if(sdk != null && new File(sdk).exists())
					ProjectPanel.sdkField.setText(sdk);
			}catch(Exception e) {}
		}
		//IDE Panel
		if(DataManager.getPathToJava() != null && new File(DataManager.getPathToJava()).exists())
			IDEPanel.sdkField.setText(DataManager.getPathToJava());
		else
			IDEPanel.sdkField.setText("Choose the folder containing the jdks");
		//Arguments
		if(Screen.getFileView().getProjectManager() != null) {
			PathPanel.cargPane.setText(Screen.getFileView().getProjectManager().compile_time_args);
			PathPanel.rargPane.setText(Screen.getFileView().getProjectManager().run_time_args);
		}
	}

	@Override
	public void setVisible(boolean value){
		if(value){
			refresh();
		}
		super.setVisible(value);
	}
}
