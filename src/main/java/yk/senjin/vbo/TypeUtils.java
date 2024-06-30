package yk.senjin.vbo;

import org.lwjgl.BufferUtils;
import yk.jcommon.fastgeom.Vec2f;
import yk.jcommon.fastgeom.Vec3f;
import yk.jcommon.fastgeom.Vec4f;
import yk.jcommon.utils.BadException;
import yk.jcommon.utils.Reflector;
import yk.senjin.shaders.gshader.StandardFragmentData;
import yk.senjin.shaders.gshader.StandardVertexData;
import yk.ycollections.YList;
import yk.ycollections.YMap;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.List;

import static yk.ycollections.YArrayList.al;
import static yk.ycollections.YHashMap.hm;

/**
 * Created by Yuri Kravchik on 02.10.18.
 */
public class TypeUtils {
    private static final YMap<Class, Integer> TYPE2SIZE_CACHE = hm();

    private static final YMap<Class, YList<Field>> CACHE =
        hm(StandardFragmentData.class, al(), Object.class, al(), StandardVertexData.class, al());

    private static final YMap<Class, DataSerializer> SERIALIZERS_CACHE = hm(
        short.class, (DataSerializer<Short>) (buffer, el) -> buffer.putShort(el),
        int.class, (DataSerializer<Integer>) (buffer, el) -> buffer.putInt(el),
        float.class, (DataSerializer<Float>) (buffer, el) -> buffer.putFloat(el),
        Float.class, (DataSerializer<Float>) (buffer, el) -> buffer.putFloat(el),
        Vec2f.class, (DataSerializer<Vec2f>) (buffer, el) -> {
            buffer.putFloat(el.x);
            buffer.putFloat(el.y);
        },
        Vec3f.class, (DataSerializer<Vec3f>) (buffer, el) -> {
            buffer.putFloat(el.x);
            buffer.putFloat(el.y);
            buffer.putFloat(el.z);
        },
        Vec4f.class, (DataSerializer<Vec4f>) (buffer, el) -> {
            buffer.putFloat(el.x);
            buffer.putFloat(el.y);
            buffer.putFloat(el.z);
            buffer.putFloat(el.w);
        }
    );

    public static void setData(ByteBuffer buffer, List vertices) {
        getSerializer(vertices.get(0).getClass()).serialize(buffer, vertices);
    }

    public static void setDataVec2f(List<Vec2f> vertices, ByteBuffer buffer) {
        for (int i = 0, verticesSize = vertices.size(); i < verticesSize; i++) {
            Vec2f value = vertices.get(i);
            buffer.putFloat(value.x);
            buffer.putFloat(value.y);
        }
    }

    public static void setDataVec3f(List<Vec3f> vertices, ByteBuffer buffer) {
        for (int i = 0, verticesSize = vertices.size(); i < verticesSize; i++) {
            Vec3f value = vertices.get(i);
            buffer.putFloat(value.x);
            buffer.putFloat(value.y);
            buffer.putFloat(value.z);
        }
    }

    public static void setDataVec4f(List<Vec4f> vertices, ByteBuffer buffer) {
        for (int i = 0, verticesSize = vertices.size(); i < verticesSize; i++) {
            Vec4f value = vertices.get(i);
            buffer.putFloat(value.x);
            buffer.putFloat(value.y);
            buffer.putFloat(value.z);
            buffer.putFloat(value.w);
        }
    }

    public static void setDataFloat(List<Float> data, ByteBuffer buffer) {
        for (int i = 0, verticesSize = data.size(); i < verticesSize; i++) {
            buffer.putFloat(data.get(i));
        }
    }

    public static void setDataFloat(float[] data, ByteBuffer buffer) {
        for (int i = 0, verticesSize = data.length; i < verticesSize; i++) {
            buffer.putFloat(data[i]);
        }
    }

    public static void setDataInt(List<Integer> data, ByteBuffer buffer) {
        for (int i = 0, verticesSize = data.size(); i < verticesSize; i++) {
            buffer.putInt(data.get(i));
        }
    }

    public static void setDataInt(int[] data, ByteBuffer buffer) {
        for (int i = 0, verticesSize = data.length; i < verticesSize; i++) {
            buffer.putInt(data[i]);
        }
    }

    public static void setDataShort(List<Short> data, ByteBuffer buffer) {
        for (int i = 0, verticesSize = data.size(); i < verticesSize; i++) {
            buffer.putShort(data.get(i));
        }
    }

    public static void setDataShort(short[] data, ByteBuffer buffer) {
        for (int i = 0, verticesSize = data.length; i < verticesSize; i++) {
            buffer.putShort(data[i]);
        }
    }

    public static void setDataByte(List<Byte> data, ByteBuffer buffer) {
        for (int i = 0, verticesSize = data.size(); i < verticesSize; i++) {
            buffer.put(data.get(i));
        }
    }

    public static void setDataByte(byte[] data, ByteBuffer buffer) {
        for (int i = 0, verticesSize = data.length; i < verticesSize; i++) {
            buffer.put(data[i]);
        }
    }

    public static int getTypeSize(Class clazz) {
        if (clazz == null || clazz == StandardFragmentData.class || clazz == StandardVertexData.class || clazz == Object.class) return 0;

        Integer result = TYPE2SIZE_CACHE.get(clazz);
        if (result == null) {
            result = getSimpleTypeSize(clazz);
            if (result == 0) {
                result = getFieldsForData(clazz).reduce(0, (i, f) -> i + getTypeSize(f.getType()));
            }
            TYPE2SIZE_CACHE.put(clazz, result);
        }
        return getTypeSize(clazz.getSuperclass()) + result;
    }

    public  static int getSimpleTypeSize(Class<?> type) {
        if (type == float.class) return 4;
        else if (type == int.class) return 4;
        else if (type == short.class) return 2;
        else if (type == byte.class) return 1;
        else return 0;
    }

    public static ByteBuffer createByteBuffer(List data) {
        int capacity = getTypeSize(data.get(0).getClass()) * data.size();
        ByteBuffer bb = BufferUtils.createByteBuffer(capacity);
        setData(bb, data);
        bb.rewind();
        return bb;
    }

    public static YList<Field> getFieldsForData(Class inputClass) {
        if (inputClass.isArray()) return getFieldsForData(inputClass.getComponentType());
        YList<Field> result = CACHE.get(inputClass);
        if (result == null) {
            result = getFieldsForData(inputClass.getSuperclass())
                .withAll(al(inputClass.getDeclaredFields()).filter(f ->
                    (f.getModifiers() & (Modifier.STATIC | Modifier.TRANSIENT)) == 0));
            CACHE.put(inputClass, result);
        }
        return result;
    }

    public static DataSerializer getSerializer(Class inputClass) {
        DataSerializer result = SERIALIZERS_CACHE.get(inputClass);
        if (result == null) {
            result = getObjectSerializer(inputClass);
            SERIALIZERS_CACHE.put(inputClass, result);
        }
        return result;
    }

    private static DataSerializer getObjectSerializer(Class inputClass) {
        YList<DataSerializer> fieldSerizaliers = getFieldsForData(inputClass)
            .map(f -> {
                DataSerializer ds = getSerializer(f.getType());
                return (buffer, element) -> {
                    Object value = Reflector.get(element, f);
                    if (value == null) throw BadException.die("null value in field " + f.getName());
                    ds.serialize(buffer, value);
                };
            });

        return (DataSerializer<YList>) (buffer, ee) -> {
            for (int i = 0, eeSize = ee.size(); i < eeSize; i++) {
                Object e = ee.get(i);
                if (e == null) BadException.die("null element");
                if (e.getClass() != inputClass) BadException.die("wrong input type: "
                    + e.getClass() + ", expected: " + inputClass);
                for (int j = 0, fieldsSize = fieldSerizaliers.size(); j < fieldsSize; j++) {
                    fieldSerizaliers.get(j).serialize(buffer, e);
                }
            }
        };
    }

    public interface DataSerializer<T> {
        void serialize(ByteBuffer buffer, T element);
    }
}
