package backup.GUI;

import backup.Tools.CloudFile;
import backup.Tools.FileHelper;
import backup.Tools.OkHttpUtils;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static backup.GUIApp.panPanel;
import static backup.GUIApp.panTree;

public class PanGUI {

   public static void init(){

        panPanel.setLayout(null);

        JButton panDownload=new JButton("下载云端目录");
        panDownload.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OkHttpUtils.getCloudFileList();
                try {
                    Thread.sleep(2000);    //延时2秒
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {

                        panPanel.remove(panTree);

                        DefaultMutableTreeNode root=new DefaultMutableTreeNode("网盘文件");
                        DefaultMutableTreeNode node=null;

                        for(int i = 0; i< FileHelper.panList.size(); i++)
                        {
                            CloudFile c=FileHelper.panList.get(i);
                            System.out.println(c.filename);
                            node=new DefaultMutableTreeNode(c.filename);
                            root.add(node);
                        }
                        System.out.println("构建完成");
                        panTree=new JTree(root);
                        panTree.addTreeSelectionListener(new TreeSelectionListener() {
                            @Override
                            public void valueChanged(TreeSelectionEvent e) {
                                DefaultMutableTreeNode node = (DefaultMutableTreeNode) panTree.getLastSelectedPathComponent();
                                String name= node.getUserObject().toString();
                                System.out.println("点击"+name);
                                new DialogDownload(name);


                            }
                        });
                        panTree.setBounds(10,50,200,1000);
                        panPanel.add(panTree);
                        panPanel.updateUI();
                        panPanel.repaint();
                    }
                });


            }
        });
        panDownload.setBounds(10,10,200,40);

        panPanel.add(panDownload);


// ---------------

        DefaultMutableTreeNode root=new DefaultMutableTreeNode("网盘文件");
        DefaultMutableTreeNode node=null;
        for(int i = 0; i< FileHelper.panList.size(); i++)
        {
            System.out.println(FileHelper.panList.get(i).filename);
            node=new DefaultMutableTreeNode(FileHelper.panList.get(i).filename);
            root.add(node);
        }
        panTree=new JTree(root);
       panTree.addTreeSelectionListener(new TreeSelectionListener() {
           @Override
           public void valueChanged(TreeSelectionEvent e) {
               DefaultMutableTreeNode node = (DefaultMutableTreeNode) panTree.getLastSelectedPathComponent();
               if (node == null) {
                   return;
               }
           }
       });
       panTree.setBounds(10,50,200,1000);
       panPanel.add(panTree);

    }
}
