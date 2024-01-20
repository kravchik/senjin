package yk.senjin.examples.ds

import yk.senjin.shaders.gshader.FragmentShaderParent
import yk.senjin.shaders.gshader.StandardFSOutput
import yk.senjin.shaders.uniforms.Sampler2D

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 22/10/15
 * Time: 13:59
 */
public class DeferredDataF extends FragmentShaderParent<PoconuvFi, StandardFSOutput> {
    public Sampler2D txt = new Sampler2D()
    public float textureStrength = 1;

    void main(PoconuvFi i, StandardFSOutput o) {
        o.gl_FragData[0] = i.color + Vec4f(texture2D(txt, i.uv).xyz, 1) * textureStrength;
        o.gl_FragData[1] = Vec4f(i.csNormal, 1);
        o.gl_FragData[2] = Vec4f(i.csPos, 1);
    }
}
