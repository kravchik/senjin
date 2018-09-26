package yk.senjin.shaders.uniforms;

import org.lwjgl.opengl.GL20;

/**
 * Copyright Yuri Kravchik 2007 Created by Yuri Kravchik Date: 09.12.2007 Time:
 * 15:30:43
 */
public class Uniform1i extends UniformVariable {
    public int value;

    public Uniform1i(String name) {
        super(name);
    }

    public Uniform1i(final String name, final int value) {
        super(name);
        this.value = value;
    }

    @Override
    public void setValue(Object o) {
        value = (int) o;
    }

    @Override
    public void plug() {
        GL20.glUniform1i(index, value);
    }
}
