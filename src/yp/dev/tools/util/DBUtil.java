package yp.dev.tools.util;
import yp.dev.tools.pojo.Column;
import yp.dev.tools.pojo.Table;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yp on 2016/6/9.
 */
public class DBUtil {
    private static final String driverName = "com.mysql.jdbc.Driver";
    private static final String splitTableSuffixSplliter = "_";
    private static final String dbPort = "3306";

    public static Connection getConnection(String url, String username, String password) throws SQLException, ClassNotFoundException {
        Class.forName(driverName);
        return DriverManager.getConnection(url, username, password);
    }

    public static String genConnectionUrl(String ip, String port, String database) {
        return "jdbc:mysql://" + ip + ":" + port + "/" + database + "?characterEncoding=utf8";
    }

    public static String genConnectionUrl(String ip, String database) {
        return genConnectionUrl(ip, dbPort, database);
    }

    /**
     * 获取所有的表名，分表的只取第一个表名
     *
     * @return
     * @throws SQLException
     */
    public static List<String> getTableNames(Connection conn, List<String> choosenTables) throws SQLException {
        List<String> names = new ArrayList<>();
        List<String> realNames = new ArrayList<String>();
        PreparedStatement pstate = null;
        ResultSet results = null;
        try {
            pstate = conn.prepareStatement("SHOW TABLES");
            results = pstate.executeQuery();
            while (results.next()) {
                String tableName = results.getString(1);
                String realName = parseRealTableName(tableName);
                if (realNames.contains(realName)) {//遇到了其它分表
                    continue;
                }
                realNames.add(realName);
                //如果是指定了表名，则看看遍历到的表名是不是指定的表名(注意分表问题)
                if (choosenTables != null && choosenTables.size() > 0) {
                    if (choosenTables.contains(realName)) {
                        names.add(tableName);
                    }
                } else {//对所有表生成
                    names.add(tableName);
                }
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            IOUtil.close(results, pstate);
        }
        return names;
    }

    public static List<Table> getDbTables(Connection conn, List<String> specifiedTables) throws SQLException {
        List<Table> result = new ArrayList<>();
        List<String> tableNames = getTableNames(conn, specifiedTables);
        Map<String, String> tableComments = DBUtil.getTableComments(conn);
        if (CollectionUtil.isEmpty(tableNames)) {
            return result;
        }
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String prefix = "SHOW FULL FIELDS FROM ";
            for (String name : tableNames) {
                ps = conn.prepareStatement(prefix + name);
                rs = ps.executeQuery();
                Table t = new Table();
                t.setName(name);
                t.setComment(tableComments.get(name));

                List<Column> columns = new ArrayList<>();
                while (rs.next()) {
                    Column c = new Column();
                    c.setName(rs.getString("FIELD"));
                    c.setJdbcType(parseJDBCTypeOnly(rs.getString("TYPE")));
                    c.setComment(parseClassComment(rs.getString("COMMENT")));
                    c.setJavaType(JavaFileUtil.jdbcType2JavaType(c.getJdbcType()).getName());
                    columns.add(c);
                }
                t.setColumns(columns);
                result.add(t);
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            IOUtil.close(rs, ps);
        }
        return result;
    }

    /**
     * 获取所有的数据库表注释
     *
     * @return
     * @throws SQLException
     */
    public static Map<String, String> getTableComments(Connection conn) throws SQLException {
        Map<String, String> maps = new HashMap<String, String>();
        PreparedStatement pstate = null;
        ResultSet results = null;
        try {
            pstate = conn.prepareStatement("SHOW TABLE STATUS");
            results = pstate.executeQuery();
            List<String> realNames = new ArrayList<String>();
            while (results.next()) {
                String tableName = results.getString("NAME");
                String realName = parseRealTableName(tableName);
                if (realNames.contains(realName)) {
                    continue;
                }
                realNames.add(realName);
                String comment = results.getString("COMMENT");
                maps.put(tableName, comment);
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            IOUtil.close(results, pstate);
        }
        return maps;
    }

    /**
     * 解析实际表名，考虑分表的情况
     *
     * @param tableName
     * @return
     */
    public static String parseRealTableName(String tableName) {
        if (fitSplitRule(tableName)) {
            return tableName.substring(0, tableName.lastIndexOf(splitTableSuffixSplliter));
        }
        return tableName;
    }

    /**
     * 检查指定的表名是否是分表表名
     *
     * @param tableName
     * @return
     */
    private static boolean fitSplitRule(String tableName) {
        return tableName != null && tableName.matches("^\\w.*" + splitTableSuffixSplliter + "\\d+$");
    }

    private static String parseClassComment(String comment) {
        if (StringUtil.isBlank(comment)) {
            return comment;
        }
        if (comment.endsWith("表")) {
            comment = comment.substring(0, comment.length() - 1);
        }
        return comment;
    }

    private static String parseJDBCTypeOnly(String type) {
        if (StringUtil.isBlank(type)) {
            return type;
        }
        if (type.indexOf("(") > 0) {
            type = type.substring(0, type.indexOf("("));
        }
        if (type.indexOf(" ") > 0) {
            type = type.substring(0, type.indexOf(" "));
        }
        if(StringUtil.equalsIgnoreCase(type,"datetime")){
            type="TIMESTAMP";
        }
        return type.toUpperCase();
    }
}
