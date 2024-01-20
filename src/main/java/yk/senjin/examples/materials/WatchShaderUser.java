package yk.senjin.examples.materials;

import yk.senjin.examples.simple.SimpleLwjglRoutine;
import yk.senjin.examples.simple.stuff.Ikogl_8_Fs;
import yk.senjin.examples.simple.stuff.Ikogl_8_Vd;
import yk.senjin.examples.simple.stuff.Ikogl_8_Vs;
import yk.senjin.shaders.gshader.GProgram;
import yk.senjin.shaders.gshader.ShaderUserMap;
import yk.senjin.vbo.AVboShortIndices;
import yk.senjin.vbo.AVboTyped;

import static org.lwjgl.opengl.GL11.*;
import static yk.jcommon.fastgeom.Matrix4.ortho;
import static yk.jcommon.fastgeom.Vec3f.v3;

/**
 * Created by Yuri Kravchik on 26.09.18.
 */
public class WatchShaderUser extends SimpleLwjglRoutine {
    public static void main(String[] args) {
        new WatchShaderUser().start();
    }

    ShaderUserMap user1;
    ShaderUserMap user2;

    AVboTyped vbo;
    AVboShortIndices indices;

    @Override public void onFirstPass() {
        super.onFirstPass();
        GProgram program = GProgram.initFrom("src/main/java/", new Ikogl_8_Vs(), new Ikogl_8_Fs()).runtimeReload();
        vbo = new AVboTyped(new Ikogl_8_Vd(v3(-w(), -h(), 0)), new Ikogl_8_Vd(v3(w(),  0, 0)), new Ikogl_8_Vd(v3( 0, h(), 0)));

        user1 = new ShaderUserMap(program, vbo.inputType);
        user2 = new ShaderUserMap(program, vbo.inputType);

        indices = AVboShortIndices.simple(3, GL_TRIANGLES);

    }

    float timePassed;
    @Override public void onTick(float dt) {
        super.onTick(dt);
        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT);
        timePassed += dt;

        vbo.enable();
        user1.shaderParams.put("timePassed", timePassed);
        user1.shaderParams.put("modelViewProjectionMatrix", ortho(0, w(), 0, h(), 0, 10));
        user1.uniformsChanged = true;
        
        user1.enable();
        indices.enable();
        user1.disable();

        user2.shaderParams.put("timePassed", -timePassed);
        user2.shaderParams.put("modelViewProjectionMatrix", ortho(0, w(), 0, h(), 0, 10));
        user2.uniformsChanged = true;

        user2.enable();
        indices.enable();
        user2.disable();

        vbo.disable();
    }


}
