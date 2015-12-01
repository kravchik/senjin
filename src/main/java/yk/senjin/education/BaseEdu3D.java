package yk.senjin.education;

import yk.jcommon.collections.YList;
import yk.jcommon.fastgeom.Vec3f;
import yk.jcommon.utils.Rnd;
import yk.senjin.LoadTickUnload;
import yk.senjin.WatchReloadable;

import static org.lwjgl.opengl.GL11.*;
import static yk.jcommon.collections.YArrayList.al;
import static yk.senjin.DDDUtils.CUBE_POS_AND_COLOR;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 19/11/15
 * Time: 11:17
 */
public class BaseEdu3D implements LoadTickUnload<WatchReloadable> {
    public Rnd rnd = new Rnd();
    public Vec3f currentColor = Vec3f.v3(1, 1, 1);
    public YList<Vec3f> cubeColors = al();
    {
        setColor(1, 1, 1);
    }

    public void setColor(float r, float g, float b) {
        Vec3f c = new Vec3f(r, g, b);
        setColor(c);
    }

    public Vec3f randomVector3() {
        return new Vec3f(rnd.nextFloat(), rnd.nextFloat(), rnd.nextFloat());
    }

    public void setColor(Vec3f c) {
        cubeColors = al();
        cubeColors.add(c.mul(0.8f));
        cubeColors.add(c.mul(0.3f));
        cubeColors.add(c.mul(0.4f));
        cubeColors.add(c.mul(0.9f));
        cubeColors.add(c.mul(1.0f));
        cubeColors.add(c.mul(0.2f));
    }

    public void drawCube(float x, float y, float z) {
        glMatrixMode(GL_MODELVIEW);
        glPushMatrix();
        glTranslatef(x+0.5f, y+0.5f, z+0.5f);
        glBegin(GL_QUADS);
        for (int i1 = 0; i1 < CUBE_POS_AND_COLOR.length; i1++) {
            float[][] flat = CUBE_POS_AND_COLOR[i1];
            Vec3f c = cubeColors.get(i1);
            glColor3f(c.x, c.y, c.z);
            for (int i = 1; i < flat.length; i++) glVertex3f(flat[i][0]*0.5f, flat[i][1]*0.5f, flat[i][2]*0.5f);
        }
        glEnd();
        glPopMatrix();
    }

    @Override
    public void onLoad(WatchReloadable w) {

    }

    @Override
    public void onTick(WatchReloadable watch, float dt) {

    }

    @Override
    public void onUnload() {

    }
}
