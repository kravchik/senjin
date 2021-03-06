package yk.senjin.examples.blend

import yk.jcommon.fastgeom.Matrix4
import yk.senjin.shaders.gshader.StandardVertexData
import yk.senjin.shaders.gshader.VertexShaderParent

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 23/10/15
 * Time: 20:47
 */
class BlendV extends VertexShaderParent<StandardVertexData, BlendFi> {
    public Matrix4 modelViewProjectionMatrix;
    @Override
    void main(StandardVertexData i, BlendFi o) {
        o.gl_Position = modelViewProjectionMatrix * i.gl_Vertex;
        o.vTexCoord = i.gl_MultiTexCoord0.xy;
    }
}
