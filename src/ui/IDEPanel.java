package ui;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFileChooser;
import javax.swing.JPanel;

import ide.Screen;
import ide.utils.DataManager;
import ide.utils.UIManager;
import say.swing.JFontChooser;

public class IDEPanel extends JPanel{
	private static info.Screen infoScreen;
	public static Box sdkField;
	public IDEPanel(View view){
		setLayout(null);

        ide.utils.UIManager.setData(this);
		JFileChooser fileChooser = new JFileChooser();
		sdkField = new Box("Choose the folder contaning the jdks", ()->{});
		sdkField.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(e.getButton() == 3) {
					fileChooser.setMultiSelectionEnabled(false);
					fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					fileChooser.setDialogTitle("Choose Path to the folder contaning the jdks");
					fileChooser.setApproveButtonText("Select This As Default Java Root");
					int res = fileChooser.showOpenDialog(view);
					if(res == JFileChooser.APPROVE_OPTION) {
						sdkField.setText(fileChooser.getSelectedFile().getAbsolutePath());
						DataManager.setPathToJava(sdkField.getText());
					}
				}
			}
		});
		sdkField.setBounds(1, 2, view.getWidth() - view.getWidth() / 3 - 2, 30);
		add(sdkField);

		JFontChooser fontC = new JFontChooser();
		Box changeFontBox = new Box("Chanage Editor Font", ()->{
			UIManager.setData(fontC);
			fontC.setSelectedFontStyle(Font.BOLD);
			fontC.setSelectedFont(new Font(UIManager.fontName, Font.BOLD, UIManager.fontSize));
			int res = fontC.showDialog(view);
			if(res ==JFontChooser.OK_OPTION) {
				Font font = fontC.getSelectedFont();
				UIManager.fontName = font.getName();
				UIManager.fontSize = font.getSize();
				Screen.getFileView().getScreen().getUIManager().save();
				Screen.getFileView().getScreen().loadThemes();
			}
		});
		changeFontBox.setBounds(1, sdkField.getY() + sdkField.getHeight() + 1, view.getWidth() - view.getWidth() / 3 - 2, 30);
		add(changeFontBox);

		Box plug = new Box("Plugin Manager", ()->{
			ide.Screen.getPluginView().setVisible(true);
		});
		plug.setBounds(1, changeFontBox.getY() + changeFontBox.getHeight() + 1, view.getWidth() - view.getWidth() / 3 - 2, 30);
		add(plug);

		Box license = new Box("Info", ()->{
			if(infoScreen == null)
				infoScreen = new info.Screen(Screen.getScreen());
			infoScreen.setVisible(true);
		});
		license.setBounds(1, plug.getY() + plug.getHeight() + 1, view.getWidth() - view.getWidth() / 3 - 2, 30);
		add(license);
	}
}
