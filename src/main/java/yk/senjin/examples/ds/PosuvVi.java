package yk.senjin.examples.ds;

import yk.jcommon.fastgeom.Vec2f;
import yk.jcommon.fastgeom.Vec3f;
import yk.senjin.shaders.gshader.StandardVertexData;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 23/10/15
 * Time: 20:46
 */
public class PosuvVi extends StandardVertexData {
    public Vec3f pos;
    public Vec2f uv;

    public PosuvVi(Vec3f pos, Vec2f uv) {
        this.pos = pos;
        this.uv = uv;
    }
}
