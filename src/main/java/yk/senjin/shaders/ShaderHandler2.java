package yk.senjin.shaders;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.Util;
import yk.senjin.AbstractState;
import yk.senjin.shaders.uniforms.UniformVariable;

import java.util.*;

import static yk.jcommon.utils.BadException.shouldNeverReachHere;
import static yk.senjin.shaders.ShaderHandler.printLogInfo;

/**
 * Created by: Yuri Kravchik Date: 2/11/2007 Time: 11:29:31
 */
public class ShaderHandler2 extends AbstractState {
    public static ShaderHandler2 currentShader;

    private boolean isLinked;
    public int program = -1;
    public final List<UniformVariable> uniforms = new ArrayList<>();
    public final Map<String, VertexAttrib> vertexAttribs = new HashMap<>();

    public ShaderHandler2() {
    }

    public void initVariables() {
        for (final UniformVariable u4f : uniforms) u4f.initForProgram(program);
        for (final VertexAttrib a : vertexAttribs.values()) a.initForProgram(program);
    }

    public void addVariables(final UniformVariable... variables) {
        Collections.addAll(uniforms, variables);
    }

    public void addVertexAttrib(final VertexAttrib attrib) {
        vertexAttribs.put(attrib.getName(), attrib);
    }

    public void createProgram() {
        if (isLinked) throw shouldNeverReachHere();
        if (program != -1) throw shouldNeverReachHere();
        program = GL20.glCreateProgram();
        Util.checkGLError();
    }

    public void attachShader(int index) {
        if (isLinked) throw shouldNeverReachHere();
        if (program == -1) throw shouldNeverReachHere();
        GL20.glAttachShader(program, index);
        Util.checkGLError();
    }

    public void linkProgram() {
        if (isLinked) throw shouldNeverReachHere();
        if (program == -1) throw shouldNeverReachHere();
        GL20.glLinkProgram(program);
        printLogInfo(program);
        initVariables();
        isLinked = true;
    }

    @Override
    public void disable() {
        GL20.glUseProgram(0);
        currentShader = null;
    }

    @Override
    public void enable() {
        GL20.glUseProgram(program);
        for (int i = 0; i < uniforms.size(); i++) {
            uniforms.get(i).plug();
        }
        currentShader = this;
    }

    public VertexAttrib getVertexAttrib(final String name) {
        return vertexAttribs.get(name);
    }

    public void deleteProgram() {
        if (program != -1) GL20.glDeleteProgram(program);
        else System.out.println("WARNING: already removed at " + yk.jcommon.utils.Util.stacktraceToString(new Exception()));
        program = -1;
    }
}
