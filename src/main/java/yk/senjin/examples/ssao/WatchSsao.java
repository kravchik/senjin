package yk.senjin.examples.ssao;

import yk.jcommon.fastgeom.Matrix4;
import yk.jcommon.fastgeom.Vec3f;
import yk.jcommon.fastgeom.Vec4f;
import yk.jcommon.utils.Rnd;
import yk.senjin.*;
import yk.senjin.examples.ds.DeferredDataF;
import yk.senjin.examples.ds.PoconuvV;
import yk.senjin.examples.ds.PoconuvVi;
import yk.senjin.examples.ds.PosuvV;
import yk.senjin.shaders.gshader.GProgram;
import yk.senjin.vbo.AVboTyped;
import yk.ycollections.YList;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL30.GL_RGB32F;
import static yk.jcommon.fastgeom.Matrix4.ortho;
import static yk.jcommon.fastgeom.Vec3f.v3;
import static yk.jcommon.fastgeom.Vec4f.v4;
import static yk.jcommon.utils.IO.readImage;
import static yk.ycollections.YArrayList.al;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 09/12/15
 * Time: 12:09
 */
public class WatchSsao implements LoadTickUnload<WatchReloadable> {

    public GProgram<PosuvV, DeferredShadeSsao> ssaoProgram;
    public GProgram<PoconuvV, DeferredDataF> defDataProgram;

    public AVboTyped vbo1;
    public SomeTexture textureJfdi;
    public DrawIndicesShort indices;
    public FrameBuffer fbo1;
    public FrameBuffer ssaoBuffer;
    private int fboSize;


    public static void main(String[] args) {
        new WatchReloadable(new WatchSsao()) {{
            SIMPLE_AA = false;
        }};
    }

    @Override
    public void onLoad(WatchReloadable watch) {
        defDataProgram = new GProgram(new PoconuvV(), new DeferredDataF()).runtimeReload();
        ssaoProgram = new GProgram<>(new PosuvV(), new DeferredShadeSsao()).runtimeReload();
        ssaoProgram.vs.modelViewProjectionMatrix = ortho(-1, 1, 1, -1, 1, -1);

        textureJfdi = new SomeTexture(readImage("jfdi.png"));

        YList<PoconuvVi> all = al();
        Rnd rnd = new Rnd(0);
        for (int i = 0; i < 10; i++) {
            all.addAll(makeCube(rnd.nextVec3f().mul(5), v3(1, 1, 1), rnd.nextVec3f().mul(0.5f).add(0.5f).toVec4f(1)));
        }
        vbo1 = new AVboTyped(PoconuvV.class, all.size());
        vbo1.addChange(all, 0);
        indices = DrawIndicesShort.simple(all.size(), GL_QUADS);


        fboSize = 1024;
        SomeTexture renderTexture1 = new SomeTexture();
        renderTexture1.internalformat = GL_RGB32F;
        renderTexture1.init(fboSize, fboSize);

        SomeTexture renderTexture2 = new SomeTexture();
        renderTexture2.internalformat = GL_RGB32F;
        renderTexture2.init(fboSize, fboSize);
        renderTexture2.magFilter = GL_NEAREST;
        renderTexture2.minFilter = GL_NEAREST;

        SomeTexture renderTexture3 = new SomeTexture();
        renderTexture3.internalformat = GL_RGB32F;
        renderTexture3.init(fboSize, fboSize);
        renderTexture3.magFilter = GL_NEAREST;
        renderTexture3.minFilter = GL_NEAREST;

        fbo1 = new FrameBuffer();
        fbo1.initFBO(renderTexture1, renderTexture2, renderTexture3);

        SomeTexture ssaoTexture = new SomeTexture();
        ssaoTexture.init(256, 256);
        ssaoBuffer = new FrameBuffer();
        ssaoBuffer.initFBO(ssaoTexture);
    }

    public static YList<PoconuvVi> makeCube(Vec3f pos, Vec3f size, Vec4f color) {
        return DDDUtils.CUBE.flatMap(q -> q.map((PoconuvVi poco) -> new PoconuvVi(poco.pos.mul(size).add(pos), poco.normal, color, poco.uv)));
    }

    @Override
    public void onTick(WatchReloadable watch, float dt) {

        //scene -> fbo1
        fbo1.beginRenderToFbo();
        defDataProgram.vs.modelViewProjectionMatrix = watch.camModelViewProjectionMatrix;
        defDataProgram.vs.modelViewMatrix = watch.camModelViewMatrix;
        defDataProgram.vs.normalMatrix = watch.camNormalMatrix.get33();
        defDataProgram.fs.txt.set(textureJfdi);
        defDataProgram.fs.textureStrength = 0;
        DDDUtils.cameraDraw(defDataProgram, vbo1, indices, textureJfdi);
        fbo1.endRenderToFbo();


        //fbo1 -> ssao texture
        ssaoBuffer.beginRenderToFbo();
        ssaoBuffer.endRenderToFbo();

        //blur ssao texture



        //fbo1 -> standard frame
        fbo1.textures.get(0).enable(0);
        fbo1.textures.get(1).enable(1);
        fbo1.textures.get(2).enable(2);
        ssaoProgram.fs.txt1.set(fbo1.textures.get(0));
        ssaoProgram.fs.txt2.set(fbo1.textures.get(1));
        ssaoProgram.fs.txt3.set(fbo1.textures.get(2));
        ssaoProgram.fs.csLightDir = watch.camNormalMatrix.multiply(v4(1, 0.9f, 0.8f, 0)).getXyz().normalized();

        ssaoProgram.vs.modelViewProjectionMatrix = Matrix4.identity();
        ssaoProgram.enable();
        FrameBuffer.renderTexture0(watch.w, watch.h);
        ssaoProgram.disable();
        fbo1.textures.get(2).disable();
        fbo1.textures.get(1).disable();
        fbo1.textures.get(0).disable();
    }

    @Override
    public void onUnload() {
        ssaoProgram.release();
        defDataProgram.release();
        vbo1.release();
        textureJfdi.release();
        fbo1.release();
    }
}
