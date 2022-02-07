package omega.ui.window.donation;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import omega.io.IconManager;

import java.awt.geom.RoundRectangle2D;

import omegaui.component.TextComp;

import omega.Screen;

import javax.swing.JDialog;
import javax.swing.JPanel;

import static omega.io.UIManager.*;
import static omegaui.component.animation.Animations.*;

public class DonationWindow extends JDialog{

	private static BufferedImage upiImage;
	private static BufferedImage walletImage;
	static{
		try{
			upiImage = ImageIO.read(DonationWindow.class.getResource("/upi_qr_code.jpg"));
			walletImage = ImageIO.read(DonationWindow.class.getResource("/meta_wallet.jpg"));
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private TextComp titleComp;
	
	private TextComp upiComp;
	private TextComp upiLabelComp;
	
	private TextComp walletComp;
	private TextComp walletLabelComp;
	
	private TextComp closeComp;
	
	public DonationWindow(Screen screen){
		super(screen);
		setUndecorated(true);
		setSize(1100, 600);
		setLocationRelativeTo(null);
		setAlwaysOnTop(true);
		setResizable(false);
		JPanel panel = new JPanel(null);
		panel.setBackground(c2);
		setContentPane(panel);
		init();
	}

	public void init(){
		titleComp = new TextComp("YOUR donation will HELP in taking omegaide to the next LEVEL", c2, c2, glow, null);
		titleComp.setBounds(0, 0, getWidth(), 30);
		titleComp.setArc(0, 0);
		titleComp.attachDragger(this);
		titleComp.setFont(PX14);
		titleComp.setClickable(false);
		titleComp.setHighlightColor(TOOLMENU_COLOR1);
		titleComp.addHighlightText("YOUR", "HELP", "omegaide", "next LEVEL");
		add(titleComp);

		closeComp = new TextComp("Close", TOOLMENU_COLOR1_SHADE, TOOLMENU_GRADIENT, glow, this::dispose);
		closeComp.setBounds(getWidth() - 100, getHeight() - 50, 80, 25);
		closeComp.setFont(PX14);
		closeComp.setArc(0, 0);
		add(closeComp);

		upiComp = new TextComp(upiImage, upiImage.getWidth(), upiImage.getHeight(), c2, TOOLMENU_GRADIENT, c2, null);
		upiComp.setBounds(20, 40, 500, 500);
		upiComp.setArc(0, 0);
		upiComp.setClickable(false);
		add(upiComp);

		walletComp = new TextComp(walletImage, walletImage.getWidth(), walletImage.getHeight(), c2, TOOLMENU_GRADIENT, c2, null);
		walletComp.setBounds(getWidth() - 520, 40, 500, 500);
		walletComp.setArc(0, 0);
		walletComp.setClickable(false);
		add(walletComp);

		upiLabelComp = new TextComp("Using UPI", c2, c2, glow, null);
		upiLabelComp.setBounds(20, 545, 500, 25);
		upiLabelComp.setFont(PX16);
		upiLabelComp.setArc(0, 0);
		upiLabelComp.setClickable(false);
		add(upiLabelComp);

		walletLabelComp = new TextComp("Using Blockchain", c2, c2, glow, null);
		walletLabelComp.setBounds(getWidth() - 490, 545, 470, 25);
		walletLabelComp.setFont(PX16);
		walletLabelComp.setArc(0, 0);
		walletLabelComp.setClickable(false);
		add(walletLabelComp);
	}

	@Override
	public void setSize(int width, int height){
		super.setSize(width, height);
		setShape(new RoundRectangle2D.Double(0, 0, width, height, 20, 20));
	}
}
