package yk.senjin.examples.ds;

import yk.jcommon.fastgeom.Vec2f;
import yk.jcommon.fastgeom.Vec3f;
import yk.senjin.shaders.gshader.StandardFSInput;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 22/10/15
 * Time: 13:58
 */
public class SpecularFi extends StandardFSInput {
    public Vec3f csNormal;
    public Vec3f csPos;
    public Vec2f uv;
}
