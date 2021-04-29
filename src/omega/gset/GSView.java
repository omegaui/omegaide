package omega.gset;
import omega.comp.*;
import omega.utils.UIManager;
import java.awt.event.MouseEvent;
import omega.Screen;
import java.awt.Dimension;
import omega.deassembler.SourceReader;
import java.awt.event.MouseAdapter;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import omega.deassembler.DataMember;
import omega.comp.TextComp;
import java.util.LinkedList;
import javax.swing.JDialog;
import static omega.utils.UIManager.*;
import static omega.settings.Screen.*;
public class GSView extends JDialog{
	private int mouseX;
	private int mouseY;
	private LinkedList<TextComp> comps = new LinkedList<>();
	private LinkedList<DataMember> members = new LinkedList<>();
	private RSyntaxTextArea textArea;
	private String className;
	private JScrollPane scrollPane;
	private JPanel panel;
	private TextComp accessComp;
	private TextComp gsComp;
	public GSView(Screen screen){
		super(screen);
		setModal(false);
		setLayout(null);
		setUndecorated(true);
		setSize(600, 500);
		setLocationRelativeTo(screen);
		setResizable(false);
		init();
	}
	public void init(){
		scrollPane = new JScrollPane(panel = new JPanel(null));
		scrollPane.setBounds(0, 30, getWidth(), getHeight() - 90);
		panel.setBackground(c2);
		add(scrollPane);
		
		TextComp closeComp = new TextComp("x", TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR2, ()->setVisible(false));
		closeComp.setBounds(0, 0, 30, 30);
		closeComp.setFont(PX16);
		closeComp.setArc(0, 0);
		add(closeComp);
          
		TextComp titleComp = new TextComp("Generate Getters/Setters", TOOLMENU_COLOR1_SHADE, c2, TOOLMENU_COLOR1, ()->{});
		titleComp.setBounds(30, 0, getWidth() - 30, 30);
		titleComp.setFont(PX18);
		titleComp.setClickable(false);
		titleComp.addMouseMotionListener(new MouseAdapter(){
			@Override
			public void mouseDragged(MouseEvent e) {
				setLocation(e.getXOnScreen() - mouseX - 30, e.getYOnScreen() - mouseY);
			}
		});
		titleComp.addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e) {
				mouseX = e.getX();
				mouseY = e.getY();
			}
		});
		titleComp.setArc(0, 0);
		add(titleComp);
		accessComp = new TextComp("Use Access : public", TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR3, ()->{});
		accessComp.setBounds(0, getHeight() - 60, 300, 30);
		accessComp.setRunnable(()->{
			switch(accessComp.getText()){
				case "Use Access : public": accessComp.setText("Use Access : protected");break;
				case "Use Access : protected": accessComp.setText("Use Access : private");break;
				case "Use Access : private": accessComp.setText("Use Access : none(default)");break;
				default: accessComp.setText("Use Access : public");
			}
		});
		accessComp.setFont(PX16);
		accessComp.setArc(0, 0);
		add(accessComp);
		gsComp = new TextComp("Getter&Setter", TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR3, ()->{});
		gsComp.setBounds(300, getHeight() - 60, 200, 30);
		gsComp.setRunnable(()->{
			switch(gsComp.getText()){
				case "Getter&Setter": gsComp.setText("Getter");break;
				case "Getter": gsComp.setText("Setter");break;
				default: gsComp.setText("Getter&Setter");
			}
		});
		gsComp.setFont(PX16);
		gsComp.setArc(0, 0);
		add(gsComp);
		TextComp genComp = new TextComp("Generate", TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR2, this::generate);
		genComp.setBounds(500, getHeight() - 60, getWidth() - 500, 30);
		genComp.setFont(PX16);
		genComp.setArc(0, 0);
		add(genComp);
		NoCaretField searchField = new NoCaretField("", "search any field here", TOOLMENU_COLOR2, c2, TOOLMENU_COLOR3);
		searchField.setBounds(0, getHeight() - 30, getWidth(), 30);
		searchField.setFont(PX14);
          searchField.setOnAction(()->search(searchField.getText()));
		add(searchField);
          addKeyListener(searchField);
	}
	public synchronized void search(String text){
		comps.forEach(panel::remove);
		int y = 0;
		for(TextComp comp : comps) {
			if(comp.getName().contains(text)){
				comp.setBounds(0, y, getWidth(), 30);
				panel.add(comp);
				y += 30;
			}
		}
		panel.setPreferredSize(new Dimension(getWidth(), y));
		scrollPane.getVerticalScrollBar().setVisible(true);
		scrollPane.getVerticalScrollBar().setValue(0);
		scrollPane.repaint();
	}
	public void generate(){
		LinkedList<DataMember> selections = new LinkedList<>();
		comps.forEach(c->{
			if(c.color2 == TOOLMENU_COLOR3)
				selections.add(members.get(comps.indexOf(c)));
		});
		String access = accessComp.getText().substring(accessComp.getText().indexOf(':') + 1);
		if(access.contains("none"))
			access = "";
		for(DataMember d : selections){
			if(gsComp.getText().contains("&")){
				Generator.genGetter(d, textArea, access);
				Generator.genSetter(d, textArea, access, className);
			}
			else if(gsComp.getText().contains("G")){
				Generator.genGetter(d, textArea, access);
			}
			else if(gsComp.getText().contains("S")){
				Generator.genSetter(d, textArea, access, className);
			}
		}
		selections.clear();
	}
	public void genView(RSyntaxTextArea textArea){
		if(omega.Screen.getFileView().getProjectManager().non_java) return;
		if(textArea == null) return;
		new Thread(()->{
			this.textArea = textArea;
			comps.forEach(panel::remove);
			comps.clear();
			members.clear();
			SourceReader reader = new SourceReader(textArea.getText());
			this.className = reader.className;
			int y = 0;
			for(DataMember d : reader.dataMembers){
				if(d.modifier != null && !d.modifier.contains("final")){
					if(d.parameters == null){
						TextComp textComp = new TextComp(d.name, TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR3, ()->{});
						textComp.setRunnable(()->{
							textComp.setColors(textComp.color1, textComp.color3, textComp.color2);
						});
						textComp.setBounds(0, y, getWidth(), 30);
						textComp.setArc(0, 0);
						textComp.setFont(PX14);
						textComp.alignX = 5;
                              textComp.setName(d.name);
						panel.add(textComp);
						comps.add(textComp);
						members.add(d);
						y += 30;
					}
				}
			}
			panel.setPreferredSize(new Dimension(getWidth(), y));
			setVisible(true);
			scrollPane.getVerticalScrollBar().setVisible(true);
			scrollPane.getVerticalScrollBar().setValue(0);
			scrollPane.repaint();
		}).start();
	}
}