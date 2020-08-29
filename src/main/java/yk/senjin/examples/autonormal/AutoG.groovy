package yk.senjin.examples.autonormal

import yk.jcommon.fastgeom.Vec3f
import yk.senjin.examples.ds.PoconuvFi
import yk.senjin.shaders.gshader.GeometryShaderParent

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 04/03/16
 * Time: 10:31
 */
class AutoG extends GeometryShaderParent<AutoGi, PoconuvFi> {

    void main(AutoGi[] input, PoconuvFi o) {
        Vec3f n = normalize(cross(input[1].csPosG.xyz-input[0].csPosG.xyz, input[2].csPosG.xyz-input[0].csPosG.xyz));
        for(int i = 0; i < gl_PatchVerticesIn; i++) {
            o.gl_Position = gl_in[i].gl_Position;
            o.csPos = input[i].csPosG;
            o.csNormal = n;
            o.color = input[i].colorG;
            o.shininess = input[i].shininess;
            EmitVertex();
        }
    }
}
