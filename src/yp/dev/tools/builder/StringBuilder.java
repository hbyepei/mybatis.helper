package yp.dev.tools.builder;
/**
 * Created by yp on 2016/6/10.
 */
public class StringBuilder {
    private final static String lineSep = System.lineSeparator();
    private java.lang.StringBuilder sb;

    public StringBuilder() {
        this.sb = new java.lang.StringBuilder();
    }

    public StringBuilder(String str) {
        this.sb = new java.lang.StringBuilder(str);
    }

    public StringBuilder(int size) {
        this.sb = new java.lang.StringBuilder(size);
    }

    public StringBuilder append(String str) {
        this.sb.append(str);
        return this;
    }

    public StringBuilder append(int n) {
        this.sb.append(n);
        return this;
    }

    public StringBuilder append(Object o) {
        this.sb.append(o);
        return this;
    }

    public StringBuilder appendLine() {
        this.sb.append(lineSep);
        return this;
    }

    public StringBuilder appendLine(String str) {
        this.sb.append(str).append(lineSep);
        return this;
    }

    public StringBuilder appendLine(int n) {
        this.sb.append(n).append(lineSep);
        return this;
    }

    public StringBuilder appendLine(double n) {
        this.sb.append(n).append(lineSep);
        return this;
    }

    public StringBuilder appendLine(short n) {
        this.sb.append(n).append(lineSep);
        return this;
    }

    public StringBuilder appendLine(long n) {
        this.sb.append(n).append(lineSep);
        return this;
    }

    public StringBuilder appendLine(char n) {
        this.sb.append(n).append(lineSep);
        return this;
    }

    public StringBuilder appendLine(boolean n) {
        this.sb.append(n).append(lineSep);
        return this;
    }

    public StringBuilder appendLine(byte n) {
        this.sb.append(n).append(lineSep);
        return this;
    }

    public StringBuilder appendLine(Object o) {
        if (o == null) {
            o = "";
        }
        this.sb.append(o).append(lineSep);
        return this;
    }

    @Override
    public String toString() {
        return this.sb.toString();
    }
}
