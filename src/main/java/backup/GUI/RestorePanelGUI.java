package backup.GUI;

import backup.Tools.SavedFile;
import com.sun.source.tree.NewArrayTree;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import static backup.BackupApp.*;
import static backup.GUI.BackupPanelGUI.fixedSizeLabel;


public class RestorePanelGUI {
    private static JTree restoreTree;
    private static JButton restoreSourceButton;
    private static JButton restoreTargetButton;
    private static JButton restoreStartButton;
    private static JLabel restoreSourceLabel;
    private static JLabel restoreTargetLabel;
    public static JPanel restorePanel ;

    public static String sourcePath="";
    public static String targetPath="";


    public static void updateRestoreTree(){

        GridBagConstraints c=new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);

        restorePanel.remove(restoreTree);
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
        restoreTree=new JTree(root);
        restoreTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) restoreTree.getLastSelectedPathComponent();
                if (node == null) {
                    return;
                }
                String filepath= node.getUserObject().toString();
                System.out.println("点击"+filepath);
                sourcePath=new String(filepath);
                int n=JOptionPane.showConfirmDialog(null,"确认是否添加到待恢复文件","提示", JOptionPane.YES_NO_OPTION );
                System.out.println("joption:"+n);
                if(n==0){
                    //点击是
                    restoreSourceLabel.setText("待恢复文件"+filepath);
                }


            }
        });
        // backupPanel.add(tree);
        c.gridx=0;
        c.gridy=0;
        c.gridwidth=1;

        System.out.println("tree add");
        restorePanel.add(restoreTree,c);

    }
    public static void init(){

        restorePanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);

        restoreSourceLabel = fixedSizeLabel("待恢复文件");
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        restorePanel.add(restoreSourceLabel, c);


        restoreSourceButton = new JButton("选择待恢复文件");
        restoreSourceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                // 选择恢复源文件时，只允许选择普通文件
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            //    fc.setCurrentDirectory(new File("C:\\Users\\Charon\\Desktop"));
                int result = fc.showOpenDialog(null);
                if (result != JFileChooser.APPROVE_OPTION) {
                    return;
                }

                // 获取选择的文件路径并设置相应 label 的值
                File file = fc.getSelectedFile();
                String path = file.getAbsolutePath();
                sourcePath=new String(path);

                restoreSourceLabel.setText("待恢复文件"+path);

            }
        });
        c.gridx = 3;
        c.gridy = 1;
        c.gridwidth = 1;
        restorePanel.add(restoreSourceButton, c);

        restoreTargetLabel = fixedSizeLabel("恢复目录");
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 3;
        restorePanel.add(restoreTargetLabel, c);

        restoreTargetButton = new JButton("选择恢复文件存放目录");
        restoreTargetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                // 选择备份源文件、备份目标文件和恢复目标文件时，只允许选择目录
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                //fc.setCurrentDirectory(new File("~"));
                int result = fc.showOpenDialog(null);
                if (result != JFileChooser.APPROVE_OPTION) {
                    return;
                }

                // 获取选择的文件路径并设置相应 label 的值
                File file = fc.getSelectedFile();
                String path = file.getAbsolutePath();


                targetPath=new String(path);
                restoreTargetLabel.setText("恢复文件存放目录"+path);
            }
        });
        c.gridx = 3;
        c.gridy = 2;
        c.gridwidth = 1;
        restorePanel.add(restoreTargetButton, c);



        restoreStartButton = new JButton("开始恢复");
        restoreStartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ("".equals(sourcePath)) {
                    error("请选择待恢复文件");
                    return;
                }
                if ("".equals(targetPath)) {
                    error("请先选择恢复文件存放目录");
                    return;
                }

                try {
                    new RestoreDialog(sourcePath, targetPath);
                    //   String password = new String(restorePassword.getPassword()).trim();


                } catch (Exception exception) {
                    exception.printStackTrace();
                    error(exception.getMessage());
                }
            }
        });
        c.gridx = 3;
        c.gridy = 3;
        c.gridwidth = 1;

        restorePanel.add(restoreStartButton, c);
        restoreTree=new JTree();
        restorePanel.add(restoreTree);

        updateRestoreTree();
        tabbedPane.add("恢复", restorePanel);

    }
    private static void error(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
