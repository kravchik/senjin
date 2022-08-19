package yk.senjin.vbo;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.Util;
import yk.jcommon.collections.YList;

import java.nio.ByteBuffer;
import java.util.List;

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
        bufferType = GL_ELEMENT_ARRAY_BUFFER;
    }

    public AVboIntIndices(int primitiveType, List<Number> indices) {
        super(indices.size());
        this.primitiveType = primitiveType;
        bufferType = GL_ELEMENT_ARRAY_BUFFER;
        ByteBuffer bb = BufferUtils.createByteBuffer(elementSize * indices.size());
        for (int i = 0, verticesSize = indices.size(); i < verticesSize; i++) bb.putInt(indices.get(i).intValue());
        bb.rewind();
        addChange(bb, 0).recreate = true;
    }

    public AVboIntIndices(int primitiveType, int[] indices) {
        super(indices.length);
        this.primitiveType = primitiveType;
        bufferType = GL_ELEMENT_ARRAY_BUFFER;
        ByteBuffer bb = BufferUtils.createByteBuffer(elementSize * indices.length);
        for (int i = 0, verticesSize = indices.length; i < verticesSize; i++) bb.putInt(indices[i]);
        bb.rewind();
        addChange(bb, 0).recreate = true;
    }

    public void enable() {
        enable(elementsCount);
    }

    public void enable(int count) {//call it if your buffer larger than you want to draw
        enabled = true;
        glBindBuffer(bufferType, bufferId);
        checkDirty();
        GL11.glDrawElements(primitiveType, count, GL_UNSIGNED_INT, 0);//TODO signed?
        enabled = false;
    }

    //TODO int[]
    public static AVboShortIndices simple(int count, int primitiveType) {
        AVboShortIndices result = new AVboShortIndices(primitiveType, count);
        YList<Integer> list = al();
        for (int i = 0; i < count; i++) list.add(i);
        result.reload(list);
        Util.checkGLError();
        return result;
    }

}
