package yk.senjin.examples.autonormal

import yk.jcommon.fastgeom.Matrix3
import yk.jcommon.fastgeom.Matrix4
import yk.senjin.shaders.gshader.VertexShaderParent

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 04/03/16
 * Time: 10:32
 */
class AutoV extends VertexShaderParent<Poco, AutoGi> {
    public Matrix3 normalMatrix;
    public Matrix4 modelViewMatrix;
    public Matrix4 modelViewProjectionMatrix;

    @Override
    void main(Poco i, AutoGi o) {
        o.gl_Position = modelViewProjectionMatrix * Vec4f(i.pos, 1)
        o.csPosG = (modelViewMatrix * Vec4f(i.pos, 1)).xyz
        o.colorG = i.color
    }
}
