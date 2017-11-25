package yk.senjin.shaders.gshader;

import yk.jcommon.fastgeom.Vec4f;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 14/12/14
 * Time: 16:52
 */
public class StandardFragmentData {

    public Vec4f gl_Position;
    public Vec4f gl_FragCoord;
    @Deprecated //gl_FrontColor is deprecated after version 120
    public Vec4f gl_FrontColor;
    //for reading in fragment shader only
    public Vec4f gl_Color;
    public boolean gl_FrontFacing;

}
