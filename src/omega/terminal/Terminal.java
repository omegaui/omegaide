package omega.terminal;
import omega.utils.*;
import org.fife.ui.rsyntaxtextarea.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import javax.swing.*;
import static omega.utils.UIManager.*;
import static omega.settings.Screen.*;
public class Terminal extends RSyntaxTextArea{
	public Process shellProcess;
	public PrintWriter writer;
	public Scanner inputReader;
	public Scanner errorReader;
	public String shell = File.pathSeparator.equals(":") ? "sh" : "cmd";
	public volatile boolean lineChange = false;
	public volatile boolean launched = false;
	public volatile boolean printed = false;
	public File workingDirectory = new File(System.getProperty("user.home"));
	public String cmd;
	public String inputToken = "";
	public LinkedList<String> inputs = new LinkedList<>();
	public int pointer = -1;
	
	
	public Terminal(){
		setFont(new Font(fontName, fontState, fontSize));
		setSyntaxEditingStyle(Editor.SYNTAX_STYLE_UNIX_SHELL);
		addKeyListener(new KeyAdapter(){
			@Override
			public void keyPressed(KeyEvent e){
				int code = e.getKeyCode();
				char ch = e.getKeyChar();
				if(code == KeyEvent.VK_UP){
					
					e.consume();
				}
				else if(code == KeyEvent.VK_DOWN){
					
					e.consume();
				}
				if(code == KeyEvent.VK_BACK_SPACE){
					if(inputToken.length() > 0){
						if(inputToken.length() - 1 == 0)
							inputToken = "";
						else
							inputToken = inputToken.substring(0, inputToken.length() - 1);
					}
				}
				if(code == KeyEvent.VK_ENTER){
					if(inputToken.equals(""))
						return;
					inputs.add(inputToken);
					write(inputToken);
					inputToken = "";
					pointer = -1;
					e.consume();
				}
				else if(Character.isLetterOrDigit(ch) || isSymbol(ch))
					inputToken += ch;
			}
		});
	}
	public Terminal(String cmd){
		this();
		this.cmd = cmd;
	}
	public void start(){
		if(launched)
			return;
		launched = true;
		
		Editor.getTheme().apply(this);
		
		new Thread(()->{
			try{
				ProcessBuilder processBuilder = new ProcessBuilder(shell);
				processBuilder.directory(workingDirectory);
				processBuilder.environment().put("TERM", "xterm-256color");
				shellProcess = processBuilder.start();
				writer = new PrintWriter(shellProcess.getOutputStream());
				inputReader = new Scanner(shellProcess.getInputStream());
				errorReader = new Scanner(shellProcess.getErrorStream());
				new Thread(()->{
					while(shellProcess.isAlive()){
						while(errorReader.hasNextLine()){
							print(errorReader.nextLine());
						}
					}
				}).start();
				while(shellProcess.isAlive()){
					while(inputReader.hasNextLine()){
						print(inputReader.nextLine());
					}
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}).start();
	}
	public void startOnce(){
		if(launched)
			return;
		launched = true;
		
		Editor.getTheme().apply(this);
		
		new Thread(()->{
			try{
				ProcessBuilder processBuilder = new ProcessBuilder(shell);
				processBuilder.directory(workingDirectory);
				processBuilder.environment().put("TERM", "xterm-256color");
				shellProcess = processBuilder.start();
				writer = new PrintWriter(shellProcess.getOutputStream());
				inputReader = new Scanner(shellProcess.getInputStream());
				errorReader = new Scanner(shellProcess.getErrorStream());
				writer.println(cmd);
				writer.close();
				new Thread(()->{
                         while(shellProcess.isAlive()){
                              while(errorReader.hasNextLine()){
                                   print(errorReader.nextLine());
                              }
                         }
                    }).start();
                    while(shellProcess.isAlive()){
                         while(inputReader.hasNextLine()){
                              print(inputReader.nextLine());
                         }
                    }
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}).start();
	}
	public void print(String text){
		append("\n" + text);
		inputToken = "";
		setCaretPosition(getText().length());
	}
	public void write(String text){
		if(writer == null)
			return;
		if(text.equals("clear") || text.equalsIgnoreCase("clr")){
			setText("");
			return;
		}
		writer.println(text);
		writer.flush();
	}
	public java.io.File getWorkingDirectory() {
		return workingDirectory;
	}
	
	public void setWorkingDirectory(java.io.File workingDirectory) {
		this.workingDirectory = workingDirectory;
	}
	public void destroyShell(){
		shellProcess.destroyForcibly();
		inputReader.close();
		errorReader.close();
		writer.close();
		setText("");
		launched = false;
	}
	public boolean isSymbol(char ch){
		return "`~!@#$%^&*()_+-={}|[]\\:\";\'<>?,./]) ".contains(ch + "");
	}
}
