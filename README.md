# Backup App

![demo_wslg](https://s2.loli.net/2022/11/17/MF7LErvhaoye5SD.png)



## 功能

整体功能为备份指定文件夹并还原。

备份过程分为以下几步：

1. 打包（`sourceDir -> sourceDir.pack` ）：将指定文件夹下的文件（普通文件、目录和软链接）打包，可以通过创建 `.bakignore` 文件指定需要过滤的文件。打包目录中如果多个硬链接指向同一份数据，那么该数据只会打包一次，并且还原时也只还原一份，并建立多个指向它的硬链接。
2. 压缩（`sourceDir.pack -> sourceDir.pack.huff`）：使用 Huffman 压缩算法进行压缩。
3. 加密（`sourceDir.pack.huff -> sourceDir.pack.huff.enc` ）：使用一个简单的带混淆机制的加密算法进行加密。

还原过程就是上面过程反过来。

上传至网盘，当然是公共目录。http://120.24.176.162:5244/aliyun/pantest

## 编译运行

本项目使用 Maven 构建。
编译：

```shell
mvn compile
```

运行:

```shell
java -jar backup_lab.jar
```
## docker 运行环境

本项目使用的运行环境 已上传至docker hub

```shell
docker pull bachwv/docker-desktop:zh_CN
```

构建参考
https://github.com/BachWV/x11vnc-desktop/
的zh_CN分支，并使用Github Action打包，直接使用dockerfile打包可能出现问题

使用`python init_desktop.py`运行docker（挂载当前目录至docker中的~/shared目录，在docker中使用`java -jar backup_lab.jar`运行本软件 ）

![image-20221117163256291](https://s2.loli.net/2022/11/17/FSWDYUKEZHPeqIb.png)
