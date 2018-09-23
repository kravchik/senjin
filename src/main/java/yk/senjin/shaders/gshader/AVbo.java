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
 * must be able to upload partially changed data
 */

//TODO use instead ReflectionVBO
//TODO use instead IndexBufferShort
//TODO local ByteBuffer cache for continuous update
//TODO discard old and make new (to avoid stalls when continuous full updates are performed)
public class AVbo implements Vbo {//TODO remove implements
    public final Class inputType;
    public final int elementsCount;
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

    public AVbo(Class inputType, int elementsCount) {
        this.inputType = inputType;
        this.elementsCount = elementsCount;
        simpleTyped = ReflectionVBO.isSimpleType(inputType);
        elementSize = simpleTyped ? ReflectionVBO.getTypeSize(inputType) : ReflectionVBO.getComplexTypeSize(inputType);
    }

    public void addChange(List data) {
        //if (data.size() != elementsCount) BadException.die("expected full size data here"); //we can supply NOT full data
        addChange(data, 0).wholeData = true;
    }

    public Change addChange(ByteBuffer buffer) {
        Change change = new Change();
        change.data = buffer;
        change.wholeData = true;
        if (changes == null) changes = al();
        changes.add(change);
        dirty = true;
        return change;
    }

    public Change addChange(ByteBuffer buffer, int position) {
        Change change = new Change();
        change.data = buffer;
        change.changeFrom = position;
        if (changes == null) changes = al();
        changes.add(change);
        dirty = true;
        return change;
    }

    public Change addChange(int[] data, int position) {
        assertSize(position, data.length);
        if (inputType != int.class) BadException.die("buffer type differs from supplied data type: " + inputType + " " + int.class);
        ByteBuffer bb = BufferUtils.createByteBuffer(elementSize * data.length);
        ReflectionVBO.setDataInt(data, bb);
        bb.rewind();
        return addChange(bb, elementSize * position);
    }
    
    public Change addChange(List data, int position) {
        Class<?> aClass = data.get(0).getClass();
        assertSize(position, data.size());
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

    private void assertSize(int position, int elementsCount) {
        if (position + elementsCount > this.elementsCount)
            BadException.die("not matching size. Max: " + this.elementsCount + " position: " + position + " data.size(): " + elementsCount);
    }

    //override this to add type-aware serialization
    public void serializeData(ByteBuffer buffer, Object data) {
        if (simpleTyped) {
            if (inputType == float.class) ReflectionVBO.setDataFloat((float[])data, buffer);
            //else if (inputType == byte.class) ReflectionVBO.setDataByte((byte[])data, buffer);
            //else if (inputType == short.class) ReflectionVBO.setDataShort((short[])data, buffer);
            //else if (inputType == int.class) ReflectionVBO.setDataInt((int[])data, buffer);
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

    private AVbo initBuffer() {
        if (bufferInited) BadException.shouldNeverReachHere();

        glBindBuffer(GL_ARRAY_BUFFER, bufferId);
        glBufferData(GL_ARRAY_BUFFER, (long)elementSize * elementsCount, usage);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        bufferInited = true;
        return this;
    }
    
    private void partialUpload() {
        if (changes == null) BadException.shouldNeverReachHere();
        if (!bufferInited) initBuffer();
        glBindBuffer(GL_ARRAY_BUFFER, bufferId);
        for (Change change : changes) {
            if (change.wholeData) {
                //orphan old content, so driver can continue render from it while we are writing into the new buffer (or into old one if it is not occupied)
                //https://www.khronos.org/opengl/wiki/Vertex_Specification_Best_Practices
                glBufferData(GL_ARRAY_BUFFER, (long)elementSize * elementsCount, usage);
            }
            glBufferSubData(GL_ARRAY_BUFFER, change.changeFrom, change.data);
        }
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        dirty = false;
        changes = null;
    }

    @Override public void enable() {
        checkDirty();
        glBindBuffer(GL_ARRAY_BUFFER, bufferId);
    }
    @Override public void disable() {
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
    @Override public void release() {
        glDeleteBuffers(bufferId);
    }

    @Override public Class getInputType() {
        return inputType;
    }

    @Override public void checkDirty() {
        if (dirty) partialUpload();
    }

    public static class Change {
        int changeFrom;
        ByteBuffer data;
        boolean wholeData;
    }

}
