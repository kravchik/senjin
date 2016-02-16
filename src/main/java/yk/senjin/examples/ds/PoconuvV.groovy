package yk.senjin.examples.ds

import yk.jcommon.fastgeom.Matrix3
import yk.jcommon.fastgeom.Matrix4
import yk.senjin.shaders.gshader.VertexShaderParent

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 22/10/15
 * Time: 20:54
 */
class PoconuvV extends VertexShaderParent<PoconuvVi, PoconuvFi> {//TODO rename
    public Matrix3 normalMatrix;
    public Matrix4 modelViewMatrix;
    public Matrix4 modelViewProjectionMatrix;

    void main(PoconuvVi i, PoconuvFi o) {
        o.gl_Position = modelViewProjectionMatrix * Vec4f(i.pos, 1)
        o.csPos = (modelViewMatrix * Vec4f(i.pos, 1)).xyz
        o.csNormal = normalMatrix * i.normal
        o.uv = i.uv
        o.color = i.color
    }
}
