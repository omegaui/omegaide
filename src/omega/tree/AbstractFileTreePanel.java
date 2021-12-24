package omega.tree;

import java.awt.LayoutManager;
import java.io.File;
import javax.swing.JPanel;

public abstract class AbstractFileTreePanel extends JPanel {

  public AbstractFileTreePanel(LayoutManager layoutManager) {
    super(layoutManager);
  }

  public abstract AbstractFileTreePanel init(File parentDirectory);

  public abstract void refresh();
}
