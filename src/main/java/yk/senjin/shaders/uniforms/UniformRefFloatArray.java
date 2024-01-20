package yk.senjin.shaders.uniforms;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import yk.jcommon.utils.Reflector;

import java.lang.reflect.Array;
import java.nio.FloatBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 21/10/15
 * Time: 12:45
 */
public class UniformRefFloatArray extends UniformRef {
    public FloatBuffer buffer;

    public UniformRefFloatArray(String name, Object src, String fieldName) {
        super(name, src, fieldName);
        buffer = BufferUtils.createFloatBuffer(Array.getLength(Reflector.get(src, fieldName)));
    }

    @Override
    public float[] getValue() {
        try {
            return (float[]) _field.get(src);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void plug() {
        buffer.put(getValue());
        buffer.rewind();
        GL20.glUniform1fv(index, buffer);
    }
}
