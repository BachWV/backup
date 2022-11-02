package backup.GUI;

import backup.BackupApp;
import backup.Tools.FileHelper;
import backup.Tools.OkHttpUtils;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static backup.BackupApp.panPanel;
import static backup.BackupApp.panTree;

public class PanGUI {
    public static void updateTree(){
        panPanel.remove(panTree);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx=0;
        c.gridy=1;
        c.gridwidth=3;
        DefaultMutableTreeNode root=new DefaultMutableTreeNode("网盘文件");
        DefaultMutableTreeNode node=null;
        for(int i = 0; i< FileHelper.panList.size(); i++)
        {
            System.out.println(FileHelper.panList.get(i).filename);


            node=new DefaultMutableTreeNode(FileHelper.panList.get(i).filename);

            root.add(node);
        }
        c.gridx=0;
        c.gridy=2;
        c.gridwidth=3;
        panTree=new JTree(root);
        panTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) panTree.getLastSelectedPathComponent();
                String name=node.getUserObject().toString();
                System.out.println(name);
                 new DialogDownload(name);

            }
        });
        panPanel.add(panTree,c);


    }
   public static void init(){

        panPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx=0;
        c.gridy=0;
        c.gridwidth=1;
        JButton panDownload=new JButton("下载云端目录");
        panDownload.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OkHttpUtils.main();


            }
        });
        panPanel.add(panDownload,c);
        c.gridy=1;

// ---------------

        DefaultMutableTreeNode root=new DefaultMutableTreeNode("网盘文件");
        DefaultMutableTreeNode node=null;
        for(int i = 0; i< FileHelper.panList.size(); i++)
        {
            System.out.println(FileHelper.panList.get(i).filename);


            node=new DefaultMutableTreeNode(FileHelper.panList.get(i).filename);

            root.add(node);
        }
        c.gridx=0;
        c.gridy=2;
        c.gridwidth=3;
        panTree=new JTree(root);
       panTree.addTreeSelectionListener(new TreeSelectionListener() {
           @Override
           public void valueChanged(TreeSelectionEvent e) {
               DefaultMutableTreeNode node = (DefaultMutableTreeNode) panTree.getLastSelectedPathComponent();
               if (node == null) {
                   return;
               }
               // Container contentPane = getContentPane();

               ///   JOptionPane.showMessageDialog("点中了"+node.toString(), "OK", JOptionPane.INFORMATION_MESSAGE);
           }
       });
        panPanel.add(panTree,c);

    }
}
