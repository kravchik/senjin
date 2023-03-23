package yk.senjin.examples.simple;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Yuri Kravchik on 16.11.17.
 */
public class IKnowOpenGL_1_Clip extends SimpleLwjglRoutine {
    public static void main(String[] args) {
        new IKnowOpenGL_1_Clip().start();
    }

    @Override public void onTick(float dt) {
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        glBegin(GL_TRIANGLES);
        glColor3f(1, 0, 0);

        glVertex3f(0, 0, 2);//z == 2, look how triangle is cut by far plane (which is z == -1)
        glVertex3f(1, 0, 0);
        glVertex3f(0, 1, 0);

        glColor3f(0, 1, 0);
        glVertex3f(0, 0, -2);//z == -2, look how triangle is cut by near plane (which is z == 1)
        glVertex3f(-1, 0, 0);
        glVertex3f(0, -1, 0);

        glEnd();
        super.onTick(dt);
    }
}
