package yk.senjin.examples.simple;

import yk.senjin.FrameBuffer;
import yk.senjin.SomeTexture;
import yk.senjin.examples.simple.stuff.Ikogl_8_Fs;
import yk.senjin.examples.simple.stuff.Ikogl_8_Vd;
import yk.senjin.examples.simple.stuff.Ikogl_8_Vs;
import yk.senjin.shaders.gshader.GProgram;
import yk.senjin.vbo.AVboShortIndices;
import yk.senjin.vbo.AVboTyped;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static yk.jcommon.fastgeom.Matrix4.ortho;
import static yk.jcommon.fastgeom.Vec3f.v3;

/**
 * Created by Yuri Kravchik on 25.11.17.
 */
public class IKnowOpenGL_9_FrameBuffer2 extends SimpleLwjglRoutine {
    public static void main(String[] args) {
        new IKnowOpenGL_9_FrameBuffer2().main();
    }

    public Ikogl_8_Vs vs;
    public Ikogl_8_Fs fs;
    public GProgram program;

    AVboTyped vbo;
    AVboShortIndices indices;

    SomeTexture texture;
    FrameBuffer fb;

    @Override public void onFirstPass() {
        super.onFirstPass();
        program = GProgram.initFrom("src/main/java/", vs = new Ikogl_8_Vs(), fs = new Ikogl_8_Fs()).runtimeReload();
        vbo = new AVboTyped(new Ikogl_8_Vd(v3(-w, -h, 0)), new Ikogl_8_Vd(v3( w,  0, 0)), new Ikogl_8_Vd(v3( 0,  h, 0)));
        indices = AVboShortIndices.simple(3, GL_TRIANGLES);

        texture = new SomeTexture(20, 20);
        texture.magFilter = GL_NEAREST;
        fb = new FrameBuffer().initFBO(texture);
    }

    @Override public void onTick(float dt) {
        super.onTick(dt);
        fb.beginRenderToFbo();
        vs.modelViewProjectionMatrix = ortho(0, w, 0, h, 0, 10);
        vs.timePassed += dt;
        program.setInput(vbo);
        program.enable();
        indices.enable();
        program.disable();

        fb.endRenderToFbo();
        fb.render(w, h);
    }


}
