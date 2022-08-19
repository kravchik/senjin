package yk.senjin.examples.ssao.blurred;

import yk.jcommon.collections.YList;
import yk.jcommon.fastgeom.Matrix4;
import yk.jcommon.utils.Rnd;
import yk.senjin.*;
import yk.senjin.examples.ds.DeferredDataF;
import yk.senjin.examples.ds.PoconuvV;
import yk.senjin.examples.ds.PoconuvVi;
import yk.senjin.examples.ds.PosuvV;
import yk.senjin.shaders.gshader.GProgram;
import yk.senjin.vbo.AVboTyped;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL30.GL_RGB32F;
import static yk.jcommon.collections.YArrayList.al;
import static yk.jcommon.fastgeom.Matrix4.ortho;
import static yk.jcommon.fastgeom.Vec3f.v3;
import static yk.jcommon.utils.IO.readImage;
import static yk.senjin.examples.ssao.WatchSsao.makeCube;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 09/12/15
 * Time: 12:09
 */
public class WatchSsaoBlurred implements LoadTickUnload<WatchReloadable> {

    public GProgram<PoconuvV, DeferredDataF> defDataProgram;

    public GProgram<PosuvV, DeferredShadeSsao2> ssaoProgram;
    public GProgram<PosuvV, DefShaderFinal> finalProgram;

    public AVboTyped vbo1;
    public SomeTexture textureJfdi;
    public DrawIndicesShort indices;
    public FrameBuffer dataFrame;
    private int fboSize;

    public Blender2 blender = new Blender2();

    public static void main(String[] args) {
        new WatchReloadable(new WatchSsaoBlurred(), Blender2.class) {{
            SIMPLE_AA = false;
        }};
    }

    @Override
    public void onLoad(WatchReloadable watch) {
        defDataProgram = new GProgram<>(new PoconuvV(), new DeferredDataF()).link().runtimeReload();
        ssaoProgram = new GProgram<>(new PosuvV(), new DeferredShadeSsao2()).link().runtimeReload();
        ssaoProgram.vs.modelViewProjectionMatrix = ortho(-1, 1, -1, 1, 1, -1);
        finalProgram = new GProgram<>(new PosuvV(), new DefShaderFinal()).link().runtimeReload();

        textureJfdi = new SomeTexture(readImage("jfdi.png"));

        YList<PoconuvVi> all = al();
        Rnd rnd = new Rnd(0);
        for (int i = 0; i < 10; i++) {
            all.addAll(makeCube(rnd.nextVec3f().mul(5), v3(1, 1, 1), rnd.nextVec3f().mul(0.5f).add(0.5f).toVec4f(1)));
        }
        vbo1 = new AVboTyped(all);
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

        dataFrame = new FrameBuffer();
        dataFrame.initFBO(renderTexture1, renderTexture2, renderTexture3);

        blender.size = 512;
        blender.init();
    }

    @Override
    public void onTick(WatchReloadable watch, float dt) {

        //scene -> data
        dataFrame.beginRenderToFbo();
        defDataProgram.vs.modelViewProjectionMatrix = watch.camModelViewProjectionMatrix;
        defDataProgram.vs.modelViewMatrix = watch.camModelViewMatrix;
        defDataProgram.vs.normalMatrix = watch.camNormalMatrix.get33();
        defDataProgram.fs.txt.set(textureJfdi);
        defDataProgram.fs.textureStrength = 0;
        DDDUtils.cameraDraw(defDataProgram, vbo1, indices, textureJfdi);
        dataFrame.endRenderToFbo();

        blender.fbo1.beginRenderToFbo();

        //data -> ssao
        dataFrame.textures.get(0).enable(0);
        dataFrame.textures.get(1).enable(1);
        dataFrame.textures.get(2).enable(2);
        ssaoProgram.fs.txt1.set(dataFrame.textures.get(0));
        ssaoProgram.fs.txt2.set(dataFrame.textures.get(1));
        ssaoProgram.fs.txt3.set(dataFrame.textures.get(2));
        ssaoProgram.vs.modelViewProjectionMatrix = Matrix4.identity();

        ssaoProgram.enable();
        FrameBuffer.renderTexture0(blender.size, blender.size);
        ssaoProgram.disable();

        dataFrame.textures.get(2).disable();
        dataFrame.textures.get(1).disable();
        dataFrame.textures.get(0).disable();

        blender.fbo1.endRenderToFbo();




        //blending

        blender.depthTexture =  dataFrame.textures.get(2);
        blender.depthTexture.enable(2);
        blender.render21();
        blender.depthTexture.disable();













        //data + blended ssao -> standard frame
        dataFrame.textures.get(0).enable(0);
        dataFrame.textures.get(1).enable(1);
        dataFrame.textures.get(2).enable(2);
        blender.fbo1.textures.car().enable(3);

        finalProgram.fs.txt1.set(dataFrame.textures.get(0));
        finalProgram.fs.txt2.set(dataFrame.textures.get(1));
        finalProgram.fs.txt3.set(dataFrame.textures.get(2));
        finalProgram.fs.txt4.set(blender.fbo1.textures.car());

        finalProgram.vs.modelViewProjectionMatrix = Matrix4.identity();

        finalProgram.enable();
        FrameBuffer.renderTexture0(watch.w, watch.h);
        finalProgram.disable();

        blender.fbo1.textures.car().disable();
        dataFrame.textures.get(0).disable();
        dataFrame.textures.get(1).disable();
        dataFrame.textures.get(2).disable();


    }

    @Override
    public void onUnload() {
        ssaoProgram.release();
        defDataProgram.release();
        vbo1.release();
        textureJfdi.release();
        dataFrame.release();
    }
}
