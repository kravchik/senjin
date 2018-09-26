package yk.senjin.shaders.gshader;

import org.lwjgl.opengl.GL20;
import yk.jcommon.collections.YList;
import yk.jcommon.utils.BadException;
import yk.senjin.AbstractState;
import yk.senjin.shaders.ShaderHandler;
import yk.senjin.shaders.arraystructure.AbstractArrayStructure;
import yk.senjin.shaders.uniforms.*;

import static yk.jcommon.collections.YArrayList.al;

abstract public class ShaderUser<V extends VertexShaderParent, F extends FragmentShaderParent> extends AbstractState {
    private ShaderHandler oldShaderHandler;
    private GProgram<V, F> program;

    private Vbo vbo;

    YList<Sampler2D> samplers;
    protected YList<UniformVariable> uniforms;
    private YList<AbstractArrayStructure> currentStructure;

    public boolean uniformsChanged;
    abstract public void updateUniforms();

    //TODO init uniforms

    public ShaderUser(GProgram<V, F> program, Vbo vbo) {
        this.program = program;
        currentStructure = program.getShaderSpecificStructure(vbo.getInputType());
        this.vbo = vbo;
        initUniformsCopy();
    }

    private void initUniformsCopy() {
        uniforms = al();
        samplers = al();
        for (UniformVariable uniform : program.shaderState.uniforms) {
            UniformVariable result = null;
            if (uniform instanceof UniformRefVec4f) result = new Uniform4f(uniform.name, ((UniformRefVec4f) uniform).getValue());
            else if (uniform instanceof UniformRefVec3f) result = new Uniform3f(uniform.name, ((UniformRefVec3f) uniform).getValue());
            else if (uniform instanceof UniformRefVec2f) result = new Uniform2f(uniform.name, ((UniformRefVec2f) uniform).getValue());
            else if (uniform instanceof UniformRefFloat) result = new Uniform1f(uniform.name, ((UniformRefFloat) uniform).getValue());
            else if (uniform instanceof UniformRefInt) result = new Uniform1i(uniform.name, ((UniformRefInt) uniform).getValue());
            else if (uniform instanceof UniformRefMatrix4) result = new UniformMatrix4(uniform.name);
            else if (uniform instanceof UniformRefMatrix3) result = new UniformMatrix3(uniform.name);
            else if (uniform instanceof Sampler2D) {
                result = new Sampler2D();
                samplers.add((Sampler2D) result);
            }
            else BadException.die("not implemented for type: " + uniform.getClass());

            result.index = uniform.index;
            uniforms.add(result);
        }
    }

    @Override
    public void enable() {
        checkChanges();

        //vbo (should be before array structure)
        vbo.enable();
        //textures
        for (int i = 0; i < samplers.size(); i++) samplers.get(i).value.enable(i);
        //program
        GL20.glUseProgram(program.shaderState.program);
        //uniforms
        for (int i = 0; i < uniforms.size(); i++) uniforms.get(i).plug();
        //array structure
        for (int i = 0; i < currentStructure.size(); i++) currentStructure.get(i).turnOn();
    }

    private void checkChanges() {
        if (uniformsChanged || oldShaderHandler != program.shaderState) {
            oldShaderHandler = program.shaderState;
            updateUniforms();
            uniformsChanged = false;
        }
    }

    @Override
    public void disable() {
        //array structure
        for (int i = 0; i < currentStructure.size(); i++) currentStructure.get(i).turnOff();
        //program
        GL20.glUseProgram(0);
        //textures
        for (int i = 0; i < samplers.size(); i++) samplers.get(i).value.disable();
        //vbo
        vbo.disable();
    }



}
