package yk.senjin.shaders.gshader;

import yk.senjin.VertexStructureState;
import yk.senjin.shaders.ShaderHandler;
import yk.senjin.shaders.UniformVariable;
import yk.senjin.shaders.VertexAttrib;
import yk.senjin.AbstractState;

import java.util.Map;

import static yk.jcommon.collections.YHashMap.hm;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 14/12/14
 * Time: 18:46
 */
public class GShader extends AbstractState {

    public ShaderHandler shader;
    private ProgramGenerator pvs;
    private ProgramGenerator pfs;

    public Class currentVBO;
    public Map<Object, VertexStructureState> vbo2structure = hm();

    public Object vs;

    public GShader(Object vs, Object fs) {
        this.vs = vs;
        init(vs, fs);
    }

    public GShader(String srcDir, Object vs, Object fs) {
        this.vs = vs;
        init(srcDir, vs, fs);
    }

    public void init(Object vs, Object fs) {
        init("src/main/java/", vs, fs);
    }

    public void init(String srcDir, Object vs, Object fs) {
        pvs = createProgram(srcDir, vs, "vs");
        pfs = createProgram(srcDir, fs, "fs");
        if (pvs.outputClass != pfs.inputClass) throw new Error("output of VS " + pvs.outputClass.getName() + " must be same as input to FS " + pfs.inputClass.getName());
        if (!BaseVSOutput.class.isAssignableFrom(pvs.outputClass)) throw new Error("output of VS must extends BaseVSOutput");
        if (pfs.outputClass != StandardFrame.class) throw new Error("output of FS must be StandardFrame class");

        Map<String, String> seenAt = hm();
        for (VertexAttrib a : pvs.attributes) {
            System.out.println("checking " + a.getName());
            String old = seenAt.put(a.getName(), "VS input");
            if (old != null) throw new Error("name clash for " + a.getName() + " at " + old + " and " + seenAt.get(a.getName()));
        }
        for (String a : pfs.varyingFS) {
            System.out.println("checking " + a);
            String old = seenAt.put(a, "VS output");
            if (old != null) throw new Error("name clash for " + a + " at " + old + " and " + seenAt.get(a));
        }
        for (UniformVariable a : pvs.uniforms) {
            System.out.println("checking " + a.name);
            String old = seenAt.put(a.name, "VS uniforms");
            if (old != null) {
                throw new Error("name clash for " + a.name + " at " + old + " and " + seenAt.get(a.name));
            }
        }
        for (UniformVariable a : pfs.uniforms) {
            System.out.println("checking " + a.name);
            String old = seenAt.put(a.name, "FS uniforms");
            if (old != null) throw new Error("name clash for " + a.name + " at " + old + " and " + seenAt.get(a.name));
        }




        shader = new ShaderHandler();
        //

        String res = "";
        //for (String s : pfs.getVaryingFS()) res += s;
        //pvs.setResultSrc(res + pvs.getResultSrc());

        System.out.println(pvs.resultSrc);
        System.out.println(pfs.resultSrc);
        //
        for (UniformVariable u : pvs.uniforms) shader.addVariables(u);
        for (VertexAttrib v : pvs.attributes) shader.addVertexAttrib(v);

        for (UniformVariable u : pfs.uniforms) shader.addVariables(u);
        //for (VertexAttrib v : pfs.getVarying()) shader.addVertexAttrib(v);

        shader.createFromSrc(pvs.resultSrc, pfs.resultSrc);
    }

    public static ProgramGenerator createProgram(String srcDir, Object vs, String programType) {
        String path1 = vs.getClass().getName();
        //path1 = "common/src/" + path1.replace(".", "/") + ".groovy";
//        path1 = "secret/src/" + path1.replace(".", "/") + ".groovy";
        path1 = srcDir + path1.replace(".", "/") + ".groovy";
        System.out.println(path1);
        return new ProgramGenerator(path1, vs, programType);
    }

    @Override
    public void enable() {
        shader.enable();
        if (vbo2structure.containsKey(currentVBO)) vbo2structure.get(currentVBO).enable();
    }

    @Override
    public void disable() {
        if (vbo2structure.containsKey(currentVBO)) vbo2structure.get(currentVBO).disable();
        shader.disable();
    }

}
