package yk.senjin.examples.ssao.blurred;

import yk.jcommon.fastgeom.Vec2f;
import yk.senjin.FrameBuffer;
import yk.senjin.SomeTexture;
import yk.senjin.examples.blend.BlendV;
import yk.senjin.examples.blend.Blender1;
import yk.senjin.shaders.gshader.GProgram;

import static org.lwjgl.opengl.GL30.GL_RG32F;
import static yk.jcommon.fastgeom.Matrix4.ortho;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 24/02/16
 * Time: 14:41
 */
//monochrome blender with custom shader
public class Blender2 {
    //TODO don't use deprecated gl_Vertex
    //TODO use Poconuv instesad

    public int size = 512;

    public GProgram<BlendV, MonoBlenderF> blendProgram;

    public FrameBuffer fbo1;
    public FrameBuffer fbo2;
    
    public void beginRenderToFbo1() {
        fbo1.beginRenderToFbo();
    }

    public void endRenderToFbo1() {
        fbo1.endRenderToFbo();
    }

    public void init() {
        blendProgram = new GProgram<>(new BlendV(), new MonoBlenderF()).runtimeReload();
        blendProgram.vs.modelViewProjectionMatrix = ortho(-1, 1, 1, -1, 1, -1);
        fbo1 = new FrameBuffer();
        fbo2 = new FrameBuffer();

        //R - value
        //G - depth

        //initBuffers();

        //http://dev.theomader.com/gaussian-kernel-calculator/
        blendProgram.fs.koeff = Blender1.KERNEL_1_5;
//        blendProgram.fs.koeff = new float[]{0.1f, 0.15f, 0.2f, 0.15f, 0.1f};
//        blendProgram.fs.koeff = new float[]{1f, 1f, 1, 1f, 1f};
        blendProgram.fs.kSize = blendProgram.fs.koeff.length;

    }

    public void initBuffers() {
        SomeTexture renderTexture = new SomeTexture();
        renderTexture.internalformat = GL_RG32F;
        renderTexture.init(size, size);
        fbo1.initFBO(renderTexture);
        SomeTexture renderTexture2 = new SomeTexture();
        renderTexture2.internalformat = GL_RG32F;
        renderTexture2.init(size, size);
        fbo2.initFBO(renderTexture2);
    }

    public void renderToScreen(int w, int h) {
        renderToFbo2();

        //fbo2 -> standard frame (with result blur)
        fbo2.textures.car().enable(0);
//        blendF.txt.set(fbo2.textures.car());
//        blendF.direction = new Vec2f(0f, 1f/size);
//        cameraDraw(blendProgram, ???, ???, fbo2.texture);
        blendProgram.enable();
        FrameBuffer.renderFBO(w, h);
        blendProgram.disable();
        fbo2.textures.car().disable();
    }

    public void render21() {
        renderToFbo2();

        //fbo2 -> fbo1
        beginRenderToFbo1();
        fbo2.textures.car().enable(0);
        blendProgram.fs.txt.set(fbo2.textures.car());
        blendProgram.fs.direction = new Vec2f(0f, 1f/size);
//        cameraDraw(blendProgram, ???, ???, fbo2.texture);
        blendProgram.enable();
        FrameBuffer.renderFBO(size, size);
        blendProgram.disable();
        fbo2.textures.car().disable();
        endRenderToFbo1();
    }

    // fbo1 -> fbo2 (with horizontal blur)
    private void renderToFbo2() {
        fbo2.beginRenderToFbo();
        fbo1.textures.car().enable(0);
        blendProgram.fs.txt.set(fbo1.textures.car());
        blendProgram.fs.direction = new Vec2f(1f/size, 0f);
//        cameraDraw(fbo2, blendProgram, ???, ???, fbo1.texture);
        blendProgram.enable();
        FrameBuffer.renderFBO(size, size);
        blendProgram.disable();
        fbo1.textures.car().disable();
        fbo2.endRenderToFbo();
    }

}
