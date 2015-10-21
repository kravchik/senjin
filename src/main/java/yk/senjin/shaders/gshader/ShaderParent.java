package yk.senjin.shaders.gshader;

import yk.jcommon.fastgeom.*;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 11/6/14
 * Time: 12:37 PM
 */
public class ShaderParent {

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
        return null;
    }

    public Vec2f vec2(double x, double y) {
        return null;
    }

    public Vec3f normalize(Vec3f p) {
        return null;
    }

    public float dot(Vec3f a, Vec3f b) {
        return a.scalarProduct(b);
    }

    public Vec3f vec3(double x, double y, double z) {
        return new Vec3f((float)x, (float)y, (float)z);
    }

    public Vec4f vec4(double x, double y, double z, double w) {
        return null;
    }

    public Vec4f vec4(Vec3f v, double w) {
        return null;
    }
}
