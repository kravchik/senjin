package yk.senjin.collections

import yk.jcommon.fastgeom.Vec4f
import yk.senjin.shaders.gshader.FragmentShaderParent
import yk.senjin.shaders.gshader.StandardFSOutput
import yk.senjin.shaders.gshader.StandardFragmentData

/**
 * Created by Yuri Kravchik on 05.10.2018
 */
class SuiFS extends FragmentShaderParent<Input, StandardFSOutput> {
    static class Input extends StandardFragmentData {
        public Vec4f color;
    }

    @Override
    void main(Input i, StandardFSOutput o) {
//        o.gl_FragColor = Vec4f(i.pos.x, i.pos.y, 1, 1);
        o.gl_FragColor = i.color;
    }
}

