package importUI;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;

import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import ide.Screen;
import ide.utils.Editor;
import ide.utils.UIManager;
import ide.utils.systems.View;
import importIO.ImportManager;

public class ImportSelector extends View implements KeyListener{

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
		textField.addKeyListener(this);
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
	public void keyTyped(KeyEvent e){}

	@Override
	public void keyPressed(KeyEvent e){}

	@Override
	public void keyReleased(KeyEvent e)
	{

	}

	@Override
	public void paint(Graphics g)
	{
		super.paint(g);
		scrollPane.repaint();
		textArea.repaint();
		textField.repaint();
		choice.repaint();
	}
}
