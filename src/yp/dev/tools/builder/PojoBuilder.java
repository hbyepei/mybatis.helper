package yp.dev.tools.builder;
import yp.dev.tools.pojo.Column;
import yp.dev.tools.pojo.Table;
import yp.dev.tools.util.*;

import java.io.*;
import java.util.Date;
import java.util.List;

/**
 * Created by yp on 2016/6/9.
 */
public class PojoBuilder {
    private String pkgName;
    private String pojoSuffix;
    private String filePath;

    public PojoBuilder(String pkgName, String pojoSuffix, String filePath) {
        this.pkgName = pkgName;
        this.pojoSuffix = pojoSuffix;
        if (this.pojoSuffix == null) {
            this.pojoSuffix = "";
        }
        this.filePath = filePath;
    }

    /**
     * 生成实体类
     *
     * @param table
     * @param pojoAnnotations
     * @throws IOException
     */
    public void build(Table table, List<String> pojoAnnotations) throws IOException {

        String comment = table.getComment();
        List<Column> columns = table.getColumns();

        String beanName = StringUtil.upperFirst(StringUtil.underlineToUpper(DBUtil.parseNaturalTableName(table.getNaturalName())));
        BufferedWriter bw = null;
        String anno = "";
        if (!CollectionUtil.isEmpty(pojoAnnotations)) {
            for (String s : pojoAnnotations) {
                anno += ("@" + s.substring(s.lastIndexOf(".") + 1) + StringUtil.lingSeperator);
            }
        }
        try {
            IOUtil.createDir(this.filePath);
            File beanFile = new File(this.filePath, beanName + pojoSuffix + ".java");
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(beanFile), "UTF-8"));
            StrBuilder sb = new StrBuilder()
                    .appendLine(JavaFileUtil.genPackageAndImports(pkgName, pojoAnnotations, columns))//包声明及导入声明
                    .appendLine(JavaFileUtil.genClassComment(new Date(), null, comment))//类注释
//                    .appendLine("@SuppressWarnings(\"serial\")")//No serial警告压制
                    .append(anno)
                    .appendLine("public class " + beanName + pojoSuffix + " implements Serializable {")
                    .appendLine("\tprivate static final long serialVersionUID = 1L;")
                    .appendLine(JavaFileUtil.genFields(columns))
                    .appendLine(JavaFileUtil.genGetterAndSetters(columns)).append("}");
            bw.write(sb.toString());
            bw.flush();
        } finally {
            IOUtil.close(bw);
        }
    }
}
