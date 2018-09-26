package yk.senjin.shaders.uniforms;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import yk.jcommon.fastgeom.Matrix3;

import java.nio.FloatBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 06/01/15
 * Time: 13:33
 */
public class UniformMatrix3 extends UniformVariable {
    public FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(3*3);

    public UniformMatrix3(String name) {
        super(name);
    }

    @Override
    public void setValue(Object o) {
        set((Matrix3) o);
    }

    public void set(Matrix3 m) {
        m.store(matrixBuffer);
        matrixBuffer.rewind();
    }

    @Override
    public void plug() {
        GL20.glUniformMatrix3(index, false, matrixBuffer);
    }
}
