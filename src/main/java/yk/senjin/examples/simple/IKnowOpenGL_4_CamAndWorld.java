package yk.senjin.examples.simple;

import yk.jcommon.fastgeom.Matrix4;
import yk.senjin.DDDUtils;

import static org.lwjgl.opengl.GL11.*;
import static yk.jcommon.fastgeom.Matrix4.identity;
import static yk.jcommon.fastgeom.Matrix4.perspective;
import static yk.jcommon.fastgeom.Quaternionf.fromAngleAxisFast;
import static yk.jcommon.fastgeom.Vec3f.v3;
import static yk.jcommon.utils.MyMath.PI;

/**
 * Created by Yuri Kravchik on 16.11.17.
 */
public class IKnowOpenGL_4_CamAndWorld extends SimpleLwjglRoutine {
    public static void main(String[] args) {
        new IKnowOpenGL_4_CamAndWorld().start();
    }

    @Override public void onTick(float dt) {
        glMatrixMode(GL_PROJECTION);

        Matrix4 perspective = perspective(45, (float) w() / h(), 0.1f, 500);
        DDDUtils.glLoadMatrix(perspective);

        //big axis
        glMatrixMode(GL_MODELVIEW);
        DDDUtils.glLoadMatrix(
                //SECOND: rotate world (for camera)
                fromAngleAxisFast(PI / 2, v3(0, 1, 0)).toMatrix4()
                //FIRST: displace world to the right and slightly up
                .multiply(identity().translate(v3(10, 1, 0))));

        drawAxis(1);

        //small axis
        glMatrixMode(GL_MODELVIEW);
        DDDUtils.glLoadMatrix(
                //FOURTH rotate world (for camera)
                fromAngleAxisFast(PI / 2, v3(0, 1, 0)).toMatrix4()
                //THIRD translate world (for camera)
                .multiply(identity().translate(v3(10, 1, 0)))
                //SECOND translate object
                .multiply(identity().translate(v3(0.5f, 0.5f, 0.5f)))
                //FIRST rotate object around 000
                .multiply(fromAngleAxisFast(-PI / 4, v3(0, 0, 1)).toMatrix4())
        );

        //Or imagine it in the straight way:
        //  1 rotate world (for camera)
        //  2 inside it, translate world (for camera)
        //  3 inside it, translate object
        //  4 inside it, rotate object around 000

        //As we usually want the camera to rotate and displace the same way as an object, we must inverse its "true" displacement and rotation. Because we never rotate the camera, but the world itself.
        //And also the camera has to have "rot * pos" instead of "pos * rot", as rotation of it is "more global" than it is in objects.
        //And, of course, we want cam matrix * obj matrix (not vice versa) because obj is "more local" to the camera.


        drawAxis(0.1f);

        super.onTick(dt);
    }

    public static void drawAxis(float len) {
        glLineWidth(2);
        glBegin(GL_LINES);

        //RGB => XYZ
        //R, X
        glColor3f(1, 0, 0);
        glVertex3f(0, 0, 0);
        glVertex3f(len, 0, 0);

        //G, Y
        glColor3f(0, 1, 0);
        glVertex3f(0, 0, 0);
        glVertex3f(0, len, 0);

        //B, Z
        glColor3f(0, 0, 1);
        glVertex3f(0, 0, 0);
        glVertex3f(0, 0, len);

        glEnd();
    }
}
