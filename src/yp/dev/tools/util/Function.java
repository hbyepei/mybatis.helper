package yp.dev.tools.util;
/**
 * Created by yepei.ye on 2016/6/13.
 * Description:
 */
public interface Function<T, F> {
    F apply(T t);
}
