package yk.senjin.shaders.gshader.analysis

import yk.jcommon.fastgeom.Vec3f

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 02/11/15
 * Time: 12:04
 */
class TestGroovyClass2 {

    public Vec3f foo(Vec3f someVec) {
        int x, y

        x = 5
        y = 5

        Vec3f res = -foo1(someVec)*(2f)
        res.x = x
        res.y = y
        return res
    }

    public Vec3f foo1(Vec3f v) {
        return new Vec3f(0, 0, 0)
    }
}
