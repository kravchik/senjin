package yk.senjin.examples.blend;

import yk.jcommon.fastgeom.Vec2f;
import yk.senjin.FrameBuffer;
import yk.senjin.SomeTexture;
import yk.senjin.shaders.gshader.GProgram;

import static yk.jcommon.fastgeom.Matrix4.ortho;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 24/02/16
 * Time: 14:41
 */
public class Blender1 {
    //TODO don't use deprecated gl_Vertex
    //TODO use Poconuv instesad
    //http://dev.theomader.com/gaussian-kernel-calculator/
    public static final float[] KERNEL_3_11 = new float[]{0.035822f, 0.05879f, 0.086425f, 0.113806f, 0.13424f, 0.141836f, 0.13424f, 0.113806f, 0.086425f, 0.05879f, 0.035822f};
    public static final float[] KERNEL_1_11 = new float[]{0.000003f, 0.000229f, 0.005977f, 0.060598f, 0.24173f, 0.382925f, 0.24173f, 0.060598f, 0.005977f, 0.000229f, 0.000003f};
    public static final float[] KERNEL_1_5 = new float[]{0.06136f, 0.24477f, 0.38774f, 0.24477f, 0.06136f};
    public static final float[] KERNEL_3_5 = new float[]{0.1784f, 0.210431f, 0.222338f, 0.210431f, 0.1784f};
    
    public int size = 512;

    public BlendV blendV;
    public BlendF blendF;
    public GProgram blendProgram;

    public FrameBuffer fbo1;
    public FrameBuffer fbo2;
    
    public void beginRenderToFbo1() {
        fbo1.beginRenderToFbo();
    }

    public void endRenderToFbo1() {
        fbo1.endRenderToFbo();
    }

    public void init() {
        blendF = new BlendF();
        blendV = new BlendV();
        blendV.modelViewProjectionMatrix = ortho(-1, 1, 1, -1, 1, -1);
        blendProgram = new GProgram(blendV, blendF);

        SomeTexture renderTexture = new SomeTexture();
        renderTexture.init(size, size);
        fbo1 = new FrameBuffer();
        fbo1.initFBO(renderTexture);
        SomeTexture renderTexture2 = new SomeTexture();
        renderTexture2.init(size, size);
        fbo2 = new FrameBuffer();
        fbo2.initFBO(renderTexture2);

        blendF.koeff = KERNEL_1_5;
        blendF.koeff = KERNEL_1_11;
        blendF.koeff = KERNEL_3_11;
        blendF.kSize = blendF.koeff.length;

    }
    
    public void renderToScreen(int w, int h) {
        renderToFbo2();

        //fbo2 -> standard frame (with result blur)
        fbo2.textures.car().enable(0);
        blendF.txt.set(fbo2.textures.car());
        blendF.direction = new Vec2f(0f, 1f/size);
//        cameraDraw(blendProgram, ???, ???, fbo2.texture);
        blendProgram.enable();
        FrameBuffer.renderTexture0(w, h);
        blendProgram.disable();
        fbo2.textures.car().disable();
    }

    public void renderToFbo1() {
        renderToFbo2();

        //fbo2 -> fbo1
        beginRenderToFbo1();
        fbo2.textures.car().enable(0);
        blendF.txt.set(fbo2.textures.car());
        blendF.direction = new Vec2f(0f, 1f/size);
//        cameraDraw(blendProgram, ???, ???, fbo2.texture);
        blendProgram.enable();
        FrameBuffer.renderTexture0(size, size);
        blendProgram.disable();
        fbo2.textures.car().disable();
        endRenderToFbo1();
    }

    // fbo1 -> fbo2 (with horizontal blur)
    private void renderToFbo2() {
        fbo2.beginRenderToFbo();
        fbo1.textures.car().enable(0);
        blendF.txt.set(fbo1.textures.car());
        blendF.direction = new Vec2f(1f/size, 0f);
//        cameraDraw(fbo2, blendProgram, ???, ???, fbo1.texture);
        blendProgram.enable();
        FrameBuffer.renderTexture0(size, size);
        blendProgram.disable();
        fbo1.textures.car().disable();
        fbo2.endRenderToFbo();
    }

}
