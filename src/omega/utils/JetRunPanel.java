package omega.utils;
import com.jediterm.terminal.model.TerminalLine;

import java.awt.image.BufferedImage;

import java.io.PrintWriter;

import omega.terminal.jediterm.JetTerminal;

import omega.comp.FlexPanel;
import omega.comp.TextComp;

import javax.swing.JPanel;

import static omega.utils.UIManager.*;
import static omega.comp.Animations.*;
public class JetRunPanel extends JPanel {
	private FlexPanel actionPanel;
	private TextComp runComp;
	private TextComp instantRunComp;
	private TextComp clearComp;
	private TextComp killComp;
	
	private FlexPanel runTextAreaPanel;
	public JetTerminal terminalPanel;
	
	private boolean logMode = false;
	private boolean processTerminal = false;

	private Runnable reRunAction = ()->{};
	private Runnable reRunDynamicallyAction = ()->{};

	private String[] command;
	private String directory;
	
	public JetRunPanel(boolean processTerminal, String[] command, String directory){
		super(null);
		this.processTerminal = processTerminal;
		this.command = command;
		this.directory = directory;
		setBackground(c2);
		init();
	}
	
	public void init(){
		actionPanel = new FlexPanel(null, back1, null);
		actionPanel.setArc(10, 10);
		add(actionPanel);
		
		runComp = new TextComp(IconManager.fluentrunImage, 20, 20, "Re-Run", TOOLMENU_COLOR3_SHADE, back2, TOOLMENU_COLOR3, this::reRun);
		actionPanel.add(runComp);
		
		instantRunComp = new TextComp(IconManager.fluentrocketImage, 20, 20, "Re-Run(Dynamic)", TOOLMENU_COLOR3_SHADE, back2, TOOLMENU_COLOR3, this::reRunDynamically);
		instantRunComp.setVisible(processTerminal);
		actionPanel.add(instantRunComp);
		
		clearComp = new TextComp(IconManager.fluentclearImage, 20, 20, "Clear Terminal", TOOLMENU_COLOR3_SHADE, back2, TOOLMENU_COLOR3, this::clearTerminal);
		actionPanel.add(clearComp);
		
		killComp = new TextComp(IconManager.fluentcloseImage, 15, 15, "Kill Process", TOOLMENU_COLOR3_SHADE, back2, TOOLMENU_COLOR3, this::killProcess);
		actionPanel.add(killComp);
		
		runTextAreaPanel = new FlexPanel(null, back1, null);
		runTextAreaPanel.setArc(10, 10);
		terminalPanel = new JetTerminal(command, directory);
		runTextAreaPanel.add(terminalPanel);
		add(runTextAreaPanel);
		
		putAnimationLayer(runComp, getImageSizeAnimationLayer(25, 5, true), ACTION_MOUSE_ENTERED);
		putAnimationLayer(clearComp, getImageSizeAnimationLayer(25, 5, true), ACTION_MOUSE_ENTERED);
		putAnimationLayer(killComp, getImageSizeAnimationLayer(25, 5, true), ACTION_MOUSE_ENTERED);
	}

	public void launchAsTerminal(Runnable r, BufferedImage image, String toolTip){
		runComp.setRunnable(r);
		runComp.image = image;
		runComp.setToolTipText(toolTip);
		repaint();
	}

	public void reRun(){
		reRunAction.run();
	}
	
	public void reRunDynamically(){
		reRunDynamicallyAction.run();
	}

	public JetRunPanel reRunAction(Runnable action){
		this.reRunAction = action;
		return this;
	}

	public JetRunPanel reRunDynamicallyAction(Runnable action){
		this.reRunDynamicallyAction = action;
		return this;
	}
	
	public void clearTerminal(){
		terminalPanel.widget.getTerminal().clearScreen();
		terminalPanel.widget.getTerminal().cursorPosition(0, 0);
	}

	public String getText(){
		return terminalPanel.widget.getTerminalTextBuffer().getScreenLines();
	}

	public void start(){
		terminalPanel.start();
	}
	
	public void killProcess(){
		if(terminalPanel.process != null && terminalPanel.process.isAlive()){
			try{
				terminalPanel.process.destroyForcibly();
			}
			catch(Exception e){
				
			}
		}
	}
	
	public void setLogMode(boolean logMode){
		this.logMode = logMode;
		actionPanel.setVisible(true);
	}
	
	public void print(String text){
		terminalPanel.widget.getTerminal().writeCharacters(text);
		terminalPanel.widget.getTerminal().nextLine();
		
		//layout();
	}
	
	public void printText(String text){
		print(text);
	}
	
	public void relocate(){
		if(!logMode){
			actionPanel.setBounds(5, 5, 30, getHeight() - 10);
			runComp.setBounds(3, 5, 25, 25);
			instantRunComp.setBounds(3, 32, 25, 25);
			clearComp.setBounds(3, processTerminal ? 60 : 32, 25, 25);
			killComp.setBounds(3, processTerminal ? 87 : 60, 25, 25);
		}
		if(logMode)
			runTextAreaPanel.setBounds(5, 5, getWidth() - 10, getHeight() - 10);
		else
			runTextAreaPanel.setBounds(40, 5, getWidth() - 50, getHeight() - 10);
		terminalPanel.setBounds(5, 5, runTextAreaPanel.getWidth() - 10, runTextAreaPanel.getHeight() - 10);
	}
	
	@Override
	public void layout(){
		relocate();
		super.layout();
	}
}
