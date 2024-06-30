package yk.senjin.shaders.uniforms;

import org.lwjgl.opengl.GL20;
import yk.jcommon.fastgeom.Matrix4;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 21/10/15
 * Time: 12:45
 */
public class UniformRefMatrix4 extends UniformRef<Matrix4> {

    public UniformRefMatrix4(String name, Object src, String fieldName) {
        super(name, src, fieldName);
    }

    @Override
    public void plug() {
        GL20.glUniformMatrix4fv(index, false, getValue().data);
    }
}
