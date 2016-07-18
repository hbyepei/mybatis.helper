package yp.dev.tools.builder;
/**
 * Created by yp on 2016/6/10.
 */
public class StrBuilder {
    private final static String lineSep = System.lineSeparator();
    private StringBuilder sb;

    public StrBuilder() {
        this.sb = new StringBuilder();
    }

    public StrBuilder(String str) {
        this.sb = new StringBuilder(str);
    }

    public StrBuilder(int size) {
        this.sb = new StringBuilder(size);
    }

    public StrBuilder append(String str) {
        this.sb.append(str);
        return this;
    }

    public StrBuilder append(int n) {
        this.sb.append(n);
        return this;
    }

    public StrBuilder append(Object o) {
        this.sb.append(o);
        return this;
    }

    public StrBuilder appendLine() {
        this.sb.append(lineSep);
        return this;
    }

    public StrBuilder appendLine(String str) {
        this.sb.append(str).append(lineSep);
        return this;
    }

    public StrBuilder appendLine(int n) {
        this.sb.append(n).append(lineSep);
        return this;
    }

    public StrBuilder appendLine(double n) {
        this.sb.append(n).append(lineSep);
        return this;
    }

    public StrBuilder appendLine(short n) {
        this.sb.append(n).append(lineSep);
        return this;
    }

    public StrBuilder appendLine(long n) {
        this.sb.append(n).append(lineSep);
        return this;
    }

    public StrBuilder appendLine(char n) {
        this.sb.append(n).append(lineSep);
        return this;
    }

    public StrBuilder appendLine(boolean n) {
        this.sb.append(n).append(lineSep);
        return this;
    }

    public StrBuilder appendLine(byte n) {
        this.sb.append(n).append(lineSep);
        return this;
    }

    public StrBuilder appendLine(Object o) {
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
