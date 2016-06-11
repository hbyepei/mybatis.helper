package yp.dev.tools.util;
/**
 * Created by yp on 2016/6/11.
 */
public class Validator {
    private boolean succ = false;
    private String msg = "";


    public static Validator success() {
        Validator v = new Validator();
        v.setSucc(true);
        return v;
    }

    public static Validator fail(String msg) {
        Validator v = new Validator();
        v.setSucc(false);
        v.setMsg(msg);
        return v;
    }

    public boolean isSucc() {
        return succ;
    }

    public void setSucc(boolean succ) {
        this.succ = succ;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
