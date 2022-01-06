package omega.ui.dialog;
import java.awt.Dimension;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.io.File;

import omega.ui.component.SearchComp;

import java.util.LinkedList;

import omega.Screen;

import omega.io.IconManager;
import omega.io.RecentsManager;

import omegaui.component.TextComp;
import omegaui.component.NoCaretField;
import omegaui.component.FlexPanel;

import java.awt.geom.RoundRectangle2D;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JScrollBar;

import static omega.io.UIManager.*;
import static omegaui.component.animation.Animations.*;

public class RecentsDialog extends JDialog{
	
	private TextComp titleComp;
	private TextComp filesComp;
	private TextComp projectsComp;
	private TextComp closeComp;
	
	private LinkedList<SearchComp> searchComps = new LinkedList<>();
	private LinkedList<SearchComp> currentComps = new LinkedList<>();
	
	private FlexPanel containerPanel;
	
	private JPanel panel;
	
	private JScrollPane scrollPane;
	
	private NoCaretField field;
	
	private TextComp infoComp;
	
	private int blockY;
	
	private int pointer;
	
	public RecentsDialog(Screen screen){
		super(screen, true);
		setTitle("Recents Dialog");
		setUndecorated(true);
		setSize(500, 400);
		setLocationRelativeTo(null);
		setAlwaysOnTop(true);
		setResizable(false);
		JPanel panel = new JPanel(null);
		panel.setBackground(c2);
		setContentPane(panel);
		init();
	}
	
	public void init(){
		field = new NoCaretField("", "Type File Name", TOOLMENU_COLOR2, c2, TOOLMENU_COLOR3);
		field.setBounds(0, 25, getWidth(), 30);
		field.setFont(PX16);
		field.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(!currentComps.isEmpty()) {
					if(e.getKeyCode() == KeyEvent.VK_UP && pointer > 0) {
						currentComps.get(pointer).set(false);
						currentComps.get(--pointer).set(true);
						scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getValue() - 50);
					}
					else if(e.getKeyCode() == KeyEvent.VK_DOWN && pointer + 1 < currentComps.size()) {
						currentComps.get(pointer).set(false);
						currentComps.get(++pointer).set(true);
						scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getValue() + 50);
					}
					else if(e.getKeyCode() == KeyEvent.VK_ENTER) {
						currentComps.get(pointer).mousePressed(null);
					}
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() != KeyEvent.VK_UP && e.getKeyCode() != KeyEvent.VK_DOWN && e.getKeyCode() != KeyEvent.VK_ENTER)
					genView(field.getText());
			}
		});
		add(field);
		addKeyListener(field);
		
		containerPanel = new FlexPanel(null, back1, null);
		containerPanel.setBounds(5, 60, getWidth() - 10, getHeight() - 70 - 30);
		containerPanel.setArc(10, 10);
		add(containerPanel);
		
		scrollPane = new JScrollPane(panel = new JPanel(null));
		scrollPane.setBackground(back2);
		scrollPane.setBounds(5, 5, containerPanel.getWidth() - 10, containerPanel.getHeight() - 10);
		scrollPane.setBorder(null);
		panel.setBackground(c2);
		panel.setSize(scrollPane.getWidth() - 5, 100);
		scrollPane.setHorizontalScrollBar(new JScrollBar(JScrollBar.HORIZONTAL){
			@Override
			public void setVisible(boolean value){
				super.setVisible(false);
			}
		});
		containerPanel.add(scrollPane);
		
		infoComp = new TextComp("", c2, c2, glow, null);
		infoComp.setBounds(0, getHeight() - 25, getWidth(), 25);
		infoComp.setFont(PX14);
		infoComp.setArc(0, 0);
		infoComp.setClickable(false);
		infoComp.alignX = 10;
		add(infoComp);
		
		titleComp = new TextComp("Quick Open Recents Files / Projects", c2, c2, glow, null);
		titleComp.setBounds(0, 0, getWidth() - 90, 30);
		titleComp.setFont(PX14);
		titleComp.setArc(0, 0);
		titleComp.setClickable(false);
		titleComp.attachDragger(this);
		add(titleComp);
		
		filesComp = new TextComp(IconManager.fluentfileImage, 20, 20, "Clear File List!",TOOLMENU_COLOR3_SHADE, c2, c2, ()->{
			RecentsManager.removeAllFiles();
			initView();
		});
		filesComp.setBounds(getWidth() - 90, 0, 30, 30);
		filesComp.setArc(0, 0);
		add(filesComp);
		
		projectsComp = new TextComp(IconManager.fluentfolderImage, 20, 20, "Clear Project List!", TOOLMENU_COLOR1_SHADE, c2, c2, ()->{
			RecentsManager.removeAllProjects();
			initView();
		});
		projectsComp.setBounds(getWidth() - 60, 0, 30, 30);
		projectsComp.setArc(0, 0);
		add(projectsComp);
		
		closeComp = new TextComp(IconManager.fluentcloseImage, 20, 20, TOOLMENU_COLOR2_SHADE, c2, c2, this::dispose);
		closeComp.setBounds(getWidth() - 30, 0, 30, 30);
		closeComp.setArc(0, 0);
		add(closeComp);
		
		putAnimationLayer(filesComp, getImageSizeAnimationLayer(20, +5, true), ACTION_MOUSE_ENTERED);
		putAnimationLayer(projectsComp, getImageSizeAnimationLayer(20, +5, true), ACTION_MOUSE_ENTERED);
		putAnimationLayer(closeComp, getImageSizeAnimationLayer(20, +5, true), ACTION_MOUSE_ENTERED);
	}
	
	public void initView(){
		try{
			currentComps.forEach(panel::remove);
			currentComps.clear();
			searchComps.clear();
			
			blockY = 0;
			File file;
			//Creating Directory Comps
			for(String path : RecentsManager.RECENTS){
				file = new File(path);
				if(!file.exists() || !file.isDirectory())
					continue;
				
				SearchComp comp = new SearchComp(this, file){
					@Override
					public String getExtension(){
						return "Project";
					}
				};
				comp.setBounds(0, blockY, panel.getWidth(), 50);
				comp.initUI();
				comp.setClickAction(()->Screen.getScreen().loadProject(comp.getFile()));
				panel.add(comp);
				searchComps.add(comp);
				currentComps.add(comp);
				
				blockY += 50;
			}
			
			//Creating Files Comps
			for(String path : RecentsManager.RECENTS){
				file = new File(path);
				if(!file.exists() || file.isDirectory())
					continue;
				
				SearchComp comp = new SearchComp(this, file);
				comp.setBounds(0, blockY, panel.getWidth(), 50);
				comp.initUI();
				panel.add(comp);
				searchComps.add(comp);
				currentComps.add(comp);
				
				blockY += 50;
			}
			
			panel.setPreferredSize(new Dimension(scrollPane.getWidth() - 5, blockY));
			scrollPane.repaint();
			scrollPane.getVerticalScrollBar().setVisible(true);
			scrollPane.getVerticalScrollBar().setValue(0);
			repaint();
			
			if(!currentComps.isEmpty()) {
				currentComps.get(pointer = 0).set(true);
				infoComp.setText(currentComps.size() + " File" + (currentComps.size() > 1 ? "s" : "") + " Found!");
			}
			else{
				infoComp.setText("Not at least One File Found!");
			}
			doLayout();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void genView(String match){
		currentComps.forEach(panel::remove);
		currentComps.clear();
		
		blockY = 0;
		
		for(SearchComp comp : searchComps){
			if(comp.getName().contains(match)){
				
				comp.setLocation(0, blockY);
				panel.add(comp);
				currentComps.add(comp);
				
				blockY += 50;
			}
		}
		
		panel.setPreferredSize(new Dimension(scrollPane.getWidth() - 5, blockY));
		scrollPane.repaint();
		scrollPane.getVerticalScrollBar().setVisible(true);
		scrollPane.getVerticalScrollBar().setValue(0);
		repaint();
		
		if(!currentComps.isEmpty()) {
			currentComps.get(pointer = 0).set(true);
			infoComp.setText(currentComps.size() + " File" + (currentComps.size() > 1 ? "s" : "") + " Found!");
		}
		else{
			infoComp.setText("Not at least One File Found!");
		}
		doLayout();
	}
	
	@Override
	public void setVisible(boolean value){
		if(value){
			initView();
		}
		super.setVisible(value);
	}
	
	@Override
	public void setSize(int width, int height){
		super.setSize(width, height);
		setShape(new RoundRectangle2D.Double(0, 0, width, height, 20, 20));
	}
}
