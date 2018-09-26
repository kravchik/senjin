package yk.senjin.shaders.gshader;

import yk.jcommon.collections.YMap;
import yk.jcommon.utils.BadException;
import yk.senjin.shaders.uniforms.UniformVariable;

import java.util.Map;

import static yk.jcommon.collections.YHashMap.hm;

public class ShaderUserMap<V extends VertexShaderParent, F extends FragmentShaderParent> extends ShaderUser<V, F> {
    public YMap<String, Object> shaderParams = hm();

    public ShaderUserMap(GProgram<V, F> program, Vbo vertexClass) {
        super(program, vertexClass);
    }

    @Override
    public void updateUniforms() {
        a:for (Map.Entry<String, Object> entry : shaderParams.entrySet()) {
            for (UniformVariable uniform : uniforms) {
                if (uniform.name.equals(entry.getKey())) {
                    uniform.setValue(entry.getValue());
                    continue a;
                }
            }
            BadException.die("not found " + entry.getKey() + " in uniforms");
        }
    }
}
