package backup.GUI;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;


import backup.Tools.SavedFile;

import static backup.BackupApp.fileSavedlist;
import static backup.BackupApp.tabbedPane;

public class BackupPanelGUI {
    private static JTree tree;
    private static JLabel backupSourceLabel;
    private static JLabel backTargetLabel;
    private static JButton backupSourceButton;
    private static JButton backupTargetButton;
    private static JButton backupStartButton;
    public static JPanel backupPanel ;
    private static final String BACKUP_SOURCE_PLACEHOLDER = "选择源备份目录";
    private static final String BACKUP_TARGET_PLACEHOLDER = "选择目标存放目录";
    public static void updateTree(){

        GridBagConstraints c=new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);

        backupPanel.remove(tree);
        DefaultMutableTreeNode root=new DefaultMutableTreeNode("已备份文件");
        DefaultMutableTreeNode node=null;
        for(int i = 0; i< fileSavedlist.size(); i++)
        {
            SavedFile ss=fileSavedlist.get(i);
            System.out.println(ss.src);
            File src=new File(ss.src);
            String name=ss.trg+File.separator+src.getName()+".pack.huff.enc";

            node=new DefaultMutableTreeNode(name);

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
                String filepath= node.getUserObject().toString();
                System.out.println("点击"+filepath);
                new UploadDialog(filepath);

            }
        });
        // backupPanel.add(tree);
        c.gridx=0;
        c.gridy=4;
        c.gridwidth=1;

        System.out.println("tree add");
        backupPanel.add(tree,c);

    }
    private static JLabel fixedSizeLabel(String text) {
        JLabel label = new JLabel(text);
        Dimension dimension = new Dimension(320, 32);
        label.setMinimumSize(dimension);
        label.setMaximumSize(dimension);
        label.setPreferredSize(dimension);
        return label;
    }
    public static void init(){
        backupPanel = new JPanel();
        backupPanel.setLayout(new GridBagLayout());
        GridBagConstraints c=new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);

        backupSourceLabel = fixedSizeLabel(BACKUP_SOURCE_PLACEHOLDER);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 3;
        backupPanel.add(backupSourceLabel, c);
        backupSourceLabel.setFont(new Font("微软雅黑", Font.BOLD, 23));
        backupSourceButton = new JButton("Choose source dir");
        backupSourceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                // 选择备份源文件、备份目标文件和恢复目标文件时，只允许选择目录
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fc.setCurrentDirectory(new File("C:\\Users\\Charon\\Desktop"));
                int result = fc.showOpenDialog(null);
                if (result != JFileChooser.APPROVE_OPTION) {
                    return;
                }

                // 获取选择的文件路径并设置相应 label 的值
                File file = fc.getSelectedFile();
                String path = file.getAbsolutePath();
                backupSourceLabel.setText(path);
            }
        });
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
        backupTargetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                // 选择备份源文件、备份目标文件和恢复目标文件时，只允许选择目录
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fc.setCurrentDirectory(new File("C:\\Users\\Charon\\Desktop"));
                int result = fc.showOpenDialog(null);
                if (result != JFileChooser.APPROVE_OPTION) {
                    return;
                }

                // 获取选择的文件路径并设置相应 label 的值
                File file = fc.getSelectedFile();
                String path = file.getAbsolutePath();

                backTargetLabel.setText(path);

            }
        });
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
        tree=new JTree();
        backupPanel.add(tree);
        updateTree();
        tabbedPane.add("备份", backupPanel);

    }
    private static void error(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

}