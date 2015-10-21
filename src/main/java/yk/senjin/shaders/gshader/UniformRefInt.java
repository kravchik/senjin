package yk.senjin.shaders.gshader;

import yk.jcommon.fastgeom.Vec2f;
import yk.jcommon.utils.Reflector;
import yk.senjin.DDDUtils;
import yk.senjin.shaders.UniformVariable;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 21/10/15
 * Time: 12:45
 */
public class UniformRefInt extends UniformRef<Integer> {

    public UniformRefInt(String name, Object src, String fieldName) {
        super(name, src, fieldName);
    }

    @Override
    public void plug() {
        DDDUtils.uniform(index, getValue());
    }
}
