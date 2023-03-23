package yk.senjin.examples.simple;

import yk.senjin.FrameBuffer;
import yk.senjin.SomeTexture;

import static org.lwjgl.opengl.GL11.GL_NEAREST;

/**
 * Created by Yuri Kravchik on 25.11.17.
 */
public class IKnowOpenGL_9_FrameBuffer extends SimpleLwjglRoutine {
    public static void main(String[] args) {
        new IKnowOpenGL_9_FrameBuffer().start();
    }

    SomeTexture texture;
    FrameBuffer fb;

    @Override public void onFirstPass() {
        super.onFirstPass();
        texture = new SomeTexture();
        texture.init(46, 46);
        texture.magFilter = GL_NEAREST;
        fb = new FrameBuffer();
        fb.initFBO(texture);
    }

    @Override public void onTick(float dt) {
        super.onTick(dt);
        fb.beginRenderToFbo();
        IKnowOpenGL_5_PixelPerfect.drawPixelScheme(texture.width, texture.height);
        fb.endRenderToFbo();
        fb.render(w, h);
    }


}
