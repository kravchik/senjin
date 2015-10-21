package yk.senjin.shaders.gshader

import yk.jcommon.fastgeom.Matrix4
import yk.jcommon.fastgeom.Vec4f

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 14/12/14
 * Time: 16:52
 */
class StandardVSInput {

    public Matrix4 gl_ModelViewMatrix;
    public Matrix4 gl_ModelViewProjectionMatrix;
    public Matrix4 gl_NormalMatrix;
    public Vec4f gl_Vertex;
    public Vec4f gl_Normal;
    public Vec4f gl_Color;

}
