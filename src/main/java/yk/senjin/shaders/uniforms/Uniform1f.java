/**
 * File Uniform1f.java
 * @author Yuri Kravchik
 * Created 16.05.2008
 */
package yk.senjin.shaders.uniforms;

import org.lwjgl.opengl.GL20;

/**
 * Uniform1f
 *
 * @author Yuri Kravchik Created 16.05.2008
 */
public class Uniform1f extends UniformVariable {
    public float value;

    public Uniform1f(final String name) {
        super(name);
    }

    public Uniform1f(final String name, final float value) {
        super(name);
        this.value = value;
    }

    @Override
    public void setValue(Object o) {
        value = (float) o;
    }

    @Override
    public void plug() {
        GL20.glUniform1f(index, value);
    }

}
