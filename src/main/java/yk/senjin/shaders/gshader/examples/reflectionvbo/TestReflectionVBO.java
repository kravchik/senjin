package yk.senjin.shaders.gshader.examples.reflectionvbo;

import org.lwjgl.LWJGLException;
import yk.jcommon.fastgeom.Vec3f;
import yk.senjin.DrawIndicesShort;
import yk.senjin.Simple3DWatch;
import yk.senjin.SomeTexture;
import yk.senjin.shaders.gshader.GShader;
import yk.senjin.shaders.gshader.ReflectionVBO;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static yk.jcommon.collections.YArrayList.al;
import static yk.jcommon.utils.IO.readImage;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 09/06/15
 * Time: 20:12
 */
public class TestReflectionVBO extends Simple3DWatch {

    private ShaderV vs;

    public TestReflectionVBO(int w, int h, boolean createThread) throws LWJGLException {
        super(w, h, createThread);
        SIMPLE_AA = true;
    }

    public static void main(String[] args) throws LWJGLException {
        new TestReflectionVBO(800, 600, true);
    }

    GShader shader1;
    ReflectionVBO vbo1;
    SomeTexture texture;
    DrawIndicesShort indices;
    ShaderF fs;

    @Override
    public void firstFrame() {
        fs = new ShaderF();
        vs = new ShaderV();
        shader1 = new GShader(vs, fs);
        texture = new SomeTexture(readImage("jfdi.png"));
        vbo1 = new ReflectionVBO();
        vbo1.bindToShader(shader1);

        vbo1.setData(al(new VSInput(new Vec3f(0, 0, 0)), new VSInput(new Vec3f(0, 10, 0)), new VSInput(new Vec3f(10, 10, 0))));
        indices = new DrawIndicesShort(GL_TRIANGLES, al(0, 1, 2));
    }

    @Override
    public void tick(float dt) {
        vs.mvp = camModelViewProjectionMatrix;
        vbo1.upload();
        texture.enable(0);

        fs.txt.set(texture);
        shader1.currentVBO = vbo1;
        shader1.enable();

        indices.draw();

        shader1.disable();
        texture.disable();

        super.tick(dt);
    }

}
