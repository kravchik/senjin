package yk.senjin.collections;

import org.lwjgl.BufferUtils;
import yk.senjin.vbo.AVboIntIndices;
import yk.senjin.vbo.AVboTyped;
import yk.senjin.viewers.GlWindow1;
import yk.ycollections.YList;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static yk.ycollections.YArrayList.al;

public class GeometryListTest {

    private final GlWindow1 window = new GlWindow1()
        .startHidden()
        .onTick(this::tick);

    public static void main(String[] args) {
        new GeometryListTest().window.start(1);
    }

    private void tick(float v) {
        smoke();
        window.requestStop();
    }

    public void smoke() {

        GeometryList geo = new GeometryList(new AVboTyped(byte.class, 0), new AVboIntIndices(GL_TRIANGLES, 0));
        geo.vboVertices.debugBuffer = new byte[]{};
        geo.vboIndices.debugBuffer = new byte[]{};

        int[] i012 = new int[]{0, 1, 2};

        //add first, grow
        geo.add(i012, 3, getVb(0));
        geo.flush();
        asserts(geo, 1, al(0, 1, 2), new byte[]{0, 1, 2});

        //remove last, shrink
        geo.removeFast(0);
        geo.flush();
        assertEmpty(geo);

        //add again
        geo.add(i012, 3, getVb(0));
        geo.flush();
        asserts(geo, 1, al(0, 1, 2), new byte[]{0, 1, 2});

        //add all 3, grow
        geo.add(i012, 3, getVb(10));
        geo.add(i012, 3, getVb(20));
        geo.flush();
        asserts(geo, 3, al(0, 1, 2, 3, 4, 5, 6, 7, 8), new byte[]{0, 1, 2, 10, 11, 12, 20, 21, 22});

        //change order
        geo.removeFast(1);
        geo.add(i012, 3, getVb(10));
        geo.flush();
        asserts(geo, 3, al(0, 1, 2, 3, 4, 5, 6, 7, 8, 0, 0, 0, 0),
            new byte[]{0, 1, 2, 20, 21, 22, 10, 11, 12, 0, 0, 0, 0});

        //remove middle
        geo.removeFast(1);
        geo.flush();
        asserts(geo, 2, al(0, 1, 2, 6, 7, 8, 0, 0, 0, 0, 0, 0, 0),
            new byte[]{0, 1, 2, 20, 21, 22, 10, 11, 12, 0, 0, 0, 0});

        //add again
        geo.add(i012, 3, getVb(20));
        geo.flush();
        asserts(geo, 3, al(0, 1, 2, 6, 7, 8, 9, 10, 11, 0, 0, 0, 0),
            new byte[]{0, 1, 2, 20, 21, 22, 10, 11, 12, 20, 21, 22, 0});

        //remove all one by one
        geo.removeFast(0);
        geo.removeFast(0);
        geo.removeFast(0);
        geo.flush();
        assertEmpty(geo);

        //add three at a time
        geo.add(i012, 3, getVb(0));
        geo.add(i012, 3, getVb(20));
        geo.add(i012, 3, getVb(30));
        geo.flush();
        asserts(geo, 3, al(0, 1, 2, 3, 4, 5, 6, 7, 8),
            new byte[]{0, 1, 2, 20, 21, 22, 30, 31, 32});

        //remove last and add there
        geo.removeFast(2);
        geo.add(i012, 3, getVb(40));
        geo.flush();
        asserts(geo, 3, al(0, 1, 2, 3, 4, 5, 6, 7, 8),
            new byte[]{0, 1, 2, 20, 21, 22, 40, 41, 42});

        //add two and remove them
        geo.add(i012, 3, getVb(50));
        geo.add(i012, 3, getVb(60));
        geo.removeFast(3);
        geo.removeFast(3);
        geo.flush();
        asserts(geo, 3, al(0, 1, 2, 3, 4, 5, 6, 7, 8, 0, 0, 0, 0),
            new byte[]{0, 1, 2, 20, 21, 22, 40, 41, 42, 0, 0, 0, 0});

        //add one and remove all
        geo.add(i012, 3, getVb(50));
        geo.removeFast(3);
        geo.removeFast(2);
        geo.removeFast(1);
        geo.removeFast(0);
        geo.flush();
        assertEmpty(geo);
    }

    private void assertEmpty(GeometryList db2) {
        assertEquals(0, db2.verticesInfo.size);
        assertEquals(0, db2.indicesInfo.size);
        assertEquals(0, db2.instances.size());
    }

    private void asserts(GeometryList db2, int instancesCount, YList<Integer> indices, byte[] vertices) {
        enableDisable(db2);
        assertEquals(instancesCount, db2.instances.size());
        System.out.println();
        System.out.println("V expected " + Arrays.toString(vertices));
        System.out.println("V actual   " + Arrays.toString(db2.vboVertices.debugBuffer));
        assertArrayEquals(vertices, db2.vboVertices.debugBuffer);

        IntBuffer intBuf = ByteBuffer.wrap(db2.vboIndices.debugBuffer).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer();
        intBuf.rewind();
        YList<Integer> actual = al();
        while (intBuf.hasRemaining()) actual.add(intBuf.get());
        System.out.println("I expected " + indices);
        System.out.println("I actual   " + actual);
        assertEquals(indices, actual);
    }

    private void enableDisable(GeometryList db2) {
        db2.vboVertices.enable();
        db2.vboVertices.disable();
        db2.vboIndices.enable();
        db2.vboIndices.disable();
    }

    private ByteBuffer getVb(int i) {
        ByteBuffer vb = BufferUtils.createByteBuffer(3);
        vb.put((byte) (i +0));
        vb.put((byte) (i +1));
        vb.put((byte) (i +2));
        vb.rewind();
        return vb;
    }

}
