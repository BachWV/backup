package backup.GUI;

import backup.BackupApp;
import backup.Packer;
import backup.Tools.CloudFile;
import backup.Tools.DownloadFile;
import backup.Tools.FileHelper;
import backup.Tools.SavedFile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;


import static backup.BackupApp.fileSavedlist;

public class DialogDownload extends JDialog implements ActionListener{
    String ok="下载";
    String cancel="取消";
    String path ;
    String trueUrl;
    String filename;

    //construct method 构造方法初始化弹窗样式
    public DialogDownload(String filename){
        for(CloudFile c:FileHelper.panList){
            if(c.filename.equals(filename)){
                this.trueUrl=c.tureUrl;
                System.out.println("找到了");


            }else{
                System.out.println("未找到");
            }
        }

        this.filename=filename;

        this.setTitle("下载到");
        this.setVisible(true);
        this.setLocation(200,200);
        this.setSize(200,250);
        //add one label
        //center 居中



        JButton button = new JButton("选择备份位置");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("别按了"+"选择备份位置");
              JFileChooser fc = new JFileChooser();
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fc.setCurrentDirectory(new File("C:\\Users\\Charon\\Desktop"));
                int result = fc.showOpenDialog(null);
                if (result != JFileChooser.APPROVE_OPTION) {
                   // return;
                }

                // 获取选择的文件路径并设置相应 label 的值
                File file = fc.getSelectedFile();
                path = file.getAbsolutePath();
                System.out.println("here path="+path);


            }
        });
        button.setBounds(10,10,200,31);
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
//        JFrame jf=new JFrame();
//        Container c=jf.getContentPane();
//        // 向对话框中加入各组件
//        //  add(jlImg);
//        jf.add(button);
//        jf.add(okBut);
//        jf.add(cancelBut);
//        // 对话框流式布局
//        jf.setLayout(null);
        // 窗口左上角的小图标
        //setIconImage(icon.getImage());
        // 设置标题
        //   setTitle(title);
        // 设置为模态窗口,此时不能操作父窗口
        //setModal();
        add(button);
        add(okBut);
        add(cancelBut);
        setLayout(null);
        // 设置对话框大小
        setSize(300, 210);
        // 对话框局域屏幕中央
        setLocationRelativeTo(null);
        // 对话框不可缩放
        setResizable(false);
        // 点击对话框关闭按钮时,销毁对话框
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        add(button);

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

            System.out.println("我退出程序了...");

            System.out.println(fileSavedlist);
            //String url="https://cn-beijing-data.aliyundrive.net/5fb583f91e3b39a7d7a9495c9646b03662400114%2F5fb583f994427eda018d4cd99eaaaf359c805e54?di=bj29&dr=504864870&f=635f7d8942ee83237c0a48599307df6daccc3f7a&security-token=CAIS%2BgF1q6Ft5B2yfSjIr5f%2FEYLelO1s8o%2B%2BRVCJh2EROr5N2Z%2F%2B1Dz2IHFPeHJrBeAYt%2FoxmW1X5vwSlq5rR4QAXlDfNXujY1f8qVHPWZHInuDox55m4cTXNAr%2BIhr%2F29CoEIedZdjBe%2FCrRknZnytou9XTfimjWFrXWv%2Fgy%2BQQDLItUxK%2FcCBNCfpPOwJms7V6D3bKMuu3OROY6Qi5TmgQ41Uh1jgjtPzkkpfFtkGF1GeXkLFF%2B97DRbG%2FdNRpMZtFVNO44fd7bKKp0lQLukMWr%2Fwq3PIdp2ma447NWQlLnzyCMvvJ9OVDFyN0aKEnH7J%2Bq%2FzxhTPrMnpkSlacGoABRjTXEby619blJSkO0SvBuRLCCoFxvjKs2G4SVoXX4U8O90aLsMW2h7%2Bsj48X8nnm3Zlkj2LGvsxy%2B%2B1Ui9tHv8qole%2FPAPLdO7JwN4da71tpmfynx%2BUMZXECHRAM4A1Xen2JZa2hZfNqouq6Mry7Stoco1W0AKYfjDxWFQzQ5JU%3D&u=fe41fb4999f3492abd07f436e0ec49bd&x-oss-access-key-id=STS.NTJZ8jy2MEMUGv8caD61a6ZU6&x-oss-expires=1667238961&x-oss-signature=hHB6m3uhLUzVlTvsci2X46NMrYOW5jOvqUX84FFF%2FLQ%3D&x-oss-signature-version=OSS2";
            String dir="D://";
            if(path==null){
                System.out.println("请添加下载位置");
            }else{
                System.out.println("下载路径" +path);
                System.out.println("url="+trueUrl);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        DownloadFile.main(trueUrl,path,filename);
                    }
                }).start();

            }
            System.out.println("准备关闭窗口");


            //
            this.setVisible(false);
            this.dispose();
         //   this.dispose();

            // System.exit(0);

        }
        if (cancel.equals(e.getActionCommand())) {
            this.setVisible(false);
            this.dispose();
            System.out.println("我啥也没干...");
        }
    }
}
