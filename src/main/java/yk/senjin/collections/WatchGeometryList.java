package yk.senjin.collections;

import yk.jcommon.fastgeom.Vec3f;
import yk.jcommon.fastgeom.Vec4f;
import yk.jcommon.utils.MyMath;
import yk.jcommon.utils.Rnd;
import yk.senjin.shaders.gshader.GProgram;
import yk.senjin.vbo.AVboIntIndices;
import yk.senjin.vbo.AVboTyped;
import yk.senjin.viewers.GlWindow1;

import static org.lwjgl.opengl.GL11.*;
import static yk.jcommon.fastgeom.Matrix4.ortho;
import static yk.jcommon.fastgeom.Vec3f.v3;
import static yk.ycollections.YArrayList.al;

/**
 * Created by Yuri Kravchik on 28.06.24
 */
public class WatchGeometryList {
    private final GlWindow1 window = new GlWindow1()
        .stopOnEsc()
        .setUxSize(800, 800)
        .onFirstFrame(() -> onFirstFrame())
        .onTick(this::tick);

    private final Rnd rnd = new Rnd(1);
    private GProgram program;
    private float time;
    private GeometryList geoList;

    public static void main(String[] args) {
        new WatchGeometryList().window.start(1);
    }

    private void onFirstFrame() {
        SuiVS vs = new SuiVS();
        SuiFS fs = new SuiFS();
        vs.modelViewProjectionMatrix = ortho(0, (float) window.getWidth(), 0, (float) window.getHeight(), 0, 10);
        program = GProgram.initFrom("src/main/java/", vs, fs);
        geoList = new GeometryList(new AVboTyped(SuiVS.Input.class, 0), new AVboIntIndices(GL_TRIANGLES, 0));
    }

    public void tick(float dt) {
        float w = window.getWidth();
        float h = window.getHeight();

        time += dt*2;
        glClearColor(0, 0, 0, 0);
        glClear(GL_COLOR_BUFFER_BIT);
        glDisable(GL_DEPTH_TEST);

        while (geoList.instances.size() > 10000) geoList.remove(0);

        int toAdd = rnd.nextInt(100)+1;
        for (int i = 0; i < toAdd; i++) {
            Vec3f pos = v3(MyMath.sin(time), MyMath.cos(time), 0)
                .mul(rnd.nextFloat(0.2f, 0.8f) * w/2)
                .add(w*0.5f, h*0.5f, 0);
            Vec4f color = rnd.nextVec3f().toVec4f(1);

            float size = 20;

            geoList.add(new int[]{0, 1, 2}, al(
                new SuiVS.Input(v3(-size / 2, 0, 0).add(pos), color),
                new SuiVS.Input(v3(size / 2, 0, 0).add(pos), color),
                new SuiVS.Input(v3(0, size, 0).add(pos), color)
            ));
        }
        //TODO info by Sui
        //System.out.println(geoList.verticesInfo);
        geoList.flush();
        program.setInput(geoList.vboVertices);
        program.enable();
        geoList.vboIndices.enable(geoList.indicesInfo.getFirstFree());
        program.disable();
    }


}
