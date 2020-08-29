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
    public boolean gl_FrontFacing;

    @Deprecated //gl_FrontColor, gl_Color is deprecated after version 120, use your own varying
    public Vec4f gl_FrontColor;
    public Vec4f gl_BackColor;
    //for reading in FS, calculated based on gl_FrontColor, gl_BackColor, gl_FrontFacing
    //TODO understand, is writing it in VS, overrides its default calculation?
    public Vec4f gl_Color;

}
