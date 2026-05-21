package yk.senjin.examples.simple;

import yk.senjin.FrameBuffer;
import yk.senjin.SomeTexture;
import yk.senjin.viewers.SimpleLwjglRoutine;

import static org.lwjgl.opengl.GL11.GL_NEAREST;

/**
 * Created by Yuri Kravchik on 25.11.17.
 */
public class IKnowOpenGL_9_FrameBuffer extends SimpleLwjglRoutine {
    public static void main(String[] args) {
        new IKnowOpenGL_9_FrameBuffer().start();
    }

    FrameBuffer fb;

    @Override public void onFirstPass() {
        super.onFirstPass();
        SomeTexture texture = new SomeTexture();
        texture.init(46, 46);
        texture.magFilter = GL_NEAREST;
        fb = new FrameBuffer();
        fb.initFBO(texture);
    }

    @Override public void onTick(float dt) {
        super.onTick(dt);
        fb.beginRenderToFbo();
        IKnowOpenGL_5_PixelPerfect.drawPixelScheme(fb.w, fb.h);
        fb.endRenderToFbo();
        fb.render(win.sizePixels);
    }


}
