package yk.senjin.examples.simple.stuff;

import yk.jcommon.fastgeom.Vec3f;
import yk.senjin.shaders.gshader.StandardVertexData;

/**
 * Created by Yuri Kravchik on 17.11.17.
 */
public class Ikogl_8_Vd extends StandardVertexData {
    public Vec3f pos;

    public Ikogl_8_Vd(Vec3f pos) {
        this.pos = pos;
    }
}
