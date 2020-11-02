package ui;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JTextField;

import contentUI.ContentManager;
import ide.utils.DataManager;
public class CodeAssistPanel extends JPanel{
	public static final Font font = new Font("Ubuntu Mono", Font.PLAIN, 14);
	public static ToggleBox showHintsBox;
	public static ToggleBox ctrlSpBox;
	public static ToggleBox starImports;

	public CodeAssistPanel(View view){
		setLayout(null);
          ide.utils.UIManager.setData(this);
        
		showHintsBox = new ToggleBox("Real-time suggestions", ()->{
			ctrlSpBox.selected = !showHintsBox.selected;
			ctrlSpBox.repaint();
		});
		showHintsBox.selected = true;
		showHintsBox.setBounds(1, 2, view.getWidth() - view.getWidth() / 3 - 2, 30);
		add(showHintsBox);

		ctrlSpBox = new ToggleBox("Do not Show Suggestions", ()->{
			showHintsBox.selected = !ctrlSpBox.selected;
			showHintsBox.repaint();
		});
		ctrlSpBox.setBounds(1, showHintsBox.getY() + showHintsBox.getHeight() + 1, view.getWidth() - view.getWidth() / 3 - 2, 30);
		add(ctrlSpBox);

		starImports = new ToggleBox("Use Asterisk Imports When Importing", ()->{
			
		});
		starImports.setBounds(1, ctrlSpBox.getY() + ctrlSpBox.getHeight() + 1, view.getWidth() - view.getWidth() / 3 - 2, 30);
		add(starImports);

		JTextField hintsField = new JTextField("Enter Hint-Code Here");
		hintsField.setBounds(1, starImports.getY() + starImports.getHeight() + 1, view.getWidth() - view.getWidth() / 3 - 2, 30);
		add(hintsField);

		JTextField typeField = new JTextField("Enter Hint-Type Here");
		typeField.setBounds(1, hintsField.getY() + hintsField.getHeight() + 1, view.getWidth() - view.getWidth() / 3 - 2, 30);
		add(typeField);

          if(((Color)javax.swing.UIManager.get("Button.background")).getRed() > 53){
               hintsField.setBackground(Color.WHITE);
               typeField.setBackground(Color.WHITE);
          }
          else{
               ide.utils.UIManager.setData(hintsField);
               ide.utils.UIManager.setData(typeField);
               hintsField.setFont(font);
               typeField.setFont(font);
          }

		Box addH = new Box("Add Custom Hint", ()->{
			boolean res = ContentManager.addContent(hintsField.getText(), typeField.getText());
			if(!res) {
				hintsField.setText("Code already exists in IDE ContentAssembly");
			}
			else {
				hintsField.setText("Enter another Hint-Code here");
				typeField.setText("Enter Hint-Type here");
			}
		});
		addH.setBounds((view.getWidth() - view.getWidth() / 3 - 2)/2 - 150/2, 2 + typeField.getY() + typeField.getHeight() + 1, 150, 30);
		add(addH);
	}
}
