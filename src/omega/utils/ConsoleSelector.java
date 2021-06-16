package omega.utils;
import omega.*;
import java.io.*;
import omega.comp.*;
import javax.swing.*;
import static omega.utils.UIManager.*;
import static omega.settings.Screen.*;
public class ConsoleSelector extends JDialog{
	private TextComp titleComp;
	private TextComp applyComp;
	private TextComp cancelComp;
	private NoCaretField nameField;
	public ConsoleSelector(Screen screen){
		super(screen, true);
		setUndecorated(true);
		setTitle("Console Selector");
		setIconImage(screen.getIconImage());
		setSize(400, 70);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setBackground(c2);
		JPanel panel = new JPanel(null);
		panel.setBackground(c2);
		setContentPane(panel);
		setLayout(null);
		init();
	}

	public void init(){
		titleComp = new TextComp("Specify System Terminal Launch Command", TOOLMENU_COLOR3, c2, c2, null);
		titleComp.setBounds(0, 0, getWidth() - 120, 30);
		titleComp.setClickable(false);
		titleComp.setFont(PX14);
		titleComp.setArc(0, 0);
		titleComp.attachDragger(this);
		add(titleComp);

		cancelComp = new TextComp("Cancel", TOOLMENU_COLOR1_SHADE, c2, TOOLMENU_COLOR1, this::dispose);
		cancelComp.setBounds(getWidth() - 120, 0, 60, 30);
		cancelComp.setFont(PX14);
		cancelComp.setArc(0, 0);
		add(cancelComp);

		applyComp = new TextComp("Apply", TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR2, this::apply);
		applyComp.setBounds(getWidth() - 60, 0, 60, 30);
		applyComp.setFont(PX14);
		applyComp.setArc(0, 0);
		add(applyComp);

		nameField = new NoCaretField("", "Start Typing", TOOLMENU_COLOR3, c2, TOOLMENU_COLOR2);
		nameField.setBounds(10, 40, getWidth() - 20, 30);
		nameField.setFont(PX14);
		nameField.setOnAction(this::apply);
		add(nameField);
		addKeyListener(nameField);
	}

	public void apply(){
		setVisible(false);
		DataManager.setConsoleCommand(nameField.getText() != null ? nameField.getText() : getPlatformTerminal());
	}

	public String getPlatformTerminal(){
		return File.pathSeparator.equals(":") ? "" : "cmd.exe";
	}

	public void launchTerminal(){
		if(DataManager.getConsoleCommand() == null || DataManager.getConsoleCommand().equals(""))
			setVisible(true);
		
		new Thread(()->{
			try{
				Runtime.getRuntime().exec(DataManager.getConsoleCommand());
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}).start();
		
	}

	@Override
	public void setVisible(boolean value){
		if(value){
	          nameField.setText(DataManager.getConsoleCommand());
		}
	     super.setVisible(value);
	}
}
