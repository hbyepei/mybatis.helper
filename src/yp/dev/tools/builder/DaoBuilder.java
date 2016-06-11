package yp.dev.tools.builder;
import yp.dev.tools.pojo.PojoInfo;
import yp.dev.tools.util.CollectionUtil;
import yp.dev.tools.util.IOUtil;
import yp.dev.tools.util.JavaFileUtil;
import yp.dev.tools.util.StringUtil;

import java.io.*;
import java.lang.*;
import java.util.Date;
import java.util.List;

/**
 * Created by yp on 2016/6/9.
 */
public class DaoBuilder {
    private final static String sep = StringUtil.lingSeperator;
    private String daoPkg;
    private String daoSuffix;
    private String daoFilePath;

    public DaoBuilder(String daoPkg, String daoSuffix, String daoFilePath) {
        this.daoPkg = daoPkg;
        this.daoSuffix = daoSuffix;
        if (StringUtil.isBlank(this.daoSuffix)){
            this.daoSuffix="Dao";
        }
        this.daoFilePath = daoFilePath;
    }

    public void build(PojoInfo pi, List<String> pojoAnnotations, List<JavaFileUtil.MethodSignature> methodSignatures) throws IOException {
        String daoName = pi.getBeanName() + daoSuffix;
        BufferedWriter bw = null;
        try {
            IOUtil.createDir(this.daoFilePath);
            File daoFile = new File(daoFilePath, daoName + ".java");
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(daoFile), "UTF-8"));
            String annotation = "";
            String daoAnnoImport = "";
            if (!CollectionUtil.isEmpty(pojoAnnotations)){
                for (String s: pojoAnnotations){
                    if (JavaFileUtil.isLegalFullClassName(s)) {
                        annotation += ("@" + s.substring(s.lastIndexOf(".")+1)+sep);
                        daoAnnoImport += ("import " + s + ";" + sep);
                    }
                }
            }

            StringBuilder sb = new StringBuilder()
                    .appendLine("package " + daoPkg + ";")
                    .append("import " + pi.getBeanPkg() + "." + pi.getBeanName()).appendLine(";")
                    .append(daoAnnoImport)
                    .appendLine("import org.apache.ibatis.annotations.Param;");
            if (methodSignatures.contains(JavaFileUtil.MethodSignature.deleteByIds)
                    || methodSignatures.contains(JavaFileUtil.MethodSignature.selectByIds)
                    ) {
                sb.appendLine("import java.util.List;");
            }
            sb.appendLine(JavaFileUtil.genClassComment(new Date(), null, daoName + "数据库操作接口类"))
                    .append(annotation)
                    .append("public interface ").append(daoName).appendLine("{")
                    .appendLine(JavaFileUtil.genDaoMethods(pi, methodSignatures))
                    .append("}");
            bw.write(sb.toString());
        } finally {
            IOUtil.close(bw);
        }
    }
}
