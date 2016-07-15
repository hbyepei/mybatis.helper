package yp.dev.tools.pojo;
import yp.dev.tools.util.CollectionUtil;
import yp.dev.tools.util.JavaFileUtil;
import yp.dev.tools.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yp on 2016/6/11.
 */
public class Config {
    private String ip;
    private String port;
    private String username;
    private String password;
    private String database;
    private String daoSuffix;
    private String daoPackage;
    private String daoAnnotations;
    private String pojoSuffix;
    private String pojoPackage;
    private String pojoAnnotations;
    private String targetDirectory;
    private String methods;
    private String tabels;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getDaoSuffix() {
        return daoSuffix;
    }

    public void setDaoSuffix(String daoSuffix) {
        this.daoSuffix = daoSuffix;
    }

    public String getDaoPackage() {
        return daoPackage;
    }

    public void setDaoPackage(String daoPackage) {
        this.daoPackage = daoPackage;
    }

    public String getDaoAnnotations() {
        return daoAnnotations;
    }

    public void setDaoAnnotations(String daoAnnotations) {
        this.daoAnnotations = daoAnnotations;
    }

    public String getPojoSuffix() {
        return pojoSuffix;
    }

    public void setPojoSuffix(String pojoSuffix) {
        this.pojoSuffix = pojoSuffix;
    }

    public String getPojoPackage() {
        return pojoPackage;
    }

    public void setPojoPackage(String pojoPackage) {
        this.pojoPackage = pojoPackage;
    }

    public String getPojoAnnotations() {
        return pojoAnnotations;
    }

    public void setPojoAnnotations(String pojoAnnotations) {
        this.pojoAnnotations = pojoAnnotations;
    }

    public String getTargetDirectory() {
        return targetDirectory;
    }

    public void setTargetDirectory(String targetDirectory) {
        this.targetDirectory = targetDirectory;
    }

    public String getMethods() {
        return methods;
    }

    public void setMethods(String methods) {
        this.methods = methods;
    }

    public String getTabels() {
        return tabels;
    }

    public void setTabels(String tabels) {
        this.tabels = tabels;
    }

    public static String transformStrings(String daoAnnos) {
        if (StringUtil.isBlank(daoAnnos)) {
            return null;
        }
        String[] s = daoAnnos.split(",");
        if (s.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < s.length; i++) {
                sb.append(s[i].trim());
                if (i != s.length - 1) {
                    sb.append(",");
                }
            }
            return sb.toString();
        }
        return null;
    }

    public static List<String> string2List(String str) {
        List<String> result = new ArrayList<>();
        if (StringUtil.isBlank(str)) {
            return result;
        }
        String strs = transformStrings(str);
        if (strs != null) {
            String[] ss = strs.split(",");
            for (String s : ss) {
                result.add(s);
            }
        }
        return result;
    }

    public static String transformMethods(List<JavaFileUtil.MethodSignature> signatures) {
        if (CollectionUtil.isEmpty(signatures)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < signatures.size(); i++) {
            sb.append(signatures.get(i).getName());
            if (i != signatures.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    public List<JavaFileUtil.MethodSignature> parseMethods() {
        List<JavaFileUtil.MethodSignature> signatures=new ArrayList<>();
        if (StringUtil.isBlank(this.getMethods())){
            return signatures;
        }
        List<String> mth=string2List(this.getMethods());
        if (CollectionUtil.isEmpty(mth)){
            return signatures;
        }
        for (String s:mth){
            signatures.add(JavaFileUtil.MethodSignature.fromName(s));
        }
        return signatures;
    }
}
