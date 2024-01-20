package yk.senjin.examples.simple;

import yk.jcommon.utils.MyMath;
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
import static yk.ycollections.YArrayList.al;

/**
 * Created by Yuri Kravchik on 17.11.17.
 */
public class IKnowOpenGL_8_VBO3 extends SimpleLwjglRoutine {
    public static void main(String[] args) {
        new IKnowOpenGL_8_VBO3().start();
    }

    public Ikogl_8_Vs vs;
    public Ikogl_8_Fs fs;
    public GProgram program;

    AVboTyped vbo;
    AVboShortIndices indices;
    float time;

    @Override public void onFirstPass() {
        super.onFirstPass();
        program = GProgram.initFrom("src/main/java/", vs = new Ikogl_8_Vs(), fs = new Ikogl_8_Fs()).runtimeReload();
        vbo = new AVboTyped(Ikogl_8_Vd.class, 3);
        vbo.reload(al(new Ikogl_8_Vd(v3(-w(), -h(), 0)), new Ikogl_8_Vd(v3(w(),  0, 0)), new Ikogl_8_Vd(v3( 0, h(), 0))));
        indices = AVboShortIndices.simple(3, GL_TRIANGLES);
    }
    
    @Override public void onTick(float dt) {
        time += dt;
        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT);

        vs.modelViewProjectionMatrix = ortho(0, w(), 0, h(), 0, 10);
        vs.timePassed += dt;

        //partial update
        //vbo.addChange(al(new Ikogl_8_Vd(v3( w, MyMath.sin(time * 10) * h * 0.5f, 0))), 1);
        //whole data update
        vbo.reload(al(new Ikogl_8_Vd(v3(-w(), -h(), 0)), new Ikogl_8_Vd(v3(w(), MyMath.sin(time * 10) * h() * 0.5f, 0)), new Ikogl_8_Vd(v3( 0, h(), 0))));

        program.setInput(vbo);
        program.enable();
        indices.enable();
        program.disable();

        super.onTick(dt);
    }
}
