package yk.senjin.examples.ds;

import yk.jcommon.fastgeom.Matrix4;
import yk.jcommon.fastgeom.Vec2f;
import yk.jcommon.fastgeom.Vec3f;
import yk.jcommon.fastgeom.Vec4f;
import yk.senjin.*;
import yk.senjin.shaders.gshader.GProgram;
import yk.senjin.vbo.AVboTyped;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static yk.jcommon.fastgeom.Matrix4.ortho;
import static yk.jcommon.utils.IO.readImage;
import static yk.ycollections.YArrayList.al;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 09/12/15
 * Time: 12:09
 */
public class WatchDeferredShading implements LoadTickUnload<WatchReloadable> {
    public PosuvV hdrV;
    public DeferredShadeF hdrF;
    public GProgram hdrProgram;

    public DeferredDataF specularF;
    public PoconuvV specularV;
    public GProgram specularProgram;

    public AVboTyped vbo1;
    public SomeTexture textureJfdi;
    public DrawIndicesShort indices;
    public FrameBuffer fbo1;
    private int fboSize;


    public static void main(String[] args) {
        new WatchReloadable(new WatchDeferredShading()).run();
    }

    @Override
    public void onLoad(WatchReloadable watch) {
        specularF = new DeferredDataF();
        specularV = new PoconuvV();
        specularProgram = new GProgram(specularV, specularF).link().runtimeReload();

        hdrF = new DeferredShadeF();
        hdrV = new PosuvV();
        hdrV.modelViewProjectionMatrix = ortho(-1, 1, 1, -1, 1, -1);
        hdrProgram = new GProgram(hdrV, hdrF).link().runtimeReload();

        textureJfdi = new SomeTexture(readImage("jfdi.png"));
        vbo1 = new AVboTyped(
                new PoconuvVi(new Vec3f(0, 0, 0),  new Vec3f(-1, -1, 1).normalized(), new Vec2f(0, 1)),
                new PoconuvVi(new Vec3f(10, 0, 0), new Vec3f( 1, -1, 1).normalized(), new Vec2f(1, 1)),
                new PoconuvVi(new Vec3f(10, 10, 0),new Vec3f( 1,  1, 1).normalized(), new Vec2f(1, 0)),
                new PoconuvVi(new Vec3f(0, 10, 0), new Vec3f(-1,  1, 1).normalized(), new Vec2f(0, 0)));
        indices = new DrawIndicesShort(GL_TRIANGLES, al(0, 1, 2, 0, 2, 3));

        fboSize = 1024;
        SomeTexture renderTexture1 = new SomeTexture();
        renderTexture1.internalformat = GL_RGBA32F;
        renderTexture1.init(fboSize, fboSize);

        SomeTexture renderTexture2 = new SomeTexture();
        renderTexture2.internalformat = GL_RGBA32F;
        renderTexture2.init(fboSize, fboSize);

        SomeTexture renderTexture3 = new SomeTexture();
        renderTexture3.internalformat = GL_RGBA32F;
        renderTexture3.init(fboSize, fboSize);

        fbo1 = new FrameBuffer();
        fbo1.initFBO(renderTexture1, renderTexture2, renderTexture3);
    }

    @Override
    public void onTick(WatchReloadable watch, float dt) {
        //scene -> fbo1
        fbo1.beginRenderToFbo();
        specularV.modelViewProjectionMatrix = watch.camModelViewProjectionMatrix;
        specularV.modelViewMatrix = watch.camModelViewMatrix;
        specularV.normalMatrix = watch.camNormalMatrix.get33();
        specularF.txt.set(textureJfdi);
        DDDUtils.cameraDraw(specularProgram, vbo1, indices, textureJfdi);
        watch.drawAxis();
        fbo1.endRenderToFbo();

        //fbo1 -> standard frame (with result blur)
        fbo1.textures.get(0).enable(0);
        fbo1.textures.get(1).enable(1);
        fbo1.textures.get(2).enable(2);
        hdrF.txt1.set(fbo1.textures.get(0));
        hdrF.txt2.set(fbo1.textures.get(1));
        hdrF.txt3.set(fbo1.textures.get(2));
//        hdrF.csLightDir = new Vec3f(1, 1, 1);
        hdrF.csLightDir = watch.camNormalMatrix.multiply(new Vec4f(1, 1, 0, 1)).getXyz().normalized();

//        cameraDraw(blendProgram, ???, ???, fbo2.texture);
        hdrV.modelViewProjectionMatrix = Matrix4.identity();
        hdrProgram.enable();
        FrameBuffer.renderTexture0(watch.sizePixels);
        hdrProgram.disable();
        fbo1.textures.get(2).disable();
        fbo1.textures.get(1).disable();
        fbo1.textures.get(0).disable();

        //FloatBuffer buffer = BufferUtils.createFloatBuffer(4);
        //glReadPixels(watch.w / 2, watch.h / 2, 1, 1, GL_DEPTH_COMPONENT, GL_FLOAT, buffer);
    }

    @Override
    public void onUnload() {
        hdrProgram.release();
        specularProgram.release();
        vbo1.release();
        textureJfdi.release();
        fbo1.release();
    }
}
