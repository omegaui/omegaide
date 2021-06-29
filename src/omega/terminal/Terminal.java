package omega.terminal;
import omega.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.awt.*;
import javax.swing.*;
import org.fife.ui.rsyntaxtextarea.*;
import static omega.utils.UIManager.*;
import static omega.settings.Screen.*;
public class Terminal extends JPanel {
	public static String shell = File.pathSeparator.equals(":") ? "sh" : "cmd";
	private String lastText;
	private RSyntaxTextArea outputArea;

	private Process shellProcess;
	private Scanner inputReader;
	private Scanner errorReader;
	private PrintWriter writer;
	
	public Terminal(){
		super(new BorderLayout());
		init();
	}
	
	public Terminal(String command){
		this();
		this.shell = command;
	}

	public void init(){
		outputArea = new RSyntaxTextArea();
		outputArea.setAutoscrolls(true);
		outputArea.setDragEnabled(false);
		outputArea.setAutoIndentEnabled(true);
		outputArea.setHyperlinksEnabled(true);
		outputArea.setHyperlinkForeground(glow);
		outputArea.setAntiAliasingEnabled(true);
		outputArea.setAnimateBracketMatching(true);
		outputArea.setBracketMatchingEnabled(true);
		outputArea.setFont(new Font(fontName, fontState, fontSize));
		try{
			omega.utils.Editor.getTheme().apply(outputArea);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		outputArea.addKeyListener(new KeyAdapter(){
			@Override
			public void keyPressed(KeyEvent e){
				int code = e.getKeyCode();
				if(code == KeyEvent.VK_UP || code == KeyEvent.VK_DOWN){
					e.consume();
					return;
				}
				else if(code == KeyEvent.VK_ENTER){
					if(writer == null || shellProcess == null || !shellProcess.isAlive())
						e.consume();
					String text = outputArea.getText();
					text = text.substring(0, outputArea.getCaretPosition());
					text = text.substring(text.lastIndexOf('\n') + 1);
					if(Screen.onWindows())
						outputArea.append("\n");
					writer.println(text);
					writer.flush();
				}
			}
		});
		add(new JScrollPane(outputArea), BorderLayout.CENTER);
	}

	public void launchTerminal(){
		new Thread(()->{
			try{
				shellProcess = new ProcessBuilder(shell).directory(new File(Screen.getFileView().getProjectPath())).start();
				inputReader = new Scanner(shellProcess.getInputStream());
				errorReader = new Scanner(shellProcess.getErrorStream());
				writer = new PrintWriter(shellProcess.getOutputStream());
				new Thread(()->{
					while(shellProcess.isAlive()){
						while(errorReader.hasNextLine())
							outputToTerminal(errorReader.nextLine());
					}
				}).start();
				while(shellProcess.isAlive()){
					while(inputReader.hasNextLine())
						outputToTerminal(inputReader.nextLine());
				}
				outputToTerminal("Shell Exited!");
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}).start();
	}

	public void outputToTerminal(String text){
		outputArea.append(text + "\n");
		outputArea.setCaretPosition(outputArea.getText().length());
	}

	public void exit(){
		shellProcess.destroyForcibly();
	}

	public org.fife.ui.rsyntaxtextarea.RSyntaxTextArea getOutputArea() {
		return outputArea;
	}
	public void setOutputArea(org.fife.ui.rsyntaxtextarea.RSyntaxTextArea outputArea) {
		this.outputArea = outputArea;
	}
	public java.lang.Process getShellProcess() {
		return shellProcess;
	}
	public void setShellProcess(java.lang.Process shellProcess) {
		this.shellProcess = shellProcess;
	}
	
}
