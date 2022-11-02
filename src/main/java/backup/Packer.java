package backup;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 打包 / 解包
 * 由于依赖于 {@link PosixFileAttributeView}，{@link PosixFileAttributes}，{@link PosixFilePermissions}
 * 仅支持在 Linux/Unix 下运行
 *
 * 解包还原时需要修改软连接的属性，经测试，JDK 8 和 JDK 11 下修改软连接属性会出现 Bug
 * JDK 13 运行正常，所以要求使用 JDK 13 及以上版本
 */
public class Packer {

    private static final byte DIR = 0;
    private static final byte REGULAR = 1;
    private static final byte SYMLINK = 2;

    /**
     * 将 source 目录下的文件打包到 target 指定位置
     *
     * @param source 要求为目录
     * @param target 要求为文件
     */
    public void pack(File source, File target) throws IOException {
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(target))) {
            Path sourcePath = source.toPath();
            pack(sourcePath, out, new HashMap<>(), new FileFilter(sourcePath));
        }
    }

    /**
     * 将 source 指定的备份文件恢复到 target 目录下
     *
     * @param source 备份文件
     * @param target 放置恢复出的文件的目录
     */
    public void unpack(File source, File target) throws IOException {
        String name = source.getName(); // foo.pack
        String rootDirName = name.substring(0, name.indexOf("."));
        try (DataInputStream in = new DataInputStream(new FileInputStream(source))) {
            unpack(in, target.toPath().resolve(rootDirName), null, new HashMap<>());
        }
    }

    // 打包格式:
    // regular file: creationTime|lastAccessTime|lastModifiedTime|owner|group|permissions|type|size|content
    // soft link:    creationTime|lastAccessTime|lastModifiedTime|owner|group|permissions|type|target
    // directory:    creationTime|lastAccessTime|lastModifiedTime|owner|group|permissions|type|(name|inode)*|(long)0
    private void pack(Path path, DataOutputStream out, Map<String, Path> fileKeyToPath, FileFilter fileFilter) throws IOException {
       // String fileKey = getFileKey(path);
//        if (fileKeyToPath.containsKey(fileKey)) {
//            // 如果当前文件和目录中已经打包好的某个文件 inode 相同
//            // 那么就不再重复打包 inode 及其指向的数据了
//            System.out.println(path + " is a hard link to " + fileKeyToPath.get(fileKey) + ", file key: " + fileKey);
//            return;
//        } else {
//            fileKeyToPath.put(fileKey, path);
//        }

        BasicFileAttributes attr = Files.readAttributes(path,BasicFileAttributes.class);
        //PosixFileAttributes attr = Files.readAttributes(path, PosixFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
        FileTime creationTime = attr.creationTime();
        FileTime lastAccessTime = attr.lastAccessTime();
        FileTime lastModifiedTime = attr.lastModifiedTime();
        out.writeLong(creationTime.to(TimeUnit.NANOSECONDS));
        out.writeLong(lastAccessTime.to(TimeUnit.NANOSECONDS));
        out.writeLong(lastModifiedTime.to(TimeUnit.NANOSECONDS));
       // writeString(attr.owner().getName(), out);
       // writeString(attr.group().getName(), out);
      //  writeString(PosixFilePermissions.toString(attr.permissions()), out);

        if (attr.isDirectory()) {
            System.out.println("pack dir: " + path);
            out.writeByte(DIR);
            File file = path.toFile();
            File[] files = file.listFiles();
            assert files != null;
            List<File> filesToPack = new ArrayList<>();
            for (File f : files) {
                Path childPath = path.resolve(f.getName());
                // 判断一个文件是否要备份
                // 仅备份目录、普通文件、软连接
                // 要求不被 .bakignore 文件指定的规则过滤
                if (!fileFilter.filter(childPath) && (Files.isSymbolicLink(childPath) ||
                        Files.isRegularFile(childPath, LinkOption.NOFOLLOW_LINKS) ||
                        Files.isDirectory(childPath, LinkOption.NOFOLLOW_LINKS))) {
                    filesToPack.add(f);
                }
            }

            // 写入文件名和 inode
            for (File f : filesToPack) {
                writeString(f.getName(), out);
               // writeString(getFileKey(f.toPath()), out);
            }

            // 写入一个 int 类型的 0，标识目录打包结束
            out.writeInt(0);

            // 打包该目录下需要打包的各个文件
            for (File f : filesToPack) {
                pack(path.resolve(f.getName()), out, fileKeyToPath, fileFilter);
            }
        } else if (attr.isRegularFile()) {
            System.out.println("pack regular file: " + path + ", size: " + attr.size());
            out.writeByte(REGULAR);
            out.writeLong(attr.size());
            Files.copy(path, out);
        } else if (attr.isSymbolicLink()) {
            System.out.println("pack symlink: " + path);
            out.writeByte(SYMLINK);
            Path target = Files.readSymbolicLink(path);
            writeString(target.toString(), out);
        }

        // 打包过程中由于进行了读取，修改了文件的 last access time
        // 打包结束后恢复文件的 access time
        Files.setAttribute(path, "lastAccessTime", lastAccessTime, LinkOption.NOFOLLOW_LINKS);
    }


    void unpack(DataInputStream in, Path path, String fileKey, Map<String, Path> fileKeyToPath) throws IOException {
        if (fileKey != null) {
            if (fileKeyToPath.containsKey(fileKey)) {
                // 当前文件对应的原文件与某个已经解包的文件的原文件指向同一 inode
                // 建立当前文件到该文件的硬链接
                Files.createLink(path, fileKeyToPath.get(fileKey));
                System.out.println("hard link " + path + " to " + fileKeyToPath.get(fileKey) + ", file key: " + fileKey);
                return;
            } else {
                fileKeyToPath.put(fileKey, path);
            }
        }

        FileTime creationTime = FileTime.from(in.readLong(), TimeUnit.NANOSECONDS);
        FileTime lastAccessTime = FileTime.from(in.readLong(), TimeUnit.NANOSECONDS);
        FileTime lastModifiedTime = FileTime.from(in.readLong(), TimeUnit.NANOSECONDS);
       // String ownerName = readString(in);
       // String groupName = readString(in);
       // String permissionsString = readString(in);

        byte type = in.readByte();
        if (type == DIR) {
            System.out.println("unpack dir: " + path);
            Files.createDirectory(path);

            // 读取目录下的文件名和 inode
            List<String[]> fileNameAndKeys = new ArrayList<>();
            String fileName = readString(in);
            while (fileName.length() > 0) {
                String[] fileNameAndKey = new String[2];
                fileNameAndKey[0] = fileName;
               // fileNameAndKey[1] = readString(in);
                fileNameAndKeys.add(fileNameAndKey);
                fileName = readString(in);
            }

            // 解包、创建目录下的文件
            for (String[] fileNameAndKey : fileNameAndKeys) {
                unpack(in, path.resolve(fileNameAndKey[0]), fileNameAndKey[1], fileKeyToPath);
            }
        } else if (type == REGULAR) {
            Files.createFile(path);
            long size = in.readLong();
            System.out.println("unpack file: " + path + ", size: " + size);
            // 从包中读取文件内容，写入解包后的文件中
            try (FileOutputStream out = new FileOutputStream(path.toFile())) {
                long remaining = size;
                byte[] buffer = new byte[4096];
                while (remaining > 0) {
                    int n = in.read(buffer, 0, (int) Math.min(buffer.length, remaining));
                    out.write(buffer, 0, n);
                    remaining -= n;
                }
            }
        } else if (type == SYMLINK) {
            System.out.println("unpack symlink: " + path);
            String target = readString(in);
            Files.createSymbolicLink(path, Paths.get(target));
        }

        // 虽然每个解包每个文件时都是先读取到文件属性
        // 但在文件内容解包完毕后才设置文件属性
        // 如果一开始就设置文件属性的话，以 last modified time 为例
        // 解包文件内容时，由于修改了文件内容，last modified time 会自动更新
        // 就与原来的文件不一致了
        PosixFileAttributeView attr = Files.getFileAttributeView(path, PosixFileAttributeView.class, LinkOption.NOFOLLOW_LINKS);
        //try {
            UserPrincipalLookupService userPrincipalLookupService = path.getFileSystem().getUserPrincipalLookupService();
            //attr.setOwner(userPrincipalLookupService.lookupPrincipalByName(ownerName));
            //attr.setGroup(userPrincipalLookupService.lookupPrincipalByGroupName(groupName));
//        } catch (IOException exception) {
//            throw new IOException("修改文件所属用户或组失败，请尝试使用管理员身份运行(sudo)", exception.getCause());
//        }

        if (type != SYMLINK) {
            // 排除软连接
            // 如果是软连接的话，设置 permissions 发现会报错
            // https://askubuntu.com/questions/1151269/is-it-possible-to-change-the-permissions-for-the-symbolic-link
            // https://www.gnu.org/software/coreutils/manual/html_node/chmod-invocation.html:
            // chmod never changes the permissions of symbolic links;
            // the chmod system call cannot change their permissions.
            // This is not a problem since the permissions of symbolic links are never used.
           // attr.setPermissions(PosixFilePermissions.fromString(permissionsString));
        }

        // 最后设置修改时间和访问时间
        Files.setAttribute(path, "lastModifiedTime", lastModifiedTime, LinkOption.NOFOLLOW_LINKS);
        Files.setAttribute(path, "lastAccessTime", lastAccessTime, LinkOption.NOFOLLOW_LINKS);
    }


    private static void writeString(String s, DataOutputStream out) throws IOException {
        byte[] bytes = s.getBytes();
        out.writeInt(bytes.length);
        out.write(bytes);
    }

    private static String readString(DataInputStream in) throws IOException {
        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readFully(bytes);
        return new String(bytes);
    }

    private static String getFileKey(Path path) throws IOException {
        BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
        Object fileKey = attr.fileKey();
        assert fileKey != null;
        return fileKey.toString();
    }

}
