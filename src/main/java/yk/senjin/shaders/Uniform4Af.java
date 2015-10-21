/**
 * File Uniform4Af.java
 * @author Yuri Kravchik
 * Created 16.05.2008
 */
package yk.senjin.shaders;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

/**
 * Uniform4Af
 *
 * @author Yuri Kravchik Created 16.05.2008
 */
public class Uniform4Af extends UniformVariable {
    private final FloatBuffer values;

    public Uniform4Af(final String name, final Float... values) {
        this.values = BufferUtils.createFloatBuffer(values.length);
        for (final Float i : values) {
            this.values.put(i);
        }
        this.values.rewind();
        this.name = name;
    }

    @Override
    public void plug() {
        GL20.glUniform4(index, values);
    }

}
