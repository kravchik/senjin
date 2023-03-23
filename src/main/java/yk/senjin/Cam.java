package yk.senjin;

import yk.jcommon.fastgeom.Quaternionf;
import yk.jcommon.fastgeom.Vec3f;

import static yk.jcommon.fastgeom.Quaternionf.ijka;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 21/10/15
 * Time: 10:43
 */
public class Cam {
    public Vec3f lookAt = Vec3f.ZERO;
    public Quaternionf lookRot = ijka(0, 0, 0, 1);

    public Cam() {
    }

    public Cam(Cam other) {
        this.lookAt = other.lookAt;
        this.lookRot = other.lookRot;
    }

    public Cam(Quaternionf lookRot) {
        this.lookRot = lookRot;
    }

    public Cam(Vec3f lookAt, Quaternionf lookRot) {
        this.lookAt = lookAt;
        this.lookRot = lookRot;
    }
}
