package yk.senjin.examples.simple.stuff

import yk.senjin.shaders.gshader.FragmentShaderParent
import yk.senjin.shaders.gshader.StandardFSOutput

/**
 * Created by Yuri Kravchik on 17.11.17.
 */
class Ikogl_8_Fs extends FragmentShaderParent<Ikogl_8_Fd, StandardFSOutput> {

    void main(Ikogl_8_Fd i, StandardFSOutput o) {
        o.gl_FragColor = Vec4f(i.pos.x, i.pos.y, 1, 1);
    }
}
