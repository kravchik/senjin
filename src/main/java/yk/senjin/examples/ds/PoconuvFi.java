package yk.senjin.examples.ds;

import yk.jcommon.fastgeom.Vec2f;
import yk.jcommon.fastgeom.Vec3f;
import yk.jcommon.fastgeom.Vec4f;
import yk.senjin.shaders.gshader.StandardFragmentData;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 22/10/15
 * Time: 13:58
 */
public class PoconuvFi extends StandardFragmentData {//TODO rename
    public Vec3f csPos;
    public Vec3f csNormal;
    public Vec2f uv;
    public Vec4f color;
    public float shininess;
    public float nType;
}
