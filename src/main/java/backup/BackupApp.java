package backup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import backup.GUI.DemoDialog;
import backup.GUI.PanGUI;
import backup.Tools.FileHelper;
import backup.Tools.OkHttpUtils;
import backup.Tools.SavedFile;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;

import java.util.ArrayList;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * 备份应用 GUI 入口
 */
public class BackupApp extends JFrame implements ActionListener {
    private  JButton backupSourceButton;
    private  JButton backupTargetButton;
    private  JButton backupStartButton;
    private  JButton restoreSourceButton;
    private  JButton restoreTargetButton;
    private  JButton restoreStartButton;
    private  JButton diffSourceButton;
    private  JButton diffTargetButton;
    private JButton diffCheckButton;

    private JLabel backupSourceLabel;
    private  JLabel backTargetLabel;
    private  JLabel restoreSourceLabel;
    private  JLabel restoreTargetLabel;
    private  JLabel diffSourceLabel;
    private  JLabel diffTargetLabel;

    private static JPanel restorePanel ;
    public  static JPanel panPanel;
    private static final String BACKUP_SOURCE_PLACEHOLDER = "选择源备份目录";
    private static final String BACKUP_TARGET_PLACEHOLDER = "选择目标存放目录";
    private static final String RESTORE_SOURCE_PLACEHOLDER = "选择其他已备份文件";
    private static final String RESTORE_TARGET_PLACEHOLDER = "选择还原位置";
    private static final String DIFF_SOURCE_PLACEHOLDER = "选择源目录";
    private static final String DIFF_TARGET_PLACEHOLDER = "选择目标目录";
    public static final JFileChooser fc = new JFileChooser();

    public static Backuper backuper = new Backuper();
    private final DiffChecker diffChecker = new DiffChecker();
    public static ArrayList<SavedFile> fileSavedlist =new ArrayList<>();
    public BackupApp(){
        super("备份之王");

        //Flat Light
        //FlatLightLaf.install();

//Flat Dark
        //FlatDarkLaf.install();

//Flat IntelliJ
      //  FlatIntelliJLaf.install();

//Flat Darcula
        FlatDarculaLaf.install();
        initPanel();
    }
    private static JTree tree;
    public static JTree panTree;
    public static void updateTree(){
        restorePanel.remove(tree);
        DefaultMutableTreeNode root=new DefaultMutableTreeNode("已备份文件");
        DefaultMutableTreeNode node=null;
        for(int i = 0; i< fileSavedlist.size(); i++)
        {
            System.out.println(fileSavedlist.get(i));


            node=new DefaultMutableTreeNode(fileSavedlist.get(i).getSrc());

            root.add(node);
        }
        tree=new JTree(root);
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                if (node == null) {
                    return;
                }

            }
        });
        // backupPanel.add(tree);

        restorePanel.add(tree);

    }
    private void initPanel(){
        //窗体大小
        this.setPreferredSize(new Dimension(800, 600));
        //LOGO在左上角
        int width = 45, height = 45;
        // 创建1个图标实例,注意image目录要与src同级
        ImageIcon icon = new ImageIcon("img//logo.png");
        icon.setImage(icon.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT));
        // 1个图片标签,显示图片
        JLabel jlImg = new JLabel(icon);
        jlImg.setSize(width, height);
        jlImg.setBounds(20, 44, width, height);
        setIconImage(icon.getImage());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    //

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setTabPlacement(2);

        JPanel backupPanel = new JPanel();
        backupPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);

        backupSourceLabel = fixedSizeLabel(BACKUP_SOURCE_PLACEHOLDER);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 3;
        backupPanel.add(backupSourceLabel, c);
        backupSourceLabel.setFont(new Font("微软雅黑", Font.BOLD, 23));
        backupSourceButton = new JButton("Choose source dir");
        backupSourceButton.addActionListener(this);
        c.gridx = 3;
        c.gridy = 0;
        c.gridwidth = 1;
        backupPanel.add(backupSourceButton, c);

        backTargetLabel = fixedSizeLabel(BACKUP_TARGET_PLACEHOLDER);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 3;
        backupPanel.add(backTargetLabel, c);

        backupTargetButton = new JButton("Choose target dir");
        backupTargetButton.addActionListener(this);
        c.gridx = 3;
        c.gridy = 1;
        c.gridwidth = 1;
        backupPanel.add(backupTargetButton, c);

        backupStartButton = new JButton("开始备份");
        backupStartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (BACKUP_SOURCE_PLACEHOLDER.equals(backupSourceLabel.getText())) {
                    error("You need to select the directory to backup first");
                    return;
                }
                if (BACKUP_TARGET_PLACEHOLDER.equals(backTargetLabel.getText())) {
                    error("You need to select the directory to place the backup file first");
                    return;
                }

                try {
                    new DemoDialog(backupSourceLabel.getText(),backTargetLabel.getText());


                } catch (Exception exception) {
                    exception.printStackTrace();
                    error(exception.getMessage());
                }
            }
        });
        c.gridx = 3;
        c.gridy = 2;
        c.gridwidth = 1;
        backupPanel.add(backupStartButton, c);

        tabbedPane.add("备份", backupPanel);

        //============================= restore ============================

        restorePanel = new JPanel();
        restorePanel.setLayout(new GridBagLayout());

        restoreSourceLabel = fixedSizeLabel(RESTORE_SOURCE_PLACEHOLDER);
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 3;
        restorePanel.add(restoreSourceLabel, c);

        restoreSourceButton = new JButton("Choose backup file");
        restoreSourceButton.addActionListener(this);
        c.gridx = 3;
        c.gridy = 4;
        c.gridwidth = 1;
        restorePanel.add(restoreSourceButton, c);

        restoreTargetLabel = fixedSizeLabel(RESTORE_TARGET_PLACEHOLDER);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 3;
        restorePanel.add(restoreTargetLabel, c);

        restoreTargetButton = new JButton("Choose target dir");
        restoreTargetButton.addActionListener(this);
        c.gridx = 3;
        c.gridy = 1;
        c.gridwidth = 1;
        restorePanel.add(restoreTargetButton, c);



        restoreStartButton = new JButton("Start restore");
        restoreStartButton.addActionListener(this);
        c.gridx = 3;
        c.gridy = 2;
        c.gridwidth = 1;
        restorePanel.add(restoreStartButton, c);

        tabbedPane.add("Restore", restorePanel);

        //============================== diff =============================
        JPanel diffPanel = new JPanel();
        diffPanel.setLayout(new GridBagLayout());

        diffSourceLabel = fixedSizeLabel(DIFF_SOURCE_PLACEHOLDER);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 3;
        diffPanel.add(diffSourceLabel, c);

        diffSourceButton = new JButton("Choose source directory");
        diffSourceButton.addActionListener(this);
        c.gridx = 3;
        c.gridy = 0;
        c.gridwidth = 1;
        diffPanel.add(diffSourceButton, c);

        diffTargetLabel = fixedSizeLabel(DIFF_TARGET_PLACEHOLDER);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 3;
        diffPanel.add(diffTargetLabel, c);

        diffTargetButton = new JButton("选择目标目录");
        diffTargetButton.addActionListener(this);
        c.gridx = 3;
        c.gridy = 1;
        c.gridwidth = 1;
        diffPanel.add(diffTargetButton, c);

        diffCheckButton = new JButton("Check diff");
        diffCheckButton.addActionListener(this);
        c.gridx = 3;
        c.gridy = 2;
        c.gridwidth = 1;
        diffPanel.add(diffCheckButton, c);
        tabbedPane.add("diff", diffPanel);

//-------------------
        panPanel=new JPanel();
        PanGUI.init();

        tabbedPane.add("网盘", panPanel);
        //----------------------
        JPanel panel=new JPanel();
        tree=new JTree();
        restorePanel.add(tree);
        updateTree();
        add(tabbedPane);
        //add(backupPanel);
        pack();
        setLocationRelativeTo(null);

    }

    private JLabel fixedSizeLabel(String text) {
        JLabel label = new JLabel(text);
        Dimension dimension = new Dimension(320, 32);
        label.setMinimumSize(dimension);
        label.setMaximumSize(dimension);
        label.setPreferredSize(dimension);
        return label;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
       if (e.getSource() == restoreStartButton) { // 点击开始恢复按钮
            if (RESTORE_SOURCE_PLACEHOLDER.equals(restoreSourceLabel.getText())) {
                error("You need to select the backup file first");
                return;
            }
            if (RESTORE_TARGET_PLACEHOLDER.equals(restoreTargetLabel.getText())) {
                error("You need to select the directory to place the restored files first");
                return;
            }

            try {
             //   String password = new String(restorePassword.getPassword()).trim();
              //  backuper.restore(restoreSourceLabel.getText(), restoreTargetLabel.getText(), password);

            } catch (Exception exception) {
                exception.printStackTrace();
                error(exception.getMessage());
            }
        } else if (e.getSource() == diffCheckButton) {
            if (DIFF_SOURCE_PLACEHOLDER.equals(diffSourceLabel.getText())) {
                error("You need to select the diff source directory first");
                return;
            }
            if (RESTORE_TARGET_PLACEHOLDER.equals(diffTargetLabel.getText())) {
                error("You need to select the diff target directory first");
                return;
            }

            try {
                String diff = diffChecker.dirDiff(diffSourceLabel.getText(), diffTargetLabel.getText());
                JOptionPane.showMessageDialog(this, diff, "Diff", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception exception) {
                exception.printStackTrace();
                error(exception.getMessage());
            }
        } else {
            if (e.getSource() == backupSourceButton || e.getSource() == backupTargetButton || e.getSource() == restoreTargetButton ||
                    e.getSource() == diffSourceButton || e.getSource() == diffTargetButton) {
                // 选择备份源文件、备份目标文件和恢复目标文件时，只允许选择目录
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fc.setCurrentDirectory(new File("C:\\Users\\Charon\\Desktop"));
            } else if (e.getSource() == restoreSourceButton) {
                // 选择恢复源文件时，只允许选择普通文件
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            }

            int result = fc.showOpenDialog(this);
            if (result != JFileChooser.APPROVE_OPTION) {
                return;
            }

            // 获取选择的文件路径并设置相应 label 的值
            File file = fc.getSelectedFile();
            String path = file.getAbsolutePath();
            if (e.getSource() == backupSourceButton) {
                backupSourceLabel.setText(path);
            } else if (e.getSource() == backupTargetButton) {
                backTargetLabel.setText(path);
            } else if (e.getSource() == restoreSourceButton) {
                restoreSourceLabel.setText(path);
            } else if (e.getSource() == restoreTargetButton) {
                restoreTargetLabel.setText(path);
            } else if (e.getSource() == diffSourceButton) {
                diffSourceLabel.setText(path);
            } else if (e.getSource() == diffTargetButton) {
                diffTargetLabel.setText(path);
            }
        }
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
        UIManager.setLookAndFeel(lookAndFeel);
        SwingUtilities.invokeLater(() -> {
            BackupApp app = new BackupApp();
            app.setVisible(true);
        });
    }
}
