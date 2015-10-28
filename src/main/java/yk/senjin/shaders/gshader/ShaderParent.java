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
    @Deprecated
    public Matrix4 gl_ModelViewMatrix;
    @Deprecated
    public Matrix4 gl_ModelViewProjectionMatrix;
    @Deprecated
    public Matrix3 gl_NormalMatrix;

    public static Vec4f texture2D(Sampler2D p, Vec2f uv) {
        BufferedImage image = p.texture.image;
        //TODO clamp, mag, filter, etc
        int color = image.getRGB((int) uv.x, (int) uv.y);
        float a = (color & 0xff000000) >> 24;
        float r = (color & 0x00ff0000) >> 16;
        float g = (color & 0x0000ff00) >> 8;
        float b = color & 0x000000ff;
        return new Vec4f(a / 255f, r / 255f, g / 255f, b / 255f);
    }

    public static Vec2f vec2(double x, double y) {
        return new Vec2f((float)x, (float)y);
    }

    public static Vec2f vec2(double x) {
        return new Vec2f((float)x, (float)x);
    }

    public static Vec3f normalize(Vec3f p) {
        return p.normalized();
    }

    //I - 2.0 * dot(N, I) * N
    public static Vec3f reflect(Vec3f i, Vec3f n) {
        return i.sub(n.mul(n.scalarProduct(i) * 2));
    }

    public static float dot(Vec3f a, Vec3f b) {
        return a.scalarProduct(b);
    }

    public static Vec3f Vec3f(Vec2f v2, double z) {
        return new Vec3f(v2.x, v2.y, (float)z);
    }

    public static Vec3f Vec3f(double x, double y, double z) {
        return new Vec3f((float)x, (float)y, (float)z);
    }

    public static Vec4f Vec4f(double x, double y, double z, double w) {
        return new Vec4f((float)w, (float)x, (float)y, (float)z);
    }

    public static Vec4f Vec4f(double x) {
        return new Vec4f((float)x, (float)x, (float)x, (float)x);
    }

    public static Vec3f Vec3f(double x) {
        return new Vec3f((float)x, (float)x, (float)x);
    }

    public static Vec4f Vec4f(Vec3f v, double w) {
        return new Vec4f((float) w, v.x, v.y, v.z);
    }

    public static double max(double a, double b) {
        return Math.max(a, b);
    }

    public static float max(float a, float b) {
        return Math.max(a, b); 
    }

    public static Vec2f max(Vec2f a, Vec2f b) {
        return new Vec2f(max(a.x, b.x), max(a.y, b.y)); 
    }

    public static Vec3f max(Vec3f a, Vec3f b) {
        return new Vec3f(max(a.x, b.x), max(a.y, b.y), max(a.z, b.z)); 
    }

    public static Vec4f max(Vec4f a, Vec4f b) {
        return new Vec4f(max(a.w, b.w), max(a.x, b.x), max(a.y, b.y), max(a.z, b.z)); 
    }

    public static double min(double a, double b) {
        return Math.min(a, b); 
    }

    public static float min(float a, float b) {
        return Math.min(a, b); 
    }

    public static Vec2f min(Vec2f a, Vec2f b) {
        return new Vec2f(min(a.x, b.x), min(a.y, b.y)); 
    }

    public static Vec3f min(Vec3f a, Vec3f b) {
        return new Vec3f(min(a.x, b.x), min(a.y, b.y), min(a.z, b.z)); 
    }

    public static Vec4f min(Vec4f a, Vec4f b) {
        return new Vec4f(min(a.w, b.w), min(a.x, b.x), min(a.y, b.y), min(a.z, b.z)); 
    }

    public static float pow(float value, float power) {
        return (float) Math.pow(value, power);
    }
}
