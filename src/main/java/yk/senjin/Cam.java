package yk.senjin;

import yk.jcommon.fastgeom.Quaternionf;
import yk.jcommon.fastgeom.Vec3f;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 21/10/15
 * Time: 10:43
 */
public class Cam {

    public Vec3f lookAt = Vec3f.ZERO();
    //public Vec3f lookFrom = Vec3f.ZERO;
    public Quaternionf lookRot = new Quaternionf(1, 0, 0, 0);

    public Cam() {
    }

    public Cam(Cam other) {
        this.lookAt = other.lookAt;
        this.lookRot = other.lookRot;
    }
}
