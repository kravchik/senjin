package yk.senjin.examples.blend;

import org.lwjgl.LWJGLException;
import yk.jcommon.fastgeom.Vec2f;
import yk.jcommon.fastgeom.Vec3f;
import yk.jcommon.utils.Rnd;
import yk.senjin.DrawIndicesShort;
import yk.senjin.FrameBuffer;
import yk.senjin.Simple3DWatch;
import yk.senjin.SomeTexture;
import yk.senjin.examples.specular.SpecularF;
import yk.senjin.examples.specular.SpecularV;
import yk.senjin.examples.specular.SpecularVi;
import yk.senjin.shaders.gshader.GShader;
import yk.senjin.shaders.gshader.ReflectionVBO;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static yk.jcommon.collections.YArrayList.al;
import static yk.jcommon.fastgeom.Matrix4.ortho;
import static yk.jcommon.utils.IO.readImage;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 23/10/15
 * Time: 21:21
 */
public class WatchBlend extends Simple3DWatch {

    //TODO don't use deprecated gl_Vertex
    //TODO reuse SimpleFBO in SimpleAA

    public BlendV blendV;
    public BlendF blendF;
    public GShader blendProgram;

    public SpecularF specularF;
    public SpecularV specularV;
    public GShader specularProgram;

    public ReflectionVBO vbo1;
    public SomeTexture textureJfdi;
    public DrawIndicesShort indices;
    public FrameBuffer fbo1;
    public FrameBuffer fbo2;
    private int fboSize;

    public WatchBlend() {
        super(600, 600, true);
        SIMPLE_AA =  false;
    }

    public static void main(String[] args) {
        new WatchBlend();
    }

    @Override
    public void firstFrame() {
        specularF = new SpecularF();
        specularV = new SpecularV();
        specularProgram = new GShader(specularV, specularF);

        blendF = new BlendF();
        blendV = new BlendV();
        blendV.modelViewProjectionMatrix = ortho(-1, 1, 1, -1, 1, -1);
        blendProgram = new GShader(blendV, blendF);
        blendF.kSize = 8;
        for (int i = 0; i < blendF.koeff.length; i++) blendF.koeff[i] = (Rnd.instance.nextFloat()+0.5f)/ blendF.kSize;

        textureJfdi = new SomeTexture(readImage("jfdi.png"));
        vbo1 = new ReflectionVBO(
                new SpecularVi(new Vec3f(0, 0, 0),  new Vec3f(-1, -1, 1).normalized(), new Vec2f(0, 0)),
                new SpecularVi(new Vec3f(10, 0, 0), new Vec3f( 1, -1, 1).normalized(), new Vec2f(1, 0)),
                new SpecularVi(new Vec3f(10, 10, 0),new Vec3f( 1,  1, 1).normalized(), new Vec2f(1, 1)),
                new SpecularVi(new Vec3f(0, 10, 0), new Vec3f(-1,  1, 1).normalized(), new Vec2f(0, 1)));
        vbo1.upload();
        indices = new DrawIndicesShort(GL_TRIANGLES, al(0, 1, 2, 0, 2, 3));

        fboSize = 1024;
        SomeTexture renderTexture = new SomeTexture();
        renderTexture.init(fboSize, fboSize);
        fbo1 = new FrameBuffer();
        fbo1.initFBO(renderTexture);
        SomeTexture renderTexture2 = new SomeTexture();
        renderTexture2.init(fboSize, fboSize);
        fbo2 = new FrameBuffer();
        fbo2.initFBO(renderTexture2);
    }

    @Override
    protected void commonTick(float dt) throws LWJGLException {
        if (firstFrame) onFirstFrame();
        fbo1.beginRenderToFbo();
        onRender(dt);
    }

    @Override
    public void tick(float dt) {
        //scene -> fbo1
        specularV.modelViewProjectionMatrix = camModelViewProjectionMatrix;
        specularV.normalMatrix = camNormalMatrix.get33();
        specularV.lightDir = new Vec3f(1, 1, 1).normalized();
        specularF.txt.set(textureJfdi);
        cameraDraw(specularProgram, vbo1, indices, textureJfdi);
        fbo1.endRenderToFbo();

        // fbo1 -> fbo2 (with horizontal blur)
        fbo2.beginRenderToFbo();
        fbo1.textures.car().enable(0);
        blendF.txt.set(fbo1.textures.car());
        blendF.direction = new Vec2f(0.003f, 0f);
//        cameraDraw(fbo2, blendProgram, ???, ???, fbo1.texture);
        blendProgram.shader.enable();
        FrameBuffer.renderFBO2(fboSize, fboSize);
        blendProgram.shader.disable();
        fbo1.textures.car().disable();
        fbo2.endRenderToFbo();

        //fbo2 -> standard frame (with result blur)
        fbo2.textures.car().enable(0);
        blendF.txt.set(fbo2.textures.car());
        blendF.direction = new Vec2f(0f, 0.003f);
//        cameraDraw(blendProgram, ???, ???, fbo2.texture);
        blendProgram.shader.enable();
        FrameBuffer.renderFBO2(w, h);
        blendProgram.shader.disable();
        fbo2.textures.car().disable();
    }

    public static void cameraDraw(FrameBuffer fbo, GShader shader1, ReflectionVBO vbo1, DrawIndicesShort indices, SomeTexture... textures) {
        fbo.beginRenderToFbo();
        for (int i = 0; i < textures.length; i++) textures[i].enable(i);
        shader1.setInput(vbo1);
        shader1.enable();
        indices.draw();
        shader1.disable();
        for (int i = textures.length-1; i >= 0; i--) textures[i].disable();
        fbo.endRenderToFbo();
    }

    public static void cameraDraw(GShader shader1, ReflectionVBO vbo1, DrawIndicesShort indices, SomeTexture... textures) {
        for (int i = 0; i < textures.length; i++) textures[i].enable(i);
        shader1.setInput(vbo1);
        shader1.enable();
        indices.draw();
        shader1.disable();
        for (int i = textures.length-1; i >= 0; i--) textures[i].disable();
    }



}
