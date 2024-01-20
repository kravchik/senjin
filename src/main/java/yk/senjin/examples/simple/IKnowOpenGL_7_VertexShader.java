package yk.senjin.examples.simple;

import yk.senjin.examples.simple.stuff.Ikogl_7_Vs;
import yk.senjin.shaders.gshader.GProgram;

import static org.lwjgl.opengl.GL11.*;
import static yk.jcommon.fastgeom.Matrix4.ortho;

/**
 * Created by Yuri Kravchik on 17.11.17.
 */
public class IKnowOpenGL_7_VertexShader extends SimpleLwjglRoutine {
    public static void main(String[] args) {
        new IKnowOpenGL_7_VertexShader().start();
    }

    public Ikogl_7_Vs vs;
    public GProgram program;

    @Override public void onFirstPass() {
        super.onFirstPass();
        vs = new Ikogl_7_Vs();
        program = new GProgram().addVertexShader("src/main/java/", vs).link().runtimeReload();
    }
    
    @Override public void onTick(float dt) {
        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT);

        vs.modelViewProjectionMatrix = ortho(0, w(), 0, h(), 0, 10);
        vs.timePassed += dt;
        program.enable();

        glBegin(GL_TRIANGLES);
            glVertex3f(0, 0, -5);
            glVertex3f(w(), 0, -5);
            glVertex3f(0, h(), -5);
        glEnd();

        program.disable();


        super.onTick(dt);
    }
}
