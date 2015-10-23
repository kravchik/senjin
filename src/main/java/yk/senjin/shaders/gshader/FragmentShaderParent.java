package yk.senjin.shaders.gshader;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 22/10/15
 * Time: 21:10
 */
abstract public class FragmentShaderParent<I> extends ShaderParent {
    abstract public void main(I i, StandardFrame o);
}
