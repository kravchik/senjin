package yk.senjin.vbo;

import yk.senjin.State;

/**
 * Created by Yuri Kravchik on 25.03.18.
 */
public interface Vbo extends State {
    void enable();
    void checkDirty();
}
