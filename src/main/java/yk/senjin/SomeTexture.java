package yk.senjin;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import yk.jcommon.utils.BadException;
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
    //Sets the wrap parameter for texture coordinate s to either
    // GL_CLAMP_TO_EDGE,
    // GL_MIRRORED_REPEAT, or
    // GL_REPEAT.
    //
    // GL_CLAMP_TO_EDGE causes s coordinates to be clamped to the range 1 2N 1 - 1 2N , where N is the size of the texture in the direction of clamping. GL_REPEAT causes the integer part of the s coordinate to be ignored; the GL uses only the fractional part, thereby creating a repeating pattern. GL_MIRRORED_REPEAT causes the s coordinate to be set to the fractional part of the texture coordinate if the integer part of s is even; if the integer part of s is odd, then the s texture coordinate is set to 1 - frac ⁡ s , where frac ⁡ s represents the fractional part of s. Initially, GL_TEXTURE_WRAP_S is set to GL_REPEAT.    public int wrapS = GL_MIRRORED_REPEAT;
    public int wrapS = GL_MIRRORED_REPEAT;
    public int wrapT = GL_MIRRORED_REPEAT;
    public int magFilter = GL_LINEAR;
    public int minFilter = GL_LINEAR;

    public int width;
    public int height;

    //https://www.opengl.org/sdk/docs/man/html/glTexImage2D.xhtml
    public int internalformat = GL_RGBA;
    public int pixelDataFormat = GL_RGBA;
    public int pixelDataType = GL_UNSIGNED_BYTE;

    public SomeTexture() {
    }

    public void init(int w, int h) {
        width = w;
        height = h;
        IntBuffer texNames;
        texNames = BufferUtils.createIntBuffer(1);
        GL11.glGenTextures(texNames);
        textureObjectId = texNames.get(0);
        GL11.glBindTexture(GL_TEXTURE_2D, textureObjectId);
        GL11.glTexImage2D(GL_TEXTURE_2D, 0, internalformat, width, height, 0, pixelDataFormat, pixelDataType, (java.nio.ByteBuffer)null);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    public SomeTexture(BufferedImage image) {
        IntBuffer texNames;
        texNames = BufferUtils.createIntBuffer(1);
        GL11.glGenTextures(texNames);
        textureObjectId = texNames.get(0);
        setImage(image);
    }

    public SomeTexture(int[] data, int w, int h) {
        this.width = w;
        this.height = h;
        IntBuffer texNames;
        texNames = BufferUtils.createIntBuffer(1);
        GL11.glGenTextures(texNames);
        textureObjectId = texNames.get(0);
        uploadData(w, h, convertToGL(data));
    }

    public void setImage(BufferedImage image) {
        this.image = image;
        uploadData(image.getWidth(), image.getHeight(), convertToGL(image));
    }

    private void uploadData(int w, int h, ByteBuffer byteBuffer) {
        //        ByteBuffer byteBuffer = VisualRealmManager.convertToGLUnoptimized(image);
        GL11.glBindTexture(GL_TEXTURE_2D, textureObjectId);
        GL11.glTexImage2D(GL_TEXTURE_2D, 0, internalformat, w, h, 0, pixelDataFormat, pixelDataType, byteBuffer);
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

    public static ByteBuffer convertToGL(int[] ii) {
        ByteBuffer data;
        data = BufferUtils.createByteBuffer(ii.length / 3 * 4);
        for (int i = 0; i < ii.length; i += 3) {
            data.put((byte) ii[i]);
            data.put((byte) ii[i + 1]);
            data.put((byte) ii[i + 2]);
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
        if (enabled) BadException.die("already enabled");
        enabled = true;
        if (textureGlSlot == -1) BadException.die("not set texture slot");
        glActiveTexture(textureGlSlot);
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, textureObjectId);


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
        if (!enabled) BadException.die("already disabled");
        enabled = false;
        glActiveTexture(textureGlSlot);
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
