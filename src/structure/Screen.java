package structure;
import ide.utils.UIManager;
import java.awt.Font;
import ide.utils.Editor;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import deassembler.ByteReader;
import deassembler.DataMember;
import deassembler.SourceReader;
import deassembler.CodeFramework;
import javax.swing.JComponent;
import java.awt.Dimension;
import importIO.Import;
import importIO.ImportManager;
import java.util.LinkedList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JPanel;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import settings.comp.TextComp;
import javax.swing.JDialog;

import static ide.utils.UIManager.*;
public class Screen extends JDialog {
     private TextComp textComp;
     private JPanel leftPanel;
     private RSyntaxTextArea textArea;
     private JScrollPane rightPane;
     private int leftBlock;
     private JScrollPane scrollPane;
     private TextComp toggleComp;
     private String lastSearch = "";
     private boolean nameTypeView = true;
     private LinkedList<TextComp> classComps = new LinkedList<>();
     private LinkedList<DataMember> dataMembers = new LinkedList<>();
     private int mouseX;
     private int mouseY;
     public Screen(ide.Screen screen){
          super(screen);
          setTitle("Structure View");
          setIconImage(screen.getIconImage());
          setModal(false);
          setLayout(null);
          setResizable(false);
          setBackground(c2);
          setUndecorated(true);
     	setSize(1000, 600);
          setLocationRelativeTo(null);
          init();
     }

     public void init(){
     	textComp = new TextComp("Structure View", c3, c1, c2, ()->{});
          textComp.setClickable(false);
          textComp.setFont(settings.Screen.PX18);
          textComp.setArc(0, 0);
          textComp.addMouseMotionListener(new MouseAdapter(){
               @Override
               public void mouseDragged(MouseEvent e) {
                  setLocation(e.getXOnScreen() - mouseX, e.getYOnScreen() - mouseY);
               }
          });
          textComp.addMouseListener(new MouseAdapter(){
               @Override
               public void mousePressed(MouseEvent e) {
                    mouseX = e.getX();
                    mouseY = e.getY();
               }
          });
          textComp.setBounds(0, 0, getWidth() - 40, 40);
          add(textComp);

          TextComp closeComp = new TextComp("X", c1, c2, c3, ()->setVisible(false));
          closeComp.setArc(0, 0);
          closeComp.setBounds(getWidth() - 40, 0, 40, 40);
          closeComp.setFont(settings.Screen.PX14);
          add(closeComp);

          leftPanel = new JPanel(null);
          leftPanel.setBackground(c2);
          scrollPane = new JScrollPane(leftPanel);
          scrollPane.setBounds(0, 70, getWidth()/3, getHeight() - 40);
          add(scrollPane);

          TextComp sep0 = new TextComp("", c1, c3, c3, ()->{});
          sep0.setBounds(getWidth()/3, 40, 2, getHeight() - 40);
          add(sep0);

          JTextField searchClassField = new JTextField("Class name or exact package");
          searchClassField.setBounds(0, 40, getWidth()/3, 30);
          searchClassField.setFont(settings.Screen.PX16);
          searchClassField.setForeground(c3);
          searchClassField.addActionListener((e)->new Thread(()->find(e.getActionCommand())).start());
          add(searchClassField);
          
          rightPane = new JScrollPane(textArea = new RSyntaxTextArea());
          rightPane.setBackground(c2);
          rightPane.setBounds(getWidth()/3, 70, getWidth() - getWidth()/3, getHeight() - 70);
          add(rightPane);

          JTextField searchContentField = new JTextField();
          searchContentField.setBounds(getWidth()/3 + 2, 40, rightPane.getWidth() - 10 - 30, 30);
          searchContentField.setFont(settings.Screen.PX16);
          searchContentField.setForeground(c3);
          searchContentField.addActionListener((e)->new Thread(()->search(e.getActionCommand())).start());
          add(searchContentField);

          toggleComp = new TextComp("~", c1, c2, c3, ()->{
               nameTypeView = !nameTypeView;
               search(lastSearch);
               toggleComp.setText(nameTypeView ? "~" : "$");
          });
          toggleComp.setBounds(getWidth() - 40, 40, 40, 30);
          toggleComp.setFont(settings.Screen.PX14);
          toggleComp.setArc(0, 0);
          add(toggleComp);

          textArea.setEditable(false);
          textArea.setSyntaxEditingStyle(textArea.SYNTAX_STYLE_JAVA);
          Editor.getTheme().apply(textArea);
          textArea.setFont(new Font(UIManager.fontName, Font.BOLD, UIManager.fontSize));
     }

     public void search(String text){
          lastSearch = text;
     	textArea.setText("");
          if(text.trim().equals("")){
               dataMembers.forEach(this::print);
               textArea.setCaretPosition(0);
               return;
          }
          dataMembers.forEach(d->{
               if(d.getData().contains(text))
                    print(d);
          });
          textArea.setCaretPosition(0);
     }

     public void print(DataMember d){
     	if(nameTypeView){
               if(d.parameters == null)
                    textArea.append(d.name + " -> " + d.type + "\n");
               else
                    textArea.append(d.name.substring(0, d.name.indexOf('(')) + "(" + d.parameters + ")" + " -> " + d.type + "\n");
     	}
          else
               textArea.append(d.getData().trim() + ";\n");
     }

     public void find(String path){
          textComp.setText("Searching -> "+ path);
          classComps.forEach(leftPanel::remove);
          classComps.clear();
          
     	leftBlock = 0;
          boolean containsDot = path.contains(".");
          LinkedList<String> addedImports = new LinkedList<>();
          for(Import im : ImportManager.getAllImports()){
               if(addedImports.contains(im.getImport())) continue;
               if((containsDot && im.getImport().contains(path)) || im.getClassName().equals(path)) {
                    String text = im.getImport();
                    final String TEXT = text;
                    if(text.length() > 35)
                         text = text.substring(0, 35) + "..";
                    
                    TextComp codeComp = new TextComp(text, c1, c2, c3, ()->{new Thread(()->genView(im)).start();});
                    codeComp.setBounds(0, leftBlock, getWidth()/3, 30);
                    
                    if(TEXT.length() > 35)
                         codeComp.setToolTipText(TEXT);
                    codeComp.setFont(settings.Screen.PX14);
                    classComps.add(codeComp);
                    leftPanel.add(codeComp);
                    leftBlock += 30;
                    addedImports.add(im.getImport());
                    textComp.setText(im.getImport());
               }
          }
          addedImports.clear();
          leftPanel.setPreferredSize(new Dimension(getWidth()/3, leftBlock + 30));
          leftPanel.repaint();
          textComp.setText("Structure View");
          scrollPane.getVerticalScrollBar().setVisible(true);
          scrollPane.getVerticalScrollBar().setValue(0);
          repaint();
     }

     public void genView(Import im){
          String text = im.getImport();
     	textComp.setText("Generating View for " + text);

          if(CodeFramework.isSource(im.getImport())) {
               SourceReader reader = new SourceReader(CodeFramework.getContent(im.getImport()));
               this.dataMembers = reader.dataMembers;
               search("");
          }
          else {
               ByteReader reader = new ByteReader(im.getImport());
               this.dataMembers = reader.dataMembers;
               search("");
          }
          textComp.setText(text);
     }
}