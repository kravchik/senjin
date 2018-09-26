package yk.senjin.shaders.uniforms;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import yk.jcommon.fastgeom.Matrix3;

import java.nio.FloatBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 21/10/15
 * Time: 12:45
 */
public class UniformRefMatrix3 extends UniformRef<Matrix3> {
    public FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(3 * 3);

    public UniformRefMatrix3(String name, Object src, String fieldName) {
        super(name, src, fieldName);
    }

    public void set(Matrix3 m) {
        m.store(matrixBuffer);
        matrixBuffer.rewind();
    }

    @Override
    public void plug() {
        set(getValue());
        GL20.glUniformMatrix3(index, false, matrixBuffer);
    }
}
