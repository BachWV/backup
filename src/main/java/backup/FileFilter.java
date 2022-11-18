package backup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

/**
 * 文件过滤功能
 * <p>
 * 在需要备份的文件夹下创建 .bakignore 文件
 * 在该文件中指定过滤规则即可
 * <p>
 * 支持的过滤规则：
 * 1. 后缀名过滤，如 *.jpg
 * 2. 具体的文件（采用相对路径）
 */
public class FileFilter {

    private final Set<String> suffixSet = new HashSet<>();
    private final Set<Path> specificPaths = new HashSet<>();


    public FileFilter(Path root) {
        File ignoreFile = root.resolve(".bakignore").toFile();
        if (!ignoreFile.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(ignoreFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                if (line.matches("\\*\\.\\w+$")) {
                    line = line.substring(line.lastIndexOf("."));
                    suffixSet.add(line);
                } else {
                    specificPaths.add(root.resolve(line).normalize());
                }
                System.out.println(suffixSet);
                System.out.println(specificPaths);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("filter suffix: " + suffixSet);
        System.out.println("filter path: " + specificPaths);
    }

    public boolean filter(Path path) {
        Path normalized = path.normalize();
        String pathString = normalized.toString();
        if (pathString.contains(".") && suffixSet.contains(pathString.substring(pathString.lastIndexOf("."))) // 满足后缀名规则
                || specificPaths.contains(normalized)) { // 与具体的路径匹配
            System.out.println("filter " + normalized);
            return true;
        }
        return false;
    }
}
