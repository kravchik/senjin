package yk.senjin.shaders.gshader;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBBufferObject;
import yk.jcommon.fastgeom.Vec2f;
import yk.jcommon.fastgeom.Vec3f;
import yk.jcommon.fastgeom.Vec4f;
import yk.jcommon.utils.BadException;
import yk.jcommon.utils.Reflector;
import yk.senjin.VertexStructureState;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;

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
    private int elementSize;

    public void bindToShader(GShader shader) {
        Method m = null;
        for (Method method : shader.vs.getClass().getDeclaredMethods()) {
            if (method.getName().equals("main") && !Modifier.isStatic(method.getModifiers()) && (method.getModifiers() & 0x00001000) == 0) {
                m = method;
                inputType = method.getParameterTypes()[0];
                break;
            }
        }
        if (m == null) throw BadException.die("can't find 'main' method in shader " + shader);
        VertexStructureState vss = new VertexStructureState(shader.shader, inputType, bufferId);
        shader.vbo2structure.put(this, vss);
        elementSize = vss.stride;
    }

    public void setData(List vertices) {
        if (data == null || data.size() != vertices.size()) buffer = BufferUtils.createByteBuffer(elementSize * vertices.size());
        data = vertices;

        Field[] fields = inputType.getFields();
        for (Object vertex : vertices) {
            if (vertex.getClass() != inputType) BadException.die("wrong input type: " + vertex + ", expected: " + inputType);
            for (Field field : fields) {
                if (Modifier.isTransient(field.getModifiers())) continue;
                Object value = Reflector.get(vertex, field);
                if (value instanceof Vec3f) {
                    Vec3f vec3f = (Vec3f) value;
                    buffer.putFloat(vec3f.x);
                    buffer.putFloat(vec3f.y);
                    buffer.putFloat(vec3f.z);
                } else if (value instanceof Vec2f) {
                    Vec2f vec2f = (Vec2f) value;
                    buffer.putFloat(vec2f.x);
                    buffer.putFloat(vec2f.y);
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

    public void upload() {
        glBindBuffer(GL_ARRAY_BUFFER, bufferId);
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void release() {
        ARBBufferObject.glDeleteBuffersARB(bufferId);
    }
}
