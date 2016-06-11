package yp.dev.tools.pojo;
/**
 * Created by yp on 2016/6/10.
 */
public class PojoInfo {
    /**
     * 生成的pojo的类名
     */
    private String beanName;

    private String beanSuffix;

    private String daoSuffix;

    private String daoPkg;

    /**
     * 生成的带包名的pojo类名
     */
    private String beanPkg;

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getBeanSuffix() {
        return beanSuffix;
    }

    public void setBeanSuffix(String beanSuffix) {
        this.beanSuffix = beanSuffix;
    }

    public String getBeanPkg() {
        return beanPkg;
    }

    public void setBeanPkg(String beanPkg) {
        this.beanPkg = beanPkg;
    }

    public String getDaoSuffix() {
        return daoSuffix;
    }

    public void setDaoSuffix(String daoSuffix) {
        this.daoSuffix = daoSuffix;
    }

    public String getDaoPkg() {
        return daoPkg;
    }

    public void setDaoPkg(String daoPkg) {
        this.daoPkg = daoPkg;
    }
}
