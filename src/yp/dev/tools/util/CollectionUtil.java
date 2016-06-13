package yp.dev.tools.util;
import java.util.*;

/**
 * Created by yp on 2016/6/9.
 */
public class CollectionUtil {
    public static boolean isEmpty(Collection c) {
        return c == null || c.size() == 0;
    }

    public static boolean isNotEmpty(Collection c){
        return  !isEmpty(c);
    }

    public static boolean isMapEmpty(Map m) {
        return m == null || m.size() == 0;
    }

    public static boolean isMapNotEmpty(Map m){
        return !isMapEmpty(m);
    }

    public static <T, E> List<T> transform(Collection<E> collection, Function<E, T> function) {
        if (isEmpty(collection)) {
            return Collections.emptyList();
        }
        List<T> result = new ArrayList<>();
        Iterator<E> it = collection.iterator();
        while (it.hasNext()) {
            result.add(function.apply(it.next()));
        }
        return result;
    }
}
