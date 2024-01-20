package yk.senjin.shaders.uniforms;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import yk.jcommon.fastgeom.Matrix4;

import java.nio.FloatBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 06/01/15
 * Time: 13:33
 */
public class UniformMatrix4 extends UniformVariable {
    public FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(4 * 4);

    public UniformMatrix4(String name) {
        super(name);
    }

    @Override
    public void setValue(Object o) {
        set((Matrix4) o);
    }

    public void set(Matrix4 m) {
        m.store(matrixBuffer);
        matrixBuffer.rewind();
    }

    @Override
    public void plug() {
        GL20.glUniformMatrix4fv(index, false, matrixBuffer);
    }
}
