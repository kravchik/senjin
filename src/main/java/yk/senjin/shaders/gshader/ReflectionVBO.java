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
        dirty = true;
    }

    public static void setData(List vertices, ByteBuffer buffer, Class inputType) {

        YList<Field> fields = ShaderGenerator.getFieldsForData(inputType);
        for (Object vertex : vertices) {
            if (vertex.getClass() != inputType) BadException.die("wrong input type: " + vertex + ", expected: " + inputType);
            for (Field field : fields) {
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
        buffer.rewind();
    }

    public static void setDataVec2f(List<Vec2f> vertices, ByteBuffer buffer) {
        for (Vec2f value : vertices) {
            buffer.putFloat(value.x);
            buffer.putFloat(value.y);
        }
        buffer.rewind();
    }

    public static void setDataVec3f(List<Vec3f> vertices, ByteBuffer buffer) {
        for (Vec3f value : vertices) {
            buffer.putFloat(value.x);
            buffer.putFloat(value.y);
            buffer.putFloat(value.z);
        }
        buffer.rewind();
    }

    public static void setDataVec4f(List<Vec4f> vertices, ByteBuffer buffer) {
        for (Vec4f value : vertices) {
            buffer.putFloat(value.x);
            buffer.putFloat(value.y);
            buffer.putFloat(value.z);
            buffer.putFloat(value.w);
        }
        buffer.rewind();
    }

    public static void setDataFloat(List<Float> vertices, ByteBuffer buffer) {
        for (Float value : vertices) buffer.putFloat(value);
        buffer.rewind();
    }

    public static void setDataInt(List<Integer> vertices, ByteBuffer buffer) {
        for (int value : vertices) buffer.putInt(value);
        buffer.rewind();
    }

    public static void setDataShort(List<Short> vertices, ByteBuffer buffer) {
        for (short value : vertices) buffer.putShort(value);
        buffer.rewind();
    }

    public static void setDataByte(List<Byte> vertices, ByteBuffer buffer) {
        for (byte value : vertices) buffer.put(value);
        buffer.rewind();
    }

    private static final YMap<Class, Integer> type2size = hm();
    public static int getComplexTypeSize(Class clazz) {
        if (clazz == StandardFragmentData.class || clazz == StandardVertexData.class || clazz == Object.class) return 0;

        Integer result = type2size.get(clazz);
        if (result == null) {
            result = 0;
            for (Field field : clazz.getDeclaredFields()) if (!Modifier.isTransient(field.getModifiers())) result += getTypeSize(field.getType());
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
    
    @Override public Class getInputType() {
        return inputType;
    }
    @Override public void checkDirty() {
        if (dirty) upload();
    }
}
