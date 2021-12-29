package omega.ui.component.jediterm;
import omegaui.component.FlexPanel;

import java.awt.event.FocusListener;

import java.awt.Color;

import java.nio.charset.Charset;

import com.jediterm.pty.PtyProcessTtyConnector;

import com.pty4j.PtyProcess;

import com.jediterm.terminal.TtyConnector;

import omega.Screen;

import java.util.HashMap;
import java.util.Map;

import com.jediterm.terminal.ui.JediTermWidget;

import javax.swing.JPanel;

import static omega.io.UIManager.*;
import static omegaui.component.animation.Animations.*;
public class JetTerminal extends JPanel{
	public FlexPanel panel;
	public JediTermWidget widget;
	public PtyProcess process;
	public String[] command;
	public String directory;
	private Runnable onProcessExited;
	
	public JetTerminal(){
		super(null);
		this.directory = Screen.getFileView().getProjectPath();
		init();
	}
	
	public JetTerminal(String[] command, String directory){
		super(null);
		this.command = command;
		this.directory = directory;
		init();
	}
	
	public void init(){
		setBackground(back2);
		
		panel = new FlexPanel(null, back1, null);
		panel.setArc(10, 10);
		add(panel);

		JetTermSettingsProvider jtsp = new JetTermSettingsProvider();
		widget = new JediTermWidget(jtsp);

		if(command == null)
			widget.setTtyConnector(getConnector(Screen.onWindows() ? "cmd.exe" : "/bin/bash"));
		else
			widget.setTtyConnector(getConnector(command));
		
		panel.add(widget);
	}
	
	public TtyConnector getConnector(String... command){
		try{
			this.command = command;
			
			Map<String, String> envsX = System.getenv();
			HashMap<String, String> envs = new HashMap<>();
			for(String x : envsX.keySet()){
				envs.put(x, envsX.get(x));
			}
			
			if(!Screen.onWindows())
				envs.put("TERM", "xterm");
			
			process = PtyProcess.exec(command, envs, directory);
			return new PtyProcessTtyConnector(process, Charset.forName("UTF-8"));
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public void start(){
		widget.start();
		if(onProcessExited != null){
			new Thread(()->{
				while(process.isAlive());
				onProcessExited.run();
			}).start();
		}
	}

	public void exit(){
		if(process != null && process.isAlive())
			process.destroyForcibly();
	}
	
	public void relocate(){
		panel.setBounds(5, 5, getWidth() - 10, getHeight() - 10);
		widget.setBounds(5, 5, panel.getWidth() - 10, panel.getHeight() - 10);
	}

	@Override
	public void addFocusListener(FocusListener focusListener){
		super.addFocusListener(focusListener);
		widget.getTerminalPanel().addFocusListener(focusListener);
	}

	@Override
	public void layout(){
		relocate();
		super.layout();
	}

	public java.lang.Runnable getOnProcessExited() {
		return onProcessExited;
	}
	
	public void setOnProcessExited(java.lang.Runnable onProcessExited) {
		this.onProcessExited = onProcessExited;
	}
	
}
