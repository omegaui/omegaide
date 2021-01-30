package importUI;
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
import ide.utils.UIManager;
import ide.Screen;
import ide.utils.systems.View;
import java.awt.Graphics;
import importIO.ImportManager;
import ide.utils.Editor;
import java.awt.BorderLayout;
import javax.swing.JTextField;
import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
import java.awt.Choice;

public class ImportSelector extends View {

	private Choice choice;
	private static final String INSTRUCT = "Select An Import";
	private RTextScrollPane scrollPane;
	private RTextArea textArea;
	private JTextField textField;

	public ImportSelector(Screen window) {
		super("Select a type to add Import to the Current Editor", window);
		setSize(500, 400);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());
		init();
		setAction(()->{
			try {
				Editor editor = getScreen().getTabPanel().getCurrentEditor();
				if(editor.currentFile == null) {
					setTitle("Select a type to add Import to the Current Editor");
					return;
				}
				setTitle("Select a type to add Import to "+editor.currentFile.getName());
			}catch(Exception e) {setTitle("No Editor Selected!");}
		});
	}

	private void init()
	{
		textArea = new RTextArea();
		textArea.setEditable(false);
		textArea.setHighlightCurrentLine(false);
		UIManager.setData(textArea);
		add((scrollPane = new RTextScrollPane(textArea)), BorderLayout.CENTER);
		scrollPane.setLineNumbersEnabled(true);
		scrollPane.setWheelScrollingEnabled(true);

		textField = new JTextField();
		textField.addActionListener((e)->showImportsFor(textField.getText()));
		UIManager.setData(textField);
		add(textField, BorderLayout.NORTH);

		choice = new Choice();
		choice.addItemListener((e)->{

			if(choice.getSelectedItem().equals(INSTRUCT))
				return;

			textField.setText(choice.getSelectedItem());
			Editor editor = getScreen().getTabPanel().getCurrentEditor();
			if(editor.getText().startsWith("package"))
			{
				int index = editor.getText().indexOf(';');
				editor.insert("\nimport "+choice.getSelectedItem()+";", index+1);
			}
			else
				editor.insert("import "+choice.getSelectedItem()+";\n", 0);
		});
		UIManager.setData(choice);
		add(choice, BorderLayout.SOUTH);
	}

	private boolean var0;

	public synchronized void showImportsFor(String value)
	{
		var0 = true;
		textArea.setText("");
		choice.removeAll();
		choice.addItem(INSTRUCT);
		try {
			ImportManager.getAllImports().forEach(im->{
				String import0 = im.getImport();
				if(import0.endsWith("."))
					import0 = import0.substring(0, import0.length() - 1);
				if(import0.contains(value))
				{
					var0 = false;
					print(import0);
				}
			});
		}catch(Exception e) {}
		if(var0)
			textArea.setText("No Dependencies founded containing "+value);
		textArea.repaint();
	}

	private void print(String text) {
		if(!textArea.getText().contains(text+"\n")) {
			textArea.append(text+"\n");
			choice.addItem(text);
		}
	}
    
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		scrollPane.repaint();
		textArea.repaint();
		textField.repaint();
		choice.repaint();
	}
}
