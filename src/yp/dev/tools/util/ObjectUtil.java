package yp.dev.tools.util;
import java.lang.reflect.Field;
import java.math.BigDecimal;

/**
 * Created by yp on 2016/6/11.
 */
public class ObjectUtil {
    public static <T> T avoidNullField(T object) {
        if (object == null) {
            return null;
        }
        Class clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Object fieldValue = null;
            try {
                if (field.getName().equalsIgnoreCase("serialVersionUID")) {
                    continue;
                }
                if (field.getName().equals("id")) {
                    continue;
                }
                field.setAccessible(true);
                fieldValue = field.get(object);
                if (fieldValue == null) {
                    field.set(object, getDefaultValue(field.getType()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return object;
    }

    private static Object getDefaultValue(Class clazz) {
        if (clazz.equals(String.class)) {
            return "";
        } else if (clazz.equals(Long.class)) {
            return 0L;
        } else if (clazz.equals(Float.class)) {
            return 0.0f;
        } else if (clazz.equals(Integer.class)) {
            return 0;
        } else if (clazz.equals(Short.class)) {
            return (short) 0;
        } else if (clazz.equals(BigDecimal.class)) {
            return new BigDecimal(0);
        }
        return null;
    }
}
