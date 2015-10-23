package yk.senjin.shaders.gshader;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 22/10/15
 * Time: 21:11
 */
abstract public class VertexShaderParent<I, O> extends ShaderParent {
    abstract public void main(I i, O o);
}
