package yk.senjin.shaders.uniforms;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import yk.jcommon.fastgeom.Matrix4;

import java.nio.FloatBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 21/10/15
 * Time: 12:45
 */
public class UniformRefMatrix4 extends UniformRef<Matrix4> {
    public FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(4 * 4);

    public UniformRefMatrix4(String name, Object src, String fieldName) {
        super(name, src, fieldName);
    }

    public void set(Matrix4 m) {
        m.store(matrixBuffer);
        matrixBuffer.rewind();
    }

    @Override
    public void plug() {
        set(getValue());
        GL20.glUniformMatrix4(index, false, matrixBuffer);
    }
}
