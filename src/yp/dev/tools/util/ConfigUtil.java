package yp.dev.tools.util;
import com.sun.deploy.util.StringUtils;

import java.io.*;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by yp on 2016/6/9.
 */
public class ConfigUtil {
    private static String confFile = Paths.get(System.getProperty("user.home"), "mybatisHelper.conf").toAbsolutePath().toString();
    private static Map<String, String> confMap = new HashMap<>();

    enum ConfKey {
        daoSuffix, entitySuffix,
    }

    public static Map<String, String> getAllConf() {
        if (confMap.size() < 1) {
            confMap = loadHistoryConf();
        }
        return confMap;
    }

    ///保存属性到b.properties文件
    public static void saveConf(Map<String, String> confs) {
        if (confs == null || confs.size() < 1) {
            return;
        }
        OutputStream out = null;
        try {
            File f = new File(confFile);
            f.setWritable(true, false);
            boolean exist = f.exists();
            if (!exist) {
                boolean ok = f.createNewFile();
                if (!ok) {
                    throw new RuntimeException("程序没有足够权限在目标目录(" + confFile + ")创建文件!");
                }
            }
            out = new FileOutputStream(f, false);
            Properties prop = getProperties();
            Map<String, String> existConf = getAllConf();
            for (Map.Entry<String, String> me : confs.entrySet()) {
                String key = StringUtil.trim(me.getKey());
                String value = StringUtil.trim(me.getValue());
                existConf.put(key, value);
                prop.setProperty(key, value);
            }
            String msg = null;
            if (!exist) {
                msg = "Mybatis辅助工具配置文件.\nAuthor:yepei";
            }
            prop.store(out, msg);
            confMap = existConf;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtil.close(out);
        }
    }

    public static void updateConfByKey(String key, String newValue) {
        if (key == null || newValue == null) {
            return;
        }
        Map<String, String> m = new HashMap<>();
        m.put(StringUtils.trimWhitespace(key), StringUtils.trimWhitespace(newValue));
        saveConf(m);
    }

    public static String getConfByKey(String key) {
        if (!confMap.containsKey(StringUtil.trim(key))) {
            loadHistoryConf();
        }
        return confMap.get(key);
    }

    private static Map<String, String> loadHistoryConf() {
        Map<String, String> confs = new HashMap<>();
        try {
            Properties prop = getProperties();
            for (String s : prop.stringPropertyNames()) {
                String key = StringUtil.trim(s);
                String value = StringUtil.trim(String.valueOf(prop.get(key)));
                confs.put(key, value);
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
        if (confs.size() > 0) {
            confMap = confs;
        }
        return confs;
    }

    private static Properties getProperties() {
        Properties prop = new Properties();
        InputStream in = null;
        try {
            File file = new File(confFile);
            if (file.isFile() && file.exists()) { //判断文件是否存在
                in = new FileInputStream(file);
                prop.load(in);
            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtil.close(in);
        }
        return prop;
    }
}
