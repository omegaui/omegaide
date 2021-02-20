package omega.ui;
import omega.jdk.*;
import omega.*;
import java.awt.*;
import java.awt.event.*;
import omega.comp.*;
import java.util.*;
import javax.swing.*;
import static omega.settings.Screen.*;
import static omega.utils.UIManager.*;
public class ImportResolver extends JDialog {
	private JScrollPane scrollPane;
	private LinkedList<TextComp> comps = new LinkedList<>();
	private JPanel panel = new JPanel(null);
     private int pressX;
     private int pressY;
     private int block;
     
	public ImportResolver() {
		super(Screen.getFileView().getScreen(), true);
          setTitle("Import Resolver");
          setUndecorated(true);
          setResizable(false);
          setSize(600, 400);
          setLocationRelativeTo(null);
          setLayout(null);
          setBackground(c2);
          init();
	}

     public void init(){
          scrollPane = new JScrollPane(panel);
          panel.setBackground(c2);
          scrollPane.setBounds(0, 40, getWidth(), getHeight() - 40);
          add(scrollPane);
          
     	TextComp titleComp = new TextComp("Select The Imports And Click \'x\'", c1, c2, c3, ()->{});
          titleComp.setBounds(40, 0, getWidth() - 40, 40);
          titleComp.setFont(PX16);
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
                    setLocation(e.getXOnScreen() - pressX - 40, e.getYOnScreen() - pressY);
               }
          });
          add(titleComp);

          TextComp closeComp = new TextComp("x", c1, c2, c3, ()->setVisible(false));
          closeComp.setBounds(0, 0, 40, 40);
          closeComp.setFont(PX16);
          closeComp.setArc(0, 0);
          add(closeComp);
     }

	public LinkedList<Import> resolveImports(LinkedList<Import> imports) {
		if(imports.size() <= 1) {
			imports.clear();
			return imports;
		}
          comps.forEach(panel::remove);
          comps.clear();
		LinkedList<Import> selections = new LinkedList<>();
          int maxW = getWidth();
          Graphics g = Screen.getScreen().getGraphics();
          g.setFont(PX14);
          for(Import im : imports){
               int w = g.getFontMetrics().stringWidth(im.getImport());
               if(w > maxW)
                    maxW = w;
          }
          for(Import im : imports){
               TextComp comp = new TextComp(im.getImport(), c1, c2, c3, ()->{});
               comp.setRunnable(()->{
                    comp.setColors(comp.color1, comp.color3, comp.color2);
               });
               comp.setBounds(0, block, maxW, 30);
               comp.setFont(PX14);
               comp.alignX = 5;
               comp.setArc(0, 0);
               panel.add(comp);
               comps.add(comp);
               block += 30;
          }
          panel.setPreferredSize(new Dimension(maxW, block));
          setVisible(true);
          comps.forEach(comp->{
               if(comp.color2 == c3)
                    selections.add(new Import(comp.getText(), "", false));
          });
		return selections;
	}
}
