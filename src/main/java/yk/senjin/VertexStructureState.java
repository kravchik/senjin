package yk.senjin;

import org.lwjgl.opengl.GL11;
import yk.jcommon.collections.YList;
import yk.jcommon.collections.YSet;
import yk.jcommon.fastgeom.Vec2f;
import yk.jcommon.fastgeom.Vec3f;
import yk.jcommon.fastgeom.Vec4f;
import yk.senjin.shaders.ShaderHandler;
import yk.senjin.shaders.VertexAttrib;
import yk.senjin.shaders.arraystructure.*;
import yk.senjin.shaders.bufferstructure.StructureBundleTight;
import yk.senjin.shaders.bufferstructure.StructureUnit;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static yk.jcommon.collections.YArrayList.al;
import static yk.jcommon.collections.YHashSet.hs;

/**
 * Created by: Yuri Kravchik Date: 26/10/2007 Time: 11:11:46
 */
public class VertexStructureState extends AbstractState {

    public int stride;
    public int bufferId;

    private final YList<AbstractArrayStructure> structures = al();

    public VertexStructureState(ShaderHandler handler, Class clazz, int bufferId) {
        this.bufferId = bufferId;
        Field[] fields = clazz.getDeclaredFields();
        stride = 0;
        for (Field field : fields) {
            if (field.getType() == Vec2f.class) stride += 4 * 2;
            if (field.getType() == Vec3f.class) stride += 4 * 3;
            if (field.getType() == Vec4f.class) stride += 4 * 4;
            //TODO unknown type exception
        }
        int offset = 0;
        YSet<String> hasFields = hs();
        for (Field field : fields) {
            if (Modifier.isTransient(field.getModifiers())) continue;
            hasFields.add(field.getName());
            VertexAttrib shaderAttrib = handler.getVertexAttrib(field.getName());
            if (shaderAttrib == null) throw new RuntimeException("shader has no attribute " + field.getName());

            this.structures.add(new VBOVertexAttrib(shaderAttrib.getIndex(), shaderAttrib.getSize(), shaderAttrib.getType(), shaderAttrib.isNormalized(), stride, offset));

            if (field.getType() == Vec2f.class) {
                assertType(shaderAttrib, 2, GL11.GL_FLOAT, field.getName());
                offset += 2 * 4;
            }
            if (field.getType() == Vec3f.class) {
                assertType(shaderAttrib, 3, GL11.GL_FLOAT, field.getName());
                offset += 3 * 4;
            }
            if (field.getType() == Vec4f.class) {
                assertType(shaderAttrib, 4, GL11.GL_FLOAT, field.getName());
                offset += 4 * 4;
            }
            //TODO unknown type exception
        }
        for (String attrib : handler.vertexAttribs.keySet()) if (!hasFields.contains(attrib)) throw new Error("buffer haven't field " + attrib);
    }

    public static void assertType(VertexAttrib shaderAttrib, int size, int type, String name) {
        if (shaderAttrib.getSize() != size) throw new Error("wrong type for " + name);
        if (shaderAttrib.getType() != type) throw new Error("wrong type for " + name);
    }

    public VertexStructureState(final StructureBundleTight types, final ShaderHandler shaderHandler) {
//        int stride = calcStride(units);
        int offset = 0;
        for (final StructureUnit unit : types.units) {
            if (unit.getType() < StructureUnit.BUFFERTYPE_VERTEXATTRIB) {
                switch (unit.getType()) {
                    case StructureUnit.BUFFERTYPE_VERTEX3f:
                        structures.add(new VBOVertex3f(types.stride, offset));
                        break;
                    case StructureUnit.BUFFERTYPE_NORMAL3f:
                        structures.add(new VBONormal3f(types.stride, offset));
                        break;
                    case StructureUnit.BUFFERTYPE_TEXCOORD2f:
                        structures.add(new VBOTexCoor2f(types.stride, offset));
                        break;
                    case StructureUnit.BUFFERTYPE_COLOR4b:
                        structures.add(new VBOColor4b(types.stride, offset));
                        break;
                    default:
                        throw new Error("Unsupported unit: " + unit.getType());
                }
            } else if (shaderHandler != null && unit.getType() != StructureUnit.BUFFERTYPE_UNUSED) {
                final VertexAttrib vertexAttrib = shaderHandler.getVertexAttrib(unit.getName());
                if (vertexAttrib == null) {
                    throw new Error("program has no attrib with name " + unit.getName());
                }
                structures.add(new VBOVertexAttrib(vertexAttrib.getIndex(),
                        vertexAttrib.getSize(),
                        vertexAttrib.getType(),
                        vertexAttrib.isNormalized(),
                        types.stride,
                        offset));
                System.out.println(types.stride + "  " + unit.getName());
            }
            offset += unit.getSize();
        }
    }

    @Override
    public void disable() {
        for (int i = structures.size() - 1; i >= 0; i--) {
            structures.get(i).turnOff();
        }
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    @Override
    public void enable() {
        glBindBuffer(GL_ARRAY_BUFFER, bufferId);
//        if(current != this){
        for (int i = 0; i < structures.size(); i++) {
            structures.get(i).turnOn();
        }
//        }
    }

    //public Class dataClass;
}
