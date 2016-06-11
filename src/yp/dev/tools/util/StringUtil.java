package yp.dev.tools.util;
import yp.dev.tools.builder.StringBuilder;

/**
 * Created by yp on 2016/6/9.
 */
public class StringUtil {
    public static final String lingSeperator=System.lineSeparator();
    /**
     * 将实体类名首字母改为小写
     *
     * @param str
     * @return
     */
    public static String lowerFirst(String str) {
        if (str == null) {
            return str;
        }
        str = str.trim();
        if (str.length() == 1) {
            return str.toLowerCase();
        } else {
            return str.substring(0, 1).toLowerCase() + str.substring(1);
        }
    }

    public static boolean isNotBlank(String str){
        return !isBlank(str);
    }

    /**
     * 首字母大写
     *
     * @param str
     * @return
     */
    public static String upperFirst(String str) {
        if (str == null) {
            return str;
        }
        str = str.trim();
        if (str.length() <= 1) {
            return str.toUpperCase();
        } else {
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        }
    }

    /**
     * 将字符串中的下划线转为驼峰式变量名，其中首字母仍为小写
     *
     * @param str
     * @return
     */
    public static String underlineToUpper(String str) {
        if (str == null || !str.contains("_")) {
            return str;
        }
        String[] splits = str.toLowerCase().split("_");
        StringBuilder sb = new StringBuilder();
        for (String s : splits) {
            sb.append(upperFirst(s));
        }
        return lowerFirst(sb.toString());
    }

    /**
     * trim操作
     *
     * @param str
     * @return
     */
    public static String trim(String str) {
        if (str == null) {
            return null;
        }
        return str.trim();
    }

    public static boolean equals(String s1,String s2){
        if (s1==null || s2==null){
            return false;
        }
        return s1.equals(s2);
    }
    public static boolean equalsIgnoreCase(String s1,String s2){
        if (s1==null || s2==null){
            return false;
        }
        return s1.equalsIgnoreCase(s2);
    }

    public static boolean isBlank(String str) {
        return str == null || trim(str).length() == 0;
    }
}
