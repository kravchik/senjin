package yk.senjin.examples.simple;

import yk.senjin.viewers.SimpleLwjglRoutine;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Yuri Kravchik on 16.11.17.
 */
public class IKnowOpenGL_2_ShowMe extends SimpleLwjglRoutine {
    public static void main(String[] args) {
        new IKnowOpenGL_2_ShowMe().start();
    }

    @Override public void onTick(float dt) {
        //glClearColor(0, 0, 0, 1);
        //glClear(GL_COLOR_BUFFER_BIT);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        glBegin(GL_TRIANGLES);
        glColor4f(1, 0, 0, 1);
        glVertex3f(-1, -0.8f, 0);

        glColor4f(0, 1, 0, 1);
        glVertex3f(1, -0.8f, 0);

        glColor4f(0, 0, 1, 1);
        glVertex3f(0, 0.8f, 0);

        glEnd();
        super.onTick(dt);
    }
}
