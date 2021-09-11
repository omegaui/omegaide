package omega.utils;
import omega.Screen;

import java.io.File;

import omega.comp.TextComp;

import javax.swing.JDialog;
import javax.swing.JPanel;

import static omega.utils.UIManager.*;
import static omega.comp.Animations.*;
public class ProjectDistructionWizard extends JDialog{
	private TextComp titleComp;
	private TextComp imageComp;
	private TextComp textComp;
	private TextComp text1Comp;
	private TextComp deleteComp;
	private TextComp closeComp;
	
	public ProjectDistructionWizard(Screen screen){
		super(screen, true);
		setTitle("Project Distruction Wizard");
		setUndecorated(true);
		setSize(400, 240);
		setLocationRelativeTo(null);
		JPanel panel = new JPanel(null);
		panel.setBackground(c2);
		setContentPane(panel);
		setLayout(null);
		init();
	}

	public void init(){
		titleComp = new TextComp("Do You Really Want To Delete This Project?", TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR3, null);
		titleComp.setBounds(0, 0, getWidth(), 30);
		titleComp.setFont(PX14);
		titleComp.setClickable(false);
		titleComp.attachDragger(this);
		titleComp.setArc(0, 0);
		add(titleComp);

		imageComp = new TextComp(IconManager.fluentsadImage, 48, 48, "You should have a backup before you proceed!", c2, c2, c2, null);
		imageComp.setBounds(getWidth()/2 - 48/2, 48, 48, 48);
		imageComp.setClickable(false);
		imageComp.setArc(0, 0);
		imageComp.attachDragger(this);
		add(imageComp);

		textComp = new TextComp("Really! Think One More Time", c2, c2, TOOLMENU_COLOR2, null);
		textComp.setBounds(10, imageComp.getY() + imageComp.getHeight() + 10, getWidth() - 20, 25);
		textComp.setFont(PX14);
		textComp.setClickable(false);
		textComp.setArc(0, 0);
		add(textComp);
		
		text1Comp = new TextComp("Once Started It cannot be stopped!", c2, c2, TOOLMENU_COLOR4, null);
		text1Comp.setBounds(10, textComp.getY() + textComp.getHeight() + 10, getWidth() - 20, 25);
		text1Comp.setFont(PX14);
		text1Comp.setClickable(false);
		text1Comp.setArc(0, 0);
		add(text1Comp);

		deleteComp = new TextComp("", TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR2, ()->{
			try{
				dispose();
				File projectDir = new File(Screen.getFileView().getProjectPath());
				Screen.getFileView().closeProject();
				Editor.deleteDir(projectDir);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		});
		deleteComp.setBounds(0, getHeight() - 25, getWidth()/2, 25);
		deleteComp.setFont(PX14);
		deleteComp.setArc(0, 0);
		add(deleteComp);

		closeComp = new TextComp("No! Wait", TOOLMENU_COLOR1_SHADE, c2, TOOLMENU_COLOR1, this::dispose);
		closeComp.setBounds(getWidth()/2, getHeight() - 25, getWidth()/2, 25);
		closeComp.setFont(PX14);
		closeComp.setArc(0, 0);
		add(closeComp);
	}

	@Override
	public void setVisible(boolean value){
		if(value){
			deleteComp.setClickable(false);
			deleteComp.setText("Wait! 5 seconds");
	          new Thread(()->{
	          	try{
	          		Thread.sleep(5000);
          		}
          		catch(Exception e){
          			
     			}
				deleteComp.setClickable(true);
				deleteComp.setText("Delete Now!");
          	}).start();
		}
	     super.setVisible(value);
	}
}
