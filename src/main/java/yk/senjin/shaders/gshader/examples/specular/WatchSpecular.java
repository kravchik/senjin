package yk.senjin.shaders.gshader.examples.specular;

import org.lwjgl.LWJGLException;
import yk.jcommon.fastgeom.Vec2f;
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
 * Date: 22/10/15
 * Time: 21:06
 */
public class WatchSpecular extends Simple3DWatch {

    private SpecularV vs;

    public WatchSpecular(int w, int h, boolean createThread) throws LWJGLException {
        super(w, h, createThread);
        SIMPLE_AA = true;
    }

    public static void main(String[] args) throws LWJGLException {
        new WatchSpecular(800, 600, true);
    }

    GShader shader1;
    ReflectionVBO vbo1;
    SomeTexture texture;
    DrawIndicesShort indices;
    SpecularF fs;

    @Override
    public void firstFrame() {
        fs = new SpecularF();
        vs = new SpecularV();
        shader1 = new GShader(vs, fs);
        texture = new SomeTexture(readImage("jfdi.png"));
        vbo1 = new ReflectionVBO();
        vbo1.bindToShader(shader1);

        vbo1.setData(al(
                new SpecularVi(new Vec3f(0, 0, 0),  new Vec3f(-1, -1, 1).normalized(), new Vec2f(0, 0)),
                new SpecularVi(new Vec3f(10, 0, 0), new Vec3f( 1, -1, 1).normalized(), new Vec2f(1, 0)),
                new SpecularVi(new Vec3f(10, 10, 0),new Vec3f( 1,  1, 1).normalized(), new Vec2f(1, 1)),
                new SpecularVi(new Vec3f(0, 10, 0), new Vec3f(-1,  1, 1).normalized(), new Vec2f(0, 1))));
        indices = new DrawIndicesShort(GL_TRIANGLES, al(0, 1, 2, 0, 2, 3));
    }

    @Override
    public void tick(float dt) {
        vs.modelViewProjectionMatrix = camModelViewProjectionMatrix;
        vs.normalMatrix = camNormalMatrix.get33();
        fs.shininess = 100;

        vs.lightDir = new Vec3f(1, 1, 1).normalized();
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
