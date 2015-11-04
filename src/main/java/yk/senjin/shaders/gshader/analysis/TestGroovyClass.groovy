package yk.senjin.shaders.gshader.analysis

import yk.jcommon.fastgeom.Vec3f

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 02/11/15
 * Time: 12:04
 */
class TestGroovyClass {

    public void foo(Vec3f someVec) {
        someVec = new Vec3f(0, 0, 0)
        foo1(someVec)
    }

    public void foo1(Vec3f asdf) {
        foo2(asdf)
        asdf.x = asdf.y
    }

    public void foo2(Vec3f b) {
        b.x = 5
    }

    public void foo3(Vec3f b) {
        b = b * 5
    }
}
