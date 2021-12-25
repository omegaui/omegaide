package omega.utils;
import java.io.File;

import java.awt.geom.RoundRectangle2D;

import omega.comp.TextComp;
import omega.comp.SwitchComp;

import omega.Screen;

import javax.swing.JDialog;
import javax.swing.JPanel;
import static omega.utils.UIManager.*;
import static omega.comp.Animations.*;
public class AnimationsDialog extends JDialog{

	private Screen screen;
	
	private TextComp iconComp;
	private TextComp labelComp;
	
	private TextComp winIconComp;
	private TextComp winLabelComp;
	
	private TextComp linuxIconComp;
	private TextComp linuxLabelComp;
	
	private TextComp coreLabelComp;

	private TextComp onLabelComp;
	private TextComp offLabelComp;
	private SwitchComp toggleComp;

	private TextComp closeComp;
	
	public AnimationsDialog(Screen screen){
		super(screen, true);
		this.screen = screen;
		
		setLayout(null);
		setIconImage(screen.getIconImage());
		setUndecorated(true);
		setSize(400, 450);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		JPanel panel = new JPanel(null);
		panel.setBackground(c2);
		setContentPane(panel);
		init();
		
		File firstInstallFile = new File(".omega-ide" + File.separator + ".shownAnimationsMenu");
		if(!firstInstallFile.exists()){
			setVisible(true);
			try{
				firstInstallFile.createNewFile();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	public void init(){
		iconComp = new TextComp(IconManager.fluentanimationImage, 96, 96, c2, c2, c2, null);
		iconComp.setBounds(getWidth()/2 - 50, 10, 100, 100);
		iconComp.setClickable(false);
		iconComp.attachDragger(this);
		iconComp.setArc(0, 0);
		add(iconComp);

		labelComp = new TextComp("For Super Smooth Animations", c2, c2, TOOLMENU_COLOR1, null);
		labelComp.setBounds(10, 110, getWidth() - 20, 25);
		labelComp.setFont(PX16);
		labelComp.setArc(0, 0);
		labelComp.setClickable(false);
		add(labelComp);

		winIconComp = new TextComp(IconManager.fluentwindowsImage, 50, 50, c2, c2, c2, null);
		winIconComp.setBounds(getWidth()/2 - 30, 140, 60, 60);
		winIconComp.setClickable(false);
		winIconComp.attachDragger(this);
		winIconComp.setArc(0, 0);
		add(winIconComp);

		winLabelComp = new TextComp("On Windows at least 4 GB RAM is required", c2, c2, TOOLMENU_COLOR1, null);
		winLabelComp.setBounds(10, 210, getWidth() - 20, 25);
		winLabelComp.setFont(PX14);
		winLabelComp.setArc(0, 0);
		winLabelComp.setClickable(false);
		add(winLabelComp);

		linuxIconComp = new TextComp(IconManager.fluentlinuxImage, 50, 50, c2, c2, c2, null);
		linuxIconComp.setBounds(getWidth()/2 - 30, 240, 60, 60);
		linuxIconComp.setClickable(false);
		linuxIconComp.attachDragger(this);
		linuxIconComp.setArc(0, 0);
		add(linuxIconComp);

		linuxLabelComp = new TextComp("On Linux at least 2 GB RAM is required", c2, c2, TOOLMENU_COLOR1, null);
		linuxLabelComp.setBounds(10, 310, getWidth() - 20, 25);
		linuxLabelComp.setFont(PX14);
		linuxLabelComp.setArc(0, 0);
		linuxLabelComp.setClickable(false);
		add(linuxLabelComp);

		coreLabelComp = new TextComp("With at least A Dual Core Processor", TOOLMENU_COLOR4, TOOLMENU_COLOR4, c2, null);
		coreLabelComp.setBounds(0, 345, getWidth(), 25);
		coreLabelComp.setFont(PX14);
		coreLabelComp.setArc(0, 0);
		coreLabelComp.setClickable(false);
		add(coreLabelComp);

		onLabelComp = new TextComp("On", c2, c2, TOOLMENU_COLOR2, null);
		onLabelComp.setBounds(getWidth()/2 + 35 + 10, getHeight() - 40, 50, 25);
		onLabelComp.setFont(PX14);
		onLabelComp.setArc(0, 0);
		onLabelComp.setClickable(false);
		add(onLabelComp);

		offLabelComp = new TextComp("Off", c2, c2, TOOLMENU_COLOR2, null);
		offLabelComp.setBounds(getWidth()/2 - 35 - 60, getHeight() - 40, 50, 25);
		offLabelComp.setFont(PX14);
		offLabelComp.setArc(0, 0);
		offLabelComp.setClickable(false);
		add(offLabelComp);

		toggleComp = new SwitchComp(isAnimationsOn(), TOOLMENU_COLOR1, TOOLMENU_COLOR3, TOOLMENU_COLOR2_SHADE, (value)->{});
		toggleComp.setToolTipText("Animations");
		toggleComp.setBounds(getWidth()/2 - 35, getHeight() - 45, 70, 30);
		toggleComp.setInBallColor(glow);
		add(toggleComp);

		closeComp = new TextComp(IconManager.closeImage, 25, 25, TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR2, this::dispose);
		closeComp.setBounds(getWidth() - 30, 0, 30, 30);
		closeComp.setArc(0, 0);
		add(closeComp);

		putAnimationLayer(closeComp, getImageSizeAnimationLayer(20, 5, true), ACTION_MOUSE_ENTERED);
	}

	@Override
	public void dispose(){
		super.dispose();
		UIManager.setAnimationsActive(toggleComp.isOn());
		screen.getUIManager().save();
	}
	
}
