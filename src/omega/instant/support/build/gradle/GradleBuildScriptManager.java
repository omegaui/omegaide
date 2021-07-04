package omega.instant.support.build.gradle;
import omega.utils.*;
import omega.*;
import omega.comp.*;
import javax.swing.*;
import static omega.utils.UIManager.*;
import static omega.settings.Screen.*;
public class GradleBuildScriptManager extends JDialog{
	private TextComp titleComp;
	private TextComp cancelComp;
	private NoCaretField nameField;
	private TextComp applyComp;
	
	public GradleBuildScriptManager(Screen screen){
		super(screen, true);
		setUndecorated(true);
		setTitle("Set Gradle Build and Run Script");
		setSize(400, 70);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		JPanel panel = new JPanel(null);
		panel.setBackground(c2);
		setContentPane(panel);
		setBackground(c2);
		init();
	}

	public void init(){
		titleComp = new TextComp("Specify Gradle Build Script Name", TOOLMENU_COLOR3, c2, c2, null);
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
		nameField.setToolTipText("Do not include file extension");
		add(nameField);
		addKeyListener(nameField);
	}

	public void apply(){
		String text = nameField.getText();
		if(text.equals("")){
			titleComp.setColors(TOOLMENU_COLOR2, c2, c2);
			titleComp.setText("Cannot be empty!");
			return;
		}
		titleComp.setColors(TOOLMENU_COLOR3, c2, c2);
		titleComp.setText("Specify Gradle Build Script Name");
		DataManager.setGradleCommand(text);
		setVisible(false);
	}
	
	@Override
	public void setVisible(boolean value){
		if(value){
	          nameField.setText(DataManager.getGradleCommand());
		}
	     super.setVisible(value);
	}
}
