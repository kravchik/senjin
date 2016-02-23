package yk.senjin.examples.ssao;

import yk.jcommon.collections.YList;
import yk.jcommon.fastgeom.Matrix4;
import yk.jcommon.fastgeom.Vec3f;
import yk.jcommon.fastgeom.Vec4f;
import yk.jcommon.utils.Rnd;
import yk.senjin.*;
import yk.senjin.examples.ds.DeferredDataF;
import yk.senjin.examples.ds.PoconuvV;
import yk.senjin.examples.ds.PoconuvVi;
import yk.senjin.examples.ds.PosuvV;
import yk.senjin.shaders.gshader.GShader;
import yk.senjin.shaders.gshader.ReflectionVBO;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static yk.jcommon.collections.YArrayList.al;
import static yk.jcommon.fastgeom.Matrix4.ortho;
import static yk.jcommon.fastgeom.Vec3f.v3;
import static yk.jcommon.fastgeom.Vec4f.v4;
import static yk.jcommon.utils.IO.readImage;
import static yk.senjin.examples.blend.WatchBlend.cameraDraw;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 09/12/15
 * Time: 12:09
 */
public class WatchSsao implements LoadTickUnload<WatchReloadable> {

    public PosuvV hdrV;
    public DeferredShadeSsao hdrF;
    public GShader hdrProgram;

    public DeferredDataF specularF;
    public PoconuvV specularV;
    public GShader specularProgram;

    public ReflectionVBO vbo1;
    public SomeTexture textureJfdi;
    public DrawIndicesShort indices;
    public FrameBuffer fbo1;
    private int fboSize;


    public static void main(String[] args) {
        new WatchReloadable(new WatchSsao()) {{
            SIMPLE_AA = false;
        }};
    }

    @Override
    public void onLoad(WatchReloadable watch) {
        specularF = new DeferredDataF();
        specularV = new PoconuvV();
        specularProgram = new GShader(specularV, specularF).runtimeReload();

        hdrF = new DeferredShadeSsao();
        hdrV = new PosuvV();
        hdrV.modelViewProjectionMatrix = ortho(-1, 1, 1, -1, 1, -1);
        hdrProgram = new GShader(hdrV, hdrF).runtimeReload();

        textureJfdi = new SomeTexture(readImage("jfdi.png"));

        YList<PoconuvVi> all = al();
        Rnd rnd = new Rnd(0);
        for (int i = 0; i < 10; i++) {
            all.addAll(makeCube(rnd.nextVec3f().mul(5), v3(1, 1, 1), v4(1, 1, 1, 1)));
        }
        vbo1 = new ReflectionVBO(all);

        vbo1.upload();
        indices = DrawIndicesShort.simple(all.size(), GL_QUADS);

        fboSize = 1024;
        SomeTexture renderTexture1 = new SomeTexture();
        renderTexture1.internalformat = GL_RGBA32F;
        renderTexture1.init(fboSize, fboSize);

        SomeTexture renderTexture2 = new SomeTexture();
        renderTexture2.internalformat = GL_RGBA32F;
        renderTexture2.init(fboSize, fboSize);
        renderTexture2.magFilter = GL_NEAREST;
        renderTexture2.minFilter = GL_NEAREST;

        SomeTexture renderTexture3 = new SomeTexture();
        renderTexture3.internalformat = GL_RGBA32F;
        renderTexture3.init(fboSize, fboSize);
        renderTexture3.magFilter = GL_NEAREST;
        renderTexture3.minFilter = GL_NEAREST;

        fbo1 = new FrameBuffer();
        fbo1.initFBO(renderTexture1, renderTexture2, renderTexture3);
    }

    private YList<PoconuvVi> makeCube(Vec3f pos, Vec3f size, Vec4f color) {
        return DDDUtils.CUBE.flatMap(q -> q.map((PoconuvVi poco) -> new PoconuvVi(poco.pos.mul(size).add(pos), poco.normal, color, poco.uv)));
    }

    @Override
    public void onTick(WatchReloadable watch, float dt) {

        //scene -> fbo1
        fbo1.beginRenderToFbo();
        specularV.modelViewProjectionMatrix = watch.camModelViewProjectionMatrix;
        specularV.modelViewMatrix = watch.camModelViewMatrix;
        specularV.normalMatrix = watch.camNormalMatrix.get33();
        specularF.txt.set(textureJfdi);
        specularF.textureStrength = 0;
        cameraDraw(specularProgram, vbo1, indices, textureJfdi);
        fbo1.endRenderToFbo();

        //fbo1 -> standard frame (with result blur)
        fbo1.textures.get(0).enable(0);
        fbo1.textures.get(1).enable(1);
        fbo1.textures.get(2).enable(2);
        hdrF.txt1.set(fbo1.textures.get(0));
        hdrF.txt2.set(fbo1.textures.get(1));
        hdrF.txt3.set(fbo1.textures.get(2));
//        hdrF.csLightDir = new Vec3f(1, 1, 1);
        hdrF.csLightDir = watch.camNormalMatrix.multiply(v4(1, 0.9f, 0.8f, 0)).getXyz().normalized();

//        cameraDraw(blendProgram, ???, ???, fbo2.texture);
        hdrV.modelViewProjectionMatrix = Matrix4.identity();
        hdrProgram.enable();
        FrameBuffer.renderFBO2(watch.w, watch.h);
        hdrProgram.disable();
        fbo1.textures.get(2).disable();
        fbo1.textures.get(1).disable();
        fbo1.textures.get(0).disable();
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
