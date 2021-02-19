package omega.gset;
import java.awt.event.MouseEvent;
import omega.Screen;
import java.awt.Dimension;
import omega.deassembler.Assembly;
import omega.framework.CodeFramework;
import omega.deassembler.ByteReader;
import omega.deassembler.SourceReader;
import java.awt.event.MouseAdapter;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import omega.deassembler.DataMember;
import omega.comp.TextComp;
import java.util.LinkedList;
import javax.swing.JDialog;
import omega.gset.OverView;
import static omega.utils.UIManager.*;
public class OverView extends JDialog{
     private int mouseX;
     private int mouseY;
     private LinkedList<TextComp> comps = new LinkedList<>();
     private LinkedList<DataMember> members = new LinkedList<>();
     private RSyntaxTextArea textArea;
     private JScrollPane scrollPane;
     private JPanel panel;
     public OverView(Screen screen){
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
          closeComp.setFont(omega.settings.Screen.PX16);
          closeComp.setArc(0, 0);
          add(closeComp);

          TextComp titleComp = new TextComp("Override/Implement Methods", c1, c2, c3, ()->{});
          titleComp.setBounds(40, 0, getWidth() - 40, 40);
          titleComp.setFont(omega.settings.Screen.PX18);
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

          TextComp genComp = new TextComp("Implement", c1, c2, c3, this::generate);
          genComp.setBounds(0, getHeight() - 40, getWidth(), 40);
          genComp.setFont(omega.settings.Screen.PX16);
          genComp.setArc(0, 0);
          add(genComp);
     }

     public void generate(){
          LinkedList<DataMember> selections = new LinkedList<>();
          comps.forEach(c->{
               if(c.color2 == c3)
                    selections.add(members.get(comps.indexOf(c)));
          });
          selections.forEach(d->Generator.implement(d, textArea));
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
               LinkedList<ByteReader> brs = new LinkedList<>();
               LinkedList<SourceReader> srs = new LinkedList<>();
               if(reader.features != null){
                    for(String feature : reader.features){
                         String path = reader.getPackage(feature);
                         if(CodeFramework.isSource(path))
                              srs.add(new SourceReader(CodeFramework.getContent(path)));
                         else{
                              ByteReader bx = null;
                              if(Assembly.has(path))
                                   bx = Assembly.getReader(path);
                              else 
                                   bx = new ByteReader(path);
                              brs.add(bx);
                         }
                    }
               }
               if(!reader.parent.equals("Object")){
                    String path = reader.getPackage(reader.parent);
                    if(CodeFramework.isSource(path))
                         srs.add(new SourceReader(CodeFramework.getContent(path)));
                    else{
                         ByteReader bx = null;
                         if(Assembly.has(path))
                              bx = Assembly.getReader(path);
                         else 
                              bx = new ByteReader(path);
                         brs.add(bx);
                    }
               }
               int y = 0;
               for(ByteReader b : brs){
                    TextComp txComp = new TextComp(b.className, c1, c2, c3, ()->{});
                    txComp.setBounds(0, y, getWidth(), 30);
                    txComp.setArc(0, 0);
                    txComp.setClickable(false);
                    txComp.setFont(omega.settings.Screen.PX14);
                    panel.add(txComp);
                    comps.add(txComp);
                    members.add(new DataMember("", "", "", "", ""));
                    y += 30;
                    for(DataMember d : b.dataMembers){
                         if(d.parameters != null && !Generator.isMemberOfObject(d)){
                              String rep = d.getRepresentableValue();
                              if(rep == null) continue;
                              TextComp textComp = new TextComp(rep, c1, c2, c3, ()->{});
                              textComp.setRunnable(()->{
                                   textComp.setColors(textComp.color1, textComp.color3, textComp.color2);
                              });
                              textComp.setBounds(0, y, getWidth(), 30);
                              textComp.setArc(0, 0);
                              textComp.alignX = 5;
                              textComp.setFont(omega.settings.Screen.PX14);
                              panel.add(textComp);
                              comps.add(textComp);
                              members.add(d);
                              y += 30;
                         }
                    }
               }
               for(SourceReader s : srs){
                    TextComp txComp = new TextComp(s.className, c1, c2, c3, ()->{});
                    txComp.setBounds(0, y, getWidth(), 30);
                    txComp.setArc(0, 0);
                    txComp.setClickable(false);
                    txComp.setFont(omega.settings.Screen.PX14);
                    panel.add(txComp);
                    comps.add(txComp);
                    members.add(new DataMember("", "", "", "", ""));
                    y += 30;
                    for(DataMember d : s.dataMembers){
                         if(d.parameters != null && !Generator.isMemberOfObject(d)){
                              String rep = d.getRepresentableValue();
                              if(rep == null) continue;
                              TextComp textComp = new TextComp(rep, c1, c2, c3, ()->{});
                              textComp.setRunnable(()->{
                                   textComp.setColors(textComp.color1, textComp.color3, textComp.color2);
                              });
                              textComp.setBounds(0, y, getWidth(), 30);
                              textComp.setArc(0, 0);
                              textComp.alignX = 5;
                              textComp.setFont(omega.settings.Screen.PX14);
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
