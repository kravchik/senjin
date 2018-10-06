package yk.senjin.examples.blend;

import org.lwjgl.LWJGLException;
import yk.jcommon.fastgeom.Vec2f;
import yk.jcommon.fastgeom.Vec3f;
import yk.senjin.DDDUtils;
import yk.senjin.Simple3DWatch;
import yk.senjin.SomeTexture;
import yk.senjin.examples.specular.SpecularF;
import yk.senjin.examples.specular.SpecularV;
import yk.senjin.examples.specular.SpecularVi;
import yk.senjin.shaders.gshader.GProgram;
import yk.senjin.vbo.AVboShortIndices;
import yk.senjin.vbo.AVboTyped;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static yk.jcommon.collections.YArrayList.al;
import static yk.jcommon.utils.IO.readImage;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 23/10/15
 * Time: 21:21
 */
public class WatchBlend extends Simple3DWatch {

    public SpecularF specularF;
    public SpecularV specularV;
    public GProgram specularProgram;

    public AVboTyped vbo1;
    public SomeTexture textureJfdi;
    public AVboShortIndices indices;

    public Blender1 blender;

    public WatchBlend() {
        super(600, 600, true);
        SIMPLE_AA =  false;
    }

    public static void main(String[] args) {
        new WatchBlend();
    }

    @Override
    public void firstFrame() {
        specularF = new SpecularF();
        specularV = new SpecularV();
        specularProgram = new GProgram(specularV, specularF);

        textureJfdi = new SomeTexture(readImage("jfdi.png"));
        vbo1 = new AVboTyped(
                new SpecularVi(new Vec3f(0, 0, 0),  new Vec3f(-1, -1, 1).normalized(), new Vec2f(0, 0)),
                new SpecularVi(new Vec3f(10, 0, 0), new Vec3f( 1, -1, 1).normalized(), new Vec2f(1, 0)),
                new SpecularVi(new Vec3f(10, 10, 0),new Vec3f( 1,  1, 1).normalized(), new Vec2f(1, 1)),
                new SpecularVi(new Vec3f(0, 10, 0), new Vec3f(-1,  1, 1).normalized(), new Vec2f(0, 1)));
        indices = new AVboShortIndices(GL_TRIANGLES, al(0, 1, 2, 0, 2, 3));

        blender = new Blender1();
        blender.init();
    }

    @Override
    protected void commonTick(float dt) throws LWJGLException {
        if (firstFrame) onFirstFrame();
        blender.beginRenderToFbo1();
        onRender(dt);
    }

    @Override
    public void tick(float dt) {
        //scene -> fbo1
        specularV.modelViewProjectionMatrix = camModelViewProjectionMatrix;
        specularV.normalMatrix = camNormalMatrix.get33();
        specularV.lightDir = new Vec3f(1, 1, 1).normalized();
        specularF.txt.set(textureJfdi);
        specularV.modelViewMatrix = camModelViewMatrix;
        DDDUtils.cameraDraw(specularProgram, vbo1, indices, textureJfdi);
        blender.endRenderToFbo1();

        blender.renderToScreen(w, h);

    }


}
