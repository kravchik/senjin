package yk.senjin.examples.ds

import yk.jcommon.fastgeom.Matrix4
import yk.senjin.shaders.gshader.StandardVSInput
import yk.senjin.shaders.gshader.VertexShaderParent

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 23/10/15
 * Time: 20:47
 */
class PosuvV extends VertexShaderParent<StandardVSInput, UvFi> {
    public Matrix4 modelViewProjectionMatrix;
    @Override
    void main(StandardVSInput i, UvFi o) {
        o.gl_Position = modelViewProjectionMatrix * i.gl_Vertex;
        o.uv = i.gl_MultiTexCoord0.xy;//todo get rid
//        o.vTexCoord = Vec2f(i.gl_MultiTexCoord0.x, 1-i.gl_MultiTexCoord0.y);
    }
}
