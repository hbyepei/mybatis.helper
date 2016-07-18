package yp.dev.tools.controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import yp.dev.tools.Main;
import yp.dev.tools.builder.GeneratorBuilder;
import yp.dev.tools.pojo.Config;
import yp.dev.tools.pojo.Table;
import yp.dev.tools.ui.dialog.FXOptionPane;
import yp.dev.tools.util.*;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yp on 2016/6/9.
 */
public class MainController {
    @FXML
    private TextField ip;
    @FXML
    private TextField port;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private TextField database;
    @FXML
    private TextArea tabels;
    @FXML
    private TextField daoSuffix;
    @FXML
    private TextField daoPkg;
    @FXML
    private TextArea daoAnnotations;
    @FXML
    private TextField pojoSuffix;
    @FXML
    private TextField pojoPkg;
    @FXML
    private TextArea pojoAnnotations;
    //
    @FXML
    private CheckBox msSelectById;
    @FXML
    private CheckBox msSelectByIds;
    @FXML
    private CheckBox msInsert;
    @FXML
    private CheckBox msInsertSelective;
    @FXML
    private CheckBox msUpdateById;
    @FXML
    private CheckBox msUpdateSelectiveById;
    @FXML
    private CheckBox msDeleteById;
    @FXML
    private CheckBox msDeleteByIds;
    //
    @FXML
    private TextField targetDir;
    private Main main;
    private String homeDesktop = FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath();
    private String defaultDir = Paths.get(homeDesktop, "MybatisHelper").toString();

    @FXML
    private void initialize() {
        this.targetDir.setText(defaultDir);
        Map<String, String> confs = ConfigUtil.getAllConf();
        if (CollectionUtil.isMapNotEmpty(confs)) {
            initConf(confs);
        }
    }

    public void generateCode(ActionEvent actionEvent) {
        String dbIp = ip.getText();
        String dbPort = port.getText();
        if (StringUtil.isBlank(dbPort)) {
            dbPort = "3306";
        }
        String dbUser = username.getText();
        String dbPwd = password.getText();
        String dbName = database.getText();
        String dbTable = tabels.getText();

        String daoSuff = StringUtil.upperFirst(daoSuffix.getText());
        if (StringUtil.isBlank(daoSuff)) {
            daoSuff = "Dao";
        }
        String daoPackage = daoPkg.getText();
        if (StringUtil.isBlank(daoPackage)) {
            daoPackage = "test.dao";
        }
        String daoAnnos = daoAnnotations.getText();
        if (StringUtil.isNotBlank(daoSuff)) {
            daoSuffix.setText(daoSuff);
        }

        String ps = StringUtil.trim(pojoSuffix.getText());
        if ("无".equals(ps)) {
            ps = "";
        }
        String pojoSuff = StringUtil.upperFirst(ps);
        String pojoPackage = pojoPkg.getText();
        if (StringUtil.isBlank(pojoPackage)) {
            pojoPackage = "test.pojo";
        }
        String pojoAnnos = pojoAnnotations.getText();

        if (StringUtil.isNotBlank(pojoSuff)) {
            pojoSuffix.setText(pojoSuff);
        }

        String targetDirectory = targetDir.getText();

        boolean selectById = msSelectById.isSelected();
        boolean selectByIds = msSelectByIds.isSelected();
        boolean insert = msInsert.isSelected();
        boolean insertSelective = msInsertSelective.isSelected();
        boolean updateById = msUpdateById.isSelected();
        boolean updateSelectiveById = msUpdateSelectiveById.isSelected();
        boolean deleteById = msDeleteById.isSelected();
        boolean deleteByIds = msDeleteByIds.isSelected();

        Validator v = validateDatasource(dbIp, dbPort, dbUser, dbPwd, dbName, dbTable);
        if (!v.isSucc()) {
            FXOptionPane.showMessageDialog(main.getMainStage(), v.getMsg(), "错误");
            return;
        }

        Validator v2 = validatePackages(daoPackage, daoAnnos, pojoPackage, pojoAnnos);
        if (!v2.isSucc()) {
            FXOptionPane.showMessageDialog(main.getMainStage(), v2.getMsg(), "错误");
            return;
        }
        if (!(selectById || selectByIds || insert || insertSelective || updateById || updateSelectiveById || deleteById || deleteByIds)) {
            FXOptionPane.showMessageDialog(main.getMainStage(), "至少选择一个Dao方法", "错误");
        }

        if (StringUtil.isBlank(targetDirectory)) {
            targetDirectory = defaultDir;
            targetDir.setText(defaultDir);
        }

        List<JavaFileUtil.MethodSignature> signatures = new ArrayList<>();
        if (selectById) {
            signatures.add(JavaFileUtil.MethodSignature.selectById);
        }
        if (selectByIds) {
            signatures.add(JavaFileUtil.MethodSignature.selectByIds);
        }
        if (insert) {
            signatures.add(JavaFileUtil.MethodSignature.insert);
        }
        if (insertSelective) {
            signatures.add(JavaFileUtil.MethodSignature.insertSelective);
        }
        if (updateById) {
            signatures.add(JavaFileUtil.MethodSignature.updateById);
        }
        if (updateSelectiveById) {
            signatures.add(JavaFileUtil.MethodSignature.updateSelectiveById);
        }
        if (deleteById) {
            signatures.add(JavaFileUtil.MethodSignature.deleteById);
        }
        if (deleteByIds) {
            signatures.add(JavaFileUtil.MethodSignature.deleteByIds);
        }

        Config conf = new Config();
        conf.setIp(StringUtil.trim(dbIp));
        conf.setPort(StringUtil.trim(dbPort));
        conf.setUsername(StringUtil.trim(dbUser));
        conf.setPassword(StringUtil.trim(dbPwd));
        conf.setDatabase(StringUtil.trim(dbName));
        conf.setTabels(StringUtil.trim(dbTable));

        conf.setDaoAnnotations(Config.transformStrings(daoAnnos));
        conf.setDaoPackage(StringUtil.trim(daoPackage));
        conf.setDaoSuffix(StringUtil.trim(daoSuff));

        conf.setPojoAnnotations(Config.transformStrings(pojoAnnos));
        conf.setPojoSuffix(StringUtil.trim(pojoSuff));
        conf.setPojoPackage(StringUtil.trim(pojoPackage));

        conf.setMethods(Config.transformMethods(signatures));
        conf.setTargetDirectory(StringUtil.trim(targetDirectory));
        conf=ObjectUtil.avoidNullField(conf);
        saveConf(conf);
        doGenerate(conf, dbTable);
        try {
            IOUtil.openDir(targetDirectory);
//            cancel(actionEvent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cancel(ActionEvent actionEvent) {
        System.exit(0);
    }

    public void browse(ActionEvent actionEvent) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("选择结果保存位置...");
        chooser.setInitialDirectory(new File(homeDesktop));
        File f = chooser.showDialog(main.getMainStage());
        if (f != null) {
            this.targetDir.setText(f.getAbsolutePath());
        }
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public void testDatasource(ActionEvent actionEvent) {
        String dbIp = ip.getText();
        String dbPort = port.getText();
        String dbUser = username.getText();
        String dbPwd = password.getText();
        String dbName = database.getText();
        String dbTable = tabels.getText();
        if (StringUtil.isBlank(dbPort)) {
            dbPort = "3306";
        }
        Validator v = validateDatasource(dbIp, dbPort, dbUser, dbPwd, dbName, dbTable);
        if (!v.isSucc()) {
            FXOptionPane.showMessageDialog(main.getMainStage(), v.getMsg(), "错误");
        } else {
            FXOptionPane.showMessageDialog(main.getMainStage(), "连接成功", "提示");
        }
    }

    private void doGenerate(Config conf, String dbTable) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection(DBUtil.genConnectionUrl(conf.getIp(), conf.getPort(), conf.getDatabase()), conf.getUsername(), conf.getPassword());
            List<Table> tables = DBUtil.getDbTables(conn, Config.string2List(dbTable));
            GeneratorBuilder builder = GeneratorBuilder.getInstance(tables, new File(conf.getTargetDirectory()))
                    .setDaoAnnotation(Config.string2List(conf.getDaoAnnotations()))
                    .setDaoPkg(conf.getDaoPackage())
                    .setDaoSuffix(conf.getDaoSuffix())
                    .setPojoAnnotation(Config.string2List(conf.getPojoAnnotations()))
                    .setPojoPkg(conf.getPojoPackage())
                    .setMethodSignatures(conf.parseMethods())
                    .setPojoSuffix(conf.getPojoSuffix());
            builder.generate();
            FXOptionPane.showMessageDialog(main.getMainStage(), "生成成功", "成功");
        } catch (Exception e) {
            FXOptionPane.showMessageDialog(main.getMainStage(), "系统异常:" + e.getMessage(), "错误");
            e.printStackTrace();
        } finally {
            IOUtil.close(conn);
        }
    }

    private void saveConf(Config conf) {
        if (conf == null) {
            return;
        }
        Map<String, String> confs = new HashMap<>();
        Field[] fields = conf.getClass().getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            try {
                Object value = f.get(conf);
                if (value == null || "null".equals(value)) {
                    value = "";
                }
                confs.put(f.getName(), (String) value);
            } catch (Exception e) {

            }
        }
        ConfigUtil.saveConf(confs);
    }

    private void initConf(Map<String, String> confs) {
        Config conf = new Config();
        Field[] fields = conf.getClass().getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            String name = f.getName();
            if (StringUtil.isNotBlank(confs.get(name))) {
                try {
                    String value = confs.get(name);
                    if (value == null || "null".equals(value)) {
                        value = "";
                    }
                    f.set(conf, value);
                } catch (IllegalAccessException e) {

                }
            }
        }
        this.ip.setText(conf.getIp());
        this.port.setText(conf.getPort());
        this.username.setText(conf.getUsername());
        this.password.setText(conf.getPassword());
        this.database.setText(conf.getDatabase());
        this.tabels.setText(conf.getTabels());
        this.daoSuffix.setText(conf.getDaoSuffix());
        this.daoPkg.setText(conf.getDaoPackage());
        this.daoAnnotations.setText(conf.getDaoAnnotations());
        this.pojoAnnotations.setText(conf.getPojoAnnotations());
        this.pojoPkg.setText(conf.getPojoPackage());
        this.pojoSuffix.setText(conf.getPojoSuffix());
        this.targetDir.setText(conf.getTargetDirectory());

        List<JavaFileUtil.MethodSignature> signatures = conf.parseMethods();
        if (CollectionUtil.isNotEmpty(signatures)) {
            for (JavaFileUtil.MethodSignature signature : signatures) {
                switch (signature) {
                    case selectById:
                        this.msSelectById.setSelected(true);
                        break;
                    case selectByIds:
                        this.msSelectByIds.setSelected(true);
                        break;
                    case insert:
                        this.msInsert.setSelected(true);
                        break;
                    case insertSelective:
                        this.msInsertSelective.setSelected(true);
                        break;
                    case updateById:
                        this.msUpdateById.setSelected(true);
                        break;
                    case updateSelectiveById:
                        this.msUpdateSelectiveById.setSelected(true);
                        break;
                    case deleteById:
                        this.msDeleteById.setSelected(true);
                        break;
                    case deleteByIds:
                        this.msDeleteByIds.setSelected(true);
                        break;
                }
            }
        }
    }

    private Validator validatePackages(String daoPackage, String daoAnnos, String pojoPackage, String pojoAnnos) {
        if (StringUtil.isNotBlank(daoPackage) && !JavaFileUtil.isLegalFullClassName(daoPackage)) {
            return Validator.fail("dao包名错误");
        }
        if (StringUtil.isNotBlank(pojoPackage) && !JavaFileUtil.isLegalFullClassName(pojoPackage)) {
            return Validator.fail("pojo包名错误");
        }
        if (StringUtil.isNotBlank(daoAnnos)) {
            String[] annos = daoAnnos.split(",");
            for (String s : annos) {
                String temp = s.trim();
                if (!JavaFileUtil.isLegalFullClassName(temp)) {
                    return Validator.fail("Dao注解名称有误:" + s);
                }
            }
        }
        if (StringUtil.isNotBlank(pojoAnnos)) {
            String[] annos = pojoAnnos.split(",");
            for (String s : annos) {
                String temp = s.trim();
                if (!JavaFileUtil.isLegalFullClassName(temp)) {
                    return Validator.fail("Pojo注解名称有误:" + s);
                }
            }
        }
        return Validator.success();
    }

    private Validator validateDatasource(String ip, String port, String user, String pwd, String db, String dbTable) {
        if (StringUtil.isBlank(ip)) {
            return Validator.fail("数据库ip地址不能为空");
        }
        if (StringUtil.isBlank(port)) {
            return Validator.fail("端口不能为空");
        }
        if (StringUtil.isBlank(user)) {
            return Validator.fail("数据库账号不能为空");
        }
        if (StringUtil.isBlank(pwd)) {
            return Validator.fail("密码不能为空");
        }
        if (StringUtil.isBlank(db)) {
            return Validator.fail("请指定数据库名称");
        }
        Connection conn = null;
        try {
            conn = DBUtil.getConnection(DBUtil.genConnectionUrl(ip, port, db), user, pwd);
            if (conn == null) {
                return Validator.fail("连接失败");
            }
            List<String> tableNames = DBUtil.getTableNames(conn, null);
            if (CollectionUtil.isEmpty(tableNames)) {
                return Validator.fail("数据库\"" + db + "\"中未发现表信息");
            }
            if (!StringUtil.isBlank(dbTable)) {
                String[] dbtables = dbTable.split(",");
                if (dbtables.length < 1) {
                    return Validator.fail("无效的表名");
                }
                for (String s : dbtables) {
                    boolean found = false;
                    String n = s.trim();
                    for (String name : tableNames) {
                        if (name.equalsIgnoreCase(n) || DBUtil.parseRealTableName(name).equalsIgnoreCase(n)
                                || DBUtil.parseRealTableName(n).equalsIgnoreCase(name) ||
                                DBUtil.parseRealTableName(name).equalsIgnoreCase(DBUtil.parseRealTableName(n))) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        return Validator.fail(db + "库中无此数据表:" + s);
                    }
                }
            }
        } catch (Exception e) {
            return Validator.fail("连接失败:" + e.getMessage());
        } finally {
            IOUtil.close(conn);
        }
        return Validator.success();
    }
}
