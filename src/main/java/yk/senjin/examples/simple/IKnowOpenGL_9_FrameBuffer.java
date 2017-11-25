package yk.senjin.examples.simple;

import yk.senjin.FrameBuffer;
import yk.senjin.SomeTexture;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Yuri Kravchik on 25.11.17.
 */
public class IKnowOpenGL_9_FrameBuffer extends SimpleLwjglRoutine {
    public static void main(String[] args) throws Exception {
        new IKnowOpenGL_9_FrameBuffer().main();
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
        //IKnowOpenGL_5_PixelPerfect.drawPixelScheme(w, h);
        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT);
        IKnowOpenGL_5_PixelPerfect.drawPixelScheme(texture.width, texture.height);
        fb.endRenderToFbo();

        fb.textures.car().enable(0);
        fb.renderFBO(w, h);
        fb.textures.car().disable();

    }


}
