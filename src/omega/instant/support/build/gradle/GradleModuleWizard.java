package omega.instant.support.build.gradle;
import omega.Screen;
import javax.swing.JDialog;
import java.io.PrintWriter;
import java.io.File;
import omega.comp.NoCaretField;
import omega.comp.FlexPanel;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import omega.comp.TextComp;
import javax.swing.JPanel;
import javax.swing.JFrame;
import static omega.utils.UIManager.*;
import static omega.settings.Screen.*;
public class GradleModuleWizard extends JDialog{
	private int pressX;
	private int pressY;
	private TextComp titleComp;
	private TextComp nameLabel;
	private NoCaretField nameField;
	
	private FlexPanel projectTypePanel;
	private TextComp basicComp;
	private TextComp applicationComp;
	private TextComp libraryComp;
	private TextComp pluginComp;
	private TextComp result1;
	
	private FlexPanel languagePanel;
	private TextComp cppComp;
	private TextComp groovyComp;
	private TextComp javaComp;
	private TextComp scalaComp;
	private TextComp kotlinComp;
	private TextComp swiftComp;
	private TextComp result2;
	
	private FlexPanel scriptPanel;
	private TextComp groovyDSLComp;
	private TextComp kotlinDSLComp;
	private TextComp result3;
	private TextComp closeComp;
	private TextComp createComp;
	
	public GradleModuleWizard(JFrame f){
          super(f, false);
          setTitle("Gradle Module Wizard");
		setUndecorated(true);
		setSize(700, 400);
		setLocationRelativeTo(null);
		JPanel panel = new JPanel(null);
		panel.setBackground(c2);
		setContentPane(panel);
		setBackground(c2);
		setLayout(null);
		init();
	}
	public void init(){
		titleComp = new TextComp("Gradle Module Wizard", TOOLMENU_COLOR3, c2, c2, null);
		titleComp.setBounds(0, 0, getWidth(), 30);
		titleComp.setFont(PX14);
		titleComp.setClickable(false);
		titleComp.setArc(0, 0);
		titleComp.addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e){
				pressX = e.getX();
				pressY = e.getY();
			}
		});
		titleComp.addMouseMotionListener(new MouseAdapter(){
			@Override
			public void mouseDragged(MouseEvent e){
				setLocation(e.getXOnScreen() - pressX, e.getYOnScreen() - pressY);
			}
		});
		add(titleComp);
		nameLabel = new TextComp("Module Name", TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR3, null);
		nameLabel.setBounds(50, 50, 150, 25);
		nameLabel.setClickable(false);
		nameLabel.setFont(PX14);
		add(nameLabel);
		nameField = new NoCaretField("", "Start Typing", TOOLMENU_COLOR3, c2, TOOLMENU_COLOR2);
		nameField.setBounds(210, 50, getWidth() - 220, 25);
		nameField.setFont(PX14);
		nameField.setIgnorableCharacters(' ', File.separatorChar);
		add(nameField);
		addKeyListener(nameField);
		//Project Type Panel
		projectTypePanel = new FlexPanel(null, c2, c1);
		projectTypePanel.setBounds(50, 100, (getWidth() - 110)/3, 220);
		projectTypePanel.setPaintGradientEnabled(true);
		add(projectTypePanel);
		TextComp label1 = new TextComp("Project Type", TOOLMENU_COLOR3_SHADE, TOOLMENU_COLOR3, c2, null);
		label1.setBounds(10, 10, projectTypePanel.getWidth() - 20, 25);
		label1.setFont(PX14);
		label1.setClickable(false);
		projectTypePanel.add(label1);
		basicComp = new TextComp("Basic", TOOLMENU_COLOR1_SHADE, c2, TOOLMENU_COLOR1, ()->result1.setText("Basic"));
		basicComp.setBounds(10, 40, projectTypePanel.getWidth() - 20, 25);
		basicComp.setFont(PX14);
		projectTypePanel.add(basicComp);
		applicationComp = new TextComp("Application", TOOLMENU_COLOR1_SHADE, c2, TOOLMENU_COLOR1, ()->result1.setText("Application"));
		applicationComp.setBounds(10, 70, projectTypePanel.getWidth() - 20, 25);
		applicationComp.setFont(PX14);
		projectTypePanel.add(applicationComp);
		libraryComp = new TextComp("Library", TOOLMENU_COLOR1_SHADE, c2, TOOLMENU_COLOR1, ()->result1.setText("Library"));
		libraryComp.setBounds(10, 100, projectTypePanel.getWidth() - 20, 25);
		libraryComp.setFont(PX14);
		projectTypePanel.add(libraryComp);
		pluginComp = new TextComp("Gradle Plugin", TOOLMENU_COLOR1_SHADE, c2, TOOLMENU_COLOR1, ()->result1.setText("Gradle Plugin"));
		pluginComp.setBounds(10, 130, projectTypePanel.getWidth() - 20, 25);
		pluginComp.setFont(PX14);
		projectTypePanel.add(pluginComp);
		result1 = new TextComp("", TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR3, null);
		result1.setBounds(60, 330, projectTypePanel.getWidth() - 20, 25);
		result1.setFont(PX14);
		result1.setClickable(false);
		add(result1);
		//Language Type Panel
		languagePanel = new FlexPanel(null, c1, c2);
		languagePanel.setBounds(50 + (getWidth() - 110)/3, 100, (getWidth() - 110)/3, 220);
		languagePanel.setPaintGradientEnabled(true);
		add(languagePanel);
		TextComp label2 = new TextComp("Language", TOOLMENU_COLOR3_SHADE, TOOLMENU_COLOR3, c2, null);
		label2.setBounds(10, 10, languagePanel.getWidth() - 20, 25);
		label2.setFont(PX14);
		label2.setClickable(false);
		languagePanel.add(label2);
		cppComp = new TextComp("C++", TOOLMENU_COLOR1_SHADE, c2, TOOLMENU_COLOR1, ()->result2.setText("C++"));
		cppComp.setBounds(10, 40, languagePanel.getWidth() - 20, 25);
		cppComp.setFont(PX14);
		languagePanel.add(cppComp);
		groovyComp = new TextComp("Groovy", TOOLMENU_COLOR1_SHADE, c2, TOOLMENU_COLOR1, ()->result2.setText("Groovy"));
		groovyComp.setBounds(10, 70, languagePanel.getWidth() - 20, 25);
		groovyComp.setFont(PX14);
		languagePanel.add(groovyComp);
		javaComp = new TextComp("Java", TOOLMENU_COLOR1_SHADE, c2, TOOLMENU_COLOR1, ()->result2.setText("Java"));
		javaComp.setBounds(10, 100, languagePanel.getWidth() - 20, 25);
		javaComp.setFont(PX14);
		languagePanel.add(javaComp);
		scalaComp = new TextComp("Scala", TOOLMENU_COLOR1_SHADE, c2, TOOLMENU_COLOR1, ()->result2.setText("Scala"));
		scalaComp.setBounds(10, 130, languagePanel.getWidth() - 20, 25);
		scalaComp.setFont(PX14);
		languagePanel.add(scalaComp);
		kotlinComp = new TextComp("Kotlin", TOOLMENU_COLOR1_SHADE, c2, TOOLMENU_COLOR1, ()->result2.setText("Kotlin"));
		kotlinComp.setBounds(10, 160, languagePanel.getWidth() - 20, 25);
		kotlinComp.setFont(PX14);
		languagePanel.add(kotlinComp);
		swiftComp = new TextComp("Swift", TOOLMENU_COLOR1_SHADE, c2, TOOLMENU_COLOR1, ()->result2.setText("Swift"));
		swiftComp.setBounds(10, 190, languagePanel.getWidth() - 20, 25);
		swiftComp.setFont(PX14);
		languagePanel.add(swiftComp);
		result2 = new TextComp("", TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR3, null);
		result2.setBounds(languagePanel.getX() + 10, 330, languagePanel.getWidth() - 20, 25);
		result2.setFont(PX14);
		result2.setClickable(false);
		add(result2);
		//Build Script Type Panel
		scriptPanel = new FlexPanel(null, c2, c1);
		scriptPanel.setBounds(49 + (2 * (getWidth() - 110)/3), 100, (getWidth() - 110)/3, 220);
		scriptPanel.setPaintGradientEnabled(true);
		add(scriptPanel);
		TextComp label3 = new TextComp("Build Script DSL", TOOLMENU_COLOR3_SHADE, TOOLMENU_COLOR3, c2, null);
		label3.setBounds(10, 10, scriptPanel.getWidth() - 20, 25);
		label3.setFont(PX14);
		label3.setClickable(false);
		scriptPanel.add(label3);
		
		groovyDSLComp = new TextComp("Groovy", TOOLMENU_COLOR1_SHADE, c2, TOOLMENU_COLOR1, ()->result3.setText("Groovy"));
		groovyDSLComp.setBounds(10, 40, scriptPanel.getWidth() - 20, 25);
		groovyDSLComp.setFont(PX14);
		scriptPanel.add(groovyDSLComp);
		
		kotlinDSLComp = new TextComp("Kotlin", TOOLMENU_COLOR1_SHADE, c2, TOOLMENU_COLOR1, ()->result3.setText("Kotlin"));
		kotlinDSLComp.setBounds(10, 70, scriptPanel.getWidth() - 20, 25);
		kotlinDSLComp.setFont(PX14);
		scriptPanel.add(kotlinDSLComp);
		
		result3 = new TextComp("", TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR3, null);
		result3.setBounds(scriptPanel.getX() + 10, 330, scriptPanel.getWidth() - 20, 25);
		result3.setFont(PX14);
		result3.setClickable(false);
		add(result3);
		closeComp = new TextComp("Cancel", TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR2, this::dispose);
		closeComp.setBounds(getWidth()/2 - 110, getHeight() - 40, 100, 30);
		closeComp.setFont(PX14);
		add(closeComp);
		createComp = new TextComp("Create", TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR2, this::generate);
		createComp.setBounds(getWidth()/2 + 10, getHeight() - 40, 100, 30);
		createComp.setFont(PX14);
		add(createComp);
	}
	public void generate(){
		titleComp.setColors(TOOLMENU_COLOR2_SHADE, TOOLMENU_COLOR2, c2);
		if(nameField.getText().equals("")){
			titleComp.setText("Specify Module Name");
			return;
		}
		if(result1.getText().equals("")){
			titleComp.setText("Select Project Type");
			return;
		}
		if(result2.getText().equals("")){
			titleComp.setText("Select Project Language");
			return;
		}
		if(result3.getText().equals("")){
			titleComp.setText("Select Build Script DSL");
			return;
		}
		titleComp.setText("Gradle Module Wizard");
		titleComp.setColors(TOOLMENU_COLOR3, c2, c2);
		generateApplicationTemplate(result2.getText());
		generateBuildTemplate(result3.getText());
		titleComp.setText("Module Generated Successfully");
          Screen.getProjectView().reload();
	}
	public void generateApplicationTemplate(String language){
		String moduleName = nameField.getText();
		File moduleFile = new File(Screen.getFileView().getProjectPath(), moduleName);
		if(moduleFile.exists()){
			titleComp.setText("Module Already Exists");
			titleComp.setColors(TOOLMENU_COLOR2_SHADE, TOOLMENU_COLOR2, c2);
			return;
		}
		moduleFile.mkdir();
		language = switch(language){
			case "C++" -> "cpp";
			default -> language.toLowerCase();
		};
		File srcDir = new File(moduleFile.getAbsolutePath(), "src");
		srcDir.mkdir();
		File mainDir = new File(srcDir.getAbsolutePath(), "main");
		mainDir.mkdir();
		File langDir = new File(mainDir.getAbsolutePath(), language);
		langDir.mkdir();
		File resDir = new File(mainDir.getAbsolutePath(), "resources");
		resDir.mkdir();
		if(result1.getText().equalsIgnoreCase("Gradle Plugin")){
			File funcDir = new File(srcDir.getAbsolutePath(), "functionalTest");
			funcDir.mkdir();
			langDir = new File(funcDir.getAbsolutePath(), language);
			langDir.mkdir();
		}
	}
	public void generateBuildTemplate(String descriptor){
		boolean groovyDSL = descriptor.equalsIgnoreCase("groovy");
		String buildName = groovyDSL ? "build.gradle" : "build.gradle.kts";
		try(PrintWriter writer = new PrintWriter(new File(Screen.getFileView().getProjectPath() + File.separator + nameField.getText(), buildName))){
			writer.println(getTemplate("plugins"));
			writer.println(getTemplate("repositories"));
			writer.println(getTemplate("dependencies"));
			writer.println(getTemplate("application"));
		}
		catch(Exception e){
			titleComp.setText("An internal error has occured! See log for details");
			e.printStackTrace();
		}
	}
	public String getTemplate(String header){
		return header + " {\n\t\n}";
	}
}
