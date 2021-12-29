package omega.instant.support.java.misc;
import omega.ui.component.Editor;

import omega.instant.support.java.assist.SourceReader;
import omega.instant.support.java.assist.DataMember;

import omegaui.component.FlexPanel;
import omegaui.component.NoCaretField;
import omegaui.component.TextComp;

import omega.Screen;

import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.Rectangle;

import java.util.LinkedList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import static omega.io.UIManager.*;
import static omegaui.component.animation.Animations.*;
public class JavaJumpToDefinitionPanel extends JPanel{
	
	private Editor editor;

	private FlexPanel containerPanel;
	
	private JScrollPane scrollPane;
	
	private JPanel panel;

	private NoCaretField searchField;

	private LinkedList<TextComp> definitions = new LinkedList<>();
	
	public JavaJumpToDefinitionPanel(Editor editor){
		super(null);
		setBackground(c2);
		
		this.editor = editor;

		containerPanel = new FlexPanel(null, back1, null);
		containerPanel.setArc(10, 10);
		
		scrollPane = new JScrollPane(panel = new JPanel(null));
		scrollPane.setBorder(null);
		scrollPane.setBackground(c2);
		
		panel.setBackground(c2);
		
		containerPanel.add(scrollPane);
		add(containerPanel);

		searchField = new NoCaretField("", "Search Members", TOOLMENU_COLOR3, back3, TOOLMENU_COLOR2);
		searchField.setFont(PX14);
		searchField.setOnAction(()->{
			genDefinitions(searchField.getText());
		});
		add(searchField);
		
		setVisible(false);
	}

	@Override
	public void layout(){
		containerPanel.setBounds(5, 5, getWidth() - 10, getHeight() - 40);
		scrollPane.setBounds(5, 5, containerPanel.getWidth() - 10, containerPanel.getHeight() - 10);
		searchField.setBounds(5, getHeight() - 25, getWidth() - 10, 25);
		super.layout();
	}

	public void genDefinitions(String match){
		try{
			definitions.forEach(panel::remove);
			definitions.clear();
			
			SourceReader reader = new SourceReader(editor.getText());
			
			Graphics g = Screen.getScreen().getGraphics();
			g.setFont(editor.getFont());
			
			int block = 0;
			int min_width = 0;
			int min_height = g.getFontMetrics().getHeight() + 10;
			
			for(DataMember dx : reader.ownedDataMembers){
				if(!dx.getRepresentableValue().contains(match))
					continue;
				int width = g.getFontMetrics().stringWidth(dx.name + " - " + dx.type);
				if(width > min_width)
					min_width = width + 15;
			}
			
			for(DataMember dx : reader.ownedDataMembers) {
				if(!dx.getRepresentableValue().contains(match))
					continue;
				if(!dx.isMethod()){
					TextComp comp = new TextComp(dx.name + " - " + dx.type, TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR2, ()->{
						editor.setCaretPosition(JavaCodeNavigator.getLineOffset(editor, dx.lineNumber));
						setVisible(false);
					});
					comp.setBounds(0, block, min_width, min_height);
					comp.setFont(editor.getFont());
					comp.setArc(0, 0);
					comp.alignX = 5;
					panel.add(comp);
					definitions.add(comp);
					block += min_height;
				}
			}
			
			for(DataMember dx : reader.ownedDataMembers) {
				if(!dx.getRepresentableValue().contains(match))
					continue;
				if(dx.isMethod()){
					TextComp comp = new TextComp(dx.name + " - " + dx.type, TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR3, ()->{
						editor.setCaretPosition(JavaCodeNavigator.getLineOffset(editor, dx.lineNumber));
						try{
							editor.getAttachment().getVerticalScrollBar().setValue(JavaCodeNavigator.getLineOffsetForView(editor, dx.lineNumber));
						}
						catch(Exception e){}
						setVisible(false);
					});
					comp.setBounds(0, block, min_width, min_height);
					comp.setFont(editor.getFont());
					comp.setArc(0, 0);
					comp.alignX = 5;
					panel.add(comp);
					definitions.add(comp);
					block += min_height;
				}
			}
			
			panel.setPreferredSize(new Dimension(min_width + 10, block));

			setSize(min_width + 5, block + 10 + 30);

			if(getWidth() > 400)
				setSize(400 + 20, getHeight());
			if(getHeight() > 350)
				setSize(getWidth(), 350);

			relocate();
			scrollPane.getVerticalScrollBar().setValue(0);
			scrollPane.getVerticalScrollBar().repaint();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public void relocate(){
		Rectangle rect = editor.getAttachment().getViewport().getViewRect();
		setLocation(rect.x + rect.width/2 - getWidth()/2, rect.y + rect.height/2 - getHeight()/2);
		layout();
	}

	@Override
	public void setVisible(boolean value){
		if(value){
	          genDefinitions("");
		}
	     super.setVisible(value);
	}
}
