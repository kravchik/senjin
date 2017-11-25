package yk.senjin.examples.specular;

import yk.jcommon.fastgeom.Vec2f;
import yk.jcommon.fastgeom.Vec3f;
import yk.senjin.shaders.gshader.StandardFragmentData;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 22/10/15
 * Time: 13:58
 */
public class SpecularFi extends StandardFragmentData {
    public Vec3f normal;
    public Vec3f csNormal;//cam space normal
    public Vec3f csPos;
    public Vec2f uv;
    public Vec3f csLightDir;//cam space light dir
}
