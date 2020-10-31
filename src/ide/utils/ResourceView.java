package ide.utils;

import java.awt.BorderLayout;
import java.awt.Choice;

import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import ide.Screen;
import ide.utils.systems.View;

public class ResourceView extends View {

	private RTextArea textArea;
	private Choice choice;
	
	public ResourceView(Screen screen) {
		super("View/Remove Resource Roots of This Project", screen);
		setLayout(new BorderLayout());
		setSize(500, 400);
		setLocationRelativeTo(null);
		init();
		setAction(this::read);
	}
	
	private void init() {
		textArea = new RTextArea();
		textArea.setHighlightCurrentLine(false);
		textArea.setEditable(false);
		RTextScrollPane sp = new RTextScrollPane(textArea);
		sp.setLineNumbersEnabled(true);
		add(sp, BorderLayout.CENTER);
		UIManager.setData(textArea);
		comps.add(sp);
		
		choice = new Choice();
		choice.addItemListener((e)->{
			if(choice.getSelectedItem().equals("Remove All Resource Roots")){
				choice.removeAll();
				textArea.setText("");
				ResourceManager.roots.clear();
			}
			else {
				String item = choice.getSelectedItem();
				ResourceManager.roots.remove(item);
				System.out.println("Removing Resource-Roots "+item);
			}
			Screen.getFileView().getResourceManager().saveData();
			read();
			repaint();
		});
		add(choice, BorderLayout.SOUTH);
		UIManager.setData(choice);
	}
	
	public void read() {
		choice.removeAll();
		choice.addItem("Resource Roots");
		choice.addItem("Remove All Resource Roots");
		textArea.setText("");
		ResourceManager.roots.forEach((e)->{
			choice.addItem(e);
			textArea.append(e+"\n");
		});
	}
}