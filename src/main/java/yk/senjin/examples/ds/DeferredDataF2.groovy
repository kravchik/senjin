package yk.senjin.examples.ds

import yk.senjin.shaders.gshader.FragmentShaderParent

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 22/10/15
 * Time: 13:59
 */
public class DeferredDataF2 extends FragmentShaderParent<PoconuvFi, DeferredDataFo> {

    def void main(PoconuvFi i, DeferredDataFo o) {
        o.out1 = i.color;
        o.out2 = Vec4f(i.csNormal, 1);
        o.out3 = Vec4f(i.csPos, 1);
        o.out4 = Vec4f(i.gl_FragCoord.z, 0, 0, 1);
    }
}
