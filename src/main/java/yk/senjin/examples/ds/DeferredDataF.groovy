package yk.senjin.examples.ds

import yk.senjin.shaders.gshader.FragmentShaderParent
import yk.senjin.shaders.uniforms.Sampler2D

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 22/10/15
 * Time: 13:59
 */
public class DeferredDataF extends FragmentShaderParent<PoconuvFi, DeferredDataFo> {
    public Sampler2D txt = new Sampler2D()
    public float textureStrength = 1;

    def void main(PoconuvFi i, DeferredDataFo o) {
        o.out1 = i.color + Vec4f(texture2D(txt, i.uv).xyz, 1) * textureStrength;
        o.out2 = Vec4f(i.csNormal, 1);
        o.out3 = Vec4f(i.csPos, 1);
    }
}
