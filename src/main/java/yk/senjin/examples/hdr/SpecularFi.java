package yk.senjin.examples.hdr;

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
    public Vec3f normal;
    public Vec3f csNormal;//cam space normal
    public Vec3f csEyeDir;
    public Vec2f uv;
    public Vec3f csLightDir;//cam space light dir
}
