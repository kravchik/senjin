package yk.senjin;

import org.lwjgl.opengl.GL14;

import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * Created by Yuri Kravchik on  10.06.15.
 * based on example from http://wiki.lwjgl.org/wiki/Render_to_Texture_with_Frame_Buffer_Objects_(FBO)
 */

public class SimpleAntiAliasing {
    public int colorTextureID;
    public int framebufferID;
    public int depthRenderBufferID;

    public int w, h;
    public int x = 2;

    public int textureW, textureH;

    public void initAA(int w, int h) {
        // init our fbo
        textureW = w * x;
        textureH = h * x;
        this.w = w;
        this.h = h;

        framebufferID = glGenFramebuffers();											// create a new framebuffer
        colorTextureID = glGenTextures();												// and a new texture used as a color buffer
        depthRenderBufferID = glGenRenderbuffers();									// And finally a new depthbuffer

        glBindFramebuffer(GL_FRAMEBUFFER_EXT, framebufferID); 						// switch to the new framebuffer

        // initialize color texture
        glBindTexture(GL_TEXTURE_2D, colorTextureID);									// Bind the colorbuffer texture
//        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);				// make it linear filterd
//        glTexParameteri(GL_TEXTURE_2D,
//                SGISGenerateMipmap.GL_GENERATE_MIPMAP_SGIS, GL_TRUE);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, textureW, textureH, 0, GL_RGBA, GL_INT, (java.nio.ByteBuffer) null);	// Create the texture data

/////
//        glGenerateMipmap(GL_TEXTURE_2D);

        glFramebufferTexture2D(GL_FRAMEBUFFER_EXT,GL_COLOR_ATTACHMENT0_EXT,GL_TEXTURE_2D, colorTextureID, 0); // attach it to the framebuffer


        // initialize depth renderbuffer
        glBindRenderbuffer(GL_RENDERBUFFER_EXT, depthRenderBufferID);				// bind the depth renderbuffer
        glRenderbufferStorage(GL_RENDERBUFFER_EXT, GL14.GL_DEPTH_COMPONENT24, textureW, textureH);	// get the data space for it
        glFramebufferRenderbuffer(GL_FRAMEBUFFER_EXT,GL_DEPTH_ATTACHMENT_EXT,GL_RENDERBUFFER_EXT, depthRenderBufferID); // bind it to the renderbuffer

        glBindFramebuffer(GL_FRAMEBUFFER_EXT, 0);									// Swithch back to normal framebuffer rendering
    }

    public void switchToFBO() {
        // FBO render pass

        glViewport (0, 0, textureW, textureH);									// set The Current Viewport to the fbo size

        glBindTexture(GL_TEXTURE_2D, 0);								// unlink textures because if we dont it all is gonna fail
        glBindFramebuffer(GL_FRAMEBUFFER_EXT, framebufferID);		// switch to rendering on our FBO


        glClearColor (0.0f, 0.0f, 0.0f, 0.5f);
        glClear (GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);			// Clear Screen And Depth Buffer on the fbo to red
    }

    public void renderFBO() {
        // Normal render pass, draw cube with texture

        glEnable(GL_TEXTURE_2D);										// enable texturing
        glBindTexture(GL_TEXTURE_2D, colorTextureID);
        glGenerateMipmap(GL_TEXTURE_2D);
        glBindFramebuffer(GL_FRAMEBUFFER_EXT, 0);					// switch to rendering on the framebuffer

        glClearColor (0.0f, 0.0f, 0.0f, 0.0f);
        glClear (GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);			// Clear Screen And Depth Buffer on the framebuffer to black

        glBindTexture(GL_TEXTURE_2D, colorTextureID);					// bind our FBO texture
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

        glViewport (0, 0, w/x, h/x);									// set The Current Viewport


        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        glColor4f(1, 1, 1, 1);												// set the color to white
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

        glBegin(GL_QUADS);
        glTexCoord2f(0.0f, 0.0f); glVertex3f(-1.0f, -1.0f,  0f);
        glTexCoord2f(1.0f, 0.0f); glVertex3f( 1.0f, -1.0f,  0f);
        glTexCoord2f(1.0f, 1.0f); glVertex3f( 1.0f,  1.0f,  0f);
        glTexCoord2f(0.0f, 1.0f); glVertex3f(-1.0f,  1.0f,  0f);
        glEnd();

        glDisable(GL_TEXTURE_2D);
        glFlush ();
        glViewport(0, 0, w/x, h/x);

    }



}
