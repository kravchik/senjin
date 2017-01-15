package yk.senjin;

import yk.jcommon.collections.YList;
import yk.jcommon.fastgeom.Quaternionf;
import yk.jcommon.fastgeom.Vec3f;
import yk.jcommon.utils.Rnd;

import static org.lwjgl.opengl.GL11.*;
import static yk.jcommon.collections.YArrayList.al;
import static yk.senjin.DDDUtils.PI;
import static yk.senjin.DDDUtils.glVec3f;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 15/10/15
 * Time: 21:52
 */
public class TreeDecor {

    public Vec3f pos, size;

    public YList<Vec3f> points = al();

    public TreeDecor(Vec3f pos, Vec3f size) {
        this.pos = pos;
        this.size = size;

        float xDif = 0.03f;
        points = al(
                new Vec3f(0, -size.y / 2, xDif + -size.x / 2),
                new Vec3f(0, -size.y / 2, xDif + size.x / 2),
                new Vec3f(0, size.y / 2, xDif + size.x / 2),
                new Vec3f(0, size.y / 2, xDif + -size.x / 2),
                new Vec3f(xDif + -size.x / 2, -size.y / 2, 0),
                new Vec3f(xDif + size.x / 2, -size.y / 2, 0),
                new Vec3f(xDif + size.x / 2, size.y / 2, 0),
                new Vec3f(xDif + -size.x / 2, size.y / 2, 0));
        Quaternionf rot = Quaternionf.fromAngleAxisFast(Rnd.instance.nextFloat() * PI, Vec3f.AXISY());
        points = points.map(p -> rot.rotateFast(p));
    }

    public void render() {
        glMatrixMode(GL_MODELVIEW);
        glPushMatrix();
        glTranslatef(pos.x, pos.y, pos.z);

//        st.enable();

        glBegin(GL_QUADS);
        glColor3f(1, 1, 1);

        glTexCoord2f(0, 1);
        glVec3f(points.get(0));
        glTexCoord2f(1, 1);
        glVec3f(points.get(1));
        glTexCoord2f(1, 0);
        glVec3f(points.get(2));
        glTexCoord2f(0, 0);
        glVec3f(points.get(3));
        glTexCoord2f(0, 1);
        glVec3f(points.get(4));
        glTexCoord2f(1, 1);
        glVec3f(points.get(5));
        glTexCoord2f(1, 0);
        glVec3f(points.get(6));
        glTexCoord2f(0, 0);
        glVec3f(points.get(7));


        glEnd();
//        st.disable();
        glPopMatrix();
    }


}
