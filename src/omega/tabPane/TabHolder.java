package omega.tabPane;
import omega.utils.Editor;

import java.awt.BorderLayout;

import omega.comp.FlexPanel;

import javax.swing.JPanel;
import javax.swing.JComponent;

import static omega.utils.UIManager.*;
import static omega.comp.Animations.*;
public class TabHolder extends JPanel{
	public JComponent component;
	
	private FlexPanel contentPanel;
	private JPanel mainPanel;
	
	public TabHolder(JComponent component){
		super(null);
		this.component = component;
		setBorder(null);
		setBackground(c2);
		init();
	}
	
	public void init(){
		contentPanel = new FlexPanel(null, back1, null);
		contentPanel.setArc(10, 10);
		add(contentPanel);

		mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBackground(c2);
		mainPanel.setBorder(null);

		if(!(component instanceof Editor))
			mainPanel.add(component, BorderLayout.CENTER);

		contentPanel.add(mainPanel);
	}

	public JPanel getMainPanel(){
		return mainPanel;
	}
	
	public void relocate(){
		contentPanel.setBounds(5, 5, getWidth() - 10, getHeight() - 10);
		mainPanel.setBounds(5, 5, contentPanel.getWidth() - 10, contentPanel.getHeight() - 10);
	}
	
	@Override
	public void layout(){
		relocate();
		super.layout();
	}
}
