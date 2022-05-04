package yk.senjin.vbo;

import org.lwjgl.BufferUtils;
import yk.jcommon.fastgeom.Vec2f;
import yk.jcommon.fastgeom.Vec3f;
import yk.jcommon.fastgeom.Vec4f;
import yk.jcommon.utils.BadException;

import java.nio.ByteBuffer;
import java.util.List;

import static yk.jcommon.collections.YArrayList.al;

/**
 * Created by Yuri Kravchik on 01.10.18.
 */

public class AVboTyped extends AVbo {
    public final Class inputType;
    protected int elementsCount;
    public final int elementSize;
    public final boolean simpleTyped;

    public int getElementsCount() {
        return elementsCount;
    }

    public AVboTyped(Class inputType, int elementsCount) {
        this.inputType = inputType;
        this.elementsCount = elementsCount;
        simpleTyped = TypeUtils.isSimpleType(inputType);
        elementSize = simpleTyped ? TypeUtils.getTypeSize(inputType) : TypeUtils.getComplexTypeSize(inputType);
        size = elementsCount * elementSize;
    }

    public AVboTyped(List initial) {
        this(initial.get(0).getClass(), initial.size());
        addChange(initial);
    }

    public AVboTyped(Object... initial) {
        this(initial[0].getClass(), initial.length);
        addChange(al(initial));
    }

    @Override
    public void reloadResize(ByteBuffer data) {
        int capacity = data.capacity();
        if (capacity % elementSize != 0) BadException.die("wrong buffer size " + capacity + " for element size " + elementSize);
        this.elementsCount = capacity / elementSize;
        super.reloadResize(data);
    }

    /**
     * Sets new size with new data.
     * <br>
     * <li>Loses all previous changes.
     * <li>Input buffer can be smaller than a requested count.
     */
    public void reloadRecount(ByteBuffer data, int newCount) {
        int capacity = data.capacity();
        if (capacity % elementSize != 0) BadException.die("wrong buffer size " + capacity + " for element size " + elementSize);
        if (newCount * elementSize < capacity) BadException.die("wrong buffer size " + capacity + " for elements count " + newCount);
        size = newCount * elementSize;
        this.elementsCount = newCount;
        reload(data);
    }

    /**
     * Sets new size.
     * <br>
     * <li>Loses current buffer contents as it can be recreated.
     * <li>Loses all previous changes.
     */
    public void setCount(int count) {
        this.elementsCount = count;
        this.size = size * count * elementSize;
        Change change = new Change();
        change.recreate = true;
        changes = al(change);//we don't need previous changes as a whole data is updated
        dirty = true;
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
        if (data.size() > elementsCount) BadException.die("Too many elements (" + data.size() + " > " + elementsCount + ")");
        //if (data.size() != elementsCount) BadException.die("expected full size data here"); //we can supply NOT full data
        changes = null;//we don't need previous changes as a whole data is updated anyway
        addChange(data, 0).recreate = true;
    }

    /**
     * Upload from start.
     * <br>
     * <li>NOT changes size
     * <li>NOT streaming, could cause delay as has to wait until buffer is not occupied
     * <li>NOT loses buffer's content in other parts.
     * <li>ADDS changes, previous changes will be called before this.
     */
    public Change addChange(List data) {
        return addChange(data, 0);
    }

    /**
     * Upload at position without size change.
     * <br>
     * <li>NOT changes size
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
            if (inputType == float.class) TypeUtils.setDataFloat((float[])data, buffer);
            else if (inputType == Vec2f.class) TypeUtils.setDataVec2f((List)data, buffer);
            else if (inputType == Vec3f.class) TypeUtils.setDataVec3f((List)data, buffer);
            else if (inputType == Vec4f.class) TypeUtils.setDataVec4f((List)data, buffer);
            else if (inputType == byte.class) TypeUtils.setDataByte((List)data, buffer);
            else if (inputType == short.class) TypeUtils.setDataShort((List)data, buffer);
            else if (inputType == int.class) TypeUtils.setDataInt((List)data, buffer);
            else if (inputType == float.class) TypeUtils.setDataFloat((List)data, buffer);

        } else {
            TypeUtils.setData((List)data, buffer, inputType);
        }
    }

    public Class getInputType() {
        return inputType;
    }

}
