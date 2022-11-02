package backup.GUI;

import backup.BackupApp;
import backup.Backuper;
import backup.Tools.FileHelper;
import backup.Tools.SavedFile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;

import static backup.BackupApp.fileSavedlist;

public class DemoDialog extends JDialog implements ActionListener{
    String ok="确定";
    String cancel="取消";
    String src;
    String trg;
    private  JPasswordField backupPassword;
    //construct method 构造方法初始化弹窗样式
    public DemoDialog(String src, String tag){
        this.src=src;
        trg=tag;
        this.setTitle("Dialog弹窗");
        this.setVisible(true);
        this.setLocation(200,200);
        this.setSize(200,250);
        //add one label
        //Container contentPane = this.getContentPane();
        JLabel jLabel = new JLabel("再容器中添加标签");
        backupPassword = new JPasswordField(16);


        //center 居中
        jLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JButton button = new JButton("Start backup");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String password = new String(backupPassword.getPassword()).trim();
                try {
                    BackupApp.backuper.backup(src, tag, password);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        JButton withoutPasswd=new JButton("跳过密码开始备份");
        withoutPasswd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String password = new String(backupPassword.getPassword()).trim();
                try {
                    BackupApp.backuper.backup(src, tag, password);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        backupPassword.setBounds(10,10,200,31);
        JButton okBut = new JButton(ok);
        JButton cancelBut = new JButton(cancel);
        okBut.setBackground(Color.LIGHT_GRAY);
        okBut.setBorderPainted(false);
        okBut.setBounds(65, 126, 98, 31);
        cancelBut.setBounds(175, 126, 98, 31);
        cancelBut.setBackground(Color.LIGHT_GRAY);
        cancelBut.setBorderPainted(false);
        // 给按钮添加响应事件
        okBut.addActionListener(this);
        cancelBut.addActionListener(this);
        // 向对话框中加入各组件
        //  add(jlImg);
        add(jLabel);
        add(backupPassword);
        add(okBut);
        add(cancelBut);
        // 对话框流式布局
        setLayout(null);
        // 窗口左上角的小图标
        //setIconImage(icon.getImage());
        // 设置标题
        //   setTitle(title);
        // 设置为模态窗口,此时不能操作父窗口
        setModal(true);
        // 设置对话框大小
        setSize(300, 210);
        // 对话框局域屏幕中央
        setLocationRelativeTo(null);
        // 对话框不可缩放
        setResizable(false);
        // 点击对话框关闭按钮时,销毁对话框
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);


        add(jLabel);
        add(backupPassword);
        add(button);
        add(withoutPasswd);

    }
    /**
     * 当按钮被点击时会执行下面的方法
     *
     * @param e 事件
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // 判断是不是确定按钮被点击
        if (ok.equals(e.getActionCommand())) {
            // 对话框不可见
            this.setVisible(false);
            System.out.println(backupPassword.getPassword());
            try {
                String password = new String(backupPassword.getPassword()).trim();
                BackupApp.backuper.backup(src,trg,password);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            System.out.println("我退出程序了...");
            fileSavedlist.add(new SavedFile(src,trg));
            System.out.println(fileSavedlist);
            try {
                FileHelper.saved();
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
            BackupApp.updateTree();
            this.dispose();
            // System.exit(0);
        }
        if (cancel.equals(e.getActionCommand())) {
            this.setVisible(false);
            this.dispose();
            System.out.println("我啥也没干...");
        }
    }
}
