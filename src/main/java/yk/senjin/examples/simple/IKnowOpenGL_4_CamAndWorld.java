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

        Matrix4 perspective = perspective(45, (float) w / h, 0.1f, 500);
        DDDUtils.glLoadMatrix(perspective);

        glMatrixMode(GL_MODELVIEW);
        DDDUtils.glLoadMatrix(
                fromAngleAxisFast(PI / 2, v3(0, 1, 0)).toMatrix4()                  //SECOND: rotate world (like for camera)
                        .multiply(identity().translate(v3(10, 1, 0))));      //FIRST: displace world to the right and slightly up

        drawAxis(1);

        glMatrixMode(GL_MODELVIEW);
        DDDUtils.glLoadMatrix(
                fromAngleAxisFast(PI / 2, v3(0, 1, 0)).toMatrix4()                        //FOURTH rotate world (like for camera)
                        .multiply(identity().translate(v3(10, 1, 0)))              //THIRD translate world (like for camera)
                        .multiply(identity().translate(v3(0.5f, 0.5f, 0.5f)))     //SECOND translate object
                        .multiply(fromAngleAxisFast(-PI / 4, v3(0, 0, 1)).toMatrix4()) //FIRST rotate object around 000
        );

        //Or imagine it in the straight way:
        //  1 rotate world (like for camera)
        //  2 INSIDE IT translate world (like for camera)
        //  3 INSIDE IT translate object
        //  4 INSIDE IT rotate object around 000

        //As we always want the camera to rotate and displace the same way as an object, we must inverse its "true" displacement and rotation.
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
