package omega.terminal.jediterm;
import java.awt.Color;

import java.nio.charset.Charset;

import com.jediterm.pty.PtyProcessTtyConnector;

import com.pty4j.PtyProcess;

import com.jediterm.terminal.TtyConnector;

import omega.Screen;

import java.util.HashMap;
import java.util.Map;

import com.jediterm.terminal.ui.JediTermWidget;

import omega.comp.FlexPanel;

import javax.swing.JPanel;

import static omega.utils.UIManager.*;
import static omega.comp.Animations.*;
public class JetTerminal extends JPanel{
	public FlexPanel panel;
	public JediTermWidget widget;
	public PtyProcess process;
	public String[] command;
	public String directory;
	
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
	public void layout(){
		relocate();
		super.layout();
	}
}
