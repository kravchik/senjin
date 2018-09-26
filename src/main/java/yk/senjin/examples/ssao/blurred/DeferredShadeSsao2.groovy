package yk.senjin.examples.ssao.blurred

import yk.jcommon.fastgeom.Vec2f
import yk.jcommon.fastgeom.Vec3f
import yk.senjin.examples.ds.UvFi
import yk.senjin.shaders.gshader.FragmentShaderParent
import yk.senjin.shaders.gshader.StandardFSOutput
import yk.senjin.shaders.uniforms.Sampler2D

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 23/10/15
 * Time: 20:46
 */

//render to mono texture
class DeferredShadeSsao2 extends FragmentShaderParent<UvFi, StandardFSOutput> {
    public Sampler2D txt1 = new Sampler2D();
    public Sampler2D txt2 = new Sampler2D();
    public Sampler2D txt3 = new Sampler2D();

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
        Vec2f newPos = iuv + dif + Vec2f(v1, v2);

//without noise
//        Vec2f newPos = iuv + dif

        float longest = 0.3f;
//        float longest = -pos.z/10;

//        float longest = 0.5f

        if (newPos.x < 0 || newPos.y < 0 || newPos.x > 1 || newPos.y > 1) return 0;
        Vec3f ray = texture2D(txt3, newPos).xyz - pos

        float rayLen = length(ray)
        if (rayLen < 0.01f) return 0;
//        if (ray.z > longest || ray.z < -longest) d1 = d1 * 0.3f/(0.3f+longest-ray.z)
        if (rayLen > longest) return 0
        if (ray.z > longest || ray.z < -longest) return 0
//        float d1 = (longest-rayLen)/longest;
//        float d1 = (longest / (longest-rayLen));

//        float len2 = length(dif);
//        float d1 = dot(normal, normalize(ray)) /len2;
//        float d1 = dot(normal, normalize(ray)) * (0.5f-len2)/longest;
        Vec3f normalizedRay = normalize(ray)
        float d1 = dot(normal, normalizedRay)// * (longest-rayLen)/longest;
//        if (d1 > 0.1f) d1 = 1;
//        return min(1f, max(0, d1/ length(dif) / (-pos.z)/30));
        return pow(clamp(d1, 0, 1), 2);
//        return min(1f, max(0, d1/(max(0.1f, abs(ray.z*4)))));
    }

    @Override
    void main(UvFi i, StandardFSOutput o) {

        Vec3f normal = texture2D(txt2, i.uv).xyz
        Vec3f pos = texture2D(txt3, i.uv).xyz

        float d = -0.1f/pos.z;
        float d2 = d / 2f;

        float d1 = 1-(
                        calcDelta(normal, pos, i.uv, Vec2f(d, 0))   * 0.2f +
                        calcDelta(normal, pos, i.uv, Vec2f(-d, 0))  * 0.2f +
                        calcDelta(normal, pos, i.uv, Vec2f(0, d))   * 0.2f +
                        calcDelta(normal, pos, i.uv, Vec2f(0, -d))  * 0.2f +


                                calcDelta(normal, pos, i.uv, Vec2f(d, d2))   * 0.12f+
                                calcDelta(normal, pos, i.uv, Vec2f(-d, -d2)) * 0.12f+
                                calcDelta(normal, pos, i.uv, Vec2f(-d2, d))  * 0.12f+
                                calcDelta(normal, pos, i.uv, Vec2f(d2, -d))  * 0.12f+

                                calcDelta(normal, pos, i.uv, Vec2f(d, -d2))  * 0.12f+
                                calcDelta(normal, pos, i.uv, Vec2f(-d, d2))  * 0.12f+
                                calcDelta(normal, pos, i.uv, Vec2f(d2, d))   * 0.12f+
                                calcDelta(normal, pos, i.uv, Vec2f(-d2, -d)) * 0.12f+

                                calcDelta(normal, pos, i.uv, Vec2f(d/4, 0))   * 0.2f +
                                calcDelta(normal, pos, i.uv, Vec2f(-d/4, 0))  * 0.2f +
                                calcDelta(normal, pos, i.uv, Vec2f(0, d/4))   * 0.2f +
                                calcDelta(normal, pos, i.uv, Vec2f(0, -d/4))  * 0.2f +


//                        calcDelta(normal, pos, i.uv, Vec2f(d2, 0)) +
//                        calcDelta(normal, pos, i.uv, Vec2f(-d2, 0)) +
//                        calcDelta(normal, pos, i.uv, Vec2f(0, d2)) +
//                        calcDelta(normal, pos, i.uv, Vec2f(0, -d2)) +

                        calcDelta(normal, pos, i.uv, Vec2f(d2, d2))   *0.3f +
                        calcDelta(normal, pos, i.uv, Vec2f(-d2, d2))  *0.3f +
                        calcDelta(normal, pos, i.uv, Vec2f(-d2, -d2)) *0.3f +
                        calcDelta(normal, pos, i.uv, Vec2f(d2, -d2))  *0.3f
        )/4f
//        if (pos.z == 0) d1 = 0;


//        o.gl_FragColor = Vec4f(0, 0, -pos.z/20, 1);
//        o.gl_FragColor = Vec4f(color.x, color.y*d1, color.x*d1, 1);
//        o.gl_FragColor = Vec4f(0, d1, -pos.z, 1);
//        o.gl_FragColor = Vec4f(0, (1-dark)/1, 0, 1);
//        o.gl_FragColor = Vec4f(normal.x, normal.y, normal.z, 1);

        o.gl_FragColor = Vec4f(d1, pos.z, 0, 1);
//        o.gl_FragColor = Vec4f(d1, pos.z, 0, 0);

    }
}
