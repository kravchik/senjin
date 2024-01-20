package yk.senjin.examples.simple;

import yk.senjin.DDDUtils;
import yk.senjin.examples.simple.stuff.Ikogl_6_Fs;
import yk.senjin.shaders.gshader.GProgram;

import static org.lwjgl.opengl.GL11.*;
import static yk.jcommon.fastgeom.Matrix4.ortho;

/**
 * Created by Yuri Kravchik on 17.11.17.
 */
public class IKnowOpenGL_6_FragmentShader extends SimpleLwjglRoutine {
    public static void main(String[] args) {
        new IKnowOpenGL_6_FragmentShader().start();
    }

    public Ikogl_6_Fs fs;
    public GProgram program;

    @Override public void onFirstPass() {
        super.onFirstPass();
        fs = new Ikogl_6_Fs();
        program = GProgram.initFragmentShaderOnly("src/main/java/", fs).runtimeReload();
    }
    
    @Override public void onTick(float dt) {
        glMatrixMode(GL_PROJECTION);
        DDDUtils.glLoadMatrix(ortho(0, w(), 0, h(), 0, 10));

        fs.w = w();
        fs.h = h();
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
