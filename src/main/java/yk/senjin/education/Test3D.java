package yk.senjin.education;

import yk.senjin.WatchReloadable;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 19/11/15
 * Time: 11:17
 */
public class Test3D extends BaseEdu3D {

    public static void main(String[] args) {
        new WatchReloadable(new Test3D());
    }

    @Override
    public void onTick(WatchReloadable watch, float dt) {
        setColor(1, 1, 1);
        drawCube(0, 1, 0);
        drawCube(1, 1, 0);
    }
}
