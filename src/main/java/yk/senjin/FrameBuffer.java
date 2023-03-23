package yk.senjin;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import yk.jcommon.collections.YList;
import yk.jcommon.fastgeom.Vec3f;
import yk.jcommon.utils.BadException;

import java.nio.IntBuffer;
import java.util.List;

import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static yk.jcommon.collections.YArrayList.al;

/**
 * Created by Yuri Kravchik on  10.06.15.
 */

public class FrameBuffer {
    public int framebufferID;
    public int depthRenderBufferID;
    public YList<SomeTexture> textures;
    public Vec3f backgroundColor = Vec3f.ZERO;

    public int w, h;

    /**
     * Initializes FBO.
     * tt - is color attachments
     * depth buffer will be generated automatically
     */
    public FrameBuffer initFBO(SomeTexture... tt) {
        initColorBuffers(tt);
        initDepthBuffer();
        return this;
    }

    /**
     * Initializes FBO.
     * Creates textures with the defined width, height and formats.
     * depth buffer will be generated automatically
     */
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

    public FrameBuffer initFBO(int w, int h, List<Integer> formats) {
        SomeTexture[] tt = new SomeTexture[formats.size()];
        this.w = w;
        this.h = h;
        for (int i = 0; i < formats.size(); i++) {
            SomeTexture renderTexture4 = new SomeTexture();
            renderTexture4.internalformat = formats.get(i);
            renderTexture4.init(w, h);
            tt[i] = renderTexture4;
        }
        return initFBO(tt);
    }

    public FrameBuffer initColorBuffers(SomeTexture[] tt) {
        assertMaxColorAttachments(tt);
        if (this.textures != null) throw new RuntimeException("textures field is already assigned");
        this.textures = al(tt);
        w = textures.car().width;
        h = textures.car().height;
        for (SomeTexture t : textures) if (t.width != w || t.height != h) BadException.die("all textures must be of same size");
        framebufferID = glGenFramebuffers();										// create a new framebuffer
        glBindFramebuffer(GL_FRAMEBUFFER_EXT, framebufferID); 						// switch to the new framebuffer

        //connect textures as color buffers
        for (int i = 0; i < tt.length; i++) {
            //glBindTexture(GL_TEXTURE_2D, tt[i].textureObjectId);
            glFramebufferTexture2D(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT + i, GL_TEXTURE_2D, tt[i].textureObjectId, 0); // attach texture to the framebuffer
        }
        GL20.glDrawBuffers(getIntBuffer(textures.size()));
        return this;
    }

    public FrameBuffer initDepthBuffer() {
        if (depthRenderBufferID != 0) throw new RuntimeException("Seems like depth buffer is already created");
        depthRenderBufferID = glGenRenderbuffers();									// depthbuffer
        glBindRenderbuffer(GL_RENDERBUFFER_EXT, depthRenderBufferID);				// bind the depth renderbuffer
        glRenderbufferStorage(GL_RENDERBUFFER_EXT, GL14.GL_DEPTH_COMPONENT32, w, h);// get the data space for it
        glFramebufferRenderbuffer(GL_FRAMEBUFFER_EXT,GL_DEPTH_ATTACHMENT_EXT,GL_RENDERBUFFER_EXT, depthRenderBufferID); // bind it to the renderbuffer
        int result = glCheckFramebufferStatus(GL_FRAMEBUFFER);
        if (result != GL_FRAMEBUFFER_COMPLETE) BadException.die("Wrong FBO status: " + result);
        glBindFramebuffer(GL_FRAMEBUFFER_EXT, 0);									// Swithch back to normal framebuffer rendering
        return this;
    }

    public void beginRenderToFbo() {
        beginRenderToFbo(true);
    }

    public void beginRenderToFbo(boolean clear) {
        glViewport (0, 0, textures.car().width, textures.car().height);// set The Current Viewport to the fbo size
        glBindFramebuffer(GL_FRAMEBUFFER_EXT, framebufferID);          // switch to rendering on our FBO
        if (clear) {
            glClearColor(backgroundColor.x, backgroundColor.y, backgroundColor.z, 0.f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);        // Clear Screen And Depth Buffer on the fbo to red
        }
    }

    public void endRenderToFbo() {
        glBindFramebuffer(GL_FRAMEBUFFER_EXT, 0);					// switch to rendering on the general framebuffer
    }

    @Deprecated //just inline
    public static void renderFBO(int width, int height) {
        renderTexture0(width, height);
    }

    /**
     * Renders currently enabled texture 0 to the whole screen (width and height should be provided)
     */
    public static void renderTexture0(int width, int height) {
        renderTexture0(0, 0, width, height, true);
    }

    /**
     * Renders currently enabled texture 0 to the screen.
     *
     * left/bottom, width/height - screen space position of the rectangle with the texture to render
     * clear - if true, cleans color and depth buffers
     */
    public static void renderTexture0(int left, int bottom, int width, int height, boolean clear) {
        if (clear) glClear (GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);// Clear Screen And Depth Buffer on the framebuffer to black

        glViewport (left, bottom, width, height);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        //glOrtho(0, 0, width, height, -1, 1);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        glColor3f(1,1,1);// set the color to white (because it is multiplied by the texture color)
        //glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

        glBegin(GL_QUADS);
        glTexCoord2f(0.0f, 0.0f); glVertex3f(-1.0f, -1.0f,  0f);
        glTexCoord2f(1.0f, 0.0f); glVertex3f( 1.0f, -1.0f,  0f);
        glTexCoord2f(1.0f, 1.0f); glVertex3f( 1.0f,  1.0f,  0f);
        glTexCoord2f(0.0f, 1.0f); glVertex3f(-1.0f,  1.0f,  0f);
        glEnd();

//        glFlush();
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

    public void render(int w, int h) {
        render(0, w, h);
    }

    public void render(int textureIndex, int w, int h) {
        textures.get(textureIndex).enable(0);
        renderTexture0(w, h);
        textures.get(textureIndex).disable();
    }

    private static void assertMaxColorAttachments(SomeTexture[] tt) {
        if (tt.length > 8) {//8 is a minimum supported by all implementations
            int maxColors = glGetInteger(GL_MAX_COLOR_ATTACHMENTS);
            if (maxColors < tt.length) throw new RuntimeException("Too many color buffers (" + tt.length + ") GL_MAX_COLOR_ATTACHMENTS is " + maxColors);
        }
    }

    private void assertAttachmentIndex(int i) {
        if (i < 0 || i >= textures.size()) throw new RuntimeException("Wrong attachment id " + i + " for attachments count " + textures.size());
    }
    
    //TODO move to utils
    //private void assertInside(int ax, int ay, int aw, int ah, int bx, int by, int bw, int bh) {
    //    if (ax < bx || ax + aw >= bw || bw < 1) throw new RuntimeException(String.format("Wrong ax, aw provided (%s, %s) for bx bw (%s, %s)", ax, aw, bx, bw));
    //    if (ay < by || ay + ah >= bh || bh < 1) throw new RuntimeException(String.format("Wrong ax, aw provided (%s, %s) for bx bw (%s, %s)", ax, aw, bx, bw));
    //}
    //
    private static void assertInside(int ax, int aw, int bx, int bw) {
        if (ax < bx || ax + aw > bw || aw < 1) throw new RuntimeException(String.format("Wrong ax, aw provided (%s, %s) for bx bw (%s, %s)", ax, aw, bx, bw));
    }

    /**
     * Copies colors from one FBO to another. Multiple color attachments can be provided.
     * Seems like the copy between different attachments of one FBO is also possible (srcBuffer can be the same as dstBuffer)
     */
    public static void copyPixels(FrameBuffer srcBuffer, int[] srcAttachments, int srcX, int srcY, int srcW, int srcH,
                                  FrameBuffer dstBuffer, int[] dstAttachments, int dstX, int dstY, int dstW, int dstH) {
        //asserts
        if (srcAttachments.length != dstAttachments.length) throw new RuntimeException("Attachments length should be the same");
        for (int i = 0; i < srcAttachments.length; i++) {
            srcBuffer.assertAttachmentIndex(srcAttachments[i]);
            dstBuffer.assertAttachmentIndex(dstAttachments[i]);
        }
        assertInside(srcX, srcW, 0, srcBuffer.w);
        assertInside(srcY, srcH, 0, srcBuffer.h);
        assertInside(dstX, dstW, 0, dstBuffer.w);
        assertInside(dstY, dstH, 0, dstBuffer.h);

        glBindFramebuffer(GL_READ_FRAMEBUFFER, srcBuffer.framebufferID);//bind buffers for reading and writing
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, dstBuffer.framebufferID);

        for (int i = 0; i < srcAttachments.length; i++) {//blit attachments one by one
            glReadBuffer(GL_COLOR_ATTACHMENT0_EXT + srcAttachments[i]);
            glDrawBuffer(GL_COLOR_ATTACHMENT0_EXT + dstAttachments[i]);
            glBlitFramebuffer(srcX, srcY, srcX + srcW, srcY + srcH, dstX, dstY, dstX + dstW, dstY + dstH, GL_COLOR_BUFFER_BIT, GL_NEAREST);
        }
        glBindFramebuffer(GL_READ_FRAMEBUFFER, 0);//reset read buffer
        //restore(!) draw buffer state
        GL20.glDrawBuffers(getIntBuffer(dstBuffer.textures.size()));
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);

    }

    private static IntBuffer getIntBuffer(int count) {
        IntBuffer drawBufferParam;
        drawBufferParam = BufferUtils.createIntBuffer(count);
        for (int i = 0; i < count; i++) {
            drawBufferParam.put(GL_COLOR_ATTACHMENT0_EXT + i);
        }
        drawBufferParam.rewind();
        return drawBufferParam;
    }

}
