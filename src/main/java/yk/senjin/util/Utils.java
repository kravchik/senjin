package yk.senjin.util;

import java.util.function.Consumer;

/**
 * Created by yuri at 2023.03.22
 */
public class Utils {

    public static <T> T forThis(T t, Consumer<T> c) {
        c.accept(t);
        return t;
    }
}
