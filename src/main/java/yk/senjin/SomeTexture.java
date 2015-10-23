package yk.senjin;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import yk.jcommon.utils.XYit;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_WRAP_R;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL14.GL_MIRRORED_REPEAT;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 16/12/14
 * Time: 22:59
 */
public class SomeTexture extends AbstractState {
    public boolean enabled;
    public int textureGlSlot = -1;//GL_TEXTURE0, 1, ...
    public int textureSlot = -1;//0, 1, ... - map from textureGlSlot to normal numbers
    public int textureObjectId;//assigned by system, when uploaded
    public BufferedImage image;

    public int wrapR = -1;
    public int wrapS = GL_MIRRORED_REPEAT;
    public int wrapT = GL_MIRRORED_REPEAT;
    public int magFilter = GL_NEAREST;
    public int minFilter = GL_NEAREST;

    public SomeTexture() {
    }

    public SomeTexture(BufferedImage image) {
        this.image = image;
        ByteBuffer byteBuffer = convertToGL(image);
//        ByteBuffer byteBuffer = VisualRealmManager.convertToGLUnoptimized(image);
        IntBuffer texNames;
        texNames = BufferUtils.createIntBuffer(1);
        GL11.glGenTextures(texNames);
        textureObjectId = texNames.get(0);
        GL11.glBindTexture(GL_TEXTURE_2D, textureObjectId);
        GL11.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, byteBuffer);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    public static ByteBuffer convertToGL(final BufferedImage image) {
        if (image.getType() != 5) return convertToGLUnoptimized(image);
        final int width = image.getWidth();
        final int height = image.getHeight();
        ByteBuffer data;
        final int[] buf = new int[width * height * 3];
        data = BufferUtils.createByteBuffer(width * height * 4);
        image.getData().getPixels(0, 0, image.getWidth(), image.getHeight(), buf);
        for (int i = 0; i < buf.length; i += 3) {
            data.put((byte) buf[i]);
            data.put((byte) buf[i + 1]);
            data.put((byte) buf[i + 2]);
            data.put((byte) 255);
        }
        data.rewind();
        return data;
    }

    public static ByteBuffer convertToGLUnoptimized(final BufferedImage image) {
        final int width = image.getWidth();
        final int height = image.getHeight();
        ByteBuffer data;
        data = BufferUtils.createByteBuffer(width * height * 4);
        for (XYit xy : XYit.im(image)) {
            Color color = new Color(image.getRGB(xy.x, xy.y), true);
            data.put((byte) color.getRed());
            data.put((byte) color.getGreen());
            data.put((byte) color.getBlue());
            data.put((byte) color.getAlpha());
        }
        data.rewind();
        return data;
    }

    @Override
    public void enable() {
        if (enabled) return;
        enabled = true;
        glActiveTexture(textureSlot);
        glBindTexture(GL_TEXTURE_2D, textureObjectId);

        glEnable( GL_TEXTURE_2D );

        glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
//        glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_BLEND);
        if (wrapR != -1) glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_R, wrapR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, wrapS);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, wrapT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, magFilter);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, minFilter);
    }

    @Override
    public void disable() {
        if (!enabled) return;
        enabled = false;
        glActiveTexture(textureSlot);
        glBindTexture(GL11.GL_TEXTURE_2D, 0);
        glDisable(GL_TEXTURE_2D);
    }

    @Override
    public void release() {
        IntBuffer ib = BufferUtils.createIntBuffer(1);
        ib.put(0, textureObjectId);
        glDeleteTextures(ib);
    }

    public void setSlot(int slot) {
        if (slot < 0 || slot > 31) throw new RuntimeException("slot must be in range [0 31] but was " + slot);
        textureSlot = slot;
        textureGlSlot = GL_TEXTURE0 + slot;
    }

    public void enable(int slot) {
        setSlot(slot);
        enable();
    }
}
