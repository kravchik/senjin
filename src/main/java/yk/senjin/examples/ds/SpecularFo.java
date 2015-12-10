package yk.senjin.examples.ds;

import yk.jcommon.fastgeom.Vec4f;
import yk.senjin.shaders.gshader.StandardFSOutput;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 10/12/15
 * Time: 20:29
 */
public class SpecularFo extends StandardFSOutput {
    public Vec4f out1 = Vec4f.ZERO;//color
    public Vec4f out2 = Vec4f.ZERO;//normal
    public Vec4f out3 = Vec4f.ZERO;//csPos
}
