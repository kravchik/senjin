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
public static float sin(float arg0) {return (float)Math.sin(arg0);}
public static Vec2f sin(Vec2f arg0) {return Vec2f.v2((float)Math.sin(arg0.x), (float)Math.sin(arg0.y));}
public static Vec3f sin(Vec3f arg0) {return Vec3f.v3((float)Math.sin(arg0.x), (float)Math.sin(arg0.y), (float)Math.sin(arg0.z));}
public static Vec4f sin(Vec4f arg0) {return Vec4f.v4((float)Math.sin(arg0.x), (float)Math.sin(arg0.y), (float)Math.sin(arg0.z), (float)Math.sin(arg0.w));}
public static float cos(float arg0) {return (float)Math.cos(arg0);}
public static Vec2f cos(Vec2f arg0) {return Vec2f.v2((float)Math.cos(arg0.x), (float)Math.cos(arg0.y));}
public static Vec3f cos(Vec3f arg0) {return Vec3f.v3((float)Math.cos(arg0.x), (float)Math.cos(arg0.y), (float)Math.cos(arg0.z));}
public static Vec4f cos(Vec4f arg0) {return Vec4f.v4((float)Math.cos(arg0.x), (float)Math.cos(arg0.y), (float)Math.cos(arg0.z), (float)Math.cos(arg0.w));}
public static float tan(float arg0) {return (float)Math.tan(arg0);}
public static Vec2f tan(Vec2f arg0) {return Vec2f.v2((float)Math.tan(arg0.x), (float)Math.tan(arg0.y));}
public static Vec3f tan(Vec3f arg0) {return Vec3f.v3((float)Math.tan(arg0.x), (float)Math.tan(arg0.y), (float)Math.tan(arg0.z));}
public static Vec4f tan(Vec4f arg0) {return Vec4f.v4((float)Math.tan(arg0.x), (float)Math.tan(arg0.y), (float)Math.tan(arg0.z), (float)Math.tan(arg0.w));}
public static float asin(float arg0) {return (float)Math.asin(arg0);}
public static Vec2f asin(Vec2f arg0) {return Vec2f.v2((float)Math.asin(arg0.x), (float)Math.asin(arg0.y));}
public static Vec3f asin(Vec3f arg0) {return Vec3f.v3((float)Math.asin(arg0.x), (float)Math.asin(arg0.y), (float)Math.asin(arg0.z));}
public static Vec4f asin(Vec4f arg0) {return Vec4f.v4((float)Math.asin(arg0.x), (float)Math.asin(arg0.y), (float)Math.asin(arg0.z), (float)Math.asin(arg0.w));}
public static float acos(float arg0) {return (float)Math.acos(arg0);}
public static Vec2f acos(Vec2f arg0) {return Vec2f.v2((float)Math.acos(arg0.x), (float)Math.acos(arg0.y));}
public static Vec3f acos(Vec3f arg0) {return Vec3f.v3((float)Math.acos(arg0.x), (float)Math.acos(arg0.y), (float)Math.acos(arg0.z));}
public static Vec4f acos(Vec4f arg0) {return Vec4f.v4((float)Math.acos(arg0.x), (float)Math.acos(arg0.y), (float)Math.acos(arg0.z), (float)Math.acos(arg0.w));}
public static float atan(float arg0) {return (float)Math.atan(arg0);}
public static Vec2f atan(Vec2f arg0) {return Vec2f.v2((float)Math.atan(arg0.x), (float)Math.atan(arg0.y));}
public static Vec3f atan(Vec3f arg0) {return Vec3f.v3((float)Math.atan(arg0.x), (float)Math.atan(arg0.y), (float)Math.atan(arg0.z));}
public static Vec4f atan(Vec4f arg0) {return Vec4f.v4((float)Math.atan(arg0.x), (float)Math.atan(arg0.y), (float)Math.atan(arg0.z), (float)Math.atan(arg0.w));}
public static float atan(float y, float x) {return (float)Math.atan2(y, x);}
public static Vec2f atan(Vec2f y, Vec2f x) {return Vec2f.v2((float)Math.atan2(y.x, x.x), (float)Math.atan2(y.y, x.y));}
public static Vec3f atan(Vec3f y, Vec3f x) {return Vec3f.v3((float)Math.atan2(y.x, x.x), (float)Math.atan2(y.y, x.y), (float)Math.atan2(y.z, x.z));}
public static Vec4f atan(Vec4f y, Vec4f x) {return Vec4f.v4((float)Math.atan2(y.x, x.x), (float)Math.atan2(y.y, x.y), (float)Math.atan2(y.z, x.z), (float)Math.atan2(y.w, x.w));}
public static float pow(float value, float power) {return (float)Math.pow(value, power);}
public static Vec2f pow(Vec2f value, Vec2f power) {return Vec2f.v2((float)Math.pow(value.x, power.x), (float)Math.pow(value.y, power.y));}
public static Vec3f pow(Vec3f value, Vec3f power) {return Vec3f.v3((float)Math.pow(value.x, power.x), (float)Math.pow(value.y, power.y), (float)Math.pow(value.z, power.z));}
public static Vec4f pow(Vec4f value, Vec4f power) {return Vec4f.v4((float)Math.pow(value.x, power.x), (float)Math.pow(value.y, power.y), (float)Math.pow(value.z, power.z), (float)Math.pow(value.w, power.w));}
public static float exp(float arg0) {return (float)Math.exp(arg0);}
public static Vec2f exp(Vec2f arg0) {return Vec2f.v2((float)Math.exp(arg0.x), (float)Math.exp(arg0.y));}
public static Vec3f exp(Vec3f arg0) {return Vec3f.v3((float)Math.exp(arg0.x), (float)Math.exp(arg0.y), (float)Math.exp(arg0.z));}
public static Vec4f exp(Vec4f arg0) {return Vec4f.v4((float)Math.exp(arg0.x), (float)Math.exp(arg0.y), (float)Math.exp(arg0.z), (float)Math.exp(arg0.w));}
public static float log(float arg0) {return (float)Math.log(arg0);}
public static Vec2f log(Vec2f arg0) {return Vec2f.v2((float)Math.log(arg0.x), (float)Math.log(arg0.y));}
public static Vec3f log(Vec3f arg0) {return Vec3f.v3((float)Math.log(arg0.x), (float)Math.log(arg0.y), (float)Math.log(arg0.z));}
public static Vec4f log(Vec4f arg0) {return Vec4f.v4((float)Math.log(arg0.x), (float)Math.log(arg0.y), (float)Math.log(arg0.z), (float)Math.log(arg0.w));}
public static float sqrt(float arg0) {return (float)Math.sqrt(arg0);}
public static Vec2f sqrt(Vec2f arg0) {return Vec2f.v2((float)Math.sqrt(arg0.x), (float)Math.sqrt(arg0.y));}
public static Vec3f sqrt(Vec3f arg0) {return Vec3f.v3((float)Math.sqrt(arg0.x), (float)Math.sqrt(arg0.y), (float)Math.sqrt(arg0.z));}
public static Vec4f sqrt(Vec4f arg0) {return Vec4f.v4((float)Math.sqrt(arg0.x), (float)Math.sqrt(arg0.y), (float)Math.sqrt(arg0.z), (float)Math.sqrt(arg0.w));}
public static float abs(float arg0) {return Math.abs(arg0);}
public static Vec2f abs(Vec2f arg0) {return Vec2f.v2(Math.abs(arg0.x), Math.abs(arg0.y));}
public static Vec3f abs(Vec3f arg0) {return Vec3f.v3(Math.abs(arg0.x), Math.abs(arg0.y), Math.abs(arg0.z));}
public static Vec4f abs(Vec4f arg0) {return Vec4f.v4(Math.abs(arg0.x), Math.abs(arg0.y), Math.abs(arg0.z), Math.abs(arg0.w));}
public static float sign(float arg0) {return Math.signum(arg0);}
public static Vec2f sign(Vec2f arg0) {return Vec2f.v2(Math.signum(arg0.x), Math.signum(arg0.y));}
public static Vec3f sign(Vec3f arg0) {return Vec3f.v3(Math.signum(arg0.x), Math.signum(arg0.y), Math.signum(arg0.z));}
public static Vec4f sign(Vec4f arg0) {return Vec4f.v4(Math.signum(arg0.x), Math.signum(arg0.y), Math.signum(arg0.z), Math.signum(arg0.w));}
public static float floor(float arg0) {return (float)Math.floor(arg0);}
public static Vec2f floor(Vec2f arg0) {return Vec2f.v2((float)Math.floor(arg0.x), (float)Math.floor(arg0.y));}
public static Vec3f floor(Vec3f arg0) {return Vec3f.v3((float)Math.floor(arg0.x), (float)Math.floor(arg0.y), (float)Math.floor(arg0.z));}
public static Vec4f floor(Vec4f arg0) {return Vec4f.v4((float)Math.floor(arg0.x), (float)Math.floor(arg0.y), (float)Math.floor(arg0.z), (float)Math.floor(arg0.w));}
public static float ceil(float arg0) {return (float)Math.ceil(arg0);}
public static Vec2f ceil(Vec2f arg0) {return Vec2f.v2((float)Math.ceil(arg0.x), (float)Math.ceil(arg0.y));}
public static Vec3f ceil(Vec3f arg0) {return Vec3f.v3((float)Math.ceil(arg0.x), (float)Math.ceil(arg0.y), (float)Math.ceil(arg0.z));}
public static Vec4f ceil(Vec4f arg0) {return Vec4f.v4((float)Math.ceil(arg0.x), (float)Math.ceil(arg0.y), (float)Math.ceil(arg0.z), (float)Math.ceil(arg0.w));}
public static float fract(float arg0) {return arg0 - (float)Math.floor(arg0);}
public static Vec2f fract(Vec2f arg0) {return Vec2f.v2(arg0.x - (float)Math.floor(arg0.x), arg0.y - (float)Math.floor(arg0.y));}
public static Vec3f fract(Vec3f arg0) {return Vec3f.v3(arg0.x - (float)Math.floor(arg0.x), arg0.y - (float)Math.floor(arg0.y), arg0.z - (float)Math.floor(arg0.z));}
public static Vec4f fract(Vec4f arg0) {return Vec4f.v4(arg0.x - (float)Math.floor(arg0.x), arg0.y - (float)Math.floor(arg0.y), arg0.z - (float)Math.floor(arg0.z), arg0.w - (float)Math.floor(arg0.w));}
public static float mod(float value, float by) {return (float)(value-by*Math.floor(value/by));}
public static Vec2f mod(Vec2f value, Vec2f by) {return Vec2f.v2((float)(value.x-by.x*Math.floor(value.x/by.x)), (float)(value.y-by.y*Math.floor(value.y/by.y)));}
public static Vec3f mod(Vec3f value, Vec3f by) {return Vec3f.v3((float)(value.x-by.x*Math.floor(value.x/by.x)), (float)(value.y-by.y*Math.floor(value.y/by.y)), (float)(value.z-by.z*Math.floor(value.z/by.z)));}
public static Vec4f mod(Vec4f value, Vec4f by) {return Vec4f.v4((float)(value.x-by.x*Math.floor(value.x/by.x)), (float)(value.y-by.y*Math.floor(value.y/by.y)), (float)(value.z-by.z*Math.floor(value.z/by.z)), (float)(value.w-by.w*Math.floor(value.w/by.w)));}
public static float min(float arg0, float arg1) {return Math.min(arg0, arg1);}
public static Vec2f min(Vec2f arg0, Vec2f arg1) {return Vec2f.v2(Math.min(arg0.x, arg1.x), Math.min(arg0.y, arg1.y));}
public static Vec3f min(Vec3f arg0, Vec3f arg1) {return Vec3f.v3(Math.min(arg0.x, arg1.x), Math.min(arg0.y, arg1.y), Math.min(arg0.z, arg1.z));}
public static Vec4f min(Vec4f arg0, Vec4f arg1) {return Vec4f.v4(Math.min(arg0.x, arg1.x), Math.min(arg0.y, arg1.y), Math.min(arg0.z, arg1.z), Math.min(arg0.w, arg1.w));}
public static Vec2f min(Vec2f arg0, float arg1) {return Vec2f.v2(Math.min(arg0.x, arg1), Math.min(arg0.y, arg1));}
public static Vec3f min(Vec3f arg0, float arg1) {return Vec3f.v3(Math.min(arg0.x, arg1), Math.min(arg0.y, arg1), Math.min(arg0.z, arg1));}
public static Vec4f min(Vec4f arg0, float arg1) {return Vec4f.v4(Math.min(arg0.x, arg1), Math.min(arg0.y, arg1), Math.min(arg0.z, arg1), Math.min(arg0.w, arg1));}
public static float max(float arg0, float arg1) {return Math.max(arg0, arg1);}
public static Vec2f max(Vec2f arg0, Vec2f arg1) {return Vec2f.v2(Math.max(arg0.x, arg1.x), Math.max(arg0.y, arg1.y));}
public static Vec3f max(Vec3f arg0, Vec3f arg1) {return Vec3f.v3(Math.max(arg0.x, arg1.x), Math.max(arg0.y, arg1.y), Math.max(arg0.z, arg1.z));}
public static Vec4f max(Vec4f arg0, Vec4f arg1) {return Vec4f.v4(Math.max(arg0.x, arg1.x), Math.max(arg0.y, arg1.y), Math.max(arg0.z, arg1.z), Math.max(arg0.w, arg1.w));}
public static Vec2f max(Vec2f arg0, float arg1) {return Vec2f.v2(Math.max(arg0.x, arg1), Math.max(arg0.y, arg1));}
public static Vec3f max(Vec3f arg0, float arg1) {return Vec3f.v3(Math.max(arg0.x, arg1), Math.max(arg0.y, arg1), Math.max(arg0.z, arg1));}
public static Vec4f max(Vec4f arg0, float arg1) {return Vec4f.v4(Math.max(arg0.x, arg1), Math.max(arg0.y, arg1), Math.max(arg0.z, arg1), Math.max(arg0.w, arg1));}
public static float clamp(float min, float max, float value) {return Math.max(max, Math.min(value, min));}
public static Vec2f clamp(Vec2f min, Vec2f max, Vec2f value) {return Vec2f.v2(Math.max(max.x, Math.min(value.x, min.x)), Math.max(max.y, Math.min(value.y, min.y)));}
public static Vec3f clamp(Vec3f min, Vec3f max, Vec3f value) {return Vec3f.v3(Math.max(max.x, Math.min(value.x, min.x)), Math.max(max.y, Math.min(value.y, min.y)), Math.max(max.z, Math.min(value.z, min.z)));}
public static Vec4f clamp(Vec4f min, Vec4f max, Vec4f value) {return Vec4f.v4(Math.max(max.x, Math.min(value.x, min.x)), Math.max(max.y, Math.min(value.y, min.y)), Math.max(max.z, Math.min(value.z, min.z)), Math.max(max.w, Math.min(value.w, min.w)));}
public static Vec2f clamp(Vec2f min, float max, float value) {return Vec2f.v2(Math.max(max, Math.min(value, min.x)), Math.max(max, Math.min(value, min.y)));}
public static Vec3f clamp(Vec3f min, float max, float value) {return Vec3f.v3(Math.max(max, Math.min(value, min.x)), Math.max(max, Math.min(value, min.y)), Math.max(max, Math.min(value, min.z)));}
public static Vec4f clamp(Vec4f min, float max, float value) {return Vec4f.v4(Math.max(max, Math.min(value, min.x)), Math.max(max, Math.min(value, min.y)), Math.max(max, Math.min(value, min.z)), Math.max(max, Math.min(value, min.w)));}
public static float mix(float from, float to, float progress) {return from * (1 - progress) + to * progress;}
public static Vec2f mix(Vec2f from, Vec2f to, Vec2f progress) {return Vec2f.v2(from.x * (1 - progress.x) + to.x * progress.x, from.y * (1 - progress.y) + to.y * progress.y);}
public static Vec3f mix(Vec3f from, Vec3f to, Vec3f progress) {return Vec3f.v3(from.x * (1 - progress.x) + to.x * progress.x, from.y * (1 - progress.y) + to.y * progress.y, from.z * (1 - progress.z) + to.z * progress.z);}
public static Vec4f mix(Vec4f from, Vec4f to, Vec4f progress) {return Vec4f.v4(from.x * (1 - progress.x) + to.x * progress.x, from.y * (1 - progress.y) + to.y * progress.y, from.z * (1 - progress.z) + to.z * progress.z, from.w * (1 - progress.w) + to.w * progress.w);}
public static Vec2f mix(Vec2f from, Vec2f to, float progress) {return Vec2f.v2(from.x * (1 - progress) + to.x * progress, from.y * (1 - progress) + to.y * progress);}
public static Vec3f mix(Vec3f from, Vec3f to, float progress) {return Vec3f.v3(from.x * (1 - progress) + to.x * progress, from.y * (1 - progress) + to.y * progress, from.z * (1 - progress) + to.z * progress);}
public static Vec4f mix(Vec4f from, Vec4f to, float progress) {return Vec4f.v4(from.x * (1 - progress) + to.x * progress, from.y * (1 - progress) + to.y * progress, from.z * (1 - progress) + to.z * progress, from.w * (1 - progress) + to.w * progress);}
public static float step(float edge, float value) {return value < edge ? 0 : 1;}
public static Vec2f step(Vec2f edge, Vec2f value) {return Vec2f.v2(value.x < edge.x ? 0 : 1, value.y < edge.y ? 0 : 1);}
public static Vec3f step(Vec3f edge, Vec3f value) {return Vec3f.v3(value.x < edge.x ? 0 : 1, value.y < edge.y ? 0 : 1, value.z < edge.z ? 0 : 1);}
public static Vec4f step(Vec4f edge, Vec4f value) {return Vec4f.v4(value.x < edge.x ? 0 : 1, value.y < edge.y ? 0 : 1, value.z < edge.z ? 0 : 1, value.w < edge.w ? 0 : 1);}
public static Vec2f step(float edge, Vec2f value) {return Vec2f.v2(value.x < edge ? 0 : 1, value.y < edge ? 0 : 1);}
public static Vec3f step(float edge, Vec3f value) {return Vec3f.v3(value.x < edge ? 0 : 1, value.y < edge ? 0 : 1, value.z < edge ? 0 : 1);}
public static Vec4f step(float edge, Vec4f value) {return Vec4f.v4(value.x < edge ? 0 : 1, value.y < edge ? 0 : 1, value.z < edge ? 0 : 1, value.w < edge ? 0 : 1);}
public static float smoothstep(float from, float to, float progress) {return progress < from ? 0 : progress > to ? 1 : progress*progress*(3 - 2*progress);}
public static Vec2f smoothstep(Vec2f from, Vec2f to, Vec2f progress) {return Vec2f.v2(progress.x < from.x ? 0 : progress.x > to.x ? 1 : progress.x*progress.x*(3 - 2*progress.x), progress.y < from.y ? 0 : progress.y > to.y ? 1 : progress.y*progress.y*(3 - 2*progress.y));}
public static Vec3f smoothstep(Vec3f from, Vec3f to, Vec3f progress) {return Vec3f.v3(progress.x < from.x ? 0 : progress.x > to.x ? 1 : progress.x*progress.x*(3 - 2*progress.x), progress.y < from.y ? 0 : progress.y > to.y ? 1 : progress.y*progress.y*(3 - 2*progress.y), progress.z < from.z ? 0 : progress.z > to.z ? 1 : progress.z*progress.z*(3 - 2*progress.z));}
public static Vec4f smoothstep(Vec4f from, Vec4f to, Vec4f progress) {return Vec4f.v4(progress.x < from.x ? 0 : progress.x > to.x ? 1 : progress.x*progress.x*(3 - 2*progress.x), progress.y < from.y ? 0 : progress.y > to.y ? 1 : progress.y*progress.y*(3 - 2*progress.y), progress.z < from.z ? 0 : progress.z > to.z ? 1 : progress.z*progress.z*(3 - 2*progress.z), progress.w < from.w ? 0 : progress.w > to.w ? 1 : progress.w*progress.w*(3 - 2*progress.w));}
public static Vec2f smoothstep(float from, float to, Vec2f progress) {return Vec2f.v2(progress.x < from ? 0 : progress.x > to ? 1 : progress.x*progress.x*(3 - 2*progress.x), progress.y < from ? 0 : progress.y > to ? 1 : progress.y*progress.y*(3 - 2*progress.y));}
public static Vec3f smoothstep(float from, float to, Vec3f progress) {return Vec3f.v3(progress.x < from ? 0 : progress.x > to ? 1 : progress.x*progress.x*(3 - 2*progress.x), progress.y < from ? 0 : progress.y > to ? 1 : progress.y*progress.y*(3 - 2*progress.y), progress.z < from ? 0 : progress.z > to ? 1 : progress.z*progress.z*(3 - 2*progress.z));}
public static Vec4f smoothstep(float from, float to, Vec4f progress) {return Vec4f.v4(progress.x < from ? 0 : progress.x > to ? 1 : progress.x*progress.x*(3 - 2*progress.x), progress.y < from ? 0 : progress.y > to ? 1 : progress.y*progress.y*(3 - 2*progress.y), progress.z < from ? 0 : progress.z > to ? 1 : progress.z*progress.z*(3 - 2*progress.z), progress.w < from ? 0 : progress.w > to ? 1 : progress.w*progress.w*(3 - 2*progress.w));}

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
