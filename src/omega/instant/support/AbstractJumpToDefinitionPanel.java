package omega.instant.support;
import omega.ui.component.Editor;

import java.awt.BorderLayout;

import omegaui.component.FlexPanel;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import static omega.io.UIManager.*;
import static omegaui.component.animation.Animations.*;

public abstract class AbstractJumpToDefinitionPanel extends JPanel{
	public Editor editor;

	public FlexPanel flexPanel;
	public JPanel panel;
	public JScrollPane scrollPane;

	public AbstractJumpToDefinitionPanel(Editor editor){
		super(new BorderLayout());
		this.editor = editor;

		setBackground(c2);

		flexPanel = new FlexPanel(null, TOOLMENU_COLOR3_SHADE, null);
		flexPanel.setArc(0, 0);
		add(flexPanel, BorderLayout.CENTER);
		
		panel = new JPanel(null);
		panel.setBackground(c2);
		
		flexPanel.add(scrollPane = new JScrollPane(panel));
		
		scrollPane.setBackground(c2);
	}

	public abstract void reload(String match);
	public abstract boolean canRead(Editor editor);

	public void reload(){
		reload("");
	}

	@Override
	public void layout(){
		flexPanel.setBounds(0, 0, getWidth(), getHeight());
		scrollPane.setBounds(5, 5, flexPanel.getWidth() - 10, flexPanel.getHeight() - 10);
		super.layout();
	}
}
