package yk.senjin.shaders.gshader;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 28.08.2020
 * Time: 01:40
 */
abstract public class GeometryShaderParent<I, O> extends ShaderParent {
    abstract public void main(I[] i, O o);
}
