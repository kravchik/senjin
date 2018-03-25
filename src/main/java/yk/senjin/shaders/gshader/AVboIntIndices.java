package yk.senjin.shaders.gshader;

import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;

/**
 * Created by Yuri Kravchik on 25.03.18.
 */
public class AVboIntIndices extends AVboInt {
    /**
     * Specifies what kind of primitives to render. Symbolic constants
     * GL_POINTS, GL_LINE_STRIP, GL_LINE_LOOP, GL_LINES, GL_TRIANGLE_STRIP,
     * GL_TRIANGLE_FAN, GL_TRIANGLES, GL_QUAD_STRIP, GL_QUADS, and GL_POLYGON
     * are accepted.
     */
    private final int primitiveType;

    public AVboIntIndices(int primitiveType, int elementsCount) {
        super(elementsCount);
        this.primitiveType = primitiveType;
    }

    public void enable() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferId);
        GL11.glDrawElements(primitiveType, elementsCount * elementSize, GL_UNSIGNED_INT, 0);//TODO signed?
//        GL12.glDrawRangeElements(primitiveType, 0, indexBuffer.limit(), indexBuffer);
    }


}
