package yk.senjin.shaders.gshader;

import yk.senjin.shaders.UniformVariable;
import org.lwjgl.opengl.GL20;
import yk.senjin.SomeTexture;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 16/12/14
 * Time: 22:51
 */
public class Sampler2D extends UniformVariable {
    public SomeTexture texture;

    public void set(SomeTexture texture) {
        this.texture = texture;
    }

    @Override
    public void plug() {
        if (!texture.enabled) throw new RuntimeException("texture not enabled for " + name);
        GL20.glUniform1i(index, texture.textureSlot);
    }
}
