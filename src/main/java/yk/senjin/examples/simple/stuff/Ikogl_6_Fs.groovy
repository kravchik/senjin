package yk.senjin.examples.simple.stuff

import yk.senjin.shaders.gshader.FragmentShaderParent
import yk.senjin.shaders.gshader.StandardFSOutput
import yk.senjin.shaders.gshader.StandardFragmentData

/**
 * Created by Yuri Kravchik on 17.11.17.
 */
class Ikogl_6_Fs extends FragmentShaderParent<StandardFragmentData, StandardFSOutput> {
    public float w;
    public float h;

    void main(StandardFragmentData i, StandardFSOutput o) {
        o.gl_FragColor = Vec4f(i.gl_FragCoord.x / w, i.gl_FragCoord.y / h, 1, 1);
    }
}
