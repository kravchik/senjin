package yk.senjin;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.Util;
import yk.jcommon.collections.YList;
import yk.jcommon.utils.BadException;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static yk.jcommon.collections.YArrayList.al;

/**
 * Created by Yuri Kravchik on  10.06.15.
 * based on example from http://wiki.lwjgl.org/wiki/Render_to_Texture_with_Frame_Buffer_Objects_(FBO)
 */

public class FrameBuffer {
    public int framebufferID;
    public int depthRenderBufferID;
    public YList<SomeTexture> textures;

    public int w, h;

    public static FrameBuffer multipleRenderTargetsF32(int count, int w, int h) {
        SomeTexture tt[] = new SomeTexture[count];
        for (int i = 0; i < count; i++) {
            SomeTexture texture = new SomeTexture();
            texture.internalformat = GL_RGBA32F;
            texture.init(w, h);
            tt[i] = texture;
        }

        FrameBuffer result = new FrameBuffer();
        result.initFBO(tt);
        return result;
    }

    public FrameBuffer initFBO(SomeTexture... tt) {
        // init our fbo
        if (textures != null) {
            for (SomeTexture texture : textures) texture.release();
        }
        this.textures = al(tt);
        w = textures.car().width;
        h = textures.car().height;
        for (SomeTexture t : textures) if (t.width != w || t.height != h) BadException.die("all textures must be of same size");
        framebufferID = glGenFramebuffers();											// create a new framebuffer
        depthRenderBufferID = glGenRenderbuffers();									// And finally a new depthbuffer
        glBindFramebuffer(GL_FRAMEBUFFER_EXT, framebufferID); 						// switch to the new framebuffer

        IntBuffer drawBufferParam;
        drawBufferParam = BufferUtils.createIntBuffer(tt.length);
        for (int i = 0; i < tt.length; i++) {
            drawBufferParam.put(GL_COLOR_ATTACHMENT0_EXT + i);
            glBindTexture(GL_TEXTURE_2D, tt[i].textureObjectId);
            glFramebufferTexture2D(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT + i, GL_TEXTURE_2D, tt[i].textureObjectId, 0); // attach texture to the framebuffer
        }
        drawBufferParam.rewind();
        GL20.glDrawBuffers(drawBufferParam);



        // initialize depth renderbuffer
        glBindRenderbuffer(GL_RENDERBUFFER_EXT, depthRenderBufferID);				// bind the depth renderbuffer
        glRenderbufferStorage(GL_RENDERBUFFER_EXT, GL14.GL_DEPTH_COMPONENT32, tt[0].width, tt[0].height);	// get the data space for it
        glFramebufferRenderbuffer(GL_FRAMEBUFFER_EXT,GL_DEPTH_ATTACHMENT_EXT,GL_RENDERBUFFER_EXT, depthRenderBufferID); // bind it to the renderbuffer
        int result = glCheckFramebufferStatus(GL_FRAMEBUFFER);
        if (result != GL_FRAMEBUFFER_COMPLETE) BadException.die("Wrong FBO status: " + result);
        glBindFramebuffer(GL_FRAMEBUFFER_EXT, 0);									// Swithch back to normal framebuffer rendering

        Util.checkGLError();

        return this;
    }

    public FrameBuffer initFBO(int w, int h, int... formats) {
        SomeTexture[] tt = new SomeTexture[formats.length];
        this.w = w;
        this.h = h;
        for (int i = 0; i < formats.length; i++) {
            SomeTexture renderTexture4 = new SomeTexture();
            renderTexture4.internalformat = formats[i];
            renderTexture4.init(w, h);
            tt[i] = renderTexture4;
        }
        return initFBO(tt);
    }

    public void beginRenderToFbo() {
        beginRenderToFbo(true);
    }

    public void beginRenderToFbo(boolean clear) {
        glViewport (0, 0, textures.car().width, textures.car().height);									// set The Current Viewport to the fbo size
        glBindFramebuffer(GL_FRAMEBUFFER_EXT, framebufferID);		// switch to rendering on our FBO
        if (clear) {
            glClearColor(0, 0, 0, 0.f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);            // Clear Screen And Depth Buffer on the fbo to red
        }
    }


    public void endRenderToFbo() {
        glBindFramebuffer(GL_FRAMEBUFFER_EXT, 0);					// switch to rendering on the framebuffer
    }

    public static void renderFBO(int w, int h) {
        glClearColor (.0f, 0.0f, 0.0f, 0.f);
        glClear (GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);			// Clear Screen And Depth Buffer on the framebuffer to black

        glViewport (0, 0, w, h);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
//        glOrtho(0, 0, w, h, -1, 1);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        glColor3f(1,1,1);												// set the color to white
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

        glBegin(GL_QUADS);
        glTexCoord2f(0.0f, 0.0f); glVertex3f(-1.0f, -1.0f,  0f);
        glTexCoord2f(1.0f, 0.0f); glVertex3f( 1.0f, -1.0f,  0f);
        glTexCoord2f(1.0f, 1.0f); glVertex3f( 1.0f,  1.0f,  0f);
        glTexCoord2f(0.0f, 1.0f); glVertex3f(-1.0f,  1.0f,  0f);
        glEnd();

        glFlush();
    }

    public void enableTextures() {
        for (int i = 0; i < textures.size(); i++) textures.get(i).enable(i);
    }

    public void disableTextures() {
        for (int i = 0; i < textures.size(); i++) textures.get(i).disable();
    }

    public void release() {
        for (SomeTexture t : textures) t.release();
    }
}
