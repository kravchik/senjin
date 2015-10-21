package yk.senjin.shaders.gshader.examples

import yk.senjin.shaders.gshader.GShader
import yk.senjin.shaders.gshader.ShaderParent
import yk.jcommon.fastgeom.Matrix4

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 09/06/15
 * Time: 20:22
 */
class ShaderV extends ShaderParent {
//    public Matrix4 mvp = new Matrix4()


    public static void main(String[] args) {
        def program = GShader.createProgram(new ShaderV(), "vs")
        println program.resultSrc
        program = GShader.createProgram(new ShaderF(), "fs")
        println program.resultSrc

    }

    def main(VSInput i, VSOutput o) {
        //TODO MVP!!!

//        o.gl_Position = mvp * vec4(i.position, 1)
        o.gl_Position = gl_ModelViewProjectionMatrix * vec4(i.position, 1)
        o.pos = (gl_ModelViewProjectionMatrix * vec4(i.position, 1)).xyz
    }

}
