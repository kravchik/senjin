package yk.senjin.util;

import org.lwjgl.BufferUtils;
import yk.jcommon.utils.BadException;
import yk.jcommon.utils.XYit;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;

public class ImageUtils {
    //TODO test which method is faster and recommend it
    public static ByteBuffer convertToGl(final BufferedImage image) {
        switch (image.getType()) {
            case BufferedImage.TYPE_INT_ARGB: return convertToGl_TYPE_TYPE_INT_ARGB(image);
            case BufferedImage.TYPE_3BYTE_BGR: return convertToGl_TYPE_3BYTE_BGR(image, (byte) 0xFF);
            case BufferedImage.TYPE_4BYTE_ABGR: return convertToGl_TYPE_4BYTE_ABGR(image);
        }
        throw BadException.die("Unsupported image.getType(): " + image.getType() + ", use convertToGLUnoptimized(image) or convert image offline");
    }

    public static ByteBuffer convertToGl_TYPE_3BYTE_BGR(BufferedImage image, byte alpha) {
        ByteBuffer result = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);
        byte[] imBuffer = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        for (int i = 0; i < imBuffer.length; i += 3) {
            result.put(imBuffer[i + 2]);
            result.put(imBuffer[i + 1]);
            result.put(imBuffer[i]);
            result.put(alpha);
        }
        result.rewind();
        return result;
    }

    public static ByteBuffer convertToGl_TYPE_4BYTE_ABGR(BufferedImage image) {
        ByteBuffer result = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);
        byte[] imBuffer = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        for (int i = 0; i < imBuffer.length; i += 4) {
            result.put(imBuffer[i + 3]);
            result.put(imBuffer[i + 2]);
            result.put(imBuffer[i + 1]);
            result.put(imBuffer[i]);
        }
        result.rewind();
        return result;
    }

    //TODO test if byte[] imBuffer will be faster
    public static ByteBuffer convertToGl_TYPE_TYPE_INT_ARGB(BufferedImage image) {
        int[] imBuffer = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        ByteBuffer result = BufferUtils.createByteBuffer(imBuffer.length * 4);
        for (int i = 0; i < imBuffer.length; i++) {
            int cur = imBuffer[i];

            byte inputA = (byte) (((cur >>> 24) & 0xFF));
            byte inputR = (byte) (((cur >>> 16) & 0xFF));
            byte inputG = (byte) (((cur >>> 8) & 0xFF));
            byte inputB = (byte) (cur & 0xFF);

            result.put(inputR);
            result.put(inputG);
            result.put(inputB);
            result.put(inputA);
        }
        result.rewind();
        return result;
    }

    public static ByteBuffer convertToGl(int[] ii) {
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
}
