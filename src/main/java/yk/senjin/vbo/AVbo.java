package yk.senjin.vbo;

import yk.jcommon.utils.BadException;
import yk.senjin.State;
import yk.ycollections.YList;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL15.*;
import static yk.ycollections.YArrayList.al;

/**
 * Created by Yuri Kravchik on 25.03.18.
 */
//TODO local ByteBuffer cache for continuous update
public class AVbo implements State {

    //GL_ARRAY_BUFFER, GL_ELEMENT_ARRAY_BUFFER, ...
    protected int bufferType = GL_ARRAY_BUFFER;
    protected boolean enabled;
    public final int bufferId = glGenBuffers();
    //GL_STATIC_DRAW, GL_DYNAMIC_DRAW, GL_STREAM_DRAW
    public int usage = GL_DYNAMIC_DRAW;

    public boolean dirty;
    protected YList<AVboTyped.Change> changes;
    private boolean bufferInited;

    protected int size;

    public byte[] debugBuffer;

    /**
     * Sets new size with new data.
     * <br>
     * <li>Loses all previous changes.
     */
    public void reloadResize(ByteBuffer data) {
        this.size = data.capacity();
        reload(data);
    }

    /**
     * Whole upload but without size change
     * <br>
     * <li>Streaming, fast way to reload (<a href=https://www.khronos.org/opengl/wiki/Buffer_Object_Streaming>orphaning</a>)
     * <li>NOT changes size
     * <li>Loses rest of the buffer as it can be recreated.
     * <li>Loses all previous changes.
     */
    public AVboTyped.Change reload(ByteBuffer buffer) {
        if (buffer.capacity() > size) BadException.die("too large buffer (" + buffer.capacity() + ") should be <= " + size);
        AVboTyped.Change change = new AVboTyped.Change();
        change.data = buffer;
        change.recreate = true;
        changes = al(change);//we don't need previous changes as a whole data is updated
        dirty = true;
        return change;
    }

    /**
     * Upload at position without size change.
     * <br>
     * <li>NOT streaming, could cause delay as has to wait until buffer is not occupied
     * <li>NOT loses buffer's content in other parts.
     * <li>ADDS changes, previous changes will be called before this.
     */
    public AVboTyped.Change addChange(ByteBuffer buffer, int position) {
        if (position + buffer.capacity() > size) BadException.die("out of bounds (" + position + " + " + buffer.capacity()
                + ") should be <= " + size);
        AVboTyped.Change change = new AVboTyped.Change();
        change.data = buffer;
        change.position = position;
        if (changes == null) changes = al();
        changes.add(change);
        dirty = true;
        return change;
    }

    private void flush() {
        if (changes == null) BadException.shouldNeverReachHere();
        if (!enabled) glBindBuffer(bufferType, bufferId);

        if (!changes.get(0).recreate && !bufferInited) {
            glBufferData(bufferType, size, usage);
            bufferInited = true;
        }

        for (AVboTyped.Change change : changes) {
            if (change.recreate) {
                //orphan old content, so driver can continue render from it while we are writing into the new buffer (or into old one if it is not occupied)
                //https://www.khronos.org/opengl/wiki/Buffer_Object_Streaming
                glBufferData(bufferType, size, usage);
                bufferInited = true;
                if (debugBuffer != null) debugBuffer = new byte[size];
            }
            if (size > 0 && change.data != null) {
                glBufferSubData(bufferType, change.position, change.data);
                if (debugBuffer != null) {
                    int i = change.position;
                    while (change.data.hasRemaining()) debugBuffer[i++] = change.data.get();
                    change.data.rewind();
                }
            }
        }
        if (!enabled) glBindBuffer(bufferType, 0);
        dirty = false;
        changes = null;
    }

    @Override public void enable() {
        enabled = true;
        glBindBuffer(bufferType, bufferId);
        if(size == 0) BadException.die("shouldn't enable empty buffers check dirty before this call");
        checkDirty();
    }
    @Override public void disable() {
        glBindBuffer(bufferType, 0);
        enabled = false;
    }
    @Override public void release() {
        glDeleteBuffers(bufferId);
    }

    public void checkDirty() {
        if (dirty) flush();
    }

    public static class Change {
        int position;
        /**
         * If null, then seems like we just want to resize (recreate should be true, changeFrom should be 0)
         */
        ByteBuffer data;
        boolean recreate;
    }

}
