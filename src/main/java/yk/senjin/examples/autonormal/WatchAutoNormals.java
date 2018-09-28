package yk.senjin.examples.autonormal;

import yk.jcommon.utils.IO;
import yk.senjin.DDDUtils;
import yk.senjin.DrawIndicesShort;
import yk.senjin.LoadTickUnload;
import yk.senjin.WatchReloadable;
import yk.senjin.shaders.gshader.GProgram;
import yk.senjin.vbo.ReflectionVBO;

import static org.lwjgl.opengl.GL11.*;
import static yk.jcommon.collections.YArrayList.al;
import static yk.jcommon.fastgeom.Vec3f.v3;
import static yk.jcommon.fastgeom.Vec4f.v4;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 04/03/16
 * Time: 10:30
 */
public class WatchAutoNormals implements LoadTickUnload<WatchReloadable> {

    public static void main(String[] args) {
        new WatchReloadable(new WatchAutoNormals());
    }

    GProgram<AutoV, AutoF> shader;
    ReflectionVBO vbo;
    DrawIndicesShort indices;




    @Override
    public void onLoad(WatchReloadable watch) {
        System.out.println(glGetString(GL_VERSION));
        shader = new GProgram<>();
        shader.geometryShaderString = IO.readFile("src/main/java/yk/senjin/examples/autonormal/geo.shader");
        shader.init(new AutoV(), new AutoF());

        vbo = new ReflectionVBO(al(
                new Poco(v3(0, 0, 0),  v4(0, 0, 0, 0)),
                new Poco(v3(10, 0, 0), v4(0, 0, 0, 0)),
                new Poco(v3(0, 10, 0), v4(0, 0, 0, 0)),
                new Poco(v3(0, 0, 0),  v4(0, 0, 0, 0)),
                new Poco(v3(0, 10, 0), v4(0, 0, 0, 0)),
                new Poco(v3(0, 0, 10), v4(0, 0, 0, 0))));
        indices = DrawIndicesShort.simple(6, GL_TRIANGLES);

    }

    @Override
    public void onTick(WatchReloadable watch, float dt) {
        shader.vs.modelViewProjectionMatrix = watch.camModelViewProjectionMatrix;
        shader.vs.modelViewMatrix = watch.camModelViewMatrix;
        shader.vs.normalMatrix = watch.camNormalMatrix.get33();
        DDDUtils.cameraDraw(shader, vbo, indices);
    }

    @Override
    public void onUnload() {

    }
}
