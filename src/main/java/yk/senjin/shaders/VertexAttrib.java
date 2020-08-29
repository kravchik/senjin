/**
 * File VertexAttrib.java
 * @author Yuri Kravchik
 * Created 16.05.2008
 */
package yk.senjin.shaders;

import org.lwjgl.opengl.GL20;

/**
 * VertexAttrib - describes vertex data inside shader (index (more like an ID), type, size, name)s
 *
 * @author Yuri Kravchik Created 16.05.2008
 */
public class VertexAttrib {
    /**
     * Specifies the index of this data in this shader
     */
    private int index;
    /**
     * Specifies the data type of each component in the array. Symbolic
     * constants GL_BYTE, GL_UNSIGNED_BYTE, GL_SHORT, GL_UNSIGNED_SHORT, GL_INT,
     * GL_UNSIGNED_INT, GL_FLOAT, and GL_DOUBLE are accepted.
     */
    private final int type;
    /**
     * Specifies the number of values for each element of the generic vertex
     * attribute array. Must be 1, 2, 3, or 4.
     */
    private final int size;
    /**
     * Specifies whether fixed-point data values should be normalized (GL_TRUE)
     * or converted directly as fixed-point values (GL_FALSE) when they are
     * accessed.
     */
    private final boolean normalized = false;
    private final String name;

    public VertexAttrib(final String name, final int type, final int size) {
        this.name = name;
        this.type = type;
        this.size = size;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public int getType() {
        return type;
    }

    public void initForProgram(final int program) {
        index = GL20.glGetAttribLocation(program, name);
    }

    public boolean isNormalized() {
        return normalized;
    }

}
