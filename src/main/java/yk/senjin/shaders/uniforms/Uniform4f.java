package yk.senjin.shaders.uniforms;

import org.lwjgl.opengl.GL20;
import yk.jcommon.fastgeom.Vec4f;

import static yk.jcommon.fastgeom.Vec4f.v4;

/**
 * Created by: Yuri Kravchik Date: 2 11 2007 Time: 16:09:33
 */
public class Uniform4f extends UniformVariable {
    public Vec4f value;

    public Uniform4f(String name) {
        super(name);
    }

    public Uniform4f(final String name, Vec4f value) {
        super(name);
        this.value = new Vec4f(value);
    }

    public Uniform4f(final String name, final float x, final float y, final float z, final float w) {
        super(name);
        value = v4(x, y, z, w);
    }

    @Override
    public void setValue(Object o) {
        value = (Vec4f) o;
    }

    @Override
    public void plug() {
        GL20.glUniform4f(index, value.w, value.x, value.y, value.z);
    }
}
