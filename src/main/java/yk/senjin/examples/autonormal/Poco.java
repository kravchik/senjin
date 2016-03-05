package yk.senjin.examples.autonormal;

import yk.jcommon.fastgeom.Vec3f;
import yk.jcommon.fastgeom.Vec4f;
import yk.senjin.shaders.gshader.StandardVSInput;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 04/03/16
 * Time: 10:29
 */
public class Poco extends StandardVSInput {
    public Vec3f pos;
    public Vec4f color;

    public Poco(Vec3f pos) {
        this.pos = pos;
    }

    public Poco(Vec3f pos, Vec4f color) {
        this.pos = pos;
        this.color = color;
    }
}