package backup.GUI;

import backup.BackupApp;
import backup.Tools.FileHelper;
import backup.Tools.MultipartFileUploadTest;
import backup.Tools.SavedFile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;

import static backup.BackupApp.fileSavedlist;

public class UploadDialog extends JDialog implements ActionListener{
    String ok="上传";
    String cancel="取消";
    String path;

    //construct method 构造方法初始化弹窗样式
    public UploadDialog(String path){
        this.path=path;
        this.setTitle("确定要上传到网盘吗");
        this.setVisible(true);
        this.setLocation(200,200);
        this.setSize(200,250);
        //add one label
        //Container contentPane = this.getContentPane();


        //center 居中

        JButton okBut = new JButton(ok);
        JButton cancelBut = new JButton(cancel);
        okBut.setBackground(Color.LIGHT_GRAY);
        okBut.setBorderPainted(false);
        okBut.setBounds(65, 126, 98, 31);
        okBut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        MultipartFileUploadTest.send(path);

                    }
                }).start();
                setVisible(false);
                System.out.println("我退出Upload了...");
                dispose();
            }
        });
        cancelBut.setBounds(175, 126, 98, 31);
        cancelBut.setBackground(Color.LIGHT_GRAY);
        cancelBut.setBorderPainted(false);
        // 给按钮添加响应事件
        okBut.addActionListener(this);
        cancelBut.addActionListener(this);
        // 向对话框中加入各组件
        //  add(jlImg);


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
      if (cancel.equals(e.getActionCommand())) {
            this.setVisible(false);
            this.dispose();
            System.out.println("我啥也没干...");
        }
    }
}
