package yk.senjin.examples.hdr;

import yk.jcommon.fastgeom.Vec2f;
import yk.jcommon.fastgeom.Vec3f;
import yk.senjin.shaders.gshader.StandardVSInput;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 23/10/15
 * Time: 20:46
 */
public class HdrVi extends StandardVSInput {
    public Vec3f pos;
    public Vec2f uv;

    public HdrVi(Vec3f pos, Vec2f uv) {
        this.pos = pos;
        this.uv = uv;
    }
}
