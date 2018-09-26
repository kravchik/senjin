package yk.senjin.shaders.uniforms;

import org.lwjgl.opengl.GL20;
import yk.jcommon.fastgeom.Vec2f;

/**
 * Created by: Yuri Kravchik Date: 2 11 2007 Time: 16:25:36
 */
public class Uniform2f extends UniformVariable {
    public Vec2f value;

    public Uniform2f(final String name) {
        super(name);
    }

    public Uniform2f(final String name, Vec2f value) {
        super(name);
        this.value = new Vec2f(value);
    }

    public Uniform2f(final String name, final float x, final float y) {
        super(name);
        value = new Vec2f(x, y);
    }

    @Override
    public void setValue(Object o) {
        value = (Vec2f) o;
    }

    @Override
    public void plug() {
        GL20.glUniform2f(index, value.x, value.y);
    }
}
