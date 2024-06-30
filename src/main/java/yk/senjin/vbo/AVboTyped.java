package yk.senjin.vbo;

import yk.jcommon.utils.BadException;

import java.nio.ByteBuffer;
import java.util.List;

import static yk.ycollections.YArrayList.al;

/**
 * Created by Yuri Kravchik on 01.10.18.
 */
public class AVboTyped extends AVbo {
    public final Class elementType;
    public final int elementSize;
    public final TypeUtils.DataSerializer serializer;
    protected int count;

    public int getCount() {
        return count;
    }

    public AVboTyped(Class elementType, int count) {
        this.elementType = elementType;
        this.count = count;
        elementSize = TypeUtils.getTypeSize(elementType);
        serializer = TypeUtils.getSerializer(elementType);
        size = count * elementSize;
    }

    public AVboTyped(Class elementType) {
        this.elementType = elementType;
        elementSize = TypeUtils.getTypeSize(elementType);
        serializer = TypeUtils.getSerializer(elementType);
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
        this.count = capacity / elementSize;
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
        int newSize = newCount * elementSize;
        if (newSize < capacity) BadException.die("wrong provided buffer size " + capacity + " for new size " + newSize);
        size = newSize;
        this.count = newCount;
        reload(data);
    }

    /**
     * Sets new size.
     * <br>
     * <li>Loses current buffer contents as it can be recreated.
     * <li>Loses all previous changes.
     */
    public void setCount(int count) {
        this.count = count;
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
        if (data.size() > count) BadException.die("Too many elements (" + data.size() + " > " + count + ")");
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
        Class<?> c = data.get(0).getClass();
        int elementsCount1 = data.size();
        if (position + elementsCount1 > this.count)
            BadException.die("not matching size. Max: " + this.count + " position: " + position + " data.size(): " + elementsCount1);
        if (c != elementType) BadException.die("buffer type differs from supplied data type: " + elementType + " " + c);
        return addChange(TypeUtils.createByteBuffer(data), elementSize * position);
    }

    public Class getElementType() {
        return elementType;
    }

}
