package yp.dev.tools.builder;
import yp.dev.tools.Main;
import yp.dev.tools.pojo.PojoInfo;
import yp.dev.tools.pojo.Table;
import yp.dev.tools.util.CollectionUtil;
import yp.dev.tools.util.IOUtil;
import yp.dev.tools.util.JavaFileUtil;
import yp.dev.tools.util.StringUtil;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;

import static yp.dev.tools.util.DBUtil.parseNaturalTableName;

/**
 * Created by yp on 2016/6/9.
 */
public class GeneratorBuilder {
    private String pojoSuffix = "";
    private String pojoPkg = "sample.pojo";
    private String pojoFilePath;
    private List<String> pojoAnnotations;//Pojo注解，类全名
    private String daoSuffix = "Dao";
    private String daoPkg = "sample.dao";
    private String daoFilePath;
    private List<String> daoAnnotations;//Dao类的注解，类全名
    private List<JavaFileUtil.MethodSignature> methodSignatures;
    private String xmlFilePath;
    private List<Table> tables;
    private File targetDir;
    private static GeneratorBuilder builder;

    private GeneratorBuilder() {
    }

    public void generate() throws ClassNotFoundException, SQLException, IOException {
        if (CollectionUtil.isEmpty(tables)) {
            throw new RuntimeException("未指定表，无法生成代码");
        }
        if (CollectionUtil.isEmpty(methodSignatures)) {
            methodSignatures = JavaFileUtil.MethodSignature.getDefaultMethods();
        }
        if (this.targetDir != null && !this.targetDir.exists()) {
            IOUtil.createDir(this.targetDir);
        }

        PojoInfo pojoInfo = new PojoInfo();
        pojoInfo.setBeanSuffix(pojoSuffix);
        pojoInfo.setBeanPkg(pojoPkg);
        pojoInfo.setDaoPkg(daoPkg);
        pojoInfo.setDaoSuffix(daoSuffix);
        for (Table table : tables) {
            if (CollectionUtil.isEmpty(table.getColumns())) {
                continue;
            }
            pojoInfo.setBeanName(StringUtil.upperFirst(StringUtil.underlineToUpper(parseNaturalTableName(table.getNaturalName()))));
            new PojoBuilder(pojoPkg, pojoSuffix, pojoFilePath).build(table, pojoAnnotations);
            new DaoBuilder(daoPkg, daoSuffix, daoFilePath).build(pojoInfo, daoAnnotations, methodSignatures);
            new XmlBuilder(xmlFilePath).build(pojoInfo, table, methodSignatures);
        }
    }

    public static GeneratorBuilder getInstance(List<Table> tables, File targetDir) {
        if (builder == null) {
            builder = new GeneratorBuilder();
            builder.tables = tables;
            if (targetDir == null) {
                targetDir = new File(Paths.get(FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath(), Main.class.getSimpleName()).toString());
            }
            if (!targetDir.exists()) {
                IOUtil.createDir(targetDir);
            }
            builder.targetDir = targetDir;
            String basePath = targetDir.getPath();
            builder.setPojoFilePath(Paths.get(basePath, "pojo").toString());
            builder.setDaoFilePath(Paths.get(basePath, builder.daoSuffix.toLowerCase()).toString());
            builder.setXmlFilePath(Paths.get(basePath, "mapper").toString());
        }
        return builder;
    }

    public GeneratorBuilder setPojoSuffix(String pojoSuffix) {
        this.pojoSuffix = StringUtil.upperFirst(pojoSuffix);
        return this;
    }

    public GeneratorBuilder setPojoPkg(String pojoPkg) {
        this.pojoPkg = pojoPkg;
        return this;
    }

    private GeneratorBuilder setPojoFilePath(String pojoFilePath) {
        this.pojoFilePath = pojoFilePath;
        IOUtil.createDir(pojoFilePath);
        return this;
    }

    public GeneratorBuilder setDaoSuffix(String daoSuffix) {
        this.daoSuffix = StringUtil.upperFirst(daoSuffix);
        return this;
    }

    public GeneratorBuilder setDaoPkg(String daoPkg) {
        this.daoPkg = daoPkg;
        return this;
    }

    private GeneratorBuilder setDaoFilePath(String daoFilePath) {
        this.daoFilePath = daoFilePath;
        IOUtil.createDir(daoFilePath);
        return this;
    }

    private GeneratorBuilder setXmlFilePath(String xmlFilePath) {
        this.xmlFilePath = xmlFilePath;
        IOUtil.createDir(xmlFilePath);
        return this;
    }

    public GeneratorBuilder setDaoAnnotation(List<String> daoAnnotations) {
        this.daoAnnotations = daoAnnotations;
        return this;
    }

    public GeneratorBuilder setMethodSignatures(List<JavaFileUtil.MethodSignature> methodSignatures) {
        this.methodSignatures = methodSignatures;
        if (CollectionUtil.isEmpty(this.methodSignatures)) {
            this.methodSignatures = JavaFileUtil.MethodSignature.getDefaultMethods();
        }
        return this;
    }

    public GeneratorBuilder setPojoAnnotation(List<String> pojoAnnotations) {
        this.pojoAnnotations = pojoAnnotations;
        return this;
    }
}
