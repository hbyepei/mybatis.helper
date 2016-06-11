package yp.dev.tools.pojo;
import java.io.Serializable;
import java.util.List;

/**
 * Created by yp on 2016/6/9.
 */
public class Table implements Serializable{
    private static final long serialVersionUID = -8939471885499849096L;
    private String name;
    private String comment;
    private List<Column> columns;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }
}
