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
        new WatchReloadable(new Test3D()).run();
    }

    @Override
    public void onTick(WatchReloadable watch, float dt) {
        setColor(1, 1, 1);
        drawCube(0, 1, 0);
        drawCube(1, 1, 0);
        setColor(1, 0, 0);
        drawCube(1, 1, 1);
        drawCube(1, 3, 1);
        drawCube(1, 4, 1);
        drawCube(1, 5, 1);

        setColor(0, 1, 0);

        drawCube(4, 5, 0);
        drawCube(4, 6, 0);
        drawCube(4, 7, 0);
        drawCube(4, 8, 0);
        drawCube(4, 9, 0);
//        drawCube(4,10, 0);

        drawCube(3, 5, 0);
        drawCube(3, 6, 0);
        drawCube(3, 7, 0);
        drawCube(3, 8, 0);
        drawCube(3, 9, 0);
        drawCube(3,10, 0);
        drawCube(10, 5, 0);
        drawCube(10, 6, 0);
        drawCube(10, 7, 0);
        drawCube(10, 8, 0);
        drawCube(10, 9, 0);
        drawCube(10,10, 0);

        drawCube(5, 5, 0);
        drawCube(6, 5, 0);
        drawCube(7, 5, 0);
        drawCube(8, 5, 0);
        drawCube(9, 5, 0);
//        drawCube(5, 6, 0);
        drawCube(6, 6, 0);
        drawCube(7, 6, 0);
//        drawCube(8, 6, 0);
        drawCube(9, 6, 0);
//        drawCube(5, 7, 0);
//        drawCube(6, 7, 0);
//        drawCube(7, 7, 0);
//        drawCube(8, 7, 0);
        drawCube(9, 7, 0);
//        drawCube(5, 8, 0);
//        drawCube(6, 8, 0);
//        drawCube(7, 8, 0);
//        drawCube(8, 8, 0);
        drawCube(9, 8, 0);
        drawCube(5, 9, 0);
//        drawCube(6, 9, 0);
//        drawCube(7, 9, 0);
        drawCube(8, 9, 0);
        drawCube(9, 9, 0);

//        drawCube(5, 10, 0);
        drawCube(6, 10, 0);
        drawCube(7, 10, 0);
//        drawCube(8, 10, 0);
//        drawCube(9, 10, 0);

        drawCube(3, 11, 0);
//        drawCube(4, 11, 0);
//        drawCube(5, 11, 0);
        drawCube(6, 11, 0);
        drawCube(7, 11, 0);
//        drawCube(8, 11, 0);
//        drawCube(9, 11, 0);
        drawCube(10, 11, 0);

        drawCube(3, 12, 0);
        drawCube(4, 12, 0);
        drawCube(5, 12, 0);
        drawCube(6, 12, 0);
        drawCube(7, 12, 0);
        drawCube(8, 12, 0);
        drawCube(9, 12, 0);
        drawCube(10, 12, 0);

        drawCube(3, 13, 0);
        drawCube(4, 13, 0);
        drawCube(5, 13, 0);
        drawCube(6, 13, 0);
        drawCube(7, 13, 0);
        drawCube(8, 13, 0);
        drawCube(9, 13, 0);
        drawCube(10, 13, 0);

        drawCube(3, 14, 0);
        drawCube(4, 14, 0);
        drawCube(5, 14, 0);
        drawCube(6, 14, 0);
        drawCube(7, 14, 0);
        drawCube(8, 14, 0);
        drawCube(9, 14, 0);
        drawCube(10, 14, 0);
    }
}
