package yk.senjin.shaders.gshader;

/**
 * Created by Yuri Kravchik on 25.03.18.
 */
public interface Vbo {
    void enable();
    Class getInputType();
    void checkDirty();
}
