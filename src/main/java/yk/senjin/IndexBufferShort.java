package yk.senjin;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.ShortBuffer;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_UNSIGNED_SHORT;
import static org.lwjgl.opengl.GL15.*;

/**
 * Created by: Yuri Kravchik Date: 31/10/2007 Time: 18:17:39
 * //TODO get rid
 */
public class IndexBufferShort extends AbstractState {
    public int bufferId = glGenBuffers();
    public boolean dirty;
    private ShortBuffer indexBuffer;
    /**
     * Specifies what kind of primitives to render. Symbolic constants
     * GL_POINTS, GL_LINE_STRIP, GL_LINE_LOOP, GL_LINES, GL_TRIANGLE_STRIP,
     * GL_TRIANGLE_FAN, GL_TRIANGLES, GL_QUAD_STRIP, GL_QUADS, and GL_POLYGON
     * are accepted.
     */
    private final int primitiveType;

    //GL_STATIC_DRAW
    //GL_DYNAMIC_DRAW
    //GL_STREAM_DRAW
    public int usageType = GL_STATIC_DRAW;

    public IndexBufferShort(final int primitiveType, List<? extends Number> indices) {
        this.primitiveType = primitiveType;
        setData(indices);
    }

    public void setData(List<? extends Number> indices) {
        this.indexBuffer = BufferUtils.createShortBuffer(indices.size());
        for (Number aShort : indices) indexBuffer.put(aShort.shortValue());
        indexBuffer.rewind();
        dirty = true;
    }

    public IndexBufferShort upload() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, usageType);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        dirty = false;
        return this;
    }

    public static IndexBufferShort simple(int count, int primitiveType) {
        IndexBufferShort result = new IndexBufferShort(primitiveType);
        result.indexBuffer = BufferUtils.createShortBuffer(count);
        for (int i = 0; i < count; i++) result.indexBuffer.put((short) i);
        result.indexBuffer.rewind();
        result.upload();
        return result;
    }

    public IndexBufferShort(final int primitiveType) {
        this.primitiveType = primitiveType;
    }

    public int getCount() {
        return indexBuffer.limit();
    }

    public int getPrimitiveType() {
        return primitiveType;
    }

    public void disable() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    public void enable() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferId);
        GL11.glDrawElements(primitiveType, indexBuffer.limit(), GL_UNSIGNED_SHORT, 0);
//        GL12.glDrawRangeElements(primitiveType, 0, indexBuffer.limit(), indexBuffer);
    }

    public void draw() {
        enable();
    }

    @Override
    public void release() {
        glDeleteBuffers(bufferId);
    }
}
