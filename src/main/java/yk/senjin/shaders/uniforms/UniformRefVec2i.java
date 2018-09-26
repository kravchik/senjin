package yk.senjin.shaders.uniforms;

import yk.jcommon.fastgeom.Vec2i;
import yk.senjin.DDDUtils;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 21/10/15
 * Time: 12:45
 */
public class UniformRefVec2i extends UniformRef<Vec2i> {

    public UniformRefVec2i(String name, Object src, String fieldName) {
        super(name, src, fieldName);
    }

    @Override
    public void plug() {
        DDDUtils.uniform(index, getValue());
    }
}
