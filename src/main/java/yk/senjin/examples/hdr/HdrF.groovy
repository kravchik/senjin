package yk.senjin.examples.hdr

import yk.jcommon.fastgeom.Vec4f
import yk.senjin.shaders.gshader.FragmentShaderParent
import yk.senjin.shaders.gshader.Sampler2D
import yk.senjin.shaders.gshader.StandardFSOutput

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 23/10/15
 * Time: 20:46
 */
class HdrF extends FragmentShaderParent<HdrFi, StandardFSOutput> {
    public Sampler2D txt1 = new Sampler2D();
    public Sampler2D txt2 = new Sampler2D();

    @Override
    void main(HdrFi i, StandardFSOutput o) {
        //1.0 – exp(-fExposure x color)
        Vec4f color
        if (i.gl_FragCoord.x < 430) {
            color = texture2D(txt1, i.vTexCoord);
        } else {
            color = texture2D(txt2, i.vTexCoord);
        }
        float fExposure = 2f
        color = 1f - exp(-fExposure * color)

        o.gl_FragColor = color;

    }
}
