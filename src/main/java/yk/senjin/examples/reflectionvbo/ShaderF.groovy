package yk.senjin.examples.reflectionvbo

import yk.jcommon.fastgeom.Vec4f
import yk.senjin.shaders.gshader.FragmentShaderParent
import yk.senjin.shaders.gshader.StandardFSOutput
import yk.senjin.shaders.uniforms.Sampler2D

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 09/06/15
 * Time: 20:17
 */
class ShaderF extends FragmentShaderParent<VSOutput, StandardFSOutput> {
    public Vec4f color = new Vec4f(1, 1, 1, 1)
    public Sampler2D txt = new Sampler2D()

    public void main(VSOutput i, StandardFSOutput o) {
//        o.gl_FragColor = vec4(1, 0, 0, 1)
//        o.gl_FragColor = color;
        o.gl_FragColor = texture2D(txt, i.pos.xy);

    }
}