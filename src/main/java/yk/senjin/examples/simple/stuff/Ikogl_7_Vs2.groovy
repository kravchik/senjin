package yk.senjin.examples.simple.stuff

import yk.jcommon.fastgeom.Matrix4
import yk.jcommon.fastgeom.Vec3f
import yk.senjin.shaders.gshader.StandardFragmentData
import yk.senjin.shaders.gshader.VertexShaderParent

/**
 * Created by Yuri Kravchik on 17.11.17.
 */
class Ikogl_7_Vs2 extends VertexShaderParent<Ikogl_7_Vd, StandardFragmentData> {
    public Matrix4 modelViewProjectionMatrix
    public float timePassed;

    void main(Ikogl_7_Vd i, StandardFragmentData o) {
        Matrix4 m = modelViewProjectionMatrix;
        m = m * rotationMatrix(Vec3f(0, 0, 1), timePassed);

        o.gl_Position = m * Vec4f(i.pos, 1)
        o.gl_FrontColor = Vec4f(1, 1, 1, 1)


    }

    Matrix4 rotationMatrix(Vec3f axis, float angle)
    {
        axis = normalize(axis)
        float s = sin(angle)
        float c = cos(angle)
        float oc = 1.0 - c

        return Matrix4(oc * axis.x * axis.x + c,           oc * axis.x * axis.y - axis.z * s,  oc * axis.z * axis.x + axis.y * s,  0.0,
                oc * axis.x * axis.y + axis.z * s,  oc * axis.y * axis.y + c,           oc * axis.y * axis.z - axis.x * s,  0.0,
                oc * axis.z * axis.x - axis.y * s,  oc * axis.y * axis.z + axis.x * s,  oc * axis.z * axis.z + c,           0.0,
                0.0,                                0.0,                                0.0,                                1.0)
    }

}
