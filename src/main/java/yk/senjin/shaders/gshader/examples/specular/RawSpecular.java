package yk.senjin.shaders.gshader.examples.specular;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import yk.jcommon.fastgeom.Matrix4;
import yk.jcommon.fastgeom.Vec3f;
import yk.senjin.DrawIndicesShort;
import yk.senjin.SomeTexture;
import yk.senjin.shaders.gshader.GShader;
import yk.senjin.shaders.gshader.ReflectionVBO;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static yk.jcommon.collections.YArrayList.al;
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
        GShader shaderProgram = new GShader(vertexShader, fragmentShader);
        //texture
        SomeTexture texture = new SomeTexture(readImage("jfdi.png"));
        //data
        ReflectionVBO vbo1 = new ReflectionVBO();
        vbo1.bindToShader(shaderProgram);
        vbo1.setData(al(
                new SpecularVi(v3(-5, -5, 0), v3(-1,-1, 1).normalized(), v2(0, 1)),
                new SpecularVi(v3( 5, -5, 0), v3( 1,-1, 1).normalized(), v2(1, 1)),
                new SpecularVi(v3( 5,  5, 0), v3( 1, 1, 1).normalized(), v2(1, 0)),
                new SpecularVi(v3(-5,  5, 0), v3(-1, 1, 1).normalized(), v2(0, 0))));
        DrawIndicesShort indices = new DrawIndicesShort(GL_TRIANGLES, al(0, 2, 1, 0, 3, 2));
        vbo1.upload();

        while (!Display.isCloseRequested()) {
            //prepare matrices, just raw math, no need of glMatrixMode, glMatrixPush, etc
            Matrix4 camModelViewMatrix = new Matrix4().setIdentity().translate(new Vec3f(0, 0, -15));
            Matrix4 camNormalMatrix = camModelViewMatrix.invert().transpose();

            //set uniforms and textures
            vertexShader.modelViewProjectionMatrix = camModelViewMatrix.multiply(perspective(45.0f, (float) 400 / 400, 1, 1000.0f));
            vertexShader.normalMatrix = camNormalMatrix.get33();
            fragmentShader.shininess = 100;
            vertexShader.lightDir = new Vec3f(1, 1, 1).normalized();

            texture.enable(0);
            fragmentShader.txt.set(texture);
            shaderProgram.currentVBO = vbo1;

            shaderProgram.enable();//enable shader

            indices.draw();//draw data with enabled shader

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
