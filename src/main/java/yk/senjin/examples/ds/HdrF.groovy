package yk.senjin.examples.ds

import yk.jcommon.fastgeom.Vec3f
import yk.senjin.shaders.gshader.FragmentShaderParent
import yk.senjin.shaders.gshader.Sampler2D
import yk.senjin.shaders.gshader.StandardFSOutput

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 23/10/15
 * Time: 20:46
 */
class HdrF extends FragmentShaderParent<HdrFi, StandardFSOutput> {
    public Sampler2D txt1 = new Sampler2D();
    public Sampler2D txt2 = new Sampler2D();
    public Sampler2D txt3 = new Sampler2D();

    public float shininess = 10;
    public Vec3f ambient = Vec3f(0.1, 0.1, 0.1);
    public Vec3f csLightDir = Vec3f(1, 1, 1)

    @Override
    void main(HdrFi i, StandardFSOutput o) {
        //1.0 – exp(-fExposure x color)
        Vec3f color = texture2D(txt1, i.uv).xyz;
        Vec3f normal = texture2D(txt2, i.uv).xyz
        Vec3f pos = texture2D(txt3, i.uv).xyz

//        if (i.gl_FragCoord.x < 430) {
            Vec3f lightColor = Vec3f(1, 1, 1);
            Vec3f matSpec = Vec3f(0.6, 0.5, 0.3);


            Vec3f r = normalize(reflect(normalize(csLightDir), normalize(normal)));
            Vec3f specular = lightColor * matSpec * pow(max(0f, dot(r, -normalize(-pos))), shininess) * 20;

//            color = (csLightDir + 1)/2
            color = specular + color * (max(0f, dot(normal, csLightDir)) * lightColor + ambient);
//        } else {
//            color = texture2D(txt2, i.vTexCoord).xyz;
//        }



        float fExposure = 2f
        color = 1f - exp(-fExposure * color)

        o.gl_FragColor = Vec4f(color, 1);

    }
}
