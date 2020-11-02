package ui;
import javax.swing.*;
import java.awt.*;
public class PathPanel extends JPanel{
     public static final Font font = new Font("Consolas", Font.BOLD, 14);
     public static JTextField cargPane;
     public static JTextField rargPane;
     public PathPanel(View view){
          setLayout(null);

          Box labelCompile = new Box("Compile-Tme Arguments", ()->{
          });
          labelCompile.setFont(font);
          labelCompile.disabled = true;
          labelCompile.setBounds(1, 2, view.getWidth() - view.getWidth() / 3 - 2, 30);
          add(labelCompile);

          cargPane = new JTextField();
          cargPane.setFont(font);
          ide.utils.UIManager.setData(cargPane);
          JScrollPane cPane = new JScrollPane(cargPane);
          cPane.setBounds(1, labelCompile.getY() + labelCompile.getHeight() + 1, view.getWidth() - view.getWidth() / 3 - 2, 120);
          add(cPane);

          Box labelRun = new Box("Run-Tme Arguments", ()->{
          });
          labelRun.disabled = true;
          labelRun.setFont(font);
          labelRun.setBounds(1, cPane.getY() + cPane.getHeight() + 1, view.getWidth() - view.getWidth() / 3 - 2, 30);
          add(labelRun);

          rargPane = new JTextField();
          rargPane.setFont(font);
          ide.utils.UIManager.setData(rargPane);
          JScrollPane rPane = new JScrollPane(rargPane);
          rPane.setBounds(1, labelRun.getY() + labelRun.getHeight() + 1, view.getWidth() - view.getWidth() / 3 - 2, 117);
          add(rPane);
     }
}
