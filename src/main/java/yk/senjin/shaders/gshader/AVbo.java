package yk.senjin.shaders.gshader;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Util;
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

public class AVbo implements Vbo {//TODO remove implements ?
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
        if (data.size() != elementsCount) BadException.die("expected full size data here");
        addChange(data, 0);
    }

    public void addChange(List data, int displacement) {
        if (displacement + data.size() > elementsCount)
            BadException.die("not matching size. Max: " + elementsCount + " displacement: " + displacement + " data.size(): " + data.size());
        if (data.get(0).getClass() != inputType) BadException.die("buffer type differs from supplied data type: " + inputType + " " + data.get(0).getClass());

        Change change = new Change();
        change.data = BufferUtils.createByteBuffer(elementSize * data.size());
        serializeData(change.data, data);
        change.changeFrom = elementSize * displacement;
        if (changes == null) changes = al();
        changes.add(change);
        dirty = true;
    }

    //override this to add type-aware serialization
    public void serializeData(ByteBuffer buffer, List data) {
        if (simpleTyped) {
            if (inputType == Vec2f.class) ReflectionVBO.setDataVec2f(data, buffer);
            if (inputType == Vec3f.class) ReflectionVBO.setDataVec3f(data, buffer);
            if (inputType == Vec4f.class) ReflectionVBO.setDataVec4f(data, buffer);
            if (inputType == byte.class) ReflectionVBO.setDataByte(data, buffer);
            if (inputType == short.class) ReflectionVBO.setDataShort(data, buffer);
            if (inputType == int.class) ReflectionVBO.setDataInt(data, buffer);
            if (inputType == float.class) ReflectionVBO.setDataFloat(data, buffer);
        } else {
            ReflectionVBO.setData(data, buffer, inputType);
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
        for (Change change : changes) {
            glBindBuffer(GL_ARRAY_BUFFER, bufferId);
            Util.checkGLError();
            glBufferSubData(GL_ARRAY_BUFFER, change.changeFrom, change.data);
            Util.checkGLError();
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            Util.checkGLError();
        }
        dirty = false;
        changes = null;
    }

    @Override public void enable() {
        glBindBuffer(GL_ARRAY_BUFFER, bufferId);
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
    }

}
