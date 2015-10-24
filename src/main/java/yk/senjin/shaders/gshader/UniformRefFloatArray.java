package yk.senjin.shaders.gshader;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

import java.nio.FloatBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 21/10/15
 * Time: 12:45
 */
public class UniformRefFloatArray extends UniformRef {
    public FloatBuffer buffer;

    //TODO make it work
    public UniformRefFloatArray(String name, Object src, String fieldName, int count) {
        super(name, src, fieldName);
        buffer = BufferUtils.createFloatBuffer(count);
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
        GL20.glUniform1(index, buffer);
    }
}
