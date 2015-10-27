package yk.senjin.shaders.gshader;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

import java.nio.IntBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 21/10/15
 * Time: 12:45
 */
public class UniformRefIntArray extends UniformRef {
    public IntBuffer buffer;

    public UniformRefIntArray(String name, Object src, String fieldName, int count) {
        super(name, src, fieldName);
        buffer = BufferUtils.createIntBuffer(count);
    }

    @Override
    public int[] getValue() {
        try {
            return (int[]) _field.get(src);
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
