package yk.senjin.shaders.gshader.examples.reflectionvbo;

import yk.jcommon.fastgeom.Vec3f;
import yk.senjin.shaders.gshader.GBufferer;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 09/06/15
 * Time: 21:28
 */
public class VSInput {

    public Vec3f position;

    public static void main(String[] args) {
        System.out.println(GBufferer.gen(VSInput.class));
        GBufferer.createFile(VSInput.class);
    }

    public VSInput() {
        position = new Vec3f(0, 0, 0);
    }

    public VSInput(Vec3f position) {
        this.position = position;
    }
}
