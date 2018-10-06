package yk.senjin.vbo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.opengl.Util;
import yk.jcommon.utils.BadException;

import java.nio.ByteBuffer;

import static junit.framework.TestCase.fail;
import static yk.jcommon.collections.YArrayList.al;

public class AVboTest {

    @Before
    public void setup() throws LWJGLException {
        Display.setDisplayMode(new DisplayMode(600, 600));
        Display.create(new PixelFormat());
        Display.makeCurrent();
    }

    @After
    public void shutdown() {
        Display.destroy();
    }

    @Test
    public void testEmpty() {
        ByteBuffer allocate = BufferUtils.createByteBuffer(10);
        System.out.println(allocate.capacity());
        System.out.println(allocate.remaining());

        AVboTyped avbo = new AVboTyped(byte.class, 0);
        avbo.reloadResize(BufferUtils.createByteBuffer(0));
        avbo.enable();

        avbo.reloadResize(BufferUtils.createByteBuffer(3));
        avbo.enable();

        avbo.setCount(0);
        avbo.enable();

        try {
            avbo.addChange(al((byte) 5), 0);
            fail();
        } catch (BadException ignore) { }
        avbo.enable();

        avbo.setCount(1);
        avbo.addChange(al((byte) 5), 0);
        avbo.enable();


        Util.checkGLError();

    }

}
