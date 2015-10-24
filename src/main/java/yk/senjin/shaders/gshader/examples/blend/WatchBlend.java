package yk.senjin.shaders.gshader.examples.blend;

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
 * Date: 23/10/15
 * Time: 21:21
 */
public class WatchBlend extends Simple3DWatch {

    public BlendV vs;
    public BlendF fs;
    public GShader shader1;
    public ReflectionVBO vbo1;
    public SomeTexture texture;
    public DrawIndicesShort indices;

    public static void main(String[] args) {
        new WatchBlend();
    }

    @Override
    public void firstFrame() {
        fs = new BlendF();
        vs = new BlendV();
        shader1 = new GShader(vs, fs);
        texture = new SomeTexture(readImage("jfdi.png"));
        vbo1 = new ReflectionVBO();
        vbo1.bindToShader(shader1);

        vbo1.setData(al(
                new BlendVi(new Vec3f(0, 0, 0),  new Vec2f(0, 1)),
                new BlendVi(new Vec3f(1, 0, 0), new Vec2f(1, 1)),
                new BlendVi(new Vec3f(1, 1, 0),new Vec2f(1, 0)),
                new BlendVi(new Vec3f(0, 1, 0), new Vec2f(0, 0))));
        indices = new DrawIndicesShort(GL_TRIANGLES, al(0, 1, 2, 0, 2, 3));
    }
    @Override

    public void tick(float dt) {
//        vs.modelViewProjectionMatrix = camModelViewProjectionMatrix;
//        vs.normalMatrix = camNormalMatrix.get33();
//        fs.shininess = 100;

//        vs.lightDir = new Vec3f(1, 1, 1).normalized();
        vbo1.upload();
        texture.enable(0);
        for (int i = 0; i < fs.koeff.length; i++) fs.koeff[i] = 1f/fs.koeff.length;
        fs.txt.set(texture);
        fs.kSize = 32;
        shader1.currentVBO = vbo1;

        shader1.enable();

        indices.draw();

        shader1.disable();
        texture.disable();

        super.tick(dt);
    }




}
