package yk.senjin.examples.hdr;

import yk.jcommon.fastgeom.Matrix4;
import yk.jcommon.fastgeom.Vec2f;
import yk.jcommon.fastgeom.Vec3f;
import yk.senjin.*;
import yk.senjin.shaders.gshader.GShader;
import yk.senjin.shaders.gshader.ReflectionVBO;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static yk.jcommon.collections.YArrayList.al;
import static yk.jcommon.fastgeom.Matrix4.ortho;
import static yk.jcommon.utils.IO.readImage;
import static yk.senjin.examples.blend.WatchBlend.cameraDraw;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 09/12/15
 * Time: 12:09
 */
public class WatchHdr implements LoadTickUnload<WatchReloadable> {

    public HdrV vs;
    public HdrF fs;
    public GShader shader;

    public SpecularF specularF;
    public SpecularV specularV;
    public GShader specularProgram;

    public ReflectionVBO vbo1;
    public SomeTexture textureJfdi;
    public DrawIndicesShort indices;
    public FrameBuffer fbo1;
    private int fboSize;


    public static void main(String[] args) {
        new WatchReloadable(new WatchHdr()) {{
            SIMPLE_AA = false;
        }};
    }

    @Override
    public void onLoad(WatchReloadable watch) {
        specularF = new SpecularF();
        specularV = new SpecularV();
        specularProgram = new GShader(specularV, specularF).runtimeReload();

        fs = new HdrF();
        vs = new HdrV();
        vs.modelViewProjectionMatrix = ortho(-1, 1, 1, -1, 1, -1);
        shader = new GShader(vs, fs).runtimeReload();

        textureJfdi = new SomeTexture(readImage("jfdi.png"));
        vbo1 = new ReflectionVBO(
                new SpecularVi(new Vec3f(0, 0, 0),  new Vec3f(-1, -1, 1).normalized(), new Vec2f(0, 1)),
                new SpecularVi(new Vec3f(10, 0, 0), new Vec3f( 1, -1, 1).normalized(), new Vec2f(1, 1)),
                new SpecularVi(new Vec3f(10, 10, 0),new Vec3f( 1,  1, 1).normalized(), new Vec2f(1, 0)),
                new SpecularVi(new Vec3f(0, 10, 0), new Vec3f(-1,  1, 1).normalized(), new Vec2f(0, 0)));
        vbo1.upload();
        indices = new DrawIndicesShort(GL_TRIANGLES, al(0, 1, 2, 0, 2, 3));

        fboSize = 1024;
        SomeTexture renderTexture1 = new SomeTexture();
        renderTexture1.internalformat = GL_RGBA32F;
        renderTexture1.init(fboSize, fboSize);
        SomeTexture renderTexture2 = new SomeTexture();
        renderTexture2.internalformat = GL_RGBA32F;
        renderTexture2.init(fboSize, fboSize);
        fbo1 = new FrameBuffer();
        fbo1.initFBO(renderTexture1, renderTexture2);
    }

    @Override
    public void onTick(WatchReloadable watch, float dt) {
        //scene -> fbo1
        fbo1.beginRenderToFbo();
        specularV.modelViewProjectionMatrix = watch.camModelViewProjectionMatrix;
        specularV.normalMatrix = watch.camNormalMatrix.get33();
        specularV.lightDir = new Vec3f(1, 1, 1).normalized();
        specularF.txt.set(textureJfdi);
        cameraDraw(specularProgram, vbo1, indices, textureJfdi);
        watch.drawAxis();
        fbo1.endRenderToFbo();

        //fbo1 -> standard frame (with result blur)
        fbo1.textures.car().enable(0);
        fbo1.textures.cadr().enable(1);
        fs.txt1.set(fbo1.textures.car());
        fs.txt2.set(fbo1.textures.cadr());
//        cameraDraw(blendProgram, ???, ???, fbo2.texture);
        vs.modelViewProjectionMatrix = Matrix4.identity();
        shader.enable();
        FrameBuffer.renderFBO2(watch.w, watch.h);
        shader.disable();
        fbo1.textures.car().disable();
        fbo1.textures.cadr().disable();
    }

    @Override
    public void onUnload() {
        shader.release();
        specularProgram.release();
        vbo1.release();
        textureJfdi.release();
        fbo1.release();
    }
}
