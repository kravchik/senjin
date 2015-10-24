package yk.senjin.shaders.gshader.examples.blend

import yk.senjin.shaders.gshader.VertexShaderParent

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 23/10/15
 * Time: 20:47
 */
class BlendV extends VertexShaderParent<BlendVi, BlendFi> {
    @Override
    void main(BlendVi i, BlendFi o) {
        o.gl_Position = Vec4f(i.pos, 1.0);
        o.vTexCoord = i.uv;
    }
}
