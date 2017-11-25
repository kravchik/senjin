package yk.senjin.examples.simple;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Yuri Kravchik on 16.11.17.
 */
public class IKnowOpenGL_2_ShowMe extends SimpleLwjglRoutine {
    public static void main(String[] args) throws Exception {
        new IKnowOpenGL_2_ShowMe().main();
    }

    @Override public void onTick(float dt) {
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        glBegin(GL_TRIANGLES);
        glColor3f(1, 0, 0);
        glVertex3f(-1, -0.8f, 0);

        glColor3f(0, 1, 0);
        glVertex3f(1, -0.8f, 0);

        glColor3f(0, 0, 1);
        glVertex3f(0, 0.8f, 0);

        glEnd();
        super.onTick(dt);
    }
}
