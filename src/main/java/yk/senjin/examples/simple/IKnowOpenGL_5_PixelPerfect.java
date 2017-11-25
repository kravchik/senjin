package yk.senjin.examples.simple;

import yk.jcommon.utils.MyMath;

import static org.lwjgl.opengl.GL11.*;
import static yk.jcommon.fastgeom.Matrix4.ortho;
import static yk.senjin.DDDUtils.glLoadMatrix;

/**
 * Created by Yuri Kravchik on 17.11.17.
 */
public class IKnowOpenGL_5_PixelPerfect extends SimpleLwjglRoutine {
    public static void main(String[] args) throws Exception {
        new IKnowOpenGL_5_PixelPerfect().main();
    }

    @Override public void onTick(float dt) {
        glMatrixMode(GL_PROJECTION);
        drawPixelScheme(this.w, this.h);
        super.onTick(dt);
    }

    public static void drawPixelScheme(int w, int h) {
        //glLoadMatrix(ortho(0, w, h, 0, 0, 10));//0,0 is LEFT TOP
        glLoadMatrix(ortho(0, w, 0, h, 0, 10));//0,0 is LEFT BOTTOM

        glBegin(GL_TRIANGLES);
        glVertex3f(0, 0, 0);
        glVertex3f(10, 0, 0);
        glVertex3f(10, 10, 0);
        glEnd();

        glBegin(GL_LINES);
        glVertex3f(w - 1, h / 2 - 5, 0);
        glVertex3f(w - 1, h / 2 + 5, 0);

        glVertex3f(0, h / 2 - 5, 0);
        glVertex3f(0, h / 2 + 5, 0);

        glVertex3f(w / 2 - 5, 0, 0);
        glVertex3f(w / 2 + 5, 0, 0);

        glVertex3f(w / 2 - 5, h - 1, 0);
        glVertex3f(w / 2 + 5, h - 1, 0);

        glVertex3f(0, 0, 0);
        glVertex3f(w - 1, h - 1, 0);

        //line is drawn one pixel short! wtf??? (try to switch those two lines)
        glVertex3f(0, h - 1, 0);
        glVertex3f(w - 1, 0, 0);
        glEnd();

        glBegin(GL_POINTS);
        glVertex3f(w - 2, MyMath.floor(h / 2f - 0.1f), 0);
        glVertex3f(w - 2, MyMath.floor(h / 2f + 0.1f), 0);

        glEnd();
    }
}
