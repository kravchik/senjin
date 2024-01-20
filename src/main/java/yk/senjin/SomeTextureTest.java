package yk.senjin;

import org.junit.Test;
import yk.senjin.util.ImageUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;

public class SomeTextureTest {

    @Test
    public void convertToGl_TYPE_TYPE_INT_ARGB() {
        BufferedImage bi = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);

        Graphics g = bi.getGraphics();
        g.setColor(new Color(0, 0, 0, 255));//black
        g.drawLine(0, 0, 10, 0);
        g.setColor(new Color(255, 0, 0, 255));//R
        g.drawLine(1, 0, 10, 0);
        g.setColor(new Color(0, 255, 0, 255));//G
        g.drawLine(2, 0, 10, 0);
        g.setColor(new Color(0, 0, 255, 255));//B
        g.drawLine(3, 0, 10, 0);

        int[] imBuffer = ((DataBufferInt) bi.getRaster().getDataBuffer()).getData();
        System.out.println("Java internal (0xAARRGGBB):");
        System.out.println(Integer.toHexString(imBuffer[0]));
        System.out.println(Integer.toHexString(imBuffer[1]));
        System.out.println(Integer.toHexString(imBuffer[2]));
        System.out.println(Integer.toHexString(imBuffer[3]));


        ByteBuffer buffer = ImageUtils.convertToGl_TYPE_TYPE_INT_ARGB(bi);

        System.out.println("\nOGL internal (0xAABBGGRR):");
        System.out.println(Integer.toHexString(buffer.getInt()));
        System.out.println(Integer.toHexString(buffer.getInt()));
        System.out.println(Integer.toHexString(buffer.getInt()));
        System.out.println(Integer.toHexString(buffer.getInt()));

        buffer.rewind();
        assertEquals(0xFF000000, buffer.getInt());
        assertEquals(0xFF0000FF, buffer.getInt());
        assertEquals(0xFF00FF00, buffer.getInt());
        assertEquals(0xFFFF0000, buffer.getInt());

    }

    @Test
    public void convertToGl_TYPE_3BYTE_BGR() {
        BufferedImage bi = new BufferedImage(10, 10, BufferedImage.TYPE_3BYTE_BGR);

        Graphics g = bi.getGraphics();
        g.setColor(new Color(0, 0, 0, 255));//black
        g.drawLine(0, 0, 10, 0);
        g.setColor(new Color(255, 0, 0, 255));//R
        g.drawLine(1, 0, 10, 0);
        g.setColor(new Color(0, 255, 0, 255));//G
        g.drawLine(2, 0, 10, 0);
        g.setColor(new Color(0, 0, 255, 255));//B
        g.drawLine(3, 0, 10, 0);

        byte[] imBuffer = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
        System.out.println("Java internal (b g r):");
        System.out.println(imBuffer[0] + " " + imBuffer[1] + " " + imBuffer[2]);
        System.out.println(imBuffer[3] + " " + imBuffer[4] + " " + imBuffer[5]);
        System.out.println(imBuffer[6] + " " + imBuffer[7] + " " + imBuffer[8]);
        System.out.println(imBuffer[9] + " " + imBuffer[10] + " " + imBuffer[11]);


        ByteBuffer buffer = ImageUtils.convertToGl_TYPE_3BYTE_BGR(bi, (byte) 0xFF);

        System.out.println("\nOGL internal (0xAABBGGRR):");
        System.out.println(Integer.toHexString(buffer.getInt()));
        System.out.println(Integer.toHexString(buffer.getInt()));
        System.out.println(Integer.toHexString(buffer.getInt()));
        System.out.println(Integer.toHexString(buffer.getInt()));

        buffer.rewind();
        assertEquals(0xFF000000, buffer.getInt());
        assertEquals(0xFF0000FF, buffer.getInt());
        assertEquals(0xFF00FF00, buffer.getInt());
        assertEquals(0xFFFF0000, buffer.getInt());

    }
}
