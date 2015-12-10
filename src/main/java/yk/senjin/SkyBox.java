package yk.senjin;

import yk.jcommon.collections.YList;
import yk.jcommon.fastgeom.Quaternionf;
import yk.jcommon.fastgeom.Vec3f;

import java.awt.image.BufferedImage;

import static org.lwjgl.opengl.GL11.*;
import static yk.jcommon.collections.YArrayList.al;
import static yk.senjin.DDDUtils.glMultMatrix4;
import static yk.senjin.DDDUtils.glVec3f;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 15/10/15
 * Time: 15:16
 */
public class SkyBox {
    public BufferedImage image;
    private SomeTexture st;
    private YList<SomeTexture> st2;

    public Quaternionf rot;
    public boolean enableTexture = true;

    public void firstFrame(BufferedImage image) {
        st = new SomeTexture(image);
    }

    public void firstFrame2(BufferedImage images) {
        st2 = al();
        st2.add(new SomeTexture(getSubImage(images, 0, 1)));
        st2.add(new SomeTexture(getSubImage(images, 1, 1)));
        st2.add(new SomeTexture(getSubImage(images, 2, 1)));
        st2.add(new SomeTexture(getSubImage(images, 3, 1)));
        st2.add(new SomeTexture(getSubImage(images, 1, 0)));
        st2.add(new SomeTexture(getSubImage(images, 1, 2)));
    }

    public void firstFrame3(YList<BufferedImage> images) {
        st2 = al();
        for (BufferedImage img : images) st2.add(new SomeTexture(img));
    }

    private BufferedImage getSubImage(BufferedImage images, int px, int py) {
        int w = images.getWidth() / 4;
        int h = images.getHeight() / 3;
        BufferedImage im0 = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
        im0.getGraphics().drawImage(images, 0, 0, w, h, w * px, h * py, w * (px + 1), h * (py + 1), null);
        return im0;
    }

    public void render(Vec3f camPos) {
        if (st != null) render1(camPos);
        else render2(camPos);
    }

    private void renderQuad(SomeTexture st, Vec3f pos, Vec3f vx, Vec3f vy) {
        if (enableTexture) st.enable(0);

        glBegin(GL_QUADS);
        glColor3f(1, 1, 1);
        glTexCoord2f(0, 1); glVec3f(pos);
        glTexCoord2f(1, 1); glVec3f(pos.add(vx));
        glTexCoord2f(1, 0); glVec3f(pos.add(vx).add(vy));
        glTexCoord2f(0, 0); glVec3f(pos.add(vy));
        glEnd();

//        glBegin(GL_LINES);
//        glColor3f(1, 1, 1);
//
//        glVec3f(pos);
//        glVec3f(pos.add(vx));
//        glVec3f(pos.add(vx));
//        glVec3f(pos.add(vx).add(vy));
//        glVec3f(pos.add(vx).add(vy));
//        glVec3f(pos.add(vy));
//        glVec3f(pos);
//
//        glEnd();

        if (enableTexture) st.disable();
    }

    private void render2(Vec3f camPos) {
        glMatrixMode(GL_MODELVIEW);
        glPushMatrix();
        glTranslatef(camPos.x, camPos.y, camPos.z);
        if (rot != null) glMultMatrix4(rot.toMatrix4());

        renderQuad(st2.get(0), new Vec3f(-10, -10,  10), new Vec3f(  0, 0,-20), new Vec3f(0, 20,  0));
        renderQuad(st2.get(1), new Vec3f(-10, -10, -10), new Vec3f( 20, 0,  0), new Vec3f(0, 20,  0));
        renderQuad(st2.get(2), new Vec3f( 10, -10, -10), new Vec3f(  0, 0, 20), new Vec3f(0, 20,  0));
        renderQuad(st2.get(3), new Vec3f( 10, -10,  10), new Vec3f(-20, 0,  0), new Vec3f(0, 20,  0));
        renderQuad(st2.get(4), new Vec3f(-10,  10, -10), new Vec3f( 20, 0,  0), new Vec3f(0,  0, 20));
        renderQuad(st2.get(5), new Vec3f(-10, -10,  10), new Vec3f( 20, 0,  0), new Vec3f(0,  0,-20));


        glPopMatrix();
    }

    private void render1(Vec3f camPos) {
        glMatrixMode(GL_MODELVIEW);
        glPushMatrix();
        glTranslatef(camPos.x, camPos.y, camPos.z);

        st.enable();

        glBegin(GL_QUADS);
        glColor3f(1, 1, 1);

        glTexCoord2f(1/4f, 2/3f);
        glVertex3f(-10, -10, -10);
        glTexCoord2f(2/4f, 2/3f);
        glVertex3f( 10, -10, -10);
        glTexCoord2f(2/4f, 1/3f);
        glVertex3f( 10,  10, -10);
        glTexCoord2f(1/4f, 1/3f);
        glVertex3f(-10, 10, -10);

        glTexCoord2f(2/4f, 2/3f);
        glVertex3f( 10, -10, -10);
        glTexCoord2f(3/4f, 2/3f);
        glVertex3f( 10, -10,  10);
        glTexCoord2f(3/4f, 1/3f);
        glVertex3f( 10,  10,  10);
        glTexCoord2f(2/4f, 1/3f);
        glVertex3f( 10,  10, -10);

        glTexCoord2f(3/4f, 2/3f);
        glVertex3f( 10, -10,  10);
        glTexCoord2f(4/4f, 2/3f);
        glVertex3f(-10, -10,  10);
        glTexCoord2f(4/4f, 1/3f);
        glVertex3f(-10,  10,  10);
        glTexCoord2f(3/4f, 1/3f);
        glVertex3f( 10,  10,  10);

        glTexCoord2f(0/4f, 2/3f);
        glVertex3f(-10, -10,  10);
        glTexCoord2f(1/4f, 2/3f);
        glVertex3f(-10, -10, -10);
        glTexCoord2f(1/4f, 1/3f);
        glVertex3f(-10,  10, -10);
        glTexCoord2f(0/4f, 1/3f);
        glVertex3f(-10,  10,  10);

        glTexCoord2f(1/4f, 1/3f);
        glVertex3f(-10,  10, -10);
        glTexCoord2f(2/4f, 1/3f);
        glVertex3f( 10,  10, -10);
        glTexCoord2f(2/4f, 0/3f);
        glVertex3f( 10,  10,  10);
        glTexCoord2f(1/4f, 0/3f);
        glVertex3f(-10,  10,  10);

        glEnd();
        st.disable();
        glPopMatrix();
    }

}
