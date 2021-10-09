package omega.instant.support;
import omega.utils.IconManager;
import omega.utils.DataManager;

import java.awt.image.BufferedImage;

import java.awt.Dimension;
import java.awt.Graphics2D;

import java.util.LinkedList;

import omega.comp.FlexPanel;
import omega.comp.TextComp;

import omega.Screen;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import static omega.utils.UIManager.*;
import static omega.comp.Animations.*;
public class LanguageTagView extends JDialog{
	public static final int LANGUAGE_TAG_JAVA = 0;
	public static final int LANGUAGE_TAG_PYTHON = 1;
	public static final int LANGUAGE_TAG_KOTLIN = 2;
	public static final int LANGUAGE_TAG_GROOVY = 3;
	public static final int LANGUAGE_TAG_C = 4;
	public static final int LANGUAGE_TAG_CPLUSPLUS = 5;
	public static final int LANGUAGE_TAG_DART = 6;
	public static final int LANGUAGE_TAG_WEB = 7;
	public static final int LANGUAGE_TAG_ANY = -1;

	private FlexPanel containerPanel;
	private JScrollPane scrollPane;
	private JPanel panel;

	private TextComp titleComp;
	private TextComp closeComp;

	private int block;

	private LinkedList<TextComp> comps = new LinkedList<>();

	public LanguageTagView(Screen screen){
		super(screen, true);
		setUndecorated(true);
		setSize(150, 230);
		setLocationRelativeTo(null);
		setResizable(false);
		JPanel panel = new JPanel(null);
		panel.setBackground(c2);
		setContentPane(panel);
		init();
	}

	public void init(){
		containerPanel = new FlexPanel(null, back1, null);
		containerPanel.setArc(10, 10);
		add(containerPanel);

		scrollPane = new JScrollPane(panel = new JPanel(null));
		scrollPane.setBorder(null);
		scrollPane.setBackground(c2);
		scrollPane.setSize(80, 200);
		scrollPane.setPreferredSize(scrollPane.getSize());
		panel.setBackground(c2);
		containerPanel.add(scrollPane);

		titleComp = new TextComp("Project Lang", back1, back1, glow, null);
		titleComp.setBounds(5, 3, getWidth() - 40, 25);
		titleComp.setFont(PX14);
		titleComp.setClickable(false);
		titleComp.attachDragger(this);
		titleComp.setArc(0, 0);
		containerPanel.add(titleComp);

		closeComp = new TextComp(IconManager.fluentcloseImage, 15, 15, TOOLMENU_COLOR2_SHADE, c2, c2, ()->setVisible(false));
		closeComp.setBounds(getWidth() - 35, 3, 25, 25);
		closeComp.setArc(5, 5);
		containerPanel.add(closeComp);
	}

	public void genView(){
		comps.forEach(panel::remove);
		comps.clear();
		
		block = 0;

		addTag(prepareLangComp(IconManager.fluentjavaImage, "Java", LANGUAGE_TAG_JAVA));
		addTag(prepareLangComp(IconManager.fluentpythonImage, "Python", LANGUAGE_TAG_PYTHON));
		addTag(prepareLangComp(IconManager.fluentkotlinImage, "Kotlin", LANGUAGE_TAG_KOTLIN));
		addTag(prepareLangComp(IconManager.fluentgroovyImage, "Groovy", LANGUAGE_TAG_GROOVY));
		addTag(prepareLangComp(IconManager.fluentcImage, "C", LANGUAGE_TAG_C));
		addTag(prepareLangComp(IconManager.fluentcplusplusImage, "C++", LANGUAGE_TAG_CPLUSPLUS));
		addTag(prepareLangComp(IconManager.fluentdartImage, "Dart", LANGUAGE_TAG_DART));
		addTag(prepareLangComp(IconManager.fluentwebImage, "Web", LANGUAGE_TAG_WEB));
		addTag(prepareLangComp(IconManager.fluentanylangImage, "LangX", LANGUAGE_TAG_ANY));

		panel.setPreferredSize(new Dimension(scrollPane.getWidth(), block));
		scrollPane.getVerticalScrollBar().setVisible(true);
		scrollPane.getVerticalScrollBar().setValue(0);

		repaint();
	}

	public void addTag(TextComp comp){
		panel.add(comp);
		comps.add(comp);
	}

	public static BufferedImage getRespectiveTagImage(int tag){
		return switch(tag) {
			case LANGUAGE_TAG_JAVA -> IconManager.fluentjavaImage;
			case LANGUAGE_TAG_PYTHON -> IconManager.fluentpythonImage;
			case LANGUAGE_TAG_GROOVY -> IconManager.fluentgroovyImage;
			case LANGUAGE_TAG_KOTLIN -> IconManager.fluentkotlinImage;
			case LANGUAGE_TAG_C -> IconManager.fluentcImage;
			case LANGUAGE_TAG_CPLUSPLUS -> IconManager.fluentcplusplusImage;
			case LANGUAGE_TAG_DART -> IconManager.fluentdartImage;
			case LANGUAGE_TAG_WEB -> IconManager.fluentwebImage;
			default -> IconManager.fluentanylangImage;
		};
	}

	public void setActiveLang(int tag){
		Screen.getFileView().getProjectManager().setLanguageTag(tag);
		setVisible(false);
	}

	public TextComp prepareLangComp(BufferedImage imageX, String name, int tag){
		TextComp langComp = new TextComp(name, TOOLMENU_COLOR1_SHADE, c2, TOOLMENU_COLOR3, ()->{
			setActiveLang(tag);
		}){
			@Override
			public void draw(Graphics2D g){
				g.drawImage(imageX, 2, getHeight()/2 - 25/2, 25, 25, null);
			}
		};
		langComp.setBounds(0, block, 130, 25);
		langComp.setFont(PX14);
		langComp.setArc(0, 0);
		langComp.alignX = 30;
		block += 25;
		return langComp;
	}

	@Override
	public void setVisible(boolean value){
		if(value){
	          genView();
		}
	     super.setVisible(value);
	}

	@Override
	public void layout(){
		containerPanel.setBounds(5, 5, getWidth() - 10, getHeight() - 10);
		scrollPane.setBounds(5, 35, containerPanel.getWidth() - 10, containerPanel.getHeight() - 40);
		super.layout();
	}
}
