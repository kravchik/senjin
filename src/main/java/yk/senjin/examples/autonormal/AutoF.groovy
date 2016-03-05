package yk.senjin.examples.autonormal

import yk.senjin.examples.ds.PoconuvFi
import yk.senjin.shaders.gshader.FragmentShaderParent
import yk.senjin.shaders.gshader.StandardFSOutput

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 04/03/16
 * Time: 10:31
 */
class AutoF extends FragmentShaderParent<PoconuvFi, StandardFSOutput> {

    @Override
    void main(PoconuvFi i, StandardFSOutput o) {
//        o.gl_FragColor = Vec4f(i.csPos, 1)
        o.gl_FragColor = Vec4f(i.csNormal, 1)
    }
}
