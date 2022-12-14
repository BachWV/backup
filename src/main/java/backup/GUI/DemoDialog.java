package backup.GUI;

import backup.GUIApp;
import backup.Tools.FileHelper;
import backup.Tools.SavedFile;
import backup.Tools.WatchService;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;

import static backup.GUIApp.fileSavedlist;

public class DemoDialog extends JDialog implements ActionListener{
    String ok="跳过密码开始备份";
    String cancel="取消";
    String src;
    String trg;
    private  JPasswordField backupPassword;
    //construct method 构造方法初始化弹窗样式
    public DemoDialog(String src, String tag){
        this.src=src;
        trg=tag;
        this.setTitle("输入密码");
        this.setVisible(true);
        this.setLocation(200,200);
        this.setSize(200,250);
        //add one label
        //Container contentPane = this.getContentPane();

        backupPassword = new JPasswordField(16);


        //center 居中

        JButton button = new JButton("确定");
        button.setBounds(170,10,100,31);
        button.addActionListener(e -> startBackup());


        backupPassword.setBounds(10,10,200,31);
        JButton okBut = new JButton(ok);
        JButton cancelBut = new JButton(cancel);
    //okBut.setBackground(Color.LIGHT_GRAY);
        okBut.setBorderPainted(false);
        okBut.setBounds(60, 126, 130, 31);
        cancelBut.setBounds(175, 126, 130, 31);
   //     cancelBut.setBackground(Color.LIGHT_GRAY);
        cancelBut.setBorderPainted(false);
        // 给按钮添加响应事件
        okBut.addActionListener(this);
        cancelBut.addActionListener(this);
        // 向对话框中加入各组件
        //  add(jlImg);
        add(button);
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
        //setModal(true);
        // 设置对话框大小
        setSize(300, 210);
        // 对话框局域屏幕中央
        setLocationRelativeTo(null);
        // 对话框不可缩放
        setResizable(false);
        // 点击对话框关闭按钮时,销毁对话框
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);



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
           startBackup();
            this.dispose();
            // System.exit(0);
        }
        if (cancel.equals(e.getActionCommand())) {
            this.setVisible(false);
            this.dispose();
            System.out.println("我啥也没干...");
        }
    }
    void startBackup(){
        System.out.println(backupPassword.getPassword());
        try {
            String password = new String(backupPassword.getPassword()).trim();
            GUIApp.backuper.backup(src,trg,password);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        System.out.println("备份成功，退出");
        fileSavedlist.add(new SavedFile(src,trg));
        System.out.println(fileSavedlist);
        WatchService.registerAllFileDir();
        System.out.println("添加新监听"+src);

        try {
            FileHelper.saved();
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        }
        BackupPanelGUI.updateBackupTree();
        RestorePanelGUI.updateRestoreTree();

    }
}
