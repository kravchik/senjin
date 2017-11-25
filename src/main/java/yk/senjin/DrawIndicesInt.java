package yk.senjin;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL12;

import java.nio.IntBuffer;
import java.util.List;

/**
 * Created by: Yuri Kravchik Date: 31/10/2007 Time: 18:17:39
 */
public class DrawIndicesInt extends AbstractState {
    private IntBuffer indexBuffer;
    /**
     * Specifies what kind of primitives to render. Symbolic constants
     * GL_POINTS, GL_LINE_STRIP, GL_LINE_LOOP, GL_LINES, GL_TRIANGLE_STRIP,
     * GL_TRIANGLE_FAN, GL_TRIANGLES, GL_QUAD_STRIP, GL_QUADS, and GL_POLYGON
     * are accepted.
     */
    private final int primitiveType;

    public DrawIndicesInt(final int primitiveType, List<? extends Number> indices) {
        this.primitiveType = primitiveType;
        fill(indices);
    }

    public static DrawIndicesInt simple(int count, int primitiveType) {
        DrawIndicesInt result = new DrawIndicesInt(primitiveType);
        result.indexBuffer = BufferUtils.createIntBuffer(count);
        for (int i = 0; i < count; i++) result.indexBuffer.put((short) i);
        result.indexBuffer.rewind();
        return result;
    }

    public DrawIndicesInt(final int primitiveType) {
        this.primitiveType = primitiveType;
    }

    public void fill(List<? extends Number> indices) {
        this.indexBuffer = BufferUtils.createIntBuffer(indices.size());
        for (Number aShort : indices) indexBuffer.put(aShort.shortValue());
        indexBuffer.rewind();
    }

    public int getCount() {
        return indexBuffer.limit();
    }

    public int getPrimitiveType() {
        return primitiveType;
    }

    public void disable() {
    }

    public void enable() {
        GL12.glDrawRangeElements(primitiveType, 0, indexBuffer.limit(), indexBuffer);
    }

    public void draw() {
        enable();
    }

    @Override
    public void release() {
    }
}
