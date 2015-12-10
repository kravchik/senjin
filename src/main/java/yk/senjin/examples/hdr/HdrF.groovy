package yk.senjin.examples.hdr

import yk.jcommon.fastgeom.Vec4f
import yk.senjin.shaders.gshader.FragmentShaderParent
import yk.senjin.shaders.gshader.Sampler2D
import yk.senjin.shaders.gshader.StandardFrame

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 23/10/15
 * Time: 20:46
 */
class HdrF extends FragmentShaderParent<HdrFi> {
    public Sampler2D txt = new Sampler2D();

    @Override
    void main(HdrFi blendFi, StandardFrame o) {
        //1.0 – exp(-fExposure x color)
        Vec4f color = texture2D(txt, blendFi.vTexCoord);
        float fExposure = 2f
        color = 1f - exp(-fExposure * color)

        o.gl_FragColor = color;

    }
}
