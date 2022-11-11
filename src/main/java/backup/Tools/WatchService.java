package backup.Tools;

import backup.BackupApp;

import java.io.IOException;
import java.nio.file.*;

import static backup.BackupApp.fileSavedlist;

public class WatchService {
   public static void registerAllFileDir(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    for(SavedFile ss:fileSavedlist){
                        System.out.println(ss.src);
                        create(ss);
                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }).start();
    }
    public static void create(SavedFile ss) throws IOException {

        // 这里的监听必须是目录
        // 创建WatchService，它是对操作系统的文件监视器的封装，相对之前，不需要遍历文件目录，效率要高很多
        java.nio.file.WatchService watcher = FileSystems.getDefault().newWatchService();
        // 注册指定目录使用的监听器，监视目录下文件的变化；
        // PS：Path必须是目录，不能是文件；
        // StandardWatchEventKinds.ENTRY_MODIFY，表示监视文件的修改事件


        Path path2 = Paths.get(ss.src);
        path2.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);


        // 创建一个线程，等待目录下的文件发生变化
        try {
            while (true) {
                // 获取目录的变化:
                // take()是一个阻塞方法，会等待监视器发出的信号才返回。
                // 还可以使用watcher.poll()方法，非阻塞方法，会立即返回当时监视器中是否有信号。
                // 返回结果WatchKey，是一个单例对象，与前面的register方法返回的实例是同一个；
                WatchKey key = watcher.take();
                // 处理文件变化事件：
                // key.pollEvents()用于获取文件变化事件，只能获取一次，不能重复获取，类似队列的形式。
                for (WatchEvent<?> event : key.pollEvents()) {
                    // event.kind()：事件类型
                    /*
                    * 通过WatchService监听文件的类型也变得更加丰富：
                        ENTRY_CREATE 目标被创建
                        ENTRY_DELETE 目标被删除
                        ENTRY_MODIFY 目标被修改
                        OVERFLOW 一个特殊的Event，表示Event被放弃或者丢失
* */
                    if (event.kind() == StandardWatchEventKinds.OVERFLOW) {
                        //事件可能lost or discarded
                        continue;
                    }
                    // 返回触发事件的文件或目录的路径（相对路径）
                    Path watchable = ((Path) key.watchable()).resolve((Path)event.context());
                    Path fileDir=watchable.getParent();
                    Path fileName = watchable.getFileName();
                    System.out.println("文件更新: " + fileName);
                    System.out.println("重新备份");
                    BackupApp.backuper.backup(ss.src,ss.trg,"");

                }
                // 每次调用WatchService的take()或poll()方法时需要通过本方法重置
                if (!key.reset()) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}