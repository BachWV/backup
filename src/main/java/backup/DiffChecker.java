package backup;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

/**
 * 检测两个目录中的差异
 */
public class DiffChecker {

    public String dirDiff(String source, String target) throws Exception {
        StringBuilder sb = new StringBuilder();
        dirDiff(sb, Paths.get(source), Paths.get(target), Paths.get(source), Paths.get(target));
        return sb.toString();
    }


    // 检测 source 目录和 target 目录中的差异
    private void dirDiff(StringBuilder sb, Path sourceRoot, Path targetRoot, Path source, Path target) throws Exception {
        // 获取 source 目录下的目录集合和非目录集合
        Set<String> sourceDirSet = new HashSet<>();
        Set<String> sourceNonDirSet = new HashSet<>();
        getDirSetAndNonDirSet(sourceRoot, source, sourceDirSet, sourceNonDirSet);

        // 获取 target 目录下的文件集合和目录集合
        Set<String> targetDirSet = new HashSet<>();
        Set<String> targetNonDirSet = new HashSet<>();
        getDirSetAndNonDirSet(targetRoot, target, targetDirSet, targetNonDirSet);

        for (String s : sourceNonDirSet) {
            if (!targetNonDirSet.contains(s)) {
                sb.append(String.format("file added: %s\n", s));
            }
        }
        for (String s : targetNonDirSet) {
            if (!sourceNonDirSet.contains(s)) {
                sb.append(String.format("file deleted: %s\n", s));
            }
        }

        for (String s : sourceDirSet) {
            if (!targetDirSet.contains(s)) {
                sb.append(String.format("dir added: %s\n", s));
            }
        }
        for (String s : targetDirSet) {
            if (!sourceDirSet.contains(s)) {
                sb.append(String.format("dir deleted: %s\n", s));
            }
        }


        for (String s : sourceNonDirSet) {
            if (targetNonDirSet.contains(s)) {
                // 文件没有被删除，检查内容是否相同
                boolean diff = fileDiff(sourceRoot.resolve(s), targetRoot.resolve(s));
                if (diff) {
                    sb.append(String.format("file diff: %s\n", s));
                }
            }
        }

        for (String s : sourceDirSet) {
            if (targetDirSet.contains(s)) {
                // 目录没有被删除，递归地检查 diff
                dirDiff(sb, sourceRoot, targetRoot, sourceRoot.resolve(s), targetRoot.resolve(s));
            }
        }
    }

    private void getDirSetAndNonDirSet(Path root, Path path, Set<String> dirSet, Set<String> nonDirSet) {
        File[] sourceFiles = path.toFile().listFiles();
        assert sourceFiles != null;
        for (File file : sourceFiles) {
            Path childPath = path.resolve(file.getName());
            String name = root.relativize(childPath).toString();
            if (Files.isDirectory(childPath, LinkOption.NOFOLLOW_LINKS)) {
                dirSet.add(name);
            } else {
                nonDirSet.add(name);
            }
        }
    }

    // 文件不同返回 true
    // 文件相同返回 false
    private boolean fileDiff(Path file1, Path file2) throws IOException {
        // 都是软链接
        if (Files.isSymbolicLink(file1) && Files.isSymbolicLink(file2)) {
            Path target1 = Files.readSymbolicLink(file1);
            Path target2 = Files.readSymbolicLink(file2);
            return !target1.equals(target2);
        }

        // 有一个是软链接
        if (Files.isSymbolicLink(file1) || Files.isSymbolicLink(file2)) {
            return true;
        }

        // 都是普通文件
        byte[] bytes1 = Files.readAllBytes(file1);
        byte[] bytes2 = Files.readAllBytes(file2);
        if (bytes1.length != bytes2.length) {
            return true;
        }

        for (int i = 0; i < bytes1.length; i++) {
            if (bytes1[i] != bytes2[i]) {
                return true;
            }
        }

        return false;
    }

}
