package omega.tree;

import static omega.comp.Animations.*;
import static omega.utils.UIManager.*;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.File;
import java.util.LinkedList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import omega.Screen;
import omega.comp.FlexPanel;
import omega.utils.systems.creators.FileOperationManager;

public class FileTreePanel extends AbstractFileTreePanel {

  private File root;
  private LinkedList<FileTreeBranch> branches = new LinkedList<>();

  private FlexPanel flexPanel;
  private JPanel panel;
  private JScrollPane scrollPane;

  private int blockX;
  private int blockY;

  private int optimumBranchSize = 25;
  private int gap = 5;

  public FileTreePanel() {
    super(new BorderLayout());
    setLayout(null);
    setBackground(c2);
    setSize(300, 100);
    setPreferredSize(getSize());

    flexPanel = new FlexPanel(null, TOOLMENU_COLOR3_SHADE, null);
    flexPanel.setArc(0, 0);
    add(flexPanel, BorderLayout.CENTER);

    panel = new JPanel(null);
    panel.setBackground(c2);

    flexPanel.add(scrollPane = new JScrollPane(panel));

    scrollPane.setBackground(c2);
  }

  @Override
  public AbstractFileTreePanel init(File parentDirectory) {
    this.root = null;

    branches.forEach(panel::remove);
    branches.clear();

    blockX = blockY = 0;

    FileTreeBranch branch = new FileTreeBranch(this, parentDirectory);
    branch.setLocation(0, 0);
    branch.setRootMode(true);
    branch.lockMode();
    panel.add(branch);
    branches.add(branch);

    genBranch(parentDirectory);

    this.root = parentDirectory;

    return this;
  }

  public void genBranch(File root) {
    if (
      this.root != null &&
      this.root.getAbsolutePath().equals(root.getAbsolutePath())
    ) return;

    LinkedList<File> files = getFiles(root);
    if (files.isEmpty()) {
      return;
    }

    FileTreeBranch rootBranch = findBranch(root);
    int rootIndex = branches.indexOf(rootBranch) + 1;

    if (rootBranch.isExpanded()) {
      collapseBranch(rootBranch);
      return;
    }
    rootBranch.setExpanded(true);

    blockX = rootBranch.getX() + optimumBranchSize;
    blockY = rootBranch.getY() + optimumBranchSize + gap;

    jumpBranchesAfter(rootBranch, files.size() * (optimumBranchSize + gap));

    for (File file : files) {
      FileTreeBranch branch = new FileTreeBranch(this, file);
      branch.setLocation(blockX, blockY);
      panel.add(branch);
      branches.add(rootIndex++, branch);

      blockY += optimumBranchSize + gap;
    }

    computePreferredSize();
  }

  public void collapseBranch(FileTreeBranch branch) {
    LinkedList<FileTreeBranch> subBranches = findSubBranches(branch);
    subBranches.forEach(panel::remove);
    subBranches.forEach(branches::remove);

    jumpBranchesAfter(
      branch,
      -(subBranches.size() * (optimumBranchSize + gap))
    );

    subBranches.clear();

    branch.setExpanded(false);

    computePreferredSize();
  }

  public void expandBranch(FileTreeBranch branch) {
    genBranch(branch.getFile());
  }

  public void jumpBranchesAfter(FileTreeBranch branch, int jumpRange) {
    for (int i = branches.indexOf(branch) + 1; i < branches.size(); i++) {
      FileTreeBranch bx = branches.get(i);
      bx.setLocation(bx.getX(), bx.getY() + jumpRange);
    }
  }

  public void onFileBranchClicked(FileTreeBranch branch) {
    Screen.getScreen().loadFile(branch.getFile());
  }

  public LinkedList<File> getFiles(File dir) {
    LinkedList<File> files = new LinkedList<>();
    File[] F = dir.listFiles();
    if (F == null || F.length == 0) return files;
    for (File fx : F) files.add(fx);
    return FileOperationManager.sort(files);
  }

  public LinkedList<FileTreeBranch> findSubBranches(FileTreeBranch branch) {
    LinkedList<FileTreeBranch> results = new LinkedList<>();
    for (int i = branches.indexOf(branch) + 1; i < branches.size(); i++) {
      FileTreeBranch bx = branches.get(i);
      if (branch.isParentOf(bx)) results.add(bx); else break;
    }
    return results;
  }

  public FileTreeBranch findBranch(File file) {
    for (FileTreeBranch b : branches) {
      if (
        b.getFile().getAbsolutePath().equals(file.getAbsolutePath())
      ) return b;
    }

    return null;
  }

  public int getLargestBranchLastXPoint() {
    int x = branches.getFirst().getWidth();
    for (FileTreeBranch bx : branches) {
      if (bx.getX() + bx.getWidth() > x) {
        x = bx.getX() + bx.getWidth();
      }
    }
    return x + 2;
  }

  public int getLastBranchLastYPoint() {
    FileTreeBranch bx = branches.getLast();
    return bx.getY() + bx.getHeight() + 2;
  }

  public void computePreferredSize() {
    int w = getLargestBranchLastXPoint();
    int h = getLastBranchLastYPoint();

    panel.setSize(w, h);
    panel.setPreferredSize(panel.getSize());

    Screen.getScreen().splitPane.setDividerLocation((w > 300) ? (w + 25) : 300);
  }

  @Override
  public void setVisible(boolean value) {
    super.setVisible(value);
    computePreferredSize();
  }

  @Override
  public void layout() {
    flexPanel.setBounds(0, 0, getWidth(), getHeight());
    scrollPane.setBounds(
      5,
      5,
      flexPanel.getWidth() - 10,
      flexPanel.getHeight() - 10
    );
    super.layout();
  }

  @Override
  public void refresh() {
    LinkedList<FileTreeBranch> expandedBranches = new LinkedList<>();
    for (int i = 1; i < branches.size(); i++) {
      FileTreeBranch bx = branches.get(i);
      if (bx.isExpanded()) expandedBranches.add(bx);
    }

    int scrollPosition = scrollPane.getVerticalScrollBar().getValue();

    init(root);

    expandedBranches.forEach(this::expandBranch);
    expandedBranches.clear();

    scrollPane.getVerticalScrollBar().setValue(scrollPosition);

    new Thread(() -> {
      if (Screen.getFileView().getJDKManager() != null) Screen
        .getFileView()
        .getJDKManager()
        .readSources(getRoot().getAbsolutePath());
      Screen
        .getFileView()
        .getSearchWindow()
        .cleanAndLoad(new File(Screen.getFileView().getProjectPath()));
    })
      .start();
  }

  @Override
  public void paint(Graphics graphics) {
    Graphics2D g = (Graphics2D) graphics;
    g.setRenderingHint(
      RenderingHints.KEY_RENDERING,
      RenderingHints.VALUE_RENDER_SPEED
    );
    g.setRenderingHint(
      RenderingHints.KEY_ANTIALIASING,
      RenderingHints.VALUE_ANTIALIAS_ON
    );
    g.setRenderingHint(
      RenderingHints.KEY_TEXT_ANTIALIASING,
      RenderingHints.VALUE_TEXT_ANTIALIAS_ON
    );
    super.paint(g);
    g.dispose();
  }

  public java.io.File getRoot() {
    return root;
  }

  public static synchronized void loadFilesIncludeSubDirectories(
    LinkedList<File> files,
    File file,
    String ext
  ) {
    if (file == null) return;
    File[] F = file.listFiles();
    for (File f : F) {
      if (f.isDirectory()) loadFilesIncludeSubDirectories(
        files,
        f,
        ext
      ); else if (f.getName().endsWith(ext)) files.add(f);
    }
  }
}
