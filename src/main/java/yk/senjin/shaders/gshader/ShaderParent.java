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
    //TODO move independent math to jcommon
    @Deprecated
    public Matrix4 gl_ModelViewMatrix;
    @Deprecated
    public Matrix4 gl_ModelViewProjectionMatrix;
    @Deprecated
    public Matrix3 gl_NormalMatrix;

    public static Vec4f texture2D(Sampler2D p, Vec2f uv, int zoom) {
        return texture2D(p, uv);
    }

    public static Vec4f texture2D(Sampler2D p, Vec2f uv) {
        BufferedImage image = p.texture.image;
        //TODO clamp, mag, filter, etc
        //now works as GL_REPEAT, mag filter GL_LINEAR, min filter - I don't know
        float fx = fract(uv.x);
        float fy = fract(uv.x);
        int x = (int) (fx * image.getWidth());
        int y = (int) (fy * image.getHeight());
        float ffx = fx - x;
        float ffy = fy - y;

        Vec4f c00 = getColor(p, x, y);
        Vec4f c10 = getColor(p, x+1, y);
        Vec4f c11 = getColor(p, x+1, y+1);
        Vec4f c01 = getColor(p, x, y+1);
        return mix(mix(c00, c01, ffy), mix(c10, c11, ffy), ffx);
    }

    private static Vec4f getColor(Sampler2D p, int x, int y) {
        BufferedImage image = p.texture.image;
        x = (int) mod(x, image.getWidth());
        y = (int) mod(y, image.getHeight());
        int color = image.getRGB(x, y);
        float a = (color & 0xff000000) >> 24;
        float r = (color & 0x00ff0000) >> 16;
        float g = (color & 0x0000ff00) >> 8;
        float b = color & 0x000000ff;
        return new Vec4f(a / 255f, r / 255f, g / 255f, b / 255f);
    }

    public static Vec2f Vec2f(double x, double y) {
        return new Vec2f((float)x, (float)y);
    }

    public static Vec2f Vec2f(double x) {
        return new Vec2f((float)x, (float)x);
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

//gglsl auto generated text
public static Float plus(Float arg0, Float arg1) {return (float)arg0+(float)arg1;}
public static Vec2f plus(Vec2f arg0, Vec2f arg1) {return Vec2f.v2((float)arg0.x+(float)arg1.x, (float)arg0.y+(float)arg1.y);}
public static Vec3f plus(Vec3f arg0, Vec3f arg1) {return Vec3f.v3((float)arg0.x+(float)arg1.x, (float)arg0.y+(float)arg1.y, (float)arg0.z+(float)arg1.z);}
public static Vec4f plus(Vec4f arg0, Vec4f arg1) {return Vec4f.v4((float)arg0.x+(float)arg1.x, (float)arg0.y+(float)arg1.y, (float)arg0.z+(float)arg1.z, (float)arg0.w+(float)arg1.w);}
public static Vec2f plus(float arg0, Vec2f arg1) {return Vec2f.v2((float)arg0+(float)arg1.x, (float)arg0+(float)arg1.y);}
public static Vec3f plus(float arg0, Vec3f arg1) {return Vec3f.v3((float)arg0+(float)arg1.x, (float)arg0+(float)arg1.y, (float)arg0+(float)arg1.z);}
public static Vec4f plus(float arg0, Vec4f arg1) {return Vec4f.v4((float)arg0+(float)arg1.x, (float)arg0+(float)arg1.y, (float)arg0+(float)arg1.z, (float)arg0+(float)arg1.w);}
public static Vec2f plus(Float arg0, Vec2f arg1) {return Vec2f.v2((float)arg0+(float)arg1.x, (float)arg0+(float)arg1.y);}
public static Vec3f plus(Float arg0, Vec3f arg1) {return Vec3f.v3((float)arg0+(float)arg1.x, (float)arg0+(float)arg1.y, (float)arg0+(float)arg1.z);}
public static Vec4f plus(Float arg0, Vec4f arg1) {return Vec4f.v4((float)arg0+(float)arg1.x, (float)arg0+(float)arg1.y, (float)arg0+(float)arg1.z, (float)arg0+(float)arg1.w);}
public static Vec2f plus(Number arg0, Vec2f arg1) {return Vec2f.v2((float)arg0+(float)arg1.x, (float)arg0+(float)arg1.y);}
public static Vec3f plus(Number arg0, Vec3f arg1) {return Vec3f.v3((float)arg0+(float)arg1.x, (float)arg0+(float)arg1.y, (float)arg0+(float)arg1.z);}
public static Vec4f plus(Number arg0, Vec4f arg1) {return Vec4f.v4((float)arg0+(float)arg1.x, (float)arg0+(float)arg1.y, (float)arg0+(float)arg1.z, (float)arg0+(float)arg1.w);}
public static Vec2f plus(Vec2f arg0, float arg1) {return Vec2f.v2((float)arg0.x+(float)arg1, (float)arg0.y+(float)arg1);}
public static Vec3f plus(Vec3f arg0, float arg1) {return Vec3f.v3((float)arg0.x+(float)arg1, (float)arg0.y+(float)arg1, (float)arg0.z+(float)arg1);}
public static Vec4f plus(Vec4f arg0, float arg1) {return Vec4f.v4((float)arg0.x+(float)arg1, (float)arg0.y+(float)arg1, (float)arg0.z+(float)arg1, (float)arg0.w+(float)arg1);}
public static Vec2f plus(Vec2f arg0, Float arg1) {return Vec2f.v2((float)arg0.x+(float)arg1, (float)arg0.y+(float)arg1);}
public static Vec3f plus(Vec3f arg0, Float arg1) {return Vec3f.v3((float)arg0.x+(float)arg1, (float)arg0.y+(float)arg1, (float)arg0.z+(float)arg1);}
public static Vec4f plus(Vec4f arg0, Float arg1) {return Vec4f.v4((float)arg0.x+(float)arg1, (float)arg0.y+(float)arg1, (float)arg0.z+(float)arg1, (float)arg0.w+(float)arg1);}
public static Vec2f plus(Vec2f arg0, Number arg1) {return Vec2f.v2((float)arg0.x+(float)arg1, (float)arg0.y+(float)arg1);}
public static Vec3f plus(Vec3f arg0, Number arg1) {return Vec3f.v3((float)arg0.x+(float)arg1, (float)arg0.y+(float)arg1, (float)arg0.z+(float)arg1);}
public static Vec4f plus(Vec4f arg0, Number arg1) {return Vec4f.v4((float)arg0.x+(float)arg1, (float)arg0.y+(float)arg1, (float)arg0.z+(float)arg1, (float)arg0.w+(float)arg1);}
public static Float minus(Float arg0, Float arg1) {return (float)arg0-(float)arg1;}
public static Vec2f minus(Vec2f arg0, Vec2f arg1) {return Vec2f.v2((float)arg0.x-(float)arg1.x, (float)arg0.y-(float)arg1.y);}
public static Vec3f minus(Vec3f arg0, Vec3f arg1) {return Vec3f.v3((float)arg0.x-(float)arg1.x, (float)arg0.y-(float)arg1.y, (float)arg0.z-(float)arg1.z);}
public static Vec4f minus(Vec4f arg0, Vec4f arg1) {return Vec4f.v4((float)arg0.x-(float)arg1.x, (float)arg0.y-(float)arg1.y, (float)arg0.z-(float)arg1.z, (float)arg0.w-(float)arg1.w);}
public static Vec2f minus(float arg0, Vec2f arg1) {return Vec2f.v2((float)arg0-(float)arg1.x, (float)arg0-(float)arg1.y);}
public static Vec3f minus(float arg0, Vec3f arg1) {return Vec3f.v3((float)arg0-(float)arg1.x, (float)arg0-(float)arg1.y, (float)arg0-(float)arg1.z);}
public static Vec4f minus(float arg0, Vec4f arg1) {return Vec4f.v4((float)arg0-(float)arg1.x, (float)arg0-(float)arg1.y, (float)arg0-(float)arg1.z, (float)arg0-(float)arg1.w);}
public static Vec2f minus(Float arg0, Vec2f arg1) {return Vec2f.v2((float)arg0-(float)arg1.x, (float)arg0-(float)arg1.y);}
public static Vec3f minus(Float arg0, Vec3f arg1) {return Vec3f.v3((float)arg0-(float)arg1.x, (float)arg0-(float)arg1.y, (float)arg0-(float)arg1.z);}
public static Vec4f minus(Float arg0, Vec4f arg1) {return Vec4f.v4((float)arg0-(float)arg1.x, (float)arg0-(float)arg1.y, (float)arg0-(float)arg1.z, (float)arg0-(float)arg1.w);}
public static Vec2f minus(Number arg0, Vec2f arg1) {return Vec2f.v2((float)arg0-(float)arg1.x, (float)arg0-(float)arg1.y);}
public static Vec3f minus(Number arg0, Vec3f arg1) {return Vec3f.v3((float)arg0-(float)arg1.x, (float)arg0-(float)arg1.y, (float)arg0-(float)arg1.z);}
public static Vec4f minus(Number arg0, Vec4f arg1) {return Vec4f.v4((float)arg0-(float)arg1.x, (float)arg0-(float)arg1.y, (float)arg0-(float)arg1.z, (float)arg0-(float)arg1.w);}
public static Vec2f minus(Vec2f arg0, float arg1) {return Vec2f.v2((float)arg0.x-(float)arg1, (float)arg0.y-(float)arg1);}
public static Vec3f minus(Vec3f arg0, float arg1) {return Vec3f.v3((float)arg0.x-(float)arg1, (float)arg0.y-(float)arg1, (float)arg0.z-(float)arg1);}
public static Vec4f minus(Vec4f arg0, float arg1) {return Vec4f.v4((float)arg0.x-(float)arg1, (float)arg0.y-(float)arg1, (float)arg0.z-(float)arg1, (float)arg0.w-(float)arg1);}
public static Vec2f minus(Vec2f arg0, Float arg1) {return Vec2f.v2((float)arg0.x-(float)arg1, (float)arg0.y-(float)arg1);}
public static Vec3f minus(Vec3f arg0, Float arg1) {return Vec3f.v3((float)arg0.x-(float)arg1, (float)arg0.y-(float)arg1, (float)arg0.z-(float)arg1);}
public static Vec4f minus(Vec4f arg0, Float arg1) {return Vec4f.v4((float)arg0.x-(float)arg1, (float)arg0.y-(float)arg1, (float)arg0.z-(float)arg1, (float)arg0.w-(float)arg1);}
public static Vec2f minus(Vec2f arg0, Number arg1) {return Vec2f.v2((float)arg0.x-(float)arg1, (float)arg0.y-(float)arg1);}
public static Vec3f minus(Vec3f arg0, Number arg1) {return Vec3f.v3((float)arg0.x-(float)arg1, (float)arg0.y-(float)arg1, (float)arg0.z-(float)arg1);}
public static Vec4f minus(Vec4f arg0, Number arg1) {return Vec4f.v4((float)arg0.x-(float)arg1, (float)arg0.y-(float)arg1, (float)arg0.z-(float)arg1, (float)arg0.w-(float)arg1);}
public static Float multiply(Float arg0, Float arg1) {return (float)arg0*(float)arg1;}
public static Vec2f multiply(Vec2f arg0, Vec2f arg1) {return Vec2f.v2((float)arg0.x*(float)arg1.x, (float)arg0.y*(float)arg1.y);}
public static Vec3f multiply(Vec3f arg0, Vec3f arg1) {return Vec3f.v3((float)arg0.x*(float)arg1.x, (float)arg0.y*(float)arg1.y, (float)arg0.z*(float)arg1.z);}
public static Vec4f multiply(Vec4f arg0, Vec4f arg1) {return Vec4f.v4((float)arg0.x*(float)arg1.x, (float)arg0.y*(float)arg1.y, (float)arg0.z*(float)arg1.z, (float)arg0.w*(float)arg1.w);}
public static Vec2f multiply(float arg0, Vec2f arg1) {return Vec2f.v2((float)arg0*(float)arg1.x, (float)arg0*(float)arg1.y);}
public static Vec3f multiply(float arg0, Vec3f arg1) {return Vec3f.v3((float)arg0*(float)arg1.x, (float)arg0*(float)arg1.y, (float)arg0*(float)arg1.z);}
public static Vec4f multiply(float arg0, Vec4f arg1) {return Vec4f.v4((float)arg0*(float)arg1.x, (float)arg0*(float)arg1.y, (float)arg0*(float)arg1.z, (float)arg0*(float)arg1.w);}
public static Vec2f multiply(Float arg0, Vec2f arg1) {return Vec2f.v2((float)arg0*(float)arg1.x, (float)arg0*(float)arg1.y);}
public static Vec3f multiply(Float arg0, Vec3f arg1) {return Vec3f.v3((float)arg0*(float)arg1.x, (float)arg0*(float)arg1.y, (float)arg0*(float)arg1.z);}
public static Vec4f multiply(Float arg0, Vec4f arg1) {return Vec4f.v4((float)arg0*(float)arg1.x, (float)arg0*(float)arg1.y, (float)arg0*(float)arg1.z, (float)arg0*(float)arg1.w);}
public static Vec2f multiply(Number arg0, Vec2f arg1) {return Vec2f.v2((float)arg0*(float)arg1.x, (float)arg0*(float)arg1.y);}
public static Vec3f multiply(Number arg0, Vec3f arg1) {return Vec3f.v3((float)arg0*(float)arg1.x, (float)arg0*(float)arg1.y, (float)arg0*(float)arg1.z);}
public static Vec4f multiply(Number arg0, Vec4f arg1) {return Vec4f.v4((float)arg0*(float)arg1.x, (float)arg0*(float)arg1.y, (float)arg0*(float)arg1.z, (float)arg0*(float)arg1.w);}
public static Vec2f multiply(Vec2f arg0, float arg1) {return Vec2f.v2((float)arg0.x*(float)arg1, (float)arg0.y*(float)arg1);}
public static Vec3f multiply(Vec3f arg0, float arg1) {return Vec3f.v3((float)arg0.x*(float)arg1, (float)arg0.y*(float)arg1, (float)arg0.z*(float)arg1);}
public static Vec4f multiply(Vec4f arg0, float arg1) {return Vec4f.v4((float)arg0.x*(float)arg1, (float)arg0.y*(float)arg1, (float)arg0.z*(float)arg1, (float)arg0.w*(float)arg1);}
public static Vec2f multiply(Vec2f arg0, Float arg1) {return Vec2f.v2((float)arg0.x*(float)arg1, (float)arg0.y*(float)arg1);}
public static Vec3f multiply(Vec3f arg0, Float arg1) {return Vec3f.v3((float)arg0.x*(float)arg1, (float)arg0.y*(float)arg1, (float)arg0.z*(float)arg1);}
public static Vec4f multiply(Vec4f arg0, Float arg1) {return Vec4f.v4((float)arg0.x*(float)arg1, (float)arg0.y*(float)arg1, (float)arg0.z*(float)arg1, (float)arg0.w*(float)arg1);}
public static Vec2f multiply(Vec2f arg0, Number arg1) {return Vec2f.v2((float)arg0.x*(float)arg1, (float)arg0.y*(float)arg1);}
public static Vec3f multiply(Vec3f arg0, Number arg1) {return Vec3f.v3((float)arg0.x*(float)arg1, (float)arg0.y*(float)arg1, (float)arg0.z*(float)arg1);}
public static Vec4f multiply(Vec4f arg0, Number arg1) {return Vec4f.v4((float)arg0.x*(float)arg1, (float)arg0.y*(float)arg1, (float)arg0.z*(float)arg1, (float)arg0.w*(float)arg1);}
public static Float div(Float arg0, Float arg1) {return (float)arg0/(float)arg1;}
public static Vec2f div(Vec2f arg0, Vec2f arg1) {return Vec2f.v2((float)arg0.x/(float)arg1.x, (float)arg0.y/(float)arg1.y);}
public static Vec3f div(Vec3f arg0, Vec3f arg1) {return Vec3f.v3((float)arg0.x/(float)arg1.x, (float)arg0.y/(float)arg1.y, (float)arg0.z/(float)arg1.z);}
public static Vec4f div(Vec4f arg0, Vec4f arg1) {return Vec4f.v4((float)arg0.x/(float)arg1.x, (float)arg0.y/(float)arg1.y, (float)arg0.z/(float)arg1.z, (float)arg0.w/(float)arg1.w);}
public static Vec2f div(float arg0, Vec2f arg1) {return Vec2f.v2((float)arg0/(float)arg1.x, (float)arg0/(float)arg1.y);}
public static Vec3f div(float arg0, Vec3f arg1) {return Vec3f.v3((float)arg0/(float)arg1.x, (float)arg0/(float)arg1.y, (float)arg0/(float)arg1.z);}
public static Vec4f div(float arg0, Vec4f arg1) {return Vec4f.v4((float)arg0/(float)arg1.x, (float)arg0/(float)arg1.y, (float)arg0/(float)arg1.z, (float)arg0/(float)arg1.w);}
public static Vec2f div(Float arg0, Vec2f arg1) {return Vec2f.v2((float)arg0/(float)arg1.x, (float)arg0/(float)arg1.y);}
public static Vec3f div(Float arg0, Vec3f arg1) {return Vec3f.v3((float)arg0/(float)arg1.x, (float)arg0/(float)arg1.y, (float)arg0/(float)arg1.z);}
public static Vec4f div(Float arg0, Vec4f arg1) {return Vec4f.v4((float)arg0/(float)arg1.x, (float)arg0/(float)arg1.y, (float)arg0/(float)arg1.z, (float)arg0/(float)arg1.w);}
public static Vec2f div(Number arg0, Vec2f arg1) {return Vec2f.v2((float)arg0/(float)arg1.x, (float)arg0/(float)arg1.y);}
public static Vec3f div(Number arg0, Vec3f arg1) {return Vec3f.v3((float)arg0/(float)arg1.x, (float)arg0/(float)arg1.y, (float)arg0/(float)arg1.z);}
public static Vec4f div(Number arg0, Vec4f arg1) {return Vec4f.v4((float)arg0/(float)arg1.x, (float)arg0/(float)arg1.y, (float)arg0/(float)arg1.z, (float)arg0/(float)arg1.w);}
public static Vec2f div(Vec2f arg0, float arg1) {return Vec2f.v2((float)arg0.x/(float)arg1, (float)arg0.y/(float)arg1);}
public static Vec3f div(Vec3f arg0, float arg1) {return Vec3f.v3((float)arg0.x/(float)arg1, (float)arg0.y/(float)arg1, (float)arg0.z/(float)arg1);}
public static Vec4f div(Vec4f arg0, float arg1) {return Vec4f.v4((float)arg0.x/(float)arg1, (float)arg0.y/(float)arg1, (float)arg0.z/(float)arg1, (float)arg0.w/(float)arg1);}
public static Vec2f div(Vec2f arg0, Float arg1) {return Vec2f.v2((float)arg0.x/(float)arg1, (float)arg0.y/(float)arg1);}
public static Vec3f div(Vec3f arg0, Float arg1) {return Vec3f.v3((float)arg0.x/(float)arg1, (float)arg0.y/(float)arg1, (float)arg0.z/(float)arg1);}
public static Vec4f div(Vec4f arg0, Float arg1) {return Vec4f.v4((float)arg0.x/(float)arg1, (float)arg0.y/(float)arg1, (float)arg0.z/(float)arg1, (float)arg0.w/(float)arg1);}
public static Vec2f div(Vec2f arg0, Number arg1) {return Vec2f.v2((float)arg0.x/(float)arg1, (float)arg0.y/(float)arg1);}
public static Vec3f div(Vec3f arg0, Number arg1) {return Vec3f.v3((float)arg0.x/(float)arg1, (float)arg0.y/(float)arg1, (float)arg0.z/(float)arg1);}
public static Vec4f div(Vec4f arg0, Number arg1) {return Vec4f.v4((float)arg0.x/(float)arg1, (float)arg0.y/(float)arg1, (float)arg0.z/(float)arg1, (float)arg0.w/(float)arg1);}
public static float radians(float arg0) {return (float)(arg0/180f*Math.PI);}
public static Vec2f radians(Vec2f arg0) {return Vec2f.v2((float)(arg0.x/180f*Math.PI), (float)(arg0.y/180f*Math.PI));}
public static Vec3f radians(Vec3f arg0) {return Vec3f.v3((float)(arg0.x/180f*Math.PI), (float)(arg0.y/180f*Math.PI), (float)(arg0.z/180f*Math.PI));}
public static Vec4f radians(Vec4f arg0) {return Vec4f.v4((float)(arg0.x/180f*Math.PI), (float)(arg0.y/180f*Math.PI), (float)(arg0.z/180f*Math.PI), (float)(arg0.w/180f*Math.PI));}
public static float degrees(float arg0) {return (float)(arg0/Math.PI*180);}
public static Vec2f degrees(Vec2f arg0) {return Vec2f.v2((float)(arg0.x/Math.PI*180), (float)(arg0.y/Math.PI*180));}
public static Vec3f degrees(Vec3f arg0) {return Vec3f.v3((float)(arg0.x/Math.PI*180), (float)(arg0.y/Math.PI*180), (float)(arg0.z/Math.PI*180));}
public static Vec4f degrees(Vec4f arg0) {return Vec4f.v4((float)(arg0.x/Math.PI*180), (float)(arg0.y/Math.PI*180), (float)(arg0.z/Math.PI*180), (float)(arg0.w/Math.PI*180));}
public static float sin(float arg0) {return ((float)Math.sin(arg0));}
public static Vec2f sin(Vec2f arg0) {return Vec2f.v2(((float)Math.sin(arg0.x)), ((float)Math.sin(arg0.y)));}
public static Vec3f sin(Vec3f arg0) {return Vec3f.v3(((float)Math.sin(arg0.x)), ((float)Math.sin(arg0.y)), ((float)Math.sin(arg0.z)));}
public static Vec4f sin(Vec4f arg0) {return Vec4f.v4(((float)Math.sin(arg0.x)), ((float)Math.sin(arg0.y)), ((float)Math.sin(arg0.z)), ((float)Math.sin(arg0.w)));}
public static float cos(float arg0) {return ((float)Math.cos(arg0));}
public static Vec2f cos(Vec2f arg0) {return Vec2f.v2(((float)Math.cos(arg0.x)), ((float)Math.cos(arg0.y)));}
public static Vec3f cos(Vec3f arg0) {return Vec3f.v3(((float)Math.cos(arg0.x)), ((float)Math.cos(arg0.y)), ((float)Math.cos(arg0.z)));}
public static Vec4f cos(Vec4f arg0) {return Vec4f.v4(((float)Math.cos(arg0.x)), ((float)Math.cos(arg0.y)), ((float)Math.cos(arg0.z)), ((float)Math.cos(arg0.w)));}
public static float tan(float arg0) {return ((float)Math.tan(arg0));}
public static Vec2f tan(Vec2f arg0) {return Vec2f.v2(((float)Math.tan(arg0.x)), ((float)Math.tan(arg0.y)));}
public static Vec3f tan(Vec3f arg0) {return Vec3f.v3(((float)Math.tan(arg0.x)), ((float)Math.tan(arg0.y)), ((float)Math.tan(arg0.z)));}
public static Vec4f tan(Vec4f arg0) {return Vec4f.v4(((float)Math.tan(arg0.x)), ((float)Math.tan(arg0.y)), ((float)Math.tan(arg0.z)), ((float)Math.tan(arg0.w)));}
public static float asin(float arg0) {return ((float)Math.asin(arg0));}
public static Vec2f asin(Vec2f arg0) {return Vec2f.v2(((float)Math.asin(arg0.x)), ((float)Math.asin(arg0.y)));}
public static Vec3f asin(Vec3f arg0) {return Vec3f.v3(((float)Math.asin(arg0.x)), ((float)Math.asin(arg0.y)), ((float)Math.asin(arg0.z)));}
public static Vec4f asin(Vec4f arg0) {return Vec4f.v4(((float)Math.asin(arg0.x)), ((float)Math.asin(arg0.y)), ((float)Math.asin(arg0.z)), ((float)Math.asin(arg0.w)));}
public static float acos(float arg0) {return ((float)Math.acos(arg0));}
public static Vec2f acos(Vec2f arg0) {return Vec2f.v2(((float)Math.acos(arg0.x)), ((float)Math.acos(arg0.y)));}
public static Vec3f acos(Vec3f arg0) {return Vec3f.v3(((float)Math.acos(arg0.x)), ((float)Math.acos(arg0.y)), ((float)Math.acos(arg0.z)));}
public static Vec4f acos(Vec4f arg0) {return Vec4f.v4(((float)Math.acos(arg0.x)), ((float)Math.acos(arg0.y)), ((float)Math.acos(arg0.z)), ((float)Math.acos(arg0.w)));}
public static float atan(float arg0) {return ((float)Math.atan(arg0));}
public static Vec2f atan(Vec2f arg0) {return Vec2f.v2(((float)Math.atan(arg0.x)), ((float)Math.atan(arg0.y)));}
public static Vec3f atan(Vec3f arg0) {return Vec3f.v3(((float)Math.atan(arg0.x)), ((float)Math.atan(arg0.y)), ((float)Math.atan(arg0.z)));}
public static Vec4f atan(Vec4f arg0) {return Vec4f.v4(((float)Math.atan(arg0.x)), ((float)Math.atan(arg0.y)), ((float)Math.atan(arg0.z)), ((float)Math.atan(arg0.w)));}
public static float atan(float arg0, float arg1) {return (float)Math.atan2(arg0, arg1);}
public static Vec2f atan(Vec2f arg0, Vec2f arg1) {return Vec2f.v2((float)Math.atan2(arg0.x, arg1.x), (float)Math.atan2(arg0.y, arg1.y));}
public static Vec3f atan(Vec3f arg0, Vec3f arg1) {return Vec3f.v3((float)Math.atan2(arg0.x, arg1.x), (float)Math.atan2(arg0.y, arg1.y), (float)Math.atan2(arg0.z, arg1.z));}
public static Vec4f atan(Vec4f arg0, Vec4f arg1) {return Vec4f.v4((float)Math.atan2(arg0.x, arg1.x), (float)Math.atan2(arg0.y, arg1.y), (float)Math.atan2(arg0.z, arg1.z), (float)Math.atan2(arg0.w, arg1.w));}
public static float pow(float arg0, float arg1) {return (float)Math.pow(arg0, arg1);}
public static Vec2f pow(Vec2f arg0, Vec2f arg1) {return Vec2f.v2((float)Math.pow(arg0.x, arg1.x), (float)Math.pow(arg0.y, arg1.y));}
public static Vec3f pow(Vec3f arg0, Vec3f arg1) {return Vec3f.v3((float)Math.pow(arg0.x, arg1.x), (float)Math.pow(arg0.y, arg1.y), (float)Math.pow(arg0.z, arg1.z));}
public static Vec4f pow(Vec4f arg0, Vec4f arg1) {return Vec4f.v4((float)Math.pow(arg0.x, arg1.x), (float)Math.pow(arg0.y, arg1.y), (float)Math.pow(arg0.z, arg1.z), (float)Math.pow(arg0.w, arg1.w));}
public static float exp(float arg0) {return ((float)Math.exp(arg0));}
public static Vec2f exp(Vec2f arg0) {return Vec2f.v2(((float)Math.exp(arg0.x)), ((float)Math.exp(arg0.y)));}
public static Vec3f exp(Vec3f arg0) {return Vec3f.v3(((float)Math.exp(arg0.x)), ((float)Math.exp(arg0.y)), ((float)Math.exp(arg0.z)));}
public static Vec4f exp(Vec4f arg0) {return Vec4f.v4(((float)Math.exp(arg0.x)), ((float)Math.exp(arg0.y)), ((float)Math.exp(arg0.z)), ((float)Math.exp(arg0.w)));}
public static float log(float arg0) {return ((float)Math.log(arg0));}
public static Vec2f log(Vec2f arg0) {return Vec2f.v2(((float)Math.log(arg0.x)), ((float)Math.log(arg0.y)));}
public static Vec3f log(Vec3f arg0) {return Vec3f.v3(((float)Math.log(arg0.x)), ((float)Math.log(arg0.y)), ((float)Math.log(arg0.z)));}
public static Vec4f log(Vec4f arg0) {return Vec4f.v4(((float)Math.log(arg0.x)), ((float)Math.log(arg0.y)), ((float)Math.log(arg0.z)), ((float)Math.log(arg0.w)));}
public static float sqrt(float arg0) {return ((float)Math.sqrt(arg0));}
public static Vec2f sqrt(Vec2f arg0) {return Vec2f.v2(((float)Math.sqrt(arg0.x)), ((float)Math.sqrt(arg0.y)));}
public static Vec3f sqrt(Vec3f arg0) {return Vec3f.v3(((float)Math.sqrt(arg0.x)), ((float)Math.sqrt(arg0.y)), ((float)Math.sqrt(arg0.z)));}
public static Vec4f sqrt(Vec4f arg0) {return Vec4f.v4(((float)Math.sqrt(arg0.x)), ((float)Math.sqrt(arg0.y)), ((float)Math.sqrt(arg0.z)), ((float)Math.sqrt(arg0.w)));}
public static float abs(float arg0) {return ((float)Math.abs(arg0));}
public static Vec2f abs(Vec2f arg0) {return Vec2f.v2(((float)Math.abs(arg0.x)), ((float)Math.abs(arg0.y)));}
public static Vec3f abs(Vec3f arg0) {return Vec3f.v3(((float)Math.abs(arg0.x)), ((float)Math.abs(arg0.y)), ((float)Math.abs(arg0.z)));}
public static Vec4f abs(Vec4f arg0) {return Vec4f.v4(((float)Math.abs(arg0.x)), ((float)Math.abs(arg0.y)), ((float)Math.abs(arg0.z)), ((float)Math.abs(arg0.w)));}
public static float sign(float arg0) {return ((float)Math.signum(arg0));}
public static Vec2f sign(Vec2f arg0) {return Vec2f.v2(((float)Math.signum(arg0.x)), ((float)Math.signum(arg0.y)));}
public static Vec3f sign(Vec3f arg0) {return Vec3f.v3(((float)Math.signum(arg0.x)), ((float)Math.signum(arg0.y)), ((float)Math.signum(arg0.z)));}
public static Vec4f sign(Vec4f arg0) {return Vec4f.v4(((float)Math.signum(arg0.x)), ((float)Math.signum(arg0.y)), ((float)Math.signum(arg0.z)), ((float)Math.signum(arg0.w)));}
public static float floor(float arg0) {return ((float)Math.floor(arg0));}
public static Vec2f floor(Vec2f arg0) {return Vec2f.v2(((float)Math.floor(arg0.x)), ((float)Math.floor(arg0.y)));}
public static Vec3f floor(Vec3f arg0) {return Vec3f.v3(((float)Math.floor(arg0.x)), ((float)Math.floor(arg0.y)), ((float)Math.floor(arg0.z)));}
public static Vec4f floor(Vec4f arg0) {return Vec4f.v4(((float)Math.floor(arg0.x)), ((float)Math.floor(arg0.y)), ((float)Math.floor(arg0.z)), ((float)Math.floor(arg0.w)));}
public static float ceil(float arg0) {return ((float)Math.ceil(arg0));}
public static Vec2f ceil(Vec2f arg0) {return Vec2f.v2(((float)Math.ceil(arg0.x)), ((float)Math.ceil(arg0.y)));}
public static Vec3f ceil(Vec3f arg0) {return Vec3f.v3(((float)Math.ceil(arg0.x)), ((float)Math.ceil(arg0.y)), ((float)Math.ceil(arg0.z)));}
public static Vec4f ceil(Vec4f arg0) {return Vec4f.v4(((float)Math.ceil(arg0.x)), ((float)Math.ceil(arg0.y)), ((float)Math.ceil(arg0.z)), ((float)Math.ceil(arg0.w)));}
public static float fract(float arg0) {return arg0 - floor(arg0);}
public static Vec2f fract(Vec2f arg0) {return Vec2f.v2(arg0.x - floor(arg0.x), arg0.y - floor(arg0.y));}
public static Vec3f fract(Vec3f arg0) {return Vec3f.v3(arg0.x - floor(arg0.x), arg0.y - floor(arg0.y), arg0.z - floor(arg0.z));}
public static Vec4f fract(Vec4f arg0) {return Vec4f.v4(arg0.x - floor(arg0.x), arg0.y - floor(arg0.y), arg0.z - floor(arg0.z), arg0.w - floor(arg0.w));}
public static float mod(float arg0, float arg1) {return (float)(arg0-arg1*Math.floor(arg0/arg1));}
public static Vec2f mod(Vec2f arg0, Vec2f arg1) {return Vec2f.v2((float)(arg0.x-arg1.x*Math.floor(arg0.x/arg1.x)), (float)(arg0.y-arg1.y*Math.floor(arg0.y/arg1.y)));}
public static Vec3f mod(Vec3f arg0, Vec3f arg1) {return Vec3f.v3((float)(arg0.x-arg1.x*Math.floor(arg0.x/arg1.x)), (float)(arg0.y-arg1.y*Math.floor(arg0.y/arg1.y)), (float)(arg0.z-arg1.z*Math.floor(arg0.z/arg1.z)));}
public static Vec4f mod(Vec4f arg0, Vec4f arg1) {return Vec4f.v4((float)(arg0.x-arg1.x*Math.floor(arg0.x/arg1.x)), (float)(arg0.y-arg1.y*Math.floor(arg0.y/arg1.y)), (float)(arg0.z-arg1.z*Math.floor(arg0.z/arg1.z)), (float)(arg0.w-arg1.w*Math.floor(arg0.w/arg1.w)));}
public static float min(float arg0, float arg1) {return (float)Math.min(arg0, arg1);}
public static Vec2f min(Vec2f arg0, Vec2f arg1) {return Vec2f.v2((float)Math.min(arg0.x, arg1.x), (float)Math.min(arg0.y, arg1.y));}
public static Vec3f min(Vec3f arg0, Vec3f arg1) {return Vec3f.v3((float)Math.min(arg0.x, arg1.x), (float)Math.min(arg0.y, arg1.y), (float)Math.min(arg0.z, arg1.z));}
public static Vec4f min(Vec4f arg0, Vec4f arg1) {return Vec4f.v4((float)Math.min(arg0.x, arg1.x), (float)Math.min(arg0.y, arg1.y), (float)Math.min(arg0.z, arg1.z), (float)Math.min(arg0.w, arg1.w));}
public static Vec2f min(Vec2f arg0, float arg1) {return Vec2f.v2((float)Math.min(arg0.x, arg1), (float)Math.min(arg0.y, arg1));}
public static Vec3f min(Vec3f arg0, float arg1) {return Vec3f.v3((float)Math.min(arg0.x, arg1), (float)Math.min(arg0.y, arg1), (float)Math.min(arg0.z, arg1));}
public static Vec4f min(Vec4f arg0, float arg1) {return Vec4f.v4((float)Math.min(arg0.x, arg1), (float)Math.min(arg0.y, arg1), (float)Math.min(arg0.z, arg1), (float)Math.min(arg0.w, arg1));}
public static float max(float arg0, float arg1) {return (float)Math.max(arg0, arg1);}
public static Vec2f max(Vec2f arg0, Vec2f arg1) {return Vec2f.v2((float)Math.max(arg0.x, arg1.x), (float)Math.max(arg0.y, arg1.y));}
public static Vec3f max(Vec3f arg0, Vec3f arg1) {return Vec3f.v3((float)Math.max(arg0.x, arg1.x), (float)Math.max(arg0.y, arg1.y), (float)Math.max(arg0.z, arg1.z));}
public static Vec4f max(Vec4f arg0, Vec4f arg1) {return Vec4f.v4((float)Math.max(arg0.x, arg1.x), (float)Math.max(arg0.y, arg1.y), (float)Math.max(arg0.z, arg1.z), (float)Math.max(arg0.w, arg1.w));}
public static Vec2f max(Vec2f arg0, float arg1) {return Vec2f.v2((float)Math.max(arg0.x, arg1), (float)Math.max(arg0.y, arg1));}
public static Vec3f max(Vec3f arg0, float arg1) {return Vec3f.v3((float)Math.max(arg0.x, arg1), (float)Math.max(arg0.y, arg1), (float)Math.max(arg0.z, arg1));}
public static Vec4f max(Vec4f arg0, float arg1) {return Vec4f.v4((float)Math.max(arg0.x, arg1), (float)Math.max(arg0.y, arg1), (float)Math.max(arg0.z, arg1), (float)Math.max(arg0.w, arg1));}
public static float clamp(float arg0, float arg1, float arg2) {return Math.max(arg1, Math.min(arg2, arg0));}
public static Vec2f clamp(Vec2f arg0, Vec2f arg1, Vec2f arg2) {return Vec2f.v2(Math.max(arg1.x, Math.min(arg2.x, arg0.x)), Math.max(arg1.y, Math.min(arg2.y, arg0.y)));}
public static Vec3f clamp(Vec3f arg0, Vec3f arg1, Vec3f arg2) {return Vec3f.v3(Math.max(arg1.x, Math.min(arg2.x, arg0.x)), Math.max(arg1.y, Math.min(arg2.y, arg0.y)), Math.max(arg1.z, Math.min(arg2.z, arg0.z)));}
public static Vec4f clamp(Vec4f arg0, Vec4f arg1, Vec4f arg2) {return Vec4f.v4(Math.max(arg1.x, Math.min(arg2.x, arg0.x)), Math.max(arg1.y, Math.min(arg2.y, arg0.y)), Math.max(arg1.z, Math.min(arg2.z, arg0.z)), Math.max(arg1.w, Math.min(arg2.w, arg0.w)));}
public static Vec2f clamp(Vec2f arg0, float arg1, float arg2) {return Vec2f.v2(Math.max(arg1, Math.min(arg2, arg0.x)), Math.max(arg1, Math.min(arg2, arg0.y)));}
public static Vec3f clamp(Vec3f arg0, float arg1, float arg2) {return Vec3f.v3(Math.max(arg1, Math.min(arg2, arg0.x)), Math.max(arg1, Math.min(arg2, arg0.y)), Math.max(arg1, Math.min(arg2, arg0.z)));}
public static Vec4f clamp(Vec4f arg0, float arg1, float arg2) {return Vec4f.v4(Math.max(arg1, Math.min(arg2, arg0.x)), Math.max(arg1, Math.min(arg2, arg0.y)), Math.max(arg1, Math.min(arg2, arg0.z)), Math.max(arg1, Math.min(arg2, arg0.w)));}
public static float mix(float arg0, float arg1, float arg2) {return arg0 * (1 - arg2) + arg1 * arg2;}
public static Vec2f mix(Vec2f arg0, Vec2f arg1, Vec2f arg2) {return Vec2f.v2(arg0.x * (1 - arg2.x) + arg1.x * arg2.x, arg0.y * (1 - arg2.y) + arg1.y * arg2.y);}
public static Vec3f mix(Vec3f arg0, Vec3f arg1, Vec3f arg2) {return Vec3f.v3(arg0.x * (1 - arg2.x) + arg1.x * arg2.x, arg0.y * (1 - arg2.y) + arg1.y * arg2.y, arg0.z * (1 - arg2.z) + arg1.z * arg2.z);}
public static Vec4f mix(Vec4f arg0, Vec4f arg1, Vec4f arg2) {return Vec4f.v4(arg0.x * (1 - arg2.x) + arg1.x * arg2.x, arg0.y * (1 - arg2.y) + arg1.y * arg2.y, arg0.z * (1 - arg2.z) + arg1.z * arg2.z, arg0.w * (1 - arg2.w) + arg1.w * arg2.w);}
public static Vec2f mix(Vec2f arg0, Vec2f arg1, float arg2) {return Vec2f.v2(((float)arg0.x * (1 - arg2) + arg1.x * arg2), ((float)arg0.y * (1 - arg2) + arg1.y * arg2));}
public static Vec3f mix(Vec3f arg0, Vec3f arg1, float arg2) {return Vec3f.v3(((float)arg0.x * (1 - arg2) + arg1.x * arg2), ((float)arg0.y * (1 - arg2) + arg1.y * arg2), ((float)arg0.z * (1 - arg2) + arg1.z * arg2));}
public static Vec4f mix(Vec4f arg0, Vec4f arg1, float arg2) {return Vec4f.v4(((float)arg0.x * (1 - arg2) + arg1.x * arg2), ((float)arg0.y * (1 - arg2) + arg1.y * arg2), ((float)arg0.z * (1 - arg2) + arg1.z * arg2), ((float)arg0.w * (1 - arg2) + arg1.w * arg2));}
public static float step(float arg0, float arg1) {return arg1 < arg0 ? 0 : 1;}
public static Vec2f step(Vec2f arg0, Vec2f arg1) {return Vec2f.v2(arg1.x < arg0.x ? 0 : 1, arg1.y < arg0.y ? 0 : 1);}
public static Vec3f step(Vec3f arg0, Vec3f arg1) {return Vec3f.v3(arg1.x < arg0.x ? 0 : 1, arg1.y < arg0.y ? 0 : 1, arg1.z < arg0.z ? 0 : 1);}
public static Vec4f step(Vec4f arg0, Vec4f arg1) {return Vec4f.v4(arg1.x < arg0.x ? 0 : 1, arg1.y < arg0.y ? 0 : 1, arg1.z < arg0.z ? 0 : 1, arg1.w < arg0.w ? 0 : 1);}
public static Vec2f step(float arg0, Vec2f arg1) {return Vec2f.v2(arg1.x < arg0 ? 0 : 1, arg1.y < arg0 ? 0 : 1);}
public static Vec3f step(float arg0, Vec3f arg1) {return Vec3f.v3(arg1.x < arg0 ? 0 : 1, arg1.y < arg0 ? 0 : 1, arg1.z < arg0 ? 0 : 1);}
public static Vec4f step(float arg0, Vec4f arg1) {return Vec4f.v4(arg1.x < arg0 ? 0 : 1, arg1.y < arg0 ? 0 : 1, arg1.z < arg0 ? 0 : 1, arg1.w < arg0 ? 0 : 1);}
public static float smoothstep(float arg0, float arg1, float arg2) {return arg2 < arg0 ? 0 : arg2 > arg1 ? 1 : arg2*arg2*(3 - 2*arg2);}
public static Vec2f smoothstep(Vec2f arg0, Vec2f arg1, Vec2f arg2) {return Vec2f.v2(arg2.x < arg0.x ? 0 : arg2.x > arg1.x ? 1 : arg2.x*arg2.x*(3 - 2*arg2.x), arg2.y < arg0.y ? 0 : arg2.y > arg1.y ? 1 : arg2.y*arg2.y*(3 - 2*arg2.y));}
public static Vec3f smoothstep(Vec3f arg0, Vec3f arg1, Vec3f arg2) {return Vec3f.v3(arg2.x < arg0.x ? 0 : arg2.x > arg1.x ? 1 : arg2.x*arg2.x*(3 - 2*arg2.x), arg2.y < arg0.y ? 0 : arg2.y > arg1.y ? 1 : arg2.y*arg2.y*(3 - 2*arg2.y), arg2.z < arg0.z ? 0 : arg2.z > arg1.z ? 1 : arg2.z*arg2.z*(3 - 2*arg2.z));}
public static Vec4f smoothstep(Vec4f arg0, Vec4f arg1, Vec4f arg2) {return Vec4f.v4(arg2.x < arg0.x ? 0 : arg2.x > arg1.x ? 1 : arg2.x*arg2.x*(3 - 2*arg2.x), arg2.y < arg0.y ? 0 : arg2.y > arg1.y ? 1 : arg2.y*arg2.y*(3 - 2*arg2.y), arg2.z < arg0.z ? 0 : arg2.z > arg1.z ? 1 : arg2.z*arg2.z*(3 - 2*arg2.z), arg2.w < arg0.w ? 0 : arg2.w > arg1.w ? 1 : arg2.w*arg2.w*(3 - 2*arg2.w));}
public static Vec2f smoothstep(float arg0, float arg1, Vec2f arg2) {return Vec2f.v2(arg2.x < arg0 ? 0 : arg2.x > arg1 ? 1 : arg2.x*arg2.x*(3 - 2*arg2.x), arg2.y < arg0 ? 0 : arg2.y > arg1 ? 1 : arg2.y*arg2.y*(3 - 2*arg2.y));}
public static Vec3f smoothstep(float arg0, float arg1, Vec3f arg2) {return Vec3f.v3(arg2.x < arg0 ? 0 : arg2.x > arg1 ? 1 : arg2.x*arg2.x*(3 - 2*arg2.x), arg2.y < arg0 ? 0 : arg2.y > arg1 ? 1 : arg2.y*arg2.y*(3 - 2*arg2.y), arg2.z < arg0 ? 0 : arg2.z > arg1 ? 1 : arg2.z*arg2.z*(3 - 2*arg2.z));}
public static Vec4f smoothstep(float arg0, float arg1, Vec4f arg2) {return Vec4f.v4(arg2.x < arg0 ? 0 : arg2.x > arg1 ? 1 : arg2.x*arg2.x*(3 - 2*arg2.x), arg2.y < arg0 ? 0 : arg2.y > arg1 ? 1 : arg2.y*arg2.y*(3 - 2*arg2.y), arg2.z < arg0 ? 0 : arg2.z > arg1 ? 1 : arg2.z*arg2.z*(3 - 2*arg2.z), arg2.w < arg0 ? 0 : arg2.w > arg1 ? 1 : arg2.w*arg2.w*(3 - 2*arg2.w));}

//gglsl auto generated text


    public static float length(float v) {return Math.abs(v);}
    public static float length(Vec2f v) {return v.length();}
    public static float length(Vec3f v) {return v.length();}
    public static float length(Vec4f v) {return v.length();}

    public static float distance(float v1, float v2) {return Math.abs(v1-v2);}
    public static float distance(Vec2f v1, Vec2f v2) {return v1.dist(v2);}
    public static float distance(Vec3f v1, Vec3f v2) {return v1.dist(v2);}
    public static float distance(Vec4f v1, Vec4f v2) {return v1.dist(v2);}

    public static float dot(float v1, float v2) {return v1*v2;}
    public static float dot(Vec2f v1, Vec2f v2) {return v1.mulScalar(v2);}
    public static float dot(Vec3f v1, Vec3f v2) {return v1.scalarProduct(v2);}
    public static float dot(Vec4f v1, Vec4f v2) {return v1.dot(v2);}

    public static Vec3f cross(Vec3f v1, Vec3f v2) {return v1.crossProduct(v2);}

    public static float normalize(float v1) {return 1;}
    public static Vec2f normalize(Vec2f v1) {return v1.normalized();}
    public static Vec3f normalize(Vec3f v1) {return v1.normalized();}
    public static Vec4f normalize(Vec4f v1) {return v1.normalized();}

    public static float faceforward(float n, float i, float ng) {return i * ng < 0 ? n : -n;}
    public static Vec2f faceforward(Vec2f n, Vec2f i, Vec2f ng) {return i.dot(ng) < 0 ? n : n.negative();}
    public static Vec3f faceforward(Vec3f n, Vec3f i, Vec3f ng) {return i.dot(ng) < 0 ? n : n.negative();}
    public static Vec4f faceforward(Vec4f n, Vec4f i, Vec4f ng) {return i.dot(ng) < 0 ? n : n.negative();}

    public static float reflect(float i, float n) {return i - n * n * i * 2;}
    public static Vec2f reflect(Vec2f i, Vec2f n) {return i.sub(n.mul(n.dot(i) * 2));}
    public static Vec3f reflect(Vec3f i, Vec3f n) {return i.sub(n.mul(n.dot(i) * 2));}
    public static Vec4f reflect(Vec4f i, Vec4f n) {return i.sub(n.mul(n.dot(i) * 2));}

//k = 1.0 - eta * eta * (1.0 - dot(N, I) * dot(N, I));
//    if (k < 0.0)
//    R = genType(0.0);       // or genDType(0.0)
//    else
//    R = eta * I - (eta * dot(N, I) + sqrt(k)) * N;


    public static float refract(float I, float N, float eta) {
        float k = 1f - eta * eta * (1f - dot(N, I) * dot(N, I));
        if (k < 0f)
            return 0;
        else
            return I * eta - (N * I * eta + (float)Math.sqrt(k)) * N;
    }

    public static Vec2f refract(Vec2f I, Vec2f N, float eta) {
        float k = 1f - eta * eta * (1f - dot(N, I) * dot(N, I));
        if (k < 0f)
            return new Vec2f(0, 0);
        else
            return I.mul(eta).sub(N.mul(dot(N, I) * eta + (float)Math.sqrt(k)));
    }

    public static Vec3f refract(Vec3f I, Vec3f N, float eta) {
        float k = 1f - eta * eta * (1f - dot(N, I) * dot(N, I));
        if (k < 0.0)
            return new Vec3f(0, 0, 0);
        else
            return I.mul(eta).sub(N.mul(dot(N, I) * eta + (float)Math.sqrt(k)));
    }

    public static Vec4f refract(Vec4f I, Vec4f N, float eta) {
        float k = 1f - eta * eta * (1f - dot(N, I) * dot(N, I));
        if (k < 0.0)
            return new Vec4f(0, 0, 0, 0);
        else
            return I.mul(eta).sub(N.mul(dot(N, I) * eta + (float)Math.sqrt(k)));
    }

//matrix functions




}
