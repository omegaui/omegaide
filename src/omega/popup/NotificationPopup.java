package omega.popup;
import java.awt.image.BufferedImage;

import java.awt.geom.RoundRectangle2D;

import omega.comp.FlexPanel;
import omega.comp.TextComp;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Color;

import javax.swing.JInternalFrame;
import javax.swing.JDialog;

import static omega.utils.UIManager.*;
import static omega.comp.Animations.*;
public class NotificationPopup extends JDialog{
	private static Dimension MINIMUM_SIZE = new Dimension(250, 120);
	
	private TextComp closeComp;
	private TextComp dialogImageComp;
	private TextComp titleComp;
	private TextComp imageComp;
	private TextComp messageComp;
	private TextComp footerComp;

	private int offsetWidth = 20;
	private int offsetHeight = 50;
	
	public NotificationPopup(Frame f){
		super(f, false);
		setUndecorated(true);
		FlexPanel panel = new FlexPanel(null, back2, c2);
		panel.setArc(20, 20);
		panel.setPaintGradientEnabled(true);
		setContentPane(panel);
		setLayout(null);
		setBackground(back2);
		setSize(MINIMUM_SIZE);
		setType(Type.UTILITY);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		init();
	}

	public void init(){
		closeComp = new TextComp("x", TOOLMENU_COLOR4_SHADE, back2, TOOLMENU_COLOR2, this::dispose);
		closeComp.setFont(PX12);
		closeComp.setArc(6, 6);
		add(closeComp);

		dialogImageComp = new TextComp(null, 25, 25, ALPHA, ALPHA, ALPHA, null);
		dialogImageComp.setBounds(10, 7, 25, 25);
		dialogImageComp.setClickable(false);
		add(dialogImageComp);

		titleComp = new TextComp("", ALPHA, ALPHA, glow, null);
		titleComp.alignX = 10;
		titleComp.setClickable(false);
		titleComp.setFont(PX14);
		titleComp.setArc(0, 0);
		titleComp.attachDragger(this);
		add(titleComp);

		imageComp = new TextComp(null, 25, 25, c1, ALPHA, glow, null);
		add(imageComp);

		messageComp = new TextComp("", ALPHA, ALPHA, glow, null);
		messageComp.alignX = 10;
		messageComp.setClickable(false);
		messageComp.setFont(PX14);
		messageComp.setArc(0, 0);
		add(messageComp);

		footerComp = new TextComp("", ALPHA, ALPHA, glow, null);
		footerComp.alignX = 10;
		footerComp.setClickable(false);
		footerComp.setFont(PX14);
		footerComp.setArc(0, 0);
		add(footerComp);
	}
	
	public NotificationPopup title(String text){
		titleComp.setText(text);
		setTitle(text);
		return this;
	}
	
	public NotificationPopup title(String text, Color textColor){
		titleComp.setText(text);
		titleComp.color3 = textColor;
		setTitle(text);
		return this;
	}
	
	public NotificationPopup message(String text){
		messageComp.setText(text);
		return this;
	}
	
	public NotificationPopup message(String text, Color textColor){
		message(text);
		messageComp.color3 = textColor;
		return this;
	}
	
	public NotificationPopup shortMessage(String text){
		footerComp.setText(text);
		return this;
	}
	
	public NotificationPopup shortMessage(String text, Color textColor){
		shortMessage(text);
		footerComp.color3 = textColor;
		return this;
	}
	
	public NotificationPopup size(int width, int height){
		setSize(width, height);
		return this;
	}
	
	public NotificationPopup iconButton(BufferedImage image, Runnable action){
		imageComp.image = image;
		imageComp.setRunnable(action);
		return this;
	}
	
	public NotificationPopup iconButton(BufferedImage image, Runnable action, String toolTip){
		iconButton(image, action);
		imageComp.setToolTipText(toolTip);
		return this;
	}
	
	public NotificationPopup dialogIcon(BufferedImage image){
		dialogImageComp.image = image;
		return this;
	}

	public NotificationPopup locateOnBottomLeft(){
		setLocation(getOwner().getX() + getOwner().getWidth() - getWidth() - this.offsetWidth, getOwner().getY() + getOwner().getHeight() - getHeight() - this.offsetHeight);
		return this;
	}

	public NotificationPopup locateOnBottomLeft(int offsetWidth, int offsetHeight){
		this.offsetWidth = offsetWidth;
		this.offsetHeight = offsetHeight;
		setLocation(getOwner().getX() + getOwner().getWidth() - getWidth() - this.offsetWidth, getOwner().getY() + getOwner().getHeight() - getHeight() - this.offsetHeight);
		return this;
	}
	
	public NotificationPopup build(){
		setShape(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
		
		//Initializing UI Bounds
		titleComp.setBounds(dialogImageComp.image == null ? 10 : 40, 7, getWidth() - 50, 25);
		closeComp.setBounds(getWidth() - 30, 10, 20, 20);
		imageComp.setBounds(10, getHeight() - 40, 30, 30);
		messageComp.setBounds(10, titleComp.getY() + titleComp.getHeight() + 10, getWidth() - 20, 25);
		footerComp.setBounds(imageComp.image == null ? 10 : 50, getHeight() - 40, getWidth() - 100, 30);

		if(imageComp.image == null){
			remove(imageComp);
		}

		if(dialogImageComp.image == null){
			remove(dialogImageComp);
		}
		
		setLocationRelativeTo(getOwner());
		return this;
	}

	public NotificationPopup showIt(){
		setVisible(true);
		return this;
	}

	public static NotificationPopup create(Frame f){
		return new NotificationPopup(f);
	}
}
