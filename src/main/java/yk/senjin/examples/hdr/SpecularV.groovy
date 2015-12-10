package yk.senjin.examples.hdr

import yk.jcommon.fastgeom.Matrix3
import yk.jcommon.fastgeom.Matrix4
import yk.jcommon.fastgeom.Vec3f
import yk.senjin.shaders.gshader.VertexShaderParent

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 22/10/15
 * Time: 20:54
 */
class SpecularV extends VertexShaderParent<SpecularVi, SpecularFi> {
    public Matrix3 normalMatrix;
    public Matrix4 modelViewProjectionMatrix;
    public Vec3f lightDir

    void main(SpecularVi i, SpecularFi o) {
        o.normal = i.normal
        o.csNormal = normalMatrix * i.normal
        o.gl_Position = modelViewProjectionMatrix * Vec4f(i.pos, 1)
        o.csEyeDir = o.gl_Position.xyz
        o.uv = i.uv
        o.csLightDir = normalMatrix * lightDir
    }
}
