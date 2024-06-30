package yk.senjin.collections;

import yk.jcommon.utils.BadException;
import yk.senjin.vbo.AVboIntIndices;
import yk.senjin.vbo.AVboTyped;
import yk.ycollections.YArrayList;
import yk.ycollections.YList;

import java.nio.ByteBuffer;

import static org.lwjgl.BufferUtils.createByteBuffer;
import static yk.jcommon.utils.BadException.shouldNeverReachHere;
import static yk.jcommon.utils.MyMath.max;
import static yk.ycollections.YArrayList.al;

/**
 * Created by Yuri Kravchik on 25.03.18.
 * former "DynamicBatch"
 * Reworked 29.06.24
 *
 * TODO .reuseIndexLocalBuffers()
 * TODO .reuseIndexGlobalBuffer()
 * TODO .reuseVertexGlobalBuffer()
 */
public class GeometryList {
    public final AVboTyped vboVertices;
    public final AVboIntIndices vboIndices;

    YArrayList<ElementInstance> instances = al();
    YArrayList<ElementInstance> newInstances = al();
    BufferInfo verticesInfo;
    BufferInfo indicesInfo;

    public GeometryList(AVboTyped vboVertices, AVboIntIndices vboIndices) {
        this.vboVertices = vboVertices;
        this.vboIndices = vboIndices;
        verticesInfo = new BufferInfo(vboVertices.getCount(), vboVertices.elementSize);
        indicesInfo = new BufferInfo(vboIndices.getCount(), vboIndices.elementSize);
    }

    private boolean reloadVertices;
    private boolean reloadIndices;

    public void reorder(YList<ElementInstance> instances) {
        //TODO implement
        BadException.notImplemented();
    }

    public void flush() {
        //TODO remove by command, not by reloading everything if 'removeFast' was used
        //  or reload not everything, if 'remove' was used
        if (indicesInfo.freeTail != indicesInfo.freeTotal) reloadIndices = true;

        //grow
        if (verticesInfo.freeTail < 0) {
            verticesInfo.increaseCount((int) max(verticesInfo.size * 0.5f, -verticesInfo.freeTotal));
            reloadVertices = true;
        }

        if (indicesInfo.freeTail < 0) {
            indicesInfo.increaseCount((int) max(indicesInfo.size * 0.5f, -indicesInfo.freeTotal));
            reloadIndices = true;
        }

        //shrink
        if (verticesInfo.freeTotal > verticesInfo.size * 0.7f) {
            verticesInfo.setCount(max(verticesInfo.getTotal(), verticesInfo.getTotal() * 2));
            reloadVertices = true;
            System.out.println("shrink vertices to " + verticesInfo.size);
        }
        if (indicesInfo.freeTotal > indicesInfo.size * 0.7f) {
            indicesInfo.setCount(max(indicesInfo.getTotal(), indicesInfo.getTotal() * 2));
            reloadIndices = true;
            System.out.println("shrink indices to " + indicesInfo.size);
        }

        reloadIndices |= reloadVertices;
        
        //add change commands if no buffer change
        for (int i = 0; i < newInstances.size(); i++) {
            ElementInstance instance = newInstances.get(i);
            if (!reloadVertices) {
                vboVertices.addChange(instance.vertexBuffer, vboVertices.elementSize * instance.verticesAt);
            }
            if (!reloadIndices) {
                ByteBuffer buffer = createByteBuffer((instance.indices.length) * indicesInfo.bytesPerElement);
                addIndices(instance, instance.indicesAt, buffer);
                vboIndices.addChange(buffer, vboIndices.elementSize * instance.indicesAt);
                buffer.rewind();
            }
        }

        newInstances.clear();

        if (reloadVertices) compactVertices();
        if (reloadIndices) compactIndices();
        reloadVertices = false;
        reloadIndices = false;
    }

    public void removeFast(int i) {
        removeFastImpl(i, instances.get(i));
    }

    public void removeFast(ElementInstance instance) {
        removeFastImpl(instances.indexOf(instance), instance);
    }

    public void remove(int i) {
        removeImpl(i, instances.get(i));
    }

    public void remove(ElementInstance instance) {
        removeImpl(instances.indexOf(instance), instance);
    }

    private void removeFastImpl(int i, ElementInstance instance) {
        instances.removeFast(i);
        verticesInfo.countRemove(instance.verticesAt, instance.verticesCount);
        indicesInfo.countRemove(instance.indicesAt, instance.indices.length);
    }

    private void removeImpl(int i, ElementInstance instance) {
        instances.remove(i);
        verticesInfo.countRemove(instance.verticesAt, instance.verticesCount);
        indicesInfo.countRemove(instance.indicesAt, instance.indices.length);
    }

    public ElementInstance add(int[] indices, YList vertices) {
        ByteBuffer bb = createByteBuffer(vboVertices.elementSize * vertices.size());
        vboVertices.serializer.serialize(bb, vertices);
        bb.rewind();
        return add(indices, vertices.size(), bb);
    }

    public ElementInstance add(int[] indices, int verticesCount, ByteBuffer vertexBuffer) {
        ElementInstance instance = new ElementInstance(this, verticesInfo.getFirstFree(), indicesInfo.getFirstFree(),
            indices, verticesCount, vertexBuffer);
        verticesInfo.countAddTail(instance.verticesCount);
        indicesInfo.countAddTail(instance.indices.length);
        newInstances.add(instance);
        instances.add(instance);
        return instance;
    }

    private void compactVertices() {
        ByteBuffer buffer = createByteBuffer((verticesInfo.size - verticesInfo.freeTotal)
            * verticesInfo.bytesPerElement);
        int cur = 0;
        for (int instanceIndex = 0, instancesSize = instances.size(); instanceIndex < instancesSize; instanceIndex++) {
            ElementInstance instance = instances.get(instanceIndex);
            instance.verticesAt = cur;
            cur += instance.verticesCount;

            buffer.put(instance.vertexBuffer);
            instance.vertexBuffer.rewind();
        }
        verticesInfo.freeTail = verticesInfo.size - cur;
        if (verticesInfo.freeTail != verticesInfo.freeTotal) throw shouldNeverReachHere();
        buffer.rewind();
        //this should make fast update (with orphaning) + only needed beginning
        vboVertices.reloadRecount(buffer, verticesInfo.size);
    }

    private void compactIndices() {
        ByteBuffer buffer = createByteBuffer((indicesInfo.size - indicesInfo.freeTotal)
            * indicesInfo.bytesPerElement);
        int nextFree = 0;
        for (int instanceIndex = 0, instancesSize = instances.size(); instanceIndex < instancesSize; instanceIndex++) {
            nextFree = addIndices(instances.get(instanceIndex), nextFree, buffer);
        }
        indicesInfo.freeTail = indicesInfo.size - nextFree;
        if (indicesInfo.freeTail != indicesInfo.freeTotal) throw shouldNeverReachHere();
        buffer.rewind();
        //this should make fast update (with orphaning) + only needed beginning
        vboIndices.reloadRecount(buffer, indicesInfo.size);
    }

    private int addIndices(ElementInstance instance, int indicesAt, ByteBuffer buffer) {
        for (int i = 0, indicesLength = instance.indices.length; i < indicesLength; i++) {
            buffer.putInt(instance.indices[i] + instance.verticesAt);
        }
        instance.indicesAt = indicesAt;
        return indicesAt + instance.indices.length;
    }

    public static class ElementInstance {
        //TODO index
        //TODO valid
        public GeometryList parent;
        int verticesAt;
        int indicesAt;

        public int[] indices;
        public int verticesCount;
        public ByteBuffer vertexBuffer;

        public ElementInstance(GeometryList parent, int verticesAt, int indicesAt,
                               int[] indices, int verticesCount, ByteBuffer vertexBuffer) {
            this.parent = parent;
            this.verticesAt = verticesAt;
            this.indicesAt = indicesAt;
            this.indices = indices;
            this.verticesCount = verticesCount;
            this.vertexBuffer = vertexBuffer;
        }
    }

    public static class BufferInfo {
        public int bytesPerElement;

        public int freeTotal;
        public int freeTail;
        public int size;

        public BufferInfo(int elementsCount, int bytesPerElement) {
            this.bytesPerElement = bytesPerElement;
            freeTotal = elementsCount;
            freeTail = elementsCount;
            size = elementsCount;
        }

        public void increaseCount(int count) {
            size += count;
            freeTail += count;
            freeTotal += count;
        }

        public void setCount(int newCount) {
            increaseCount(newCount - size);
        }

        public void countAddTail(int elements) {
            freeTail -= elements;
            freeTotal -= elements;
        }

        public int getFirstFree() {
            return size - freeTail;
        }

        public int getTotal() {
            return size - freeTotal;
        }

        private void countRemove(int at, int elements) {
            if (at + elements == size - freeTail) {
                freeTail += elements;
                freeTotal += elements;
            } else if (at + elements > size - freeTail) {
                shouldNeverReachHere();
            } else {
                freeTotal += elements;
            }
        }

        @Override
        public String toString() {
            return "BufferInfo{bytesPerElement=" + bytesPerElement + ", freeTotal=" + freeTotal
                + ", freeTail=" + freeTail + ", size=" + size + '}';
        }
    }

}
