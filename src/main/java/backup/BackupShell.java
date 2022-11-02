package backup;

/**
 * 支持使用命令行运行
 */
public class BackupShell {

    private static void usage() {
        System.out.println("java -cp <classpath> backup.BackupShell --backup <source dir> <target dir> [<password>]");
        System.out.println("or");
        System.out.println("java -cp <classpath> backup.BackupShell --restore <pack file> <target dir> [<password>]");
    }

    public static void main(String[] args) {
        String password = "";
        if (args.length != 3 && args.length != 4) {
            usage();
            return;
        }

        if (args.length == 4) {
            password = args[3];
        }

        Backuper backuper = new Backuper();
        try {
            if ("--backup".equals(args[0])) {
                backuper.backup(args[1], args[2], password);
            } else {
                backuper.restore(args[1], args[2], password);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
