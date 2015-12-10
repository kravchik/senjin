package yk.senjin;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import yk.jcommon.fastgeom.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.util.glu.GLU.gluProject;
import static org.lwjgl.util.glu.GLU.gluUnProject;


/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 3/11/13
 * Time: 10:30 PM
 */
public class DDDUtils {//TODO extract to another lib

    public static final float PI = (float) Math.PI;

    public static Matrix4f QtoM(Quaternionf q) {
        float x2 = q.i * q.i;
        float y2 = q.j * q.j;
        float z2 = q.k * q.k;
        float xy = q.i * q.j;
        float xz = q.i * q.k;
        float yz = q.j * q.k;
        float wx = q.a * q.i;
        float wy = q.a * q.j;
        float wz = q.a * q.k;

        Matrix4f result = new Matrix4f();
        result.m00 = 1.0f - 2.0f * (y2 + z2); result.m01 = 2.0f * (xy - wz); result.m02 = 2.0f * (xz + wy);
        result.m10 = 2.0f * (xy + wz);result.m11 = 1.0f - 2.0f * (x2 + z2);result.m12 = 2.0f * (yz - wx);
        result.m20 = 2.0f * (xz - wy);result.m21 = 2.0f * (yz + wx);result.m22 = 1.0f - 2.0f * (x2 + y2);
        result.m33 = 1;
        return result;
    }

    //TODO get my own matrix
    public static Vec3f mul(Matrix4f m, Vec3f v) {
        float x = v.x * m.m00 + v.y * m.m10 + v.z * m.m20 + m.m30;
        float y = v.x * m.m01 + v.y * m.m11 + v.z * m.m21 + m.m31;
        float z = v.x * m.m02 + v.y * m.m12 + v.z * m.m22 + m.m32;
        return new Vec3f(x, y, z);
    }

    public static Vec3f mul33(Matrix4f m, Vec3f v) {
        float x = v.x * m.m00 + v.y * m.m10 + v.z * m.m20;
        float y = v.x * m.m01 + v.y * m.m11 + v.z * m.m21;
        float z = v.x * m.m02 + v.y * m.m12 + v.z * m.m22;
        return new Vec3f(x, y, z);
    }

    public static void main(String[] args) {
        System.out.println(QtoM(Quaternionf.fromAngleAxisFast(0.1f, new Vec3f(0, 0, 1))));
    }

    private static FloatBuffer multMatrixTemp = BufferUtils.createFloatBuffer(16);

    public static void multMatrix(Matrix4f m4) {
        FloatBuffer buf = BufferUtils.createFloatBuffer(16);
        m4.store(buf);
        buf.rewind();
        glMultMatrix(buf);
    }

    public static void multMatrix(Quaternionf q) {
        fillFromQuaternion(q, multMatrixTemp);
        glMultMatrix(multMatrixTemp);
    }

    public static void glLoadMatrix(Matrix4 m) {
        m.store(multMatrixTemp);
        multMatrixTemp.rewind();
        GL11.glLoadMatrix(multMatrixTemp);
    }

    public static void glMultMatrix4(Matrix4 m) {
        m.store(multMatrixTemp);
        multMatrixTemp.rewind();
        GL11.glMultMatrix(multMatrixTemp);
    }

    public static void fillFromQuaternion(Quaternionf q, FloatBuffer matrixBuffer) {
        float x2 = q.i * q.i;
        float y2 = q.j * q.j;
        float z2 = q.k * q.k;
        float xy = q.i * q.j;
        float xz = q.i * q.k;
        float yz = q.j * q.k;
        float wx = q.a * q.i;
        float wy = q.a * q.j;
        float wz = q.a * q.k;

//        Matrix4f result = new Matrix4f();
//        result.m00 = 1.0f - 2.0f * (y2 + z2);
//        result.m01 = 2.0f * (xy - wz);
//        result.m02 = 2.0f * (xz + wy);
//        result.m10 = 2.0f * (xy + wz);
//        result.m11 = 1.0f - 2.0f * (x2 + z2);
//        result.m12 = 2.0f * (yz - wx);
//        result.m20 = 2.0f * (xz - wy);
//        result.m21 = 2.0f * (yz + wx);
//        result.m22 = 1.0f - 2.0f * (x2 + y2);
//        result.m33 = 1;
//        Matrix4f m4 = result;

        matrixBuffer.put(1.0f - 2.0f * (y2 + z2));
        matrixBuffer.put(2.0f * (xy - wz));
        matrixBuffer.put(2.0f * (xz + wy));
        matrixBuffer.put(0);
        matrixBuffer.put(2.0f * (xy + wz));
        matrixBuffer.put(1.0f - 2.0f * (x2 + z2));
        matrixBuffer.put(2.0f * (yz - wx));
        matrixBuffer.put(0);
        matrixBuffer.put(2.0f * (xz - wy));
        matrixBuffer.put(2.0f * (yz + wx));
        matrixBuffer.put(1.0f - 2.0f * (x2 + y2));
        matrixBuffer.put(0);
        matrixBuffer.put(0);
        matrixBuffer.put(0);
        matrixBuffer.put(0);
        matrixBuffer.put(1);
        matrixBuffer.rewind();
    }

    public static void fillMatrix(Quaternionf q, Matrix4f m) {
        float x2 = q.i * q.i;
        float y2 = q.j * q.j;
        float z2 = q.k * q.k;
        float xy = q.i * q.j;
        float xz = q.i * q.k;
        float yz = q.j * q.k;
        float wx = q.a * q.i;
        float wy = q.a * q.j;
        float wz = q.a * q.k;
        m.m00 = 1.0f - 2.0f * (y2 + z2);
        m.m01 = 2.0f * (xy - wz);
        m.m02 = 2.0f * (xz + wy);
        m.m10 = 2.0f * (xy + wz);
        m.m11 = 1.0f - 2.0f * (x2 + z2);
        m.m12 = 2.0f * (yz - wx);
        m.m20 = 2.0f * (xz - wy);
        m.m21 = 2.0f * (yz + wx);
        m.m22 = 1.0f - 2.0f * (x2 + y2);
        m.m33 = 1;
    }

    public static Matrix4f getMatrix(Quaternionf q) {
        Matrix4f result = new Matrix4f();
        fillMatrix(q, result);
        return result;
    }


    public static void drawPointer() {
        glColor3f(1, 1, 1);
        glBegin(GL11.GL_QUADS);
        glVertex3f(0, 0, 0);
        glVertex3f(0, 0, -3);
        glVertex3f(0.3f, 0, -3);
        glVertex3f(0.3f, 0, 0);
        glColor3f(0.5f, 0.5f, 0.5f);

        glVertex3f(0, 0, 0);
        glVertex3f(0, 0.3f, 0);
        glVertex3f(1, 0.3f, 0);
        glVertex3f(1, 0, 0);

        glEnd();
    }

    public static void setCam(Cam cam) {
        glMatrixMode(GL11.GL_MODELVIEW);
        glLoadIdentity();
        multMatrix(cam.lookRot.conjug());
        GL11.glTranslatef(-cam.lookAt.x, -cam.lookAt.y, -cam.lookAt.z);
    }


    public static Vec3f getStrictLineProjection(Vec3f point, Vec3f a, Vec3f b) {
        Vec3f b1 = b.sub(a);
        Vec3f pp = point.sub(a);
        Vec3f bn = b1.normalized();
        float x = pp.scalarProduct(bn);
        if (x < 0) return null;
        if (x > b1.length()) return null;
        return a.add(bn.mul(x));
    }

    public static Vec3f getTriangleProjection(Vec3f point, Vec3f[] vertices) {
        Vec3f a1 = vertices[1].sub(vertices[0]);
        Vec3f a2 = vertices[2].sub(vertices[0]);
        Vec3f pp = point.sub(vertices[0]);

        Vec3f normal = a1.crossProduct(a2).normalized();
        float proj = pp.scalarProduct(normal);
        return pp.sub(normal.mul(proj)).add(vertices[0]);
    }

    public static Vec3f getStrictTriangleProjection(Vec3f point, Vec3f[] vertices) {
        Vec3f proj = getTriangleProjection(point, vertices);

        Vec3f a1 = vertices[1].sub(vertices[0]);
        Vec3f a2 = vertices[2].sub(vertices[0]);
        Vec3f pp = proj.sub(vertices[0]);


        float x = pp.scalarProduct(a1.normalized());
        Vec3f projVec = pp.sub(a1.normalized().mul(pp.scalarProduct(a1.normalized())));
        float y = projVec.length();

        float x1 = a2.scalarProduct(a1.normalized());
        Vec3f projVec2 = a2.sub(a1.normalized().mul(a2.scalarProduct(a1.normalized())));
        float y1 = projVec2.length();

        float pointSign = projVec2.sub(projVec).length() > projVec2.add(projVec).length() ? -1 : 1;
        Vec2f p = new Vec2f(x, y*pointSign);
        Vec2f a = new Vec2f(0, 0);
        Vec2f b = new Vec2f(x1, y1);
        Vec2f c = new Vec2f(a1.length(), 0);

        float sign = Math.signum(b.cross(c));
        if (sign == 0) return null;
        if (Math.signum(p.sub(a).cross(c.sub(a))) * sign < 1) return null;
        if (Math.signum(p.sub(b).cross(a.sub(b))) * sign < 1) return null;
        if (Math.signum(p.sub(c).cross(b.sub(c))) * sign < 1) return null;

        return proj;
    }

    public float CUBE_COL[][] = new float[][]{
            {1, 1, 1},
            {1, 0, 1},
            {1, 1, 0},
            {0, 1, 1},
            {0, 1, 0},
            {0, 0, 1}
    };

    public static float CUBE_POS[][][] = new float[][][]{
            {{-1, -1, 1},
                    {-1, 1, 1},
                    {1, 1, 1},
                    {1, -1, 1}},

            {{-1, -1, -1},
                    {-1, 1, -1},
                    {1, 1, -1},
                    {1, -1, -1}},

            {{-1, -1, -1},
                    {-1, 1, -1},
                    {-1, 1, 1},
                    {-1, -1, 1}},

            {{1, -1, -1},
                    {1, 1, -1},
                    {1, 1, 1},
                    {1, -1, 1}},

            {{-1, 1, -1},
                    {1, 1, -1},
                    {1, 1, 1},
                    {-1, 1, 1}},

            {{-1, -1, -1},
                    {1, -1, -1},
                    {1, -1, 1},
                    {-1, -1, 1}}
    };

    public static float CUBE_POS_AND_COLOR[][][] = new float[][][]{
            {
                    {1, 0, 0},
                    {-1, -1, 1},
                    {1, -1, 1},
                    {1, 1, 1},
                    {-1, 1, 1},
            },

            {
                    {0, 1, 0},
                    {-1, -1, -1},
                    {-1, 1, -1},
                    {1, 1, -1},
                    {1, -1, -1},
            },

            {
                    {0, 0, 1},
                    {-1, -1, -1},
                    {-1, -1, 1},
                    {-1, 1, 1},
                    {-1, 1, -1},
            },

            {
                    {1, 1, 0},
                    {1, -1, -1},
                    {1, 1, -1},
                    {1, 1, 1},
                    {1, -1, 1},
            },

            {
                    {1, 0, 1},
                    {-1, 1, -1},
                    {-1, 1, 1},
                    {1, 1, 1},
                    {1, 1, -1},
            },

            {
                    {0, 1, 1},
                    {-1, -1, -1},
                    {1, -1, -1},
                    {1, -1, 1},
                    {-1, -1, 1},
            }
    };
    public static void sendCubeQuat() {
        sendCubeQuat(null);
    }

    public static void sendCubeQuat1(Vec3f color) {
        for (float[][] flat : CUBE_POS_AND_COLOR) {
            if (color == null) glColor3f(flat[0][0], flat[0][1], flat[0][2]);
            else glColor3f(color.x, color.y, color.z);
            for (int i = 1; i < flat.length; i++) {
                glVertex3f(flat[i][0], flat[i][1], flat[i][2]);
            }
        }
    }

    public static void sendCubeQuat(List<Color4f> colors) {
        for (int i1 = 0; i1 < CUBE_POS_AND_COLOR.length; i1++) {
            float[][] flat = CUBE_POS_AND_COLOR[i1];
            if (colors != null) glColor(colors.get(i1));
            for (int i = 1; i < flat.length; i++) {
                glVertex3f(flat[i][0], flat[i][1], flat[i][2]);
            }
        }
    }

    public static void glVec3f(Vec3f v) {
        GL11.glVertex3f(v.x, v.y, v.z);
    }

    public static void glScale(Vec3f v) {
        glScalef(v.x, v.y, v.z);
    }

    private static FloatBuffer modelview = BufferUtils.createFloatBuffer(16);
    private static FloatBuffer projection = BufferUtils.createFloatBuffer(16);
    private static IntBuffer viewport = BufferUtils.createIntBuffer(16);
    private final static FloatBuffer tempBuffer = FloatBuffer.allocate(3);

    private static void generateArrays() {
        modelview.clear();
        projection.clear();
        viewport.clear();
        glGetFloat(GL_MODELVIEW_MATRIX, modelview);
        glGetFloat(GL_PROJECTION_MATRIX, projection);
        glGetInteger(GL_VIEWPORT, viewport);
    }

    public static Vec3f screen2World(Vec3f camera, final Vec2f p, float forZ) {
        generateArrays();
        tempBuffer.clear();
        gluUnProject(p.x, p.y, 1f, modelview, projection, viewport, tempBuffer);
        Vec3f result = new Vec3f(tempBuffer.get(0), tempBuffer.get(1), tempBuffer.get(2));
        Vec3f result2 = camera.sub(result);
        result = new Vec3f(forZ - result2.x * result.z / result2.z + result.x, forZ - result2.y * result.z / result2.z + result.y, forZ);
        return result;
    }

    public static Vec3f world2Screen(Vec3f worldPos) {
        gluProject(worldPos.x, worldPos.y, worldPos.z, modelview, projection, viewport, tempBuffer);
        return new Vec3f(tempBuffer.get(0), tempBuffer.get(1), tempBuffer.get(2));
    }

    public static void glTranslate(Vec3f v) {
        GL11.glTranslatef(v.x, v.y, v.z);
    }

    public static float toDegrees(float rads) {
        return (float) (rads / PI * 180);
    }

    public static void glColor(Color4f color) {
        glColor4f(color.r, color.g, color.b, color.a);
    }

    public static void glColor(Vec3f color) {
        glColor3f(color.x, color.y, color.z);
    }

    public static Matrix4 get2DProjectionMatrix(float w, float h, float f) {
        Matrix4 result = new Matrix4();
        result.set(0, 0, 2f / w); result.set(3, 0, -1);
        result.set(1, 1, 2f / h); result.set(3, 1, -1);
        result.set(2, 2, -2f / f); result.set(3, 1, -1);
        result.set(3, 3, 1);
        return result;
    }

    public static Matrix4 get2DProjectionMatrix(float l, float r, float b, float t, float n, float f) {
        Matrix4 result = new Matrix4();
        float w = r - l;
        float h = t - b;
        float fn = f - n;
        result.set(0, 0, 2f / w); result.set(3, 0, -(r + l) / w);
        result.set(1, 1, 2f / h); result.set(3, 1, -(t + b) / h);
        result.set(2, 2, -2f / fn); result.set(2, 2, -(f + n) / fn);
        result.set(3, 3, 1);
        return result;
    }

    public static void drawTriangle(Vec3f a, Vec3f b, Vec3f c, float shrink) {
        Vec3f center = a.add(b).add(c).div(3);
        glVec3f(a.sub(center).mul(shrink).add(center));
        glVec3f(b.sub(center).mul(shrink).add(center));
        glVec3f(c.sub(center).mul(shrink).add(center));
    }

    public static void uniform(int index, Vec2f v) {
        uniform(index, v.x, v.y);
    }
    public static void uniform(int index, Vec3f v) {
        uniform(index, v.x, v.y, v.z);
    }
    public static void uniform(int index, Vec4f v) {
        uniform(index, v.w, v.x, v.y, v.z);
    }
    public static void uniform(int index, Vec2i v) {
        uniform(index, v.x, v.y);
    }

    public static void uniform(int index, float a) {
        glUniform1f(index, a);
    }
    public static void uniform(int index, float a, float b) {
        glUniform2f(index, a, b);
    }
    public static void uniform(int index, float a, float b, float c) {
        glUniform3f(index, a, b, c);
    }
    public static void uniform(int index, float a, float b, float c, float d) {
        glUniform4f(index, a, b, c, d);
    }
    public static void uniform(int index, int a) {
        glUniform1i(index, a);
    }
    public static void uniform(int index, int a, int b) {
        glUniform2i(index, a, b);
    }
    public static void uniform(int index, int a, int b, int c) {
        glUniform3i(index, a, b, c);
    }
    public static void uniform(int index, int a, int b, int c, int d) {
        glUniform4i(index, a, b, c, d);
    }
}
