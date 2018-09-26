package yk.senjin.shaders.uniforms;

import yk.jcommon.utils.BadException;
import yk.jcommon.utils.Reflector;

import java.lang.reflect.Field;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 21/10/15
 * Time: 13:02
 */
abstract public class UniformRef<T> extends UniformVariable {
    protected Object src;
    protected Field _field;

    public UniformRef(String name, Object src, String fieldName) {
        super(name);
        _field = Reflector.getField(src.getClass(), fieldName);
        this.src = src;
    }

    public T getValue() {
        try {
            T result = (T) _field.get(src);
            if (result == null) BadException.die("null in field " + _field.getName());
            return result;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
