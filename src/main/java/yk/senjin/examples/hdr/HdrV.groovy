package yk.senjin.examples.hdr

import yk.jcommon.fastgeom.Matrix4
import yk.senjin.shaders.gshader.StandardVSInput
import yk.senjin.shaders.gshader.VertexShaderParent

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 23/10/15
 * Time: 20:47
 */
class HdrV extends VertexShaderParent<StandardVSInput, HdrFi> {
    public Matrix4 modelViewProjectionMatrix;
    @Override
    void main(StandardVSInput i, HdrFi o) {
        o.gl_Position = modelViewProjectionMatrix * i.gl_Vertex;
        o.vTexCoord = i.gl_MultiTexCoord0.xy;
    }
}
