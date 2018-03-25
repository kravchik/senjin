package yk.senjin.examples.simple;

import yk.jcommon.fastgeom.Matrix4;
import yk.senjin.DDDUtils;

import static org.lwjgl.opengl.GL11.*;
import static yk.jcommon.fastgeom.Matrix4.perspective;

/**
 * Created by Yuri Kravchik on 16.11.17.
 */
public class IKnowOpenGL_3_Projection extends SimpleLwjglRoutine {
    public static void main(String[] args) {
        new IKnowOpenGL_3_Projection().main();
    }

    @Override public void onTick(float dt) {
        glMatrixMode(GL_PROJECTION);

        // glu variant
        //glLoadIdentity();//it is important to load identity before gluPerspective
        //gluPerspective(45, (float) w / h, 10, 500);

        // custom variant
        Matrix4 perspective = perspective(45, (float) w / h, 10, 500);
        DDDUtils.glLoadMatrix(perspective);


        //camera is at 0 0 0
        //we can see things if z is <= -10 and >= -500  (because in OpenGL "forward" is -Z)
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        glBegin(GL_LINES);

        //we can see it
        glColor3f(1, 0, 0);
        glVertex3f(0, 0, -100);
        glVertex3f(0, 1, -100);

        //it is on the edge
        glColor3f(0, 1, 0);
        glVertex3f(0.1f, 0, -10);
        glVertex3f(0.1f, 1, -10);

        //can't see it
        glColor3f(0, 0, 1);
        glVertex3f(0.2f, 0, -9);
        glVertex3f(0.2f, 1, -9);


        glEnd();
        super.onTick(dt);
    }
}
