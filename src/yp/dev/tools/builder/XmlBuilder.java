package yp.dev.tools.builder;
import yp.dev.tools.pojo.Column;
import yp.dev.tools.pojo.PojoInfo;
import yp.dev.tools.pojo.Table;
import yp.dev.tools.util.IOUtil;
import yp.dev.tools.util.JavaFileUtil;
import yp.dev.tools.util.StringUtil;

import java.io.*;
import java.util.List;

/**
 * Created by yp on 2016/6/9.
 */
public class XmlBuilder {
    private String xmlFilePath;

    public XmlBuilder(String xmlFilePath) {
        this.xmlFilePath = xmlFilePath;
    }

    public void build(PojoInfo pojoInfo, Table table, List<JavaFileUtil.MethodSignature> methodSignatures) throws IOException {
        BufferedWriter bw = null;
        try {
            IOUtil.createDir(this.xmlFilePath);
            File xmlFile = new File(xmlFilePath, pojoInfo.getBeanName() + pojoInfo.getDaoSuffix() + ".xml");
            StrBuilder sb = new StrBuilder()
                    .appendLine("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
                    .append("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" ")
                    .appendLine("\"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">")
                    .appendLine("<mapper namespace=\"" + pojoInfo.getDaoPkg() + "." + pojoInfo.getBeanName() + pojoInfo.getDaoSuffix() + "\">")
                    .appendLine(genResultMap(table, pojoInfo))
                    .appendLine(genSelectSql(table.getColumns()))
                    .appendLine(genInsertSql(table.getColumns()));

            for (JavaFileUtil.MethodSignature ms : methodSignatures) {
                sb.appendLine(genMethodXml(table, ms));
            }
            sb.append("</mapper>");
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(xmlFile), "UTF-8"));
            bw.write(sb.toString());
            bw.flush();
        } finally {
            IOUtil.close(bw);
        }
    }

    private String genResultMap(Table table, PojoInfo pojoInfo) {
        StrBuilder sb = new StrBuilder("\t<!-- ResultMap定义-->").appendLine()
                .appendLine("\t<resultMap id=\"BaseResultMap\" type=\"" + pojoInfo.getBeanPkg() + "." + pojoInfo.getBeanName() + pojoInfo.getBeanSuffix() + "\" >");
        for (Column c : table.getColumns()) {
            String dbColumnName = c.getName();
            String fieldName = StringUtil.underlineToUpper(dbColumnName);
            String jdbcType = c.getJdbcType();
            if ("id".equals(c.getName())) {
                sb.appendLine("\t\t<id column=\"id\" property=\"id\"/>");
                continue;
            }
            sb.appendLine("\t\t<result column=\"" + dbColumnName + "\" property=\"" + fieldName + "\" />");
        }
        sb.appendLine("\t</resultMap>");
        return sb.toString();
    }

    private String genSelectSql(List<Column> columns) {
        StrBuilder sb = new StrBuilder("\t<!-- 通用的查询SQL字段-->").appendLine().appendLine("\t<sql id=\"All_Column_List\">")
                .append("\t\t");
        int size = columns.size();
        for (int i = 0; i < size; i++) {
            sb.append(columns.get(i).getName());
            if (i != size - 1) {
                sb.append(",");
            }
            if (i>0 && i % 10 ==0){
                sb.appendLine().append("\t\t");
            }
        }
        sb.appendLine().appendLine("\t</sql>");
        return sb.toString();
    }

    private String genInsertSql(List<Column> columns) {
        StrBuilder sb = new StrBuilder("\t<!-- insertSQL字段-->").appendLine().appendLine("\t<sql id=\"Insert_Column_List\">")
                .append("\t\t");
        int size = columns.size();
        for (int i = 0; i < size; i++) {
            String name=columns.get(i).getName();
            if (StringUtil.equalsIgnoreCase(name,"id")){
                continue;
            }
            sb.append(name);
            if (i != size - 1) {
                sb.append(",");
            }
            if (i>0 && i % 10 ==0){
                sb.appendLine().append("\t\t");
            }
        }
        sb.appendLine().appendLine("\t</sql>");
        return sb.toString();
    }

    private String genMethodXml(Table table, JavaFileUtil.MethodSignature ms) {
        StrBuilder sb = new StrBuilder("\t<!--" + ms.getComment() + "-->").appendLine();
        String methodXml = "";
        switch (ms) {
            case selectById:
                methodXml = makeSelectByIdXml(table, ms);
                break;
            case selectByIds:
                methodXml = makeSelectByIdsXml(table, ms);
                break;
            case insert:
                methodXml = makeInsertXml(table, ms);
                break;
            case insertSelective:
                methodXml = makeInsertSelectiveXml(table, ms);
                break;
            case updateById:
                methodXml = makeUpdateByIdXml(table, ms);
                break;
            case updateSelectiveById:
                methodXml = makeUpdateSelectiveByIdXml(table, ms);
                break;
            case deleteById:
                methodXml = makeDeleteByIdXml(table, ms);
                break;
            case deleteByIds:
                methodXml = makeDeleteByIdsXml(table, ms);
                break;
        }
        return sb.appendLine(methodXml).toString();
    }

    private String makeDeleteByIdsXml(Table table, JavaFileUtil.MethodSignature ms) {
        return new StrBuilder("\t<delete id=\"").append(ms.getName()).appendLine("\">")
                .append("\t\tDELETE FROM ").append(table.getName()).appendLine(" WHERE id in ")
                .appendLine("\t\t\t<foreach collection=\"" + ms.getParams().getValue() + "\" index=\"index\" item=\"item\" open=\"(\" separator=\",\" close=\")\">")
                .append("\t\t\t\t").appendLine("#{item}")
                .appendLine("\t\t\t</foreach>")
                .append("\t</delete>").toString();
    }

    private String makeDeleteByIdXml(Table table, JavaFileUtil.MethodSignature ms) {
        return new StrBuilder("\t<delete id=\"").append(ms.getName()).appendLine("\">")
                .append("\t\tDELETE FROM ").append(table.getName())
                .append(" WHERE ").append(ms.getParams().getValue()).append(" =#{").append(ms.getParams().getValue()).appendLine("}")
                .append("\t</delete>").toString();
    }

    private String makeUpdateSelectiveByIdXml(Table table, JavaFileUtil.MethodSignature ms) {
        StrBuilder sb = new StrBuilder("\t<update id=\"").append(ms.getName()).appendLine("\">")
                .append("\t\tUPDATE ").appendLine(table.getName())
                .appendLine("\t\t<set> ");
        List<Column> columns = table.getColumns();
        int size = columns.size();
        for (int i = 0; i < size; i++) {
            Column c = columns.get(i);
            String field = StringUtil.underlineToUpper(c.getName());
            sb.append("\t\t\t<if test=\"pojo.").append(field).append(" != null\">")
                    .append(c.getName()).append(" = #{").append(ms.getParams().getValue()).append(".").append(field).appendLine("},</if>");
        }
        sb.appendLine("\t\t</set>")
                .append("\t\tWHERE id = #{").append(ms.getParams().getValue()).appendLine(".id}")
                .append("\t</update>");
        return sb.toString();
    }

    private String makeUpdateByIdXml(Table table, JavaFileUtil.MethodSignature ms) {
        StrBuilder sb = new StrBuilder("\t<update id=\"").append(ms.getName()).appendLine("\">")
                .append("\t\tUPDATE ").append(table.getName()).appendLine(" SET ");
        List<Column> columns = table.getColumns();
        int size = columns.size();
        for (int i = 0; i < size; i++) {
            Column c = columns.get(i);
            String field = StringUtil.underlineToUpper(c.getName());
            sb.append("\t\t\t").append(c.getName()).append(" = #{pojo.").append(field).append("}");
            if (i != size - 1) {
                sb.appendLine(",");
            }
        }
        sb.appendLine().append("\t\tWHERE id = #{").append(ms.getParams().getValue()).appendLine(".id}")
                .append("\t</update>");
        return sb.toString();
    }

    private String makeInsertSelectiveXml(Table table, JavaFileUtil.MethodSignature ms) {
        StrBuilder sb = new StrBuilder("\t<insert id=\"").append(ms.getName()).appendLine("\">")
                .append("\t\tINSERT INTO ").appendLine(table.getName())
                .appendLine("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\" >");

        List<Column> columns = table.getColumns();
        int size = columns.size();
        for (int i = 0; i < size; i++) {
            Column c = columns.get(i);
            String field = StringUtil.underlineToUpper(c.getName());
            sb.append("\t\t\t<if test=\"").append(ms.getParams().getValue()).append(".").append(field).append(" !=null\">")
                    .append(c.getName()).appendLine(",</if>");
        }
        sb.appendLine("\t\t</trim>").appendLine("\t\tVALUES").appendLine("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\" >");
        for (int i = 0; i < size; i++) {
            Column c = columns.get(i);
            String field = StringUtil.underlineToUpper(c.getName());
            sb.append("\t\t\t<if test=\"").append(ms.getParams().getValue()).append(".").append(field).append(" !=null\">")
                    .append("#{").append(ms.getParams().getValue()).append(".").append(StringUtil.underlineToUpper(c.getName())).appendLine("},</if>");
        }
        sb.appendLine("\t\t</trim>").append("\t</insert>");
        return sb.toString();
    }

    private String makeInsertXml(Table table, JavaFileUtil.MethodSignature ms) {
        StrBuilder sb = new StrBuilder("\t<insert id=\"").append(ms.getName()).appendLine("\">")
                .append("\t\tINSERT INTO ").append(table.getName()).appendLine("(")
                .append("\t\t\t");
        List<Column> columns = table.getColumns();
        int size = columns.size();
        for (int i = 0; i < size; i++) {
            Column c = columns.get(i);
            sb.append("").append(c.getName());
            if (i != size - 1) {
                sb.append(",");
            }
            if (i>0 && i % 10 ==0){
                sb.appendLine().append("\t\t\t");
            }
        }
        sb.appendLine().appendLine("\t\t) VALUES (")
        .append("\t\t\t");
        for (int i = 0; i < size; i++) {
            sb.append("#{").append(ms.getParams().getValue()).append(".").append(ms.getParams().getValue()).append(".").append(StringUtil.underlineToUpper(columns.get(i).getName())).append("}");
            if (i != size - 1) {
                sb.appendLine(",").append("\t\t\t");
            }
        }
        sb.appendLine().appendLine("\t\t)").append("\t</insert>");
        return sb.toString();
    }

    private String makeSelectByIdsXml(Table table, JavaFileUtil.MethodSignature ms) {
        return new StrBuilder("\t<select id=\"")
                .append(ms.getName())
                .appendLine("\" resultMap=\"BaseResultMap\">")
                .append("\t\t SELECT <include refid=\"All_Column_List\"/> FROM ").appendLine(table.getName())
                .appendLine("\t\t WHERE id in ")
                .appendLine("\t\t<foreach collection=\"" + ms.getParams().getValue() + "\" index=\"index\" item=\"item\" open=\"(\" separator=\",\" close=\")\">")
                .append("\t\t\t").appendLine("#{item}")
                .appendLine("\t\t</foreach>")
                .append("\t</select>").toString();
    }

    private String makeSelectByIdXml(Table table, JavaFileUtil.MethodSignature ms) {
        return new StrBuilder("\t<select id=\"")
                .append(ms.getName())
                .appendLine("\" resultMap=\"BaseResultMap\">")
                .append("\t\t SELECT <include refid=\"All_Column_List\"/> FROM ").append(table.getName())
                .append(" WHERE " + ms.getParams().getValue() + "=#{").append(ms.getParams().getValue()).appendLine("}")
                .append("\t</select>").toString();
    }
}
