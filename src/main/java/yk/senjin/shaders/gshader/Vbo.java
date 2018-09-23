package yk.senjin.shaders.gshader;

import yk.senjin.State;

/**
 * Created by Yuri Kravchik on 25.03.18.
 */
public interface Vbo extends State {
    void enable();
    Class getInputType();
    void checkDirty();
}
