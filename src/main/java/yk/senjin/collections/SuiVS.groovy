package yk.senjin.collections

import yk.jcommon.fastgeom.Matrix4
import yk.jcommon.fastgeom.Vec3f
import yk.jcommon.fastgeom.Vec4f
import yk.senjin.shaders.gshader.StandardVertexData
import yk.senjin.shaders.gshader.VertexShaderParent
import yk.senjin.vbo.TypeUtils

import java.nio.ByteBuffer

/**
 * Created by Yuri Kravchik on 05.10.2018
 */
class SuiVS extends VertexShaderParent<Input, SuiFS.Input> {
    static class Input extends StandardVertexData {
        public static final int SIZE = TypeUtils.getTypeSize(Input.class);

        public Vec3f pos;
        public Vec4f color;

        Input(Vec3f pos, Vec4f color) {
            this.pos = pos;
            this.color = color;
        }

        void serialize(ByteBuffer bb) {
            bb.putFloat(pos.x);
            bb.putFloat(pos.y);
            bb.putFloat(pos.z);
            bb.putFloat(color.x);
            bb.putFloat(color.y);
            bb.putFloat(color.z);
            bb.putFloat(color.w);
        }
    }

    //TODO try remove
    public Matrix4 modelViewProjectionMatrix;

    @Override
    void main(Input i, SuiFS.Input o) {
//        o.gl_Position = Vec4f(i.pos, 1)
        o.gl_Position = modelViewProjectionMatrix * Vec4f(i.pos, 1)
        o.color = i.color
//        o.color = Vec4f(i.pos, 1)
    }
}

