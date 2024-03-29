package yk.senjin.examples.simple;

import yk.senjin.examples.simple.stuff.Ikogl_8_Fs;
import yk.senjin.examples.simple.stuff.Ikogl_8_Vd;
import yk.senjin.examples.simple.stuff.Ikogl_8_Vs;
import yk.senjin.shaders.gshader.GProgram;
import yk.senjin.vbo.AVboShortIndices;
import yk.senjin.vbo.AVboTyped;
import yk.senjin.viewers.SimpleLwjglRoutine;

import static org.lwjgl.opengl.GL11.*;
import static yk.jcommon.fastgeom.Matrix4.ortho;
import static yk.jcommon.fastgeom.Vec3f.v3;

/**
 * Created by Yuri Kravchik on 17.11.17.
 */
public class IKnowOpenGL_8_VBO2 extends SimpleLwjglRoutine {
    public static void main(String[] args) {
        new IKnowOpenGL_8_VBO2().start();
    }

    public Ikogl_8_Vs vs;
    public Ikogl_8_Fs fs;
    public GProgram program;

    AVboTyped vbo;
    AVboShortIndices indices;

    @Override public void onFirstPass() {
        super.onFirstPass();
        program = GProgram.initFrom("src/main/java/", vs = new Ikogl_8_Vs(), fs = new Ikogl_8_Fs()).runtimeReload();
        vbo = new AVboTyped(new Ikogl_8_Vd(v3(-w(), -h(), 0)), new Ikogl_8_Vd(v3(w(),  0, 0)), new Ikogl_8_Vd(v3( 0, h(), 0)));
        indices = AVboShortIndices.simple(3, GL_TRIANGLES);
    }
    
    @Override public void onTick(float dt) {
        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT);

        vs.modelViewProjectionMatrix = ortho(0, w(), 0, h(), 0, 10);
        vs.timePassed += dt;

        program.setInput(vbo);
        program.enable();
        indices.enable();
        program.disable();

        super.onTick(dt);
    }
}
