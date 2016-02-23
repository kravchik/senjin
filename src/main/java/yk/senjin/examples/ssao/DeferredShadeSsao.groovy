package yk.senjin.examples.ssao

import yk.jcommon.fastgeom.Vec2f
import yk.jcommon.fastgeom.Vec3f
import yk.senjin.examples.ds.UvFi
import yk.senjin.shaders.gshader.FragmentShaderParent
import yk.senjin.shaders.gshader.Sampler2D
import yk.senjin.shaders.gshader.StandardFSOutput

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 23/10/15
 * Time: 20:46
 */
class DeferredShadeSsao extends FragmentShaderParent<UvFi, StandardFSOutput> {
    public Sampler2D txt1 = new Sampler2D();
    public Sampler2D txt2 = new Sampler2D();
    public Sampler2D txt3 = new Sampler2D();

    public float shininess = 10;
    public float shininessStrength = 20;
    public Vec3f ambient = Vec3f(0.1, 0.1, 0.1);
    public Vec3f csLightDir = Vec3f(1, 1, 1)

    public static float hash12(Vec2f p) {
        float h = dot(p,Vec2f(127f,311f));
        return fract(sin(h)*43f)-0.5f;
    }


    public float calcDelta(Vec3f normal, Vec3f pos, Vec2f iuv, Vec2f dif) {
        if (pos.z == 0) return 0;

//with noise
        float d = -0.05f/pos.z;
        float v1 = hash12(iuv) *d
        float v2 = hash12(iuv + Vec2f(0.1f, 0)) *d
        Vec2f newPos = iuv + dif + Vec2f(v1, v2)

//without noise
//        Vec2f newPos = iuv + dif

        float longest = -pos.z/10;
//        float longest = 0.5f

        if (newPos.x < 0 || newPos.y < 0 || newPos.x > 1 || newPos.y > 1) return 0;
        Vec3f ray = texture2D(txt3, newPos).xyz - pos
        if (length(ray) < 0.01f) return 0;
//        if (ray.z > longest || ray.z < -longest) d1 = d1 * 0.3f/(0.3f+longest-ray.z)
        if (ray.z > longest || ray.z < -longest) return 0
        float d1 = dot(normal, normalize(ray));
        return min(1f, max(0, d1));
//        return min(1f, max(0, d1/(max(0.1f, abs(ray.z*4)))));
    }

    @Override
    void main(UvFi i, StandardFSOutput o) {

        Vec3f color = texture2D(txt1, i.uv).xyz;
        Vec3f normal = texture2D(txt2, i.uv).xyz
        Vec3f pos = texture2D(txt3, i.uv).xyz

        Vec3f lightColor = Vec3f(1, 1, 1);
        Vec3f matSpec = Vec3f(0.6, 0.5, 0.3);

        float d = -0.1f/pos.z;
//        float d = 0.02f;
        float d2 = d / 2f;
//        float d1 = 1-(
//                        calcDelta(normal, pos, i.uv, Vec2f(d, 0))
//        )

        float d1 = 1-(
                        calcDelta(normal, pos, i.uv, Vec2f(d, 0)) +
                        calcDelta(normal, pos, i.uv, Vec2f(-d, 0)) +
                        calcDelta(normal, pos, i.uv, Vec2f(0, d)) +
                        calcDelta(normal, pos, i.uv, Vec2f(0, -d)) +

//                        calcDelta(normal, pos, i.uv, Vec2f(d2, 0)) +
//                        calcDelta(normal, pos, i.uv, Vec2f(-d2, 0)) +
//                        calcDelta(normal, pos, i.uv, Vec2f(0, d2)) +
//                        calcDelta(normal, pos, i.uv, Vec2f(0, -d2))

                        calcDelta(normal, pos, i.uv, Vec2f(d2, d2)) +
                        calcDelta(normal, pos, i.uv, Vec2f(-d2, d2)) +
                        calcDelta(normal, pos, i.uv, Vec2f(-d2, -d2)) +
                        calcDelta(normal, pos, i.uv, Vec2f(d2, -d2))
        )/12f
        if (pos.z == 0) d1 = 0;

        Vec3f r = normalize(reflect(normalize(csLightDir), normalize(normal)));
        Vec3f specular = lightColor * matSpec * pow(max(0f, dot(r, -normalize(-pos))), shininess) * shininessStrength;

        if (normal == Vec3f(0, 0, 0)) {
        } else {
            color = specular + color * (max(0f, dot(normal, csLightDir)) * lightColor + ambient);
        }


        float fExposure = 2f
        color = 1f - exp(-fExposure * color)

//        o.gl_FragColor = Vec4f(0, 0, -pos.z/20, 1);
        color *= d1;
//        o.gl_FragColor = Vec4f(color.x, color.y*d1, color.x*d1, 1);
//        o.gl_FragColor = Vec4f(0, d1, -pos.z, 1);
//        o.gl_FragColor = Vec4f(0, (1-dark)/1, 0, 1);
//        o.gl_FragColor = Vec4f(normal.x, normal.y, normal.z, 1);
        o.gl_FragColor = Vec4f(color, 1);

    }
}
