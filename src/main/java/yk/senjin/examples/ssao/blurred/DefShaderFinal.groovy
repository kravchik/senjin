package yk.senjin.examples.ssao.blurred

import yk.jcommon.fastgeom.Vec3f
import yk.jcommon.fastgeom.Vec4f
import yk.senjin.examples.ds.UvFi
import yk.senjin.shaders.gshader.FragmentShaderParent
import yk.senjin.shaders.gshader.Sampler2D
import yk.senjin.shaders.gshader.StandardFSOutput

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 23/10/15
 * Time: 20:46
 */

//render to mono texture
class DefShaderFinal extends FragmentShaderParent<UvFi, StandardFSOutput> {
    public Sampler2D txt1 = new Sampler2D();
    public Sampler2D txt2 = new Sampler2D();
    public Sampler2D txt3 = new Sampler2D();

    //ssao
    public Sampler2D txt4 = new Sampler2D();

    @Override
    void main(UvFi i, StandardFSOutput o) {
        Vec3f color = texture2D(txt1, i.uv).xyz;
        Vec3f normal = texture2D(txt2, i.uv).xyz
        Vec3f pos = texture2D(txt3, i.uv).xyz

        float longest = -pos.z/10/5;//like in ssao

        Vec4f ssao = texture2D(txt4, i.uv)

        float d1 = ssao.x;
//        if (pos.z < ssao.y-0.01f) d1 = 1;
//        if (pos.z > ssao.y+0.001f) d1 = 1;
//        if (d1 == 0) d1 = 1;
        o.gl_FragColor = Vec4f(color.x*d1, color.y * d1, color.z * d1, 1);

    }
}
