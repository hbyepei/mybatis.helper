package yp.dev.tools.util;
import java.io.File;
import java.io.IOException;

/**
 * Created by yp on 2016/6/9.
 */
public class IOUtil {
    public static void createDir(String dir) {
        File folder = new File(dir);
        if (!folder.exists() || !folder.isDirectory()) {
            boolean ok = folder.mkdirs();
            if (!ok) {
                throw new RuntimeException("程序没有足够权限在目标目录创建文件夹!");
            }
        }
    }

    public static void createDir(File f) {
        if (f == null) {
            return;
        }
        if (!f.exists() || !f.isDirectory()) {
            boolean ok = f.mkdirs();
            if (!ok) {
                throw new RuntimeException("程序没有足够权限在目标目录创建文件夹!");
            }
        }
    }

    public static void openDir(String dir) throws IOException {
        java.awt.Desktop.getDesktop().browse(new File(dir).toURI());
    }

    public static void close(AutoCloseable... closeables) {
        if (closeables != null) {
            for (AutoCloseable ac : closeables) {
                try {
                    if (ac != null) {
                        ac.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
