package yk.senjin.shaders.gshader;

import org.lwjgl.BufferUtils;
import yk.jcommon.collections.YList;
import yk.jcommon.collections.YMap;
import yk.jcommon.fastgeom.Vec2f;
import yk.jcommon.fastgeom.Vec3f;
import yk.jcommon.fastgeom.Vec4f;
import yk.jcommon.utils.BadException;
import yk.jcommon.utils.Reflector;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static yk.jcommon.collections.YArrayList.al;
import static yk.jcommon.collections.YHashMap.hm;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 09/06/15
 * Time: 20:30
 */
public class ReflectionVBO implements Vbo {
    public List data;
    public ByteBuffer buffer;
    public int bufferId = glGenBuffers();
    public Class inputType;
    public boolean dirty;

    //GL_STATIC_DRAW
    //GL_DYNAMIC_DRAW
    //GL_STREAM_DRAW
    public int usageType = GL_STATIC_DRAW;

    public ReflectionVBO() {
    }

    public ReflectionVBO(Object... data) {
        setData(al(data));
    }

    public ReflectionVBO(List data) {
        setData(data);
    }

    public void setData(List vertices) {
        inputType = vertices.get(0).getClass();
        if (data == null || data.size() != vertices.size()) this.buffer = BufferUtils.createByteBuffer(getComplexTypeSize(inputType) * vertices.size());
        data = vertices;
        setData(vertices, buffer, inputType);
        buffer.rewind();
        dirty = true;
    }

    public static void setData(List vertices, ByteBuffer buffer, Class inputType) {

        YList<Field> fields = ShaderGenerator.getFieldsForData(inputType);
        for (int i = 0, verticesSize = vertices.size(); i < verticesSize; i++) {
            Object vertex = vertices.get(i);
            if (vertex.getClass() != inputType) BadException.die("wrong input type: " + vertex + ", expected: " + inputType);
            for (int i1 = 0, fieldsSize = fields.size(); i1 < fieldsSize; i1++) {
                Field field = fields.get(i1);
                if (Modifier.isStatic(field.getModifiers())) continue;
                if (Modifier.isTransient(field.getModifiers())) continue;
                Object value = Reflector.get(vertex, field);
                if (value == null) throw BadException.die("null value in field " + field.getName());
                if (value instanceof Vec3f) {
                    Vec3f vec3f = (Vec3f) value;
                    buffer.putFloat(vec3f.x);
                    buffer.putFloat(vec3f.y);
                    buffer.putFloat(vec3f.z);
                } else if (value instanceof Vec2f) {
                    Vec2f vec2f = (Vec2f) value;
                    buffer.putFloat(vec2f.x);
                    buffer.putFloat(vec2f.y);
                } else if (value instanceof Float) {
                    buffer.putFloat((Float) value);
                } else if (value instanceof Vec4f) {
                    Vec4f vec4f = (Vec4f) value;
                    buffer.putFloat(vec4f.x);
                    buffer.putFloat(vec4f.y);
                    buffer.putFloat(vec4f.z);
                    buffer.putFloat(vec4f.w);
                } else {
                    throw BadException.die("unknown VS input field type: " + value.getClass());
                }
            }
        }
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

    private static final YMap<Class, Integer> type2size = hm();
    public static int getComplexTypeSize(Class clazz) {
        if (clazz == StandardFragmentData.class || clazz == StandardVertexData.class || clazz == Object.class) return 0;

        Integer result = type2size.get(clazz);
        if (result == null) {
            result = 0;
            for (Field field : clazz.getDeclaredFields()) if (!Modifier.isTransient(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) result += getTypeSize(field.getType());
            type2size.put(clazz, result);
        }
        return getComplexTypeSize(clazz.getSuperclass()) + result;
    }

    public  static int getTypeSize(Class<?> type) {
        if (type == Vec2f.class) return 4 * 2;
        else if (type == Vec3f.class) return 4 * 3;
        else if (type == Vec4f.class) return 4 * 4;
        else if (type == float.class) return 4;
        else if (type == int.class) return 4;
        else if (type == short.class) return 2;
        else if (type == byte.class) return 1;
        else throw BadException.die("unknown type " + type);
    }

    public static boolean isSimpleType(Class c) {
        if (c == Vec2f.class) return true;
        if (c == Vec3f.class) return true;
        if (c == Vec4f.class) return true;
        if (c == float.class) return true;
        if (c == int.class) return true;
        if (c == short.class) return true;
        if (c == byte.class) return true;
        return false;
    }

    public ReflectionVBO upload() {
        glBindBuffer(GL_ARRAY_BUFFER, bufferId);
        glBufferData(GL_ARRAY_BUFFER, buffer, usageType);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        dirty = false;
        return this;
    }

    public void release() {
        glDeleteBuffers(bufferId);
    }

    @Override public void enable() {
        glBindBuffer(GL_ARRAY_BUFFER, bufferId);
    }
    @Override public void disable() {
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    @Override public Class getInputType() {
        return inputType;
    }
    @Override public void checkDirty() {
        if (dirty) upload();
    }
}
