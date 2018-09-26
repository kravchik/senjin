package yk.senjin.shaders.uniforms;

import org.lwjgl.opengl.GL20;
import yk.jcommon.fastgeom.Vec3f;

import static yk.jcommon.fastgeom.Vec3f.v3;

/**
 * Created by: Yuri Kravchik Date: 2 11 2007 Time: 16:25:05
 */
public class Uniform3f extends UniformVariable {
    public Vec3f value;

    public Uniform3f(String name) {
        super(name);
    }

    public Uniform3f(final String name, Vec3f value) {
        super(name);
        this.value = new Vec3f(value);
    }

    public Uniform3f(final String name, final float x, final float y, final float z) {
        super(name);
        value = v3(x, y, z);
    }

    @Override
    public void setValue(Object o) {
        value = (Vec3f) o;
    }

    @Override
    public void plug() {
        GL20.glUniform3f(index, value.x, value.y, value.z);
    }
}
