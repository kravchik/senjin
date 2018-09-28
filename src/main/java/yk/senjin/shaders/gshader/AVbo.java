package yk.senjin.shaders.gshader;

import org.lwjgl.BufferUtils;
import yk.jcommon.collections.YList;
import yk.jcommon.fastgeom.Vec2f;
import yk.jcommon.fastgeom.Vec3f;
import yk.jcommon.fastgeom.Vec4f;
import yk.jcommon.utils.BadException;

import java.nio.ByteBuffer;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static yk.jcommon.collections.YArrayList.al;

/**
 * Created by Yuri Kravchik on 25.03.18.
 */

//TODO use instead ReflectionVBO
//TODO use instead IndexBufferShort
//TODO local ByteBuffer cache for continuous update
public class AVbo implements Vbo {//TODO remove implements
    public final Class inputType;
    //GL_ARRAY_BUFFER
    //GL_ELEMENT_ARRAY_BUFFER
    //...
    protected int bufferType = GL_ARRAY_BUFFER;
    protected int elementsCount;
    protected boolean enabled;
    public final boolean simpleTyped;
    public final int elementSize;
    public final int bufferId = glGenBuffers();
    //GL_STATIC_DRAW
    //GL_DYNAMIC_DRAW
    //GL_STREAM_DRAW
    public int usage = GL_DYNAMIC_DRAW;

    public boolean dirty;
    private YList<Change> changes;
    private boolean bufferInited;

    public byte[] debugBuffer;

    public int getElementsCount() {
        return elementsCount;
    }

    public AVbo(Class inputType, int elementsCount) {
        this.inputType = inputType;
        this.elementsCount = elementsCount;
        simpleTyped = ReflectionVBO.isSimpleType(inputType);
        elementSize = simpleTyped ? ReflectionVBO.getTypeSize(inputType) : ReflectionVBO.getComplexTypeSize(inputType);
    }

    /**
     * Sets new size with new data.
     * <br>
     * <li>Loses all previous changes.
     */
    public void setNewSize(ByteBuffer data) {
        int capacity = data.capacity();
        if (capacity % elementSize != 0) BadException.die("wrong buffer size " + capacity + " for element size " + elementSize);
        this.elementsCount = capacity / elementSize;
        reload(data);
    }

    /**
     * Sets new size.
     * <br>
     * <li>Loses current buffer contents as it can be recreated.
     * <li>Loses all previous changes.
     */
    public void setNewItemsCount(int count) {
        this.elementsCount = count;
        Change change = new Change();
        change.recreate = true;
        changes = al(change);//we don't need previous changes as a whole data is updated
        dirty = true;
    }

    /**
     * Whole upload but without size change
     * <br>
     * <li>Streaming, fast way to reload (<a href=https://www.khronos.org/opengl/wiki/Buffer_Object_Streaming>orphaning</a>)
     * <li>NOT changes size
     * <li>Loses rest of the buffer as it can be recreated.
     * <li>Loses all previous changes.
     */
    public Change reload(ByteBuffer buffer) {
        if (buffer.capacity() > elementSize * elementsCount) BadException.die("too large buffer (" + buffer.capacity() + ") should be <= " + elementSize * elementsCount);
        Change change = new Change();
        change.data = buffer;
        change.recreate = true;
        changes = al(change);//we don't need previous changes as a whole data is updated
        dirty = true;
        return change;
    }

    /**
     * Sets some data at the start of the buffer.
     * <br>
     * <li>Streaming, fast way to reload (<a href=https://www.khronos.org/opengl/wiki/Buffer_Object_Streaming>orphaning</a>)
     * <li>NOT changes size
     * <li>Loses rest of the buffer as it can be recreated.
     * <li>Loses all previous changes.
     */
    public void reload(List data) {
        //if (data.size() != elementsCount) BadException.die("expected full size data here"); //we can supply NOT full data
        changes = null;//we don't need previous changes as a whole data is updated anyway
        addChange(data, 0).recreate = true;
    }

    /**
     * Upload at position without size change.
     * <br>
     * <li>NOT streaming, could cause delay as has to wait until buffer is not occupied
     * <li>NOT loses buffer's content in other parts.
     * <li>ADDS changes, previous changes will be called before this.
     */
    public Change addChange(ByteBuffer buffer, int position) {
        if (position + buffer.capacity() > elementSize * elementsCount) BadException.die("out of bounds (" + position + " + " + buffer.capacity()
                + ") should be <= " + elementSize * elementsCount);
        Change change = new Change();
        change.data = buffer;
        change.position = position;
        if (changes == null) changes = al();
        changes.add(change);
        dirty = true;
        return change;
    }

    //public Change addChange(int[] data, int position) {
    //    assertSize(position, data.length);
    //    if (inputType != int.class) BadException.die("buffer type differs from supplied data type: " + inputType + " " + int.class);
    //    ByteBuffer bb = BufferUtils.createByteBuffer(elementSize * data.length);
    //    ReflectionVBO.setDataInt(data, bb);
    //    bb.rewind();
    //    return addChange(bb, elementSize * position);
    //}

    /**
     * Upload at position without size change.
     * <br>
     * <li>NOT streaming, could cause delay as has to wait until buffer is not occupied
     * <li>NOT loses buffer's content in other parts.
     * <li>ADDS changes, previous changes will be called before this.
     */
    public Change addChange(List data, int position) {
        Class<?> aClass = data.get(0).getClass();
        int elementsCount1 = data.size();
        if (position + elementsCount1 > this.elementsCount)
            BadException.die("not matching size. Max: " + this.elementsCount + " position: " + position + " data.size(): " + elementsCount1);
        if (simpleTyped) {
            if (aClass == Short.class && inputType != short.class
                || aClass == Byte.class && inputType != byte.class
                || aClass == Integer.class && inputType != int.class
                || aClass == Float.class && inputType != float.class
                    )
                BadException.die("buffer type differs from supplied data type: " + inputType + " " + aClass);
        } else {
            if (aClass != inputType) BadException.die("buffer type differs from supplied data type: " + inputType + " " + aClass);
        }

        ByteBuffer bb = BufferUtils.createByteBuffer(elementSize * data.size());
        serializeData(bb, data);
        bb.rewind();
        return addChange(bb, elementSize * position);
    }

    //override this to add type-aware serialization
    public void serializeData(ByteBuffer buffer, Object data) {
        if (simpleTyped) {
            if (inputType == float.class) ReflectionVBO.setDataFloat((float[])data, buffer);
            else if (inputType == Vec2f.class) ReflectionVBO.setDataVec2f((List)data, buffer);
            else if (inputType == Vec3f.class) ReflectionVBO.setDataVec3f((List)data, buffer);
            else if (inputType == Vec4f.class) ReflectionVBO.setDataVec4f((List)data, buffer);
            else if (inputType == byte.class) ReflectionVBO.setDataByte((List)data, buffer);
            else if (inputType == short.class) ReflectionVBO.setDataShort((List)data, buffer);
            else if (inputType == int.class) ReflectionVBO.setDataInt((List)data, buffer);
            else if (inputType == float.class) ReflectionVBO.setDataFloat((List)data, buffer);

        } else {
            ReflectionVBO.setData((List)data, buffer, inputType);
        }
    }

    private void flush() {
        if (changes == null) BadException.shouldNeverReachHere();
        if (!enabled) glBindBuffer(bufferType, bufferId);

        if (!changes.get(0).recreate && !bufferInited) {
            glBufferData(bufferType, (long)elementSize * elementsCount, usage);
            bufferInited = true;
        }

        for (Change change : changes) {
            if (change.recreate) {
                //orphan old content, so driver can continue render from it while we are writing into the new buffer (or into old one if it is not occupied)
                //https://www.khronos.org/opengl/wiki/Buffer_Object_Streaming
                glBufferData(bufferType, (long)elementSize * elementsCount, usage);
                bufferInited = true;
                if (debugBuffer != null) debugBuffer = new byte[elementSize * elementsCount];
            }
            if (elementsCount > 0 && change.data != null) {
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
        if(elementSize == 0) BadException.die("shouldn't enable empty buffers check dirty before this call");
        checkDirty();
    }
    @Override public void disable() {
        glBindBuffer(bufferType, 0);
        enabled = false;
    }
    @Override public void release() {
        glDeleteBuffers(bufferId);
    }

    @Override public Class getInputType() {
        return inputType;
    }

    @Override public void checkDirty() {
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
