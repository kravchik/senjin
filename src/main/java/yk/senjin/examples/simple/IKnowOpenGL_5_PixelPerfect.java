package yk.senjin.examples.simple;

import yk.jcommon.utils.MyMath;
import yk.senjin.viewers.SimpleLwjglRoutine;

import static org.lwjgl.opengl.GL11.*;
import static yk.jcommon.fastgeom.Matrix4.orthoPixel;
import static yk.senjin.DDDUtils.glLoadMatrix;

/**
 * Created by Yuri Kravchik on 17.11.17.
 */
public class IKnowOpenGL_5_PixelPerfect extends SimpleLwjglRoutine {
    public static void main(String[] args) {
        new IKnowOpenGL_5_PixelPerfect().start();
    }

    @Override public void onTick(float dt) {
        drawPixelScheme(this.w(), this.h());
        super.onTick(dt);
    }

    public static void drawPixelScheme(int w, int h) {
        glMatrixMode(GL_PROJECTION);
        glLoadMatrix(orthoPixel(0, w, 0, h, 0, 10));//0,0 is LEFT BOTTOM

        glBegin(GL_TRIANGLES);
        glColor3f(1, 1, 1);
        glVertex3f(0 + 3, 0, 0);
        glVertex3f(5 + 3, 0, 0);
        glVertex3f(0 + 3, 5, 0);

        glVertex3f(0 + 3, h-1, 0);
        glVertex3f(5 + 3, h-1-5, 0);
        glVertex3f(5 + 3, h-1, 0);
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

        glVertex3f(w - 1, 0, 0);
        glVertex3f(0, h - 1, 0);

        //line is drawn one pixel short! [from, to)
        glVertex3f(23, 10, 0);glVertex3f(30, 10, 0);
        glColor3f(1, 0, 0);
        glVertex3f(24, 11, 0);glVertex3f(24, 11, 0);//can't see (one pixel length)
        glVertex3f(25, 11, 0);glVertex3f(25, 12, 0);//one pixel only
        glVertex3f(26, 11, 0);glVertex3f(26, 13, 0);//2 pixel



        glEnd();

        glBegin(GL_POINTS);
        glVertex3f(w - 2, MyMath.floor(h / 2f - 0.1f), 0);
        glVertex3f(w - 2, MyMath.floor(h / 2f + 0.1f), 0);

        glEnd();
    }
}
