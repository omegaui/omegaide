package omega.tabPane;
import omega.utils.Editor;

import java.awt.BorderLayout;

import omega.comp.FlexPanel;

import javax.swing.JPanel;

import static omega.utils.UIManager.*;
import static omega.comp.Animations.*;
public class EditorTabHolder extends JPanel{
	private Editor editor;
	
	private FlexPanel contentPanel;
	private JPanel editorPanel;
	
	public EditorTabHolder(Editor editor){
		super(null);
		this.editor = editor;
		setBorder(null);
		setBackground(c2);
		init();
	}
	
	public void init(){
		contentPanel = new FlexPanel(null, back1, null);
		contentPanel.setArc(10, 10);
		add(contentPanel);

		editorPanel = new JPanel(new BorderLayout());
		editorPanel.setBackground(c2);
		editorPanel.setBorder(null);
		
		editorPanel.add(editor.getAttachment(), BorderLayout.CENTER);
		editorPanel.add(editor.getFAndR(), BorderLayout.NORTH);

		contentPanel.add(editorPanel);
	}
	
	public void relocate(){
		contentPanel.setBounds(5, 5, getWidth() - 10, getHeight() - 10);
		editorPanel.setBounds(5, 5, contentPanel.getWidth() - 10, contentPanel.getHeight() - 10);
	}
	
	@Override
	public void layout(){
		relocate();
		super.layout();
	}
}
