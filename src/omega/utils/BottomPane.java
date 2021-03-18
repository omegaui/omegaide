package omega.utils;
import java.awt.Graphics;
import omega.comp.RTextField;
import java.awt.event.MouseEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import omega.Screen;
import java.awt.Dimension;
import java.awt.BorderLayout;
import omega.comp.TextComp;
import javax.swing.JPanel;
import static omega.utils.UIManager.*;
import static omega.settings.Screen.*;
public class BottomPane extends JPanel{
	private Screen screen;
	public TextComp messageComp;
	public RTextField jumpField;
     public TextComp themeComp;
     private Runnable r = ()->{};
	
	public BottomPane(Screen screen){
		super(null);
		this.screen = screen;
		setBackground(c2);
		setPreferredSize(new Dimension(100, 25));
		init();
	}
	public void init(){
		messageComp = new TextComp("Status of any process running will appear here!", c1, c3, c2, null);
		messageComp.setFont(PX14);
		messageComp.alignX = 15;
		messageComp.setPreferredSize(new Dimension(100, 25));
		messageComp.setArc(0, 0);
		messageComp.addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e){
				if(e.getButton() == 1 && e.getClickCount() == 2)
					r.run();
			}
		});
		add(messageComp);

          jumpField = new RTextField("Goto Line", "", c1, c2, c3);
          jumpField.setFont(PX14);
          jumpField.setArc(0, 0);
          jumpField.addActionListener((e)->{
               if(!jumpField.hasText())
                    return;
               String text = jumpField.getText();
               for(char c : text.toCharArray()){
                    if(!Character.isDigit(c))
                         return;
               }
               int line = Integer.parseInt(text);
               String code = Screen.getScreen().getCurrentEditor() != null ? Screen.getScreen().getCurrentEditor().getText() : "";
               if(code.equals(""))
                    return;
               int pos = 0;
               for(char c : code.toCharArray()){
                    if(line <= 0)
                         break;
                    if(c == '\n')
                         line--;
                    pos++;
               }
               Screen.getScreen().getCurrentEditor().setCaretPosition(pos - 1);
          });
          add(jumpField);

          themeComp = new TextComp("light", "Switching Theme Requires IDE\'s Restart", c1, c3, c2, null);
          themeComp.setRunnable(()->{
               Screen.pickTheme(DataManager.getTheme());
               themeComp.setText(DataManager.getTheme());
               Screen.getScreen().getToolMenu().themeComp.setText(DataManager.getTheme());
          });
          themeComp.setFont(PX14);
          themeComp.setArc(0, 0);
          add(themeComp);
	}
	@Override
	public void paint(Graphics g){
		messageComp.setBounds(0, 0, getWidth() - 170, 25);
          jumpField.setBounds(getWidth() - 170, 0, 100, 25);
          themeComp.setBounds(getWidth() - 70, 0, 70, 25);
		super.paint(g);
	}
	public void setMessage(String text){
		messageComp.setText(text);
	}
	public void setDoubleClickAction(Runnable r, String toolTip){
		this.r = r;
		this.messageComp.setToolTipText(toolTip);
	}
}
