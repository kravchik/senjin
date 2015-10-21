package yk.senjin.shaders.gshader;

import yk.jcommon.fastgeom.Vec4f;
import yk.senjin.DDDUtils;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 21/10/15
 * Time: 12:45
 */
public class UniformRefVec4f extends UniformRef<Vec4f> {

    public UniformRefVec4f(String name, Object src, String fieldName) {
        super(name, src, fieldName);
    }

    @Override
    public void plug() {
        DDDUtils.uniform(index, getValue());
    }
}
