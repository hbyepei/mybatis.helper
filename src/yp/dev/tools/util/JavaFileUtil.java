package yp.dev.tools.util;
import javafx.util.Pair;
import yp.dev.tools.builder.StrBuilder;
import yp.dev.tools.pojo.Column;
import yp.dev.tools.pojo.PojoInfo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by yp on 2016/6/9.
 */
public class JavaFileUtil {
    public static String genClassComment(Date date, String authorName, String description) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (date == null) {
            date = new Date();
        }
        if (StringUtil.isBlank(authorName)) {
            authorName = "mapper generator";
        }
        if (description == null) {
            description = "";
        }
        StrBuilder sb = new StrBuilder().appendLine()
                .appendLine("/**")
                .appendLine(" * ")
                .append(" * date : ").appendLine(df.format(date))
                .append(" * author : ").appendLine(authorName)
                .append(" * description : ").appendLine(description)
                .appendLine(" * ")
                .append(" **/");
        return sb.toString();
    }

    public static String genMethodComment(String description) {
        if (description == null) {
            description = "";
        }
        StrBuilder sb = new StrBuilder().appendLine()
                .appendLine("\t/**")
                .append("\t * ").appendLine(description)
                .appendLine("\t **/");
        return sb.toString();
    }

    public static String genPackageAndImports(String pkgName, List<String> pojoAnnotation, List<Column> columns) {
        if (StringUtil.isBlank(pkgName)) {
            throw new RuntimeException("未指定包名");
        }
        String annotationImport = "";
        if (!CollectionUtil.isEmpty(pojoAnnotation)) {
            for (String s : pojoAnnotation) {
                if (isLegalFullClassName(s)) {
                    annotationImport += "import " + pojoAnnotation + ";" + StringUtil.lingSeperator;
                }
            }
        }
        StrBuilder sb = new StrBuilder("package ").append(pkgName).appendLine(";")
                .appendLine("import java.io.Serializable;")
                .append(annotationImport);

        if (columns != null) {
            List<String> javaTypeNames = CollectionUtil.transform(columns, new Function<Column, String>() {
                @Override
                public String apply(Column column) {
                    return column.getJavaType();
                }
            });

            if (javaTypeNames.contains(JavaType._BigDecimal.getName())) {
                sb.append("import ").append(JavaType._BigDecimal.getPkg()).appendLine(";");
            }
            if (javaTypeNames.contains(JavaType._Date.getName())) {
                sb.append("import ").append(JavaType._Date.getPkg()).appendLine(";");
            }
        }
        return sb.toString();
    }

    public static JavaType jdbcType2JavaType(String type) {
        if (type.contains(JdbcType._char.name) || type.contains(JdbcType._varchar.name)) {
            return JavaType._String;
        } else if (type.contains(JdbcType._bigint.name)) {
            return JavaType._Long;
        } else if (type.contains(JdbcType._int.name)) {
            return JavaType._Integer;
        } else if (type.contains(JdbcType._date.name)) {
            return JavaType._Date;
        } else if (type.contains(JdbcType._text.name)) {
            return JavaType._String;
        } else if (type.contains(JdbcType._timeStamp.name)) {
            return JavaType._Date;
        } else if (type.contains(JdbcType._bit.name)) {
            return JavaType._Boolean;
        } else if (type.contains(JdbcType._decimal.name)) {
            return JavaType._BigDecimal;
        } else if (type.contains(JdbcType._blob.name)) {
            return JavaType._byteArr;
        }
        return JavaType.unknown;
    }

    public static String genFields(List<Column> columns) {
        StrBuilder sb = new StrBuilder().appendLine();
        if (CollectionUtil.isEmpty(columns)) {
            return sb.toString();
        }

        int size = columns.size();
        for (int i = 0; i < size; i++) {
            Column c = columns.get(i);
            sb.appendLine("\t/**")
                    .append("\t * ").appendLine(c.getComment())
                    .appendLine("\t**/")
                    .append("\tprivate ").append(c.getJavaType()).append(" ").append(StringUtil.underlineToUpper(c.getName())).appendLine(";").appendLine();
        }
        return sb.toString();
    }

    public static String genGetterAndSetters(List<Column> columns) {
        // 生成get 和 set方法
        StrBuilder sb = new StrBuilder().appendLine();
        if (CollectionUtil.isEmpty(columns)) {
            return sb.toString();
        }
        int size = columns.size();
        for (int i = 0; i < size; i++) {
            Column c = columns.get(i);
            String fieldName = StringUtil.underlineToUpper(c.getName());
            String upperedFieldName = StringUtil.upperFirst(fieldName);
            String javaType = c.getJavaType();
            sb.append("\tpublic void set").append(upperedFieldName).append("(").append(javaType).append(" ").append(fieldName).appendLine("){")
                    .append("\t\tthis.").append(fieldName).append(" = ").append(fieldName).appendLine(";")
                    .appendLine("\t}")
                    .append("\tpublic ").append(javaType).append(" get").append(upperedFieldName).appendLine("(){")
                    .append("\t\treturn this.").append(fieldName).appendLine(";")
                    .appendLine("\t}").appendLine();
        }
        return sb.toString();
    }

    public static String genDaoMethods(PojoInfo pi, List<MethodSignature> methodSignatures) {
        // ----------定义Mapper中的方法Begin----------
        StrBuilder sb = new StrBuilder();
        if (CollectionUtil.isEmpty(methodSignatures)) {
            return sb.toString();
        }
        String pojoName = pi.getBeanName() + pi.getBeanSuffix();
        for (MethodSignature ms : methodSignatures) {
            sb.append(genMethodComment(ms.getComment()))
                    .append("\t").append(ms.getReturnType())
                    .append(" ").append(ms.getName())
                    .append("(@Param(\"")
                    .append(ms.getParams().getValue())
                    .append("\")")
                    .append(ms.getParams().getKey())
                    .append(" ")
                    .append(ms.getParams().getValue())
                    .appendLine(");");
        }
        return sb.toString().replaceAll("<T>", pojoName);
    }

    public static boolean isLegalFullClassName(String className) {
        if (StringUtil.isBlank(className)) {
            return false;
        }
        String temp = className.trim();
        int index = temp.indexOf(".");
        if (index < 1 || index > temp.length() - 2) {
            return false;
        }
        return true;
    }

    public enum JdbcType {
        _char("CHAR"),
        _varchar("VARCHAR"),
        _date("DATE"),
        _timeStamp("TIMESTAMP"),
        _int("INTEGER"),
        _bigint("BIGINT"),
        _text("TEXT"),
        _bit("BIT"),
        _decimal("DECIMAL"),
        _blob("BLOB"),
        unknown("UNKNOWN");
        private String name;

        JdbcType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum JavaType {
        _String("String", "java.long.String"),
        _Long("Long", "java.long.Long"),
        _Integer("Integer", "java.long.Integer"),
        _Date("Date", "java.util.Date"),
        _BigDecimal("BigDecimal", "java.math.BigDecimal"),
        _Boolean("Boolean", "java.long.Boolean"),
        _byteArr("byte[]", ""),
        unknown("String", "java.long.String");
        private String name;
        private String pkg;

        JavaType(String name, String pkg) {
            this.name = name;
            this.pkg = pkg;
        }

        public String getName() {
            return name;
        }

        public String getPkg() {
            return pkg;
        }
    }

    public enum MethodSignature {
        selectById("selectById", "<T>", new Pair<>("Long", "id"), "根据Id查询记录"),
        selectByIds("selectByIds", "List<<T>>", new Pair<>("List<Long>", "ids"), "根据给定的记录查询一批记录"),
        insert("insert", "int", new Pair<>("<T>", "pojo"), "新增记录"),
        insertSelective("insertSelective", "int", new Pair<>("<T>", "pojo"), "新增记录，只匹配有值的字段"),
        updateById("updateById", "int", new Pair<>("<T>", "pojo"), "根据Id更新记录"),
        updateSelectiveById("updateSelectiveById", "int", new Pair<>("<T>", "pojo"), "根据Id更新记录,只更新有值的字段"),
        deleteById("deleteById", "int", new Pair<>("Long", "id"), "根据Id删除记录"),
        deleteByIds("deleteByIds", "int", new Pair<>("List<Long>", "ids"), "根据Id删除一批记录");
        private String name;
        private String returnType;
        private Pair<String, String> params;
        private String comment;

        MethodSignature(String name, String returnType, Pair<String, String> params, String comment) {
            this.name = name;
            this.returnType = returnType;
            this.params = params;
            this.comment = comment;
        }

        public static List<MethodSignature> getDefaultMethods() {
            List<MethodSignature> results = new ArrayList<>();
            results.add(selectById);
            results.add(insertSelective);
            results.add(updateSelectiveById);
            return results;
        }

        public String getName() {
            return name;
        }

        public String getComment() {
            return comment;
        }

        public String getReturnType() {
            return returnType;
        }

        public Pair<String, String> getParams() {
            return params;
        }

        public static MethodSignature fromName(String s) {
            for (MethodSignature ms : MethodSignature.values()) {
                if (StringUtil.equals(ms.getName(), s)) {
                    return ms;
                }
            }
            return null;
        }
    }
}
