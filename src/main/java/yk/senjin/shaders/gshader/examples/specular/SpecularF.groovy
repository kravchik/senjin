package yk.senjin.shaders.gshader.examples.specular

import yk.jcommon.fastgeom.Vec3f
import yk.senjin.shaders.gshader.FragmentShaderParent
import yk.senjin.shaders.gshader.Sampler2D
import yk.senjin.shaders.gshader.StandardFrame

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 22/10/15
 * Time: 13:59
 */
public class SpecularF extends FragmentShaderParent<SpecularFi> {
    public Sampler2D txt = new Sampler2D()
    public float shininess = 10;
    public Vec3f ambient = Vec3f(0.1, 0.1, 0.1);
    public Vec3f lightDir

    def void main(SpecularFi i, StandardFrame o) {
        Vec3f color = texture2D(txt, i.uv).xyz;
        Vec3f matSpec = Vec3f(0.6, 0.5, 0.3);
        Vec3f lightColor = Vec3f(1, 1, 1);

        Vec3f diffuse  = color * max(0f, dot(i.normal, lightDir)) * lightColor;
        Vec3f r = normalize(reflect(normalize(i.csLightDir), normalize(i.csNormal)));
        Vec3f specular = lightColor * matSpec * pow(max(0f, dot(r, -normalize(i.csEyeDir))), shininess);
        o.gl_FragColor =  Vec4f(ambient + diffuse + specular, 1);
    }
}
