package yk.senjin.shaders.uniforms;

import org.lwjgl.opengl.GL20;
import yk.senjin.SomeTexture;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 16/12/14
 * Time: 22:51
 */
public class Sampler2D extends UniformVariable {
    public SomeTexture value;

    public Sampler2D() {
        super(null);
    }

    public void set(SomeTexture texture) {
        this.value = texture;
    }

    @Override
    public void setValue(Object o) {
        value = (SomeTexture) o;
    }

    @Override
    public void plug() {
        if (!value.enabled) throw new RuntimeException("texture not enabled for " + name);
        GL20.glUniform1i(index, value.textureSlot);
    }
}
