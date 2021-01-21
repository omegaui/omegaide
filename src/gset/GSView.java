package gset;
import ide.utils.UIManager;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import ide.Screen;
import deassembler.DataMember;
import deassembler.SourceReader;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import java.awt.event.MouseAdapter;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import settings.comp.TextComp;
import java.util.LinkedList;
import javax.swing.JDialog;
import static ide.utils.UIManager.*;
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
          scrollPane.setBounds(0, 40, getWidth(), getHeight() - 80);
          panel.setBackground(c2);
          add(scrollPane);
          
     	TextComp closeComp = new TextComp("x", c1, c2, c3, ()->setVisible(false));
          closeComp.setBounds(0, 0, 40, 40);
          closeComp.setFont(settings.Screen.PX16);
          closeComp.setArc(0, 0);
          add(closeComp);

          TextComp titleComp = new TextComp("Generate Getters/Setters", c1, c2, c3, ()->{});
          titleComp.setBounds(40, 0, getWidth() - 40, 40);
          titleComp.setFont(settings.Screen.PX18);
          titleComp.setClickable(false);
          titleComp.addMouseMotionListener(new MouseAdapter(){
               @Override
               public void mouseDragged(MouseEvent e) {
                    setLocation(e.getXOnScreen() - mouseX - 40, e.getYOnScreen() - mouseY);
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

          accessComp = new TextComp("Use Access : public", c1, c2, c3, ()->{});
          accessComp.setBounds(0, getHeight() - 40, 300, 40);
          accessComp.setRunnable(()->{
               switch(accessComp.getText()){
                    case "Use Access : public": accessComp.setText("Use Access : protected");break;
                    case "Use Access : protected": accessComp.setText("Use Access : private");break;
                    case "Use Access : private": accessComp.setText("Use Access : none(default)");break;
               	default: accessComp.setText("Use Access : public");
               }
          });
          accessComp.setFont(settings.Screen.PX16);
          accessComp.setArc(0, 0);
          add(accessComp);

          gsComp = new TextComp("Getter&Setter", c1, c2, c3, ()->{});
          gsComp.setBounds(300, getHeight() - 40, 200, 40);
          gsComp.setRunnable(()->{
               switch(gsComp.getText()){
                    case "Getter&Setter": gsComp.setText("Getter");break;
                    case "Getter": gsComp.setText("Setter");break;
                    default: gsComp.setText("Getter&Setter");
               }
          });
          gsComp.setFont(settings.Screen.PX16);
          gsComp.setArc(0, 0);
          add(gsComp);

          TextComp genComp = new TextComp("Generate", c1, c2, c3, this::generate);
          genComp.setBounds(500, getHeight() - 40, getWidth() - 500, 40);
          genComp.setFont(settings.Screen.PX16);
          genComp.setArc(0, 0);
          add(genComp);
     }

     public void generate(){
     	LinkedList<DataMember> selections = new LinkedList<>();
          comps.forEach(c->{
               if(c.color2 == ide.utils.UIManager.c3)
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
                    if(d.parameters == null){
                         TextComp textComp = new TextComp(d.name, c1, c2, c3, ()->{});
                         textComp.setRunnable(()->{
                              textComp.setColors(textComp.color1, textComp.color3, textComp.color2);
                         });
                         textComp.setBounds(0, y, getWidth(), 30);
                         textComp.setArc(0, 0);
                         panel.add(textComp);
                         comps.add(textComp);
                         members.add(d);
                         y += 30;
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
