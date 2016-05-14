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
public class ReflectionVBO {
    public List data;
    public ByteBuffer buffer;
    public int bufferId = glGenBuffers();
    public Class inputType;

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
        if (data == null || data.size() != vertices.size()) buffer = BufferUtils.createByteBuffer(getSizeOfType(inputType) * vertices.size());
        data = vertices;

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
        upload();
    }

    private static final YMap<Class, Integer> type2size = hm();
    public static int getSizeOfType(Class clazz) {
        if (clazz == StandardFSInput.class || clazz == StandardVSInput.class || clazz == Object.class) return 0;

        Integer result = type2size.get(clazz);
        if (result == null) {
            result = 0;
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) if (!Modifier.isTransient(field.getModifiers())) {
                if (field.getType() == Vec2f.class) result += 4 * 2;
                else if (field.getType() == Vec3f.class) result += 4 * 3;
                else if (field.getType() == Vec4f.class) result += 4 * 4;
                else if (field.getType() == float.class) result += 4;
                else BadException.die("unknown type " + field.getType());
            }
            type2size.put(clazz, result);
        }
        return getSizeOfType(clazz.getSuperclass()) + result;
    }

    public void upload() {
        glBindBuffer(GL_ARRAY_BUFFER, bufferId);
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void release() {
        glDeleteBuffers(bufferId);
    }
}
