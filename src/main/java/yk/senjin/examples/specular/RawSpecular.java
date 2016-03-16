package yk.senjin.examples.specular;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.Util;
import yk.jcommon.fastgeom.Matrix4;
import yk.jcommon.fastgeom.Vec3f;
import yk.senjin.SomeTexture;
import yk.senjin.shaders.gshader.GProgram;
import yk.senjin.shaders.gshader.ReflectionVBO;

import java.nio.ShortBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.glDrawRangeElements;
import static yk.jcommon.fastgeom.Matrix4.perspective;
import static yk.jcommon.fastgeom.Vec2f.v2;
import static yk.jcommon.fastgeom.Vec3f.v3;
import static yk.jcommon.utils.IO.readImage;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 22/10/15
 * Time: 21:06
 */
public class RawSpecular {


    public void start() throws Exception {

        Display.setDisplayMode(new DisplayMode(400, 400));
        Display.create();

        //shaders
        SpecularF fragmentShader = new SpecularF();
        SpecularV vertexShader = new SpecularV();
        GProgram shaderProgram = GProgram.initFromSrcMainJava(vertexShader, fragmentShader);
        //texture
        SomeTexture texture = new SomeTexture(readImage("jfdi.png"));
        //data
        ReflectionVBO vbo1 = new ReflectionVBO(
                new SpecularVi(v3(-5, -5, 0), v3(-1,-1, 1).normalized(), v2(0, 1)),
                new SpecularVi(v3( 5, -5, 0), v3( 1,-1, 1).normalized(), v2(1, 1)),
                new SpecularVi(v3( 5,  5, 0), v3( 1, 1, 1).normalized(), v2(1, 0)),
                new SpecularVi(v3(-5,  5, 0), v3(-1, 1, 1).normalized(), v2(0, 0)));
        vbo1.upload();

        ShortBuffer indexBuffer = BufferUtils.createShortBuffer(6);
        indexBuffer.put(new short[]{0, 2, 1, 0, 3, 2});
        indexBuffer.rewind();

        while (!Display.isCloseRequested()) {
            Util.checkGLError();
            glClearColor(1, 1, 1, 1);
            glClear(GL_COLOR_BUFFER_BIT);

            //prepare matrices, just raw math, no need of glMatrixMode, glMatrixPush, etc
            Matrix4 camModelViewMatrix = new Matrix4().setIdentity().translate(new Vec3f(0, 0, -12));
            vertexShader.modelViewProjectionMatrix = camModelViewMatrix.multiply(perspective(45.0f, (float) 400 / 400, 1, 1000.0f));
            vertexShader.normalMatrix = camModelViewMatrix.invert().transpose().get33();

            //set uniforms and textures
            fragmentShader.shininess = 100;
            vertexShader.lightDir = new Vec3f(2, 1, 3).normalized();
            shaderProgram.setInput(vbo1);
            texture.setSlot(0);
            texture.enable(0);
            fragmentShader.txt.set(texture);

            shaderProgram.enable();
            glDrawRangeElements(GL_TRIANGLES, 0, indexBuffer.limit(), indexBuffer);

            shaderProgram.disable();
            texture.disable();
            Display.update();
        }

        Display.destroy();
    }

    public static void main(String[] argv) throws Exception {
        RawSpecular displayExample = new RawSpecular();
        displayExample.start();
    }
}
