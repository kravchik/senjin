package yk.senjin.examples.specular;

import org.lwjgl.LWJGLException;
import yk.jcommon.fastgeom.Vec2f;
import yk.jcommon.fastgeom.Vec3f;
import yk.senjin.DrawIndicesShort;
import yk.senjin.Simple3DWatch;
import yk.senjin.SomeTexture;
import yk.senjin.shaders.gshader.GProgram;
import yk.senjin.vbo.ReflectionVBO;

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

    public WatchSpecular(int w, int h, boolean createThread) throws LWJGLException {
        super(w, h, createThread);
    }

    public static void main(String[] args) throws LWJGLException {
        new WatchSpecular(800, 600, true);
    }

    public SpecularV vs;
    public GProgram shader1;
    public ReflectionVBO vbo1;
    public SomeTexture texture;
    public DrawIndicesShort indices;
    public SpecularF fs;

    @Override
    public void firstFrame() {
        fs = new SpecularF();
        vs = new SpecularV();
        shader1 = GProgram.initFromSrcMainJava(vs, fs).runtimeReload();
        texture = new SomeTexture(readImage("jfdi.png"));
        vbo1 = new ReflectionVBO(
                new SpecularVi(new Vec3f(0, 0, 0),  new Vec3f(-1, -1, 1).normalized(), new Vec2f(0, 1)),
                new SpecularVi(new Vec3f(10, 0, 0), new Vec3f( 1, -1, 1).normalized(), new Vec2f(1, 1)),
                new SpecularVi(new Vec3f(10, 10, 0),new Vec3f( 1,  1, 1).normalized(), new Vec2f(1, 0)),
                new SpecularVi(new Vec3f(0, 10, 0), new Vec3f(-1,  1, 1).normalized(), new Vec2f(0, 0)));
        indices = new DrawIndicesShort(GL_TRIANGLES, al(0, 1, 2, 0, 2, 3));
    }

    @Override
    public void tick(float dt) {
        vs.modelViewProjectionMatrix = camModelViewProjectionMatrix;
        vs.modelViewMatrix = camModelViewMatrix;
        vs.normalMatrix = camNormalMatrix.get33();
        fs.shininess = 100;

        vs.lightDir = new Vec3f(1, 1, 1).normalized();
        vbo1.upload();
        texture.enable(0);

        fs.txt.set(texture);
        shader1.setInput(vbo1);

        shader1.enable();

        indices.draw();

        shader1.disable();
        texture.disable();

        super.tick(dt);
    }
}
