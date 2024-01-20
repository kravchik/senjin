package yk.senjin.examples.simple;

import org.lwjgl.BufferUtils;
import yk.senjin.examples.simple.stuff.Ikogl_8_Fs;
import yk.senjin.examples.simple.stuff.Ikogl_8_Vd;
import yk.senjin.examples.simple.stuff.Ikogl_8_Vs;
import yk.senjin.shaders.gshader.GProgram;
import yk.senjin.vbo.AVboTyped;

import java.nio.ShortBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.glDrawRangeElements;
import static yk.jcommon.fastgeom.Matrix4.ortho;
import static yk.jcommon.fastgeom.Vec3f.v3;

/**
 * Created by Yuri Kravchik on 17.11.17.
 */
public class IKnowOpenGL_8_VBO extends SimpleLwjglRoutine {
    public static void main(String[] args) {
        new IKnowOpenGL_8_VBO().start();
    }

    public Ikogl_8_Vs vs;
    public Ikogl_8_Fs fs;
    public GProgram program;

    AVboTyped vbo;
    ShortBuffer indexBuffer;

    @Override public void onFirstPass() {
        super.onFirstPass();
        fs = new Ikogl_8_Fs();
        vs = new Ikogl_8_Vs();
        program = GProgram.initFrom("src/main/java/", vs, fs).runtimeReload();

        vbo = new AVboTyped(new Ikogl_8_Vd(v3(-w(), -h(), 0)), new Ikogl_8_Vd(v3(w(),  0, 0)), new Ikogl_8_Vd(v3( 0, h(), 0)));
        indexBuffer = BufferUtils.createShortBuffer(3);
        indexBuffer.put(new short[]{0, 1, 2});
        indexBuffer.rewind();

    }
    
    @Override public void onTick(float dt) {
        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT);

        vs.modelViewProjectionMatrix = ortho(0, w(), 0, h(), 0, 10);
        vs.timePassed += dt;

        program.setInput(this.vbo);
        program.enable();
        glDrawRangeElements(GL_TRIANGLES, 0, indexBuffer.limit(), indexBuffer);

        program.disable();


        super.onTick(dt);
    }
}
