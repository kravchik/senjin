package yk.senjin.shaders.gshader;

import yk.jcommon.fastgeom.*;

import java.awt.image.BufferedImage;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 11/6/14
 * Time: 12:37 PM
 */
abstract public class ShaderParent {
    public Matrix4 gl_ModelViewMatrix;
    public Matrix4 gl_ModelViewProjectionMatrix;
    public Matrix3 gl_NormalMatrix;
    public Vec4f gl_Vertex;
    public Vec4f gl_Normal;
    public Vec4f gl_Color;

    public static void main(String[] args) {
        System.out.println(new ProgramGenerator("/home/yuri/1/myproject/senjin/src/myengine/optiseq/states/shaders/gshader/VertexShader.groovy", null, "").resultSrc);
    }

    public Vec4f texture(Sampler2D p, Vec2f uv) {
        BufferedImage image = p.texture.image;
        //TODO clamp, mag, filter, etc
        int color = image.getRGB((int) uv.x, (int) uv.y);
        float a = (color & 0xff000000) >> 24;
        float r = (color & 0x00ff0000) >> 16;
        float g = (color & 0x0000ff00) >> 8;
        float b = color & 0x000000ff;
        Vec4f result = new Vec4f(a / 255f, r / 255f, g / 255f, b / 255f);
        System.out.println(result);
        return result;
    }

    public Vec2f Vec2f(double x, double y) {
        return new Vec2f((float)x, (float)y);
    }

    public Vec3f normalize(Vec3f p) {
        return p.normalized();
    }

    //I - 2.0 * dot(N, I) * N
    public Vec3f reflect(Vec3f i, Vec3f n) {
        return i.sub(n.mul(n.scalarProduct(i) * 2));
    }

    public float dot(Vec3f a, Vec3f b) {
        return a.scalarProduct(b);
    }

    public Vec3f Vec3f(Vec2f v2, double z) {
        return new Vec3f(v2.x, v2.y, (float)z);
    }

    public Vec3f Vec3f(double x, double y, double z) {
        return new Vec3f((float)x, (float)y, (float)z);
    }

    public Vec4f Vec4f(double x, double y, double z, double w) {
        return new Vec4f((float)w, (float)x, (float)y, (float)z);
    }

    public Vec4f Vec4f(Vec3f v, float w) {
        return new Vec4f(w, v.x, v.y, v.z);
    }

    public float max(double a, double b) {
        return (float) Math.max(a, b);
    }

    public float pow(float value, float power) {
        return (float) Math.pow(value, power);
    }
}
