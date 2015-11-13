package yk.senjin;

import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

/**
 * Created by Yuri Kravchik on  12.11.15
 */

public class SimpleAntiAliasing2 {
    public FrameBuffer fbo;

    public int w, h;
    public int x = 2;

    public void initAA(int w, int h) {
        SomeTexture renderTexture = new SomeTexture();
        renderTexture.minFilter = GL_LINEAR_MIPMAP_LINEAR;
        renderTexture.init(w * x, h * x);
        fbo = new FrameBuffer();
        fbo.initFBO(renderTexture);
        this.w = w;
        this.h = h;
    }

    public void switchToFBO() {
        fbo.beginRenderToFbo();
    }

    public void renderFBO() {
        fbo.endRenderToFbo();
        fbo.texture.enable(0);
        glGenerateMipmap(GL_TEXTURE_2D);
        FrameBuffer.renderFBO2(w, h);
        fbo.texture.disable();
    }
}
