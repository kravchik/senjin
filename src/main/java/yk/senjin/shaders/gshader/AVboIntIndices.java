package yk.senjin.shaders.gshader;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.Util;
import yk.jcommon.collections.YList;

import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static yk.jcommon.collections.YArrayList.al;

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
        enable(elementsCount);
    }

    public void enable(int count) {//call it if your buffer larger than you want to draw
        checkDirty();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferId);
        GL11.glDrawElements(primitiveType, count, GL_UNSIGNED_INT, 0);//TODO signed?
//        GL12.glDrawRangeElements(primitiveType, 0, indexBuffer.limit(), indexBuffer);
    }

    //TODO int[]
    public static AVboShortIndices simple(int count, int primitiveType) {
        AVboShortIndices result = new AVboShortIndices(primitiveType, count);
        YList<Integer> list = al();
        for (int i = 0; i < count; i++) list.add(i);
        result.addChange(list);
        Util.checkGLError();
        return result;
    }

}