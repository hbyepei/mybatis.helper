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
     * 获取所有的表名
     *
     * @return
     * @throws SQLException
     */
    public static List<String> getTableNames(Connection conn, List<String> choosenTables) throws SQLException {
        List<String> names = new ArrayList<>();
        PreparedStatement pstate = null;
        ResultSet results = null;
        try {
            pstate = conn.prepareStatement("SHOW TABLES");
            results = pstate.executeQuery();
            while (results.next()) {
                String tableName = results.getString(1);
                if (choosenTables != null && choosenTables.size() > 0) {
                    if (choosenTables.contains(tableName) || choosenTables.contains(parseNaturalTableName(tableName))) {
                        names.add(tableName);
                    }
                } else {
                    names.add(tableName);
                }
            }
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
        List<String> processedNaturalNames = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String prefix = "SHOW FULL FIELDS FROM ";
            for (String name : tableNames) {
                String naturalName = parseNaturalTableName(name);
                if (processedNaturalNames.contains(naturalName)) {
                    continue;
                }
                ps = conn.prepareStatement(prefix + name);
                rs = ps.executeQuery();
                Table t = new Table();
                t.setRealName(name);
                t.setNaturalName(naturalName);
                t.setComment(tableComments.get(naturalName));

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
                processedNaturalNames.add(naturalName);
                result.add(t);
            }
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
            List<String> naturalNames = new ArrayList<String>();
            while (results.next()) {
                String tableName = results.getString("NAME");
                String realName = parseNaturalTableName(tableName);
                if (naturalNames.contains(realName)) {
                    continue;
                }
                naturalNames.add(realName);
                String comment = results.getString("COMMENT");
                maps.put(tableName, comment);
            }
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
    public static String parseNaturalTableName(String tableName) {
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
        if (StringUtil.equalsIgnoreCase(type, "datetime")) {
            type = "TIMESTAMP";
        }
        if (StringUtil.equalsIgnoreCase(type, "int")) {
            type = "INTEGER";
        }
        return type.toUpperCase();
    }
}
