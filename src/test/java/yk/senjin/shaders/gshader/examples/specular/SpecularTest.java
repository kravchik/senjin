package yk.senjin.shaders.gshader.examples.specular;

import org.junit.Test;
import yk.jcommon.fastgeom.Matrix4;
import yk.jcommon.fastgeom.Vec2f;
import yk.jcommon.fastgeom.Vec3f;
import yk.jcommon.utils.IO;
import yk.senjin.SomeTexture;
import yk.senjin.shaders.gshader.Sampler2D;
import yk.senjin.shaders.gshader.StandardFrame;

import static org.junit.Assert.assertEquals;
import static yk.jcommon.fastgeom.Matrix4.perspective;

public class SpecularTest {

    @Test
    public void test1() {
        SpecularV v = new SpecularV();
        Matrix4 camModelViewMatrix = new Matrix4().setIdentity().translate(new Vec3f(0, 0, -15));
        v.modelViewProjectionMatrix = camModelViewMatrix.multiply(perspective(45.0f, (float) 400 / 400, 1, 1000.0f));
        v.normalMatrix = camModelViewMatrix.invert().transpose().get33();
        v.lightDir = new Vec3f(1, 1, 1).normalized();
        SpecularVi vsi = new SpecularVi(new Vec3f(0, 0, 0), new Vec3f(1, 0, 0), new Vec2f());
        SpecularFi vo = new SpecularFi();
        v.main(vsi, vo);

        assertEquals(v.lightDir.x, vo.csLightDir.x, 0.000001);
        assertEquals(v.lightDir.y, vo.csLightDir.y, 0.000001);
        assertEquals(v.lightDir.z, vo.csLightDir.z, 0.000001);
        assertEquals(vsi.normal, vo.normal);
        assertEquals(vsi.uv, vo.uv);



        StandardFrame frame = new StandardFrame();

        SpecularF f = new SpecularF();
        f.lightDir = new Vec3f(0, 0, 1);
        f.txt = new Sampler2D();
        f.txt.texture = new SomeTexture();
        f.txt.texture.image = IO.readImage("jfdi.png");

        SpecularFi vso = new SpecularFi();
        vso.normal = new Vec3f(0, 0, 1);
        vso.csEyeDir = new Vec3f(0, 0, 1);
        vso.csLightDir = new Vec3f(0, 0, 1);
        vso.csNormal = new Vec3f(0, 0, 1);
        vso.uv = new Vec2f(0, 0);

        f.main(vso, frame);
        assertEquals(1, frame.gl_FragColor.w, 0.000001);
        assertEquals(1 + 0.1 + 0.6, frame.gl_FragColor.x, 0.0001);
        assertEquals(1 + 0.1 + 0.5, frame.gl_FragColor.y, 0.0001);
        assertEquals(1 + 0.1 + 0.3, frame.gl_FragColor.z, 0.0001);

        f.lightDir = new Vec3f(1, 0, 0);
        vso.csLightDir = new Vec3f(1, 0, 0);
        vso.normal = new Vec3f(0, 0, 1);
        vso.csEyeDir = new Vec3f(0, 0, 1);
        vso.csNormal = new Vec3f(0, 0, 1);
        vso.uv = new Vec2f(0, 0);

        f.main(vso, frame);

        assertEquals(1, frame.gl_FragColor.w, 0.000001);
        assertEquals(f.ambient.x, frame.gl_FragColor.x, 0.0001);
        assertEquals(f.ambient.y, frame.gl_FragColor.y, 0.0001);
        assertEquals(f.ambient.z, frame.gl_FragColor.z, 0.0001);
    }

}