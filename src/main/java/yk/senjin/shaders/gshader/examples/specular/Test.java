package yk.senjin.shaders.gshader.examples.specular;

import yk.jcommon.fastgeom.Matrix3;
import yk.jcommon.fastgeom.Matrix4;
import yk.jcommon.fastgeom.Vec2f;
import yk.jcommon.fastgeom.Vec3f;
import yk.jcommon.utils.IO;
import yk.senjin.SomeTexture;
import yk.senjin.shaders.gshader.Sampler2D;
import yk.senjin.shaders.gshader.StandardFrame;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 22/10/15
 * Time: 14:15
 */
public class Test {

    public static void main(String[] args) {

        SpecularFi vso = new SpecularFi();
        vso.normal = new Vec3f(1, 0, 0);
        vso.csEyeDir = new Vec3f(0, 0, -1);
        vso.uv = new Vec2f(0, 0);

        StandardFrame frame = new StandardFrame();

        SpecularF f = new SpecularF();
        f.lightDir = new Vec3f(1, 0, 0);
        f.txt = new Sampler2D();
        f.txt.texture = new SomeTexture();
        f.txt.texture.image = IO.readImage("jfdi.png");

        f.main(vso, frame);
        System.out.println(frame.gl_FragColor);

        SpecularVi vsi = new SpecularVi(new Vec3f(0, 0, 0), new Vec3f(1, 0, 0), new Vec2f());
        SpecularV v = new SpecularV();
        v.modelViewProjectionMatrix = new Matrix4();
        v.normalMatrix = new Matrix3();
        v.main(vsi, vso);

    }

}
