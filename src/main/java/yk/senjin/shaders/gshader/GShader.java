package yk.senjin.shaders.gshader;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.Util;
import yk.jcommon.collections.YList;
import yk.jcommon.collections.YMap;
import yk.jcommon.collections.YSet;
import yk.jcommon.fastgeom.Vec2f;
import yk.jcommon.fastgeom.Vec3f;
import yk.jcommon.fastgeom.Vec4f;
import yk.jcommon.utils.BadException;
import yk.jcommon.utils.FileWatcher;
import yk.jcommon.utils.IO;
import yk.senjin.AbstractState;
import yk.senjin.shaders.ShaderHandler;
import yk.senjin.shaders.UniformVariable;
import yk.senjin.shaders.VertexAttrib;
import yk.senjin.shaders.arraystructure.AbstractArrayStructure;
import yk.senjin.shaders.arraystructure.VBOVertexAttrib;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.Map;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static yk.jcommon.collections.YArrayList.al;
import static yk.jcommon.collections.YHashMap.hm;
import static yk.jcommon.collections.YHashSet.hs;
import static yk.senjin.VertexStructureState.assertType;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 14/12/14
 * Time: 18:46
 */
public class GShader<V extends VertexShaderParent, F extends FragmentShaderParent> extends AbstractState {
    public V vs;
    public F fs;

    public ShaderHandler shader;
    public ProgramGenerator pvs;
    public ProgramGenerator pfs;

    private ReflectionVBO currentVBO;
    private YList<AbstractArrayStructure> currentStructure;

    private FileWatcher vsWatcher;
    private FileWatcher fsWatcher;
    private String srcDir;

    public <V extends VertexShaderParent, F extends FragmentShaderParent> GShader<V, F> runtimeReload() {
        if (fsWatcher != null) BadException.die("already watching");
        try {
            fsWatcher = new FileWatcher(pfs.srcPath);
        } catch (Exception e) {
            System.out.println("warning: not watching for " + pfs.srcPath);
        }
        try {
            vsWatcher = new FileWatcher(pvs.srcPath);
        } catch (Exception e) {
            System.out.println("warning: not watching for " + pvs.srcPath);
        }
        return (GShader<V, F>) this;
    }

    public static GShader initFromSrcMainJava(ShaderParent vs, ShaderParent fs) {
        return new GShader(vs, fs);
    }

    public static GShader initFromSrc(ShaderParent vs, ShaderParent fs) {
        return new GShader("src/", vs, fs);
    }

    public static GShader initFrom(String src, ShaderParent vs, ShaderParent fs) {
        return new GShader(src, vs, fs);
    }

    public GShader(ShaderParent vs, ShaderParent fs) {
        init(vs, fs);
    }

    public GShader(String srcDir, ShaderParent vs, ShaderParent fs) {
        init(srcDir, vs, fs);
    }

    public void init(ShaderParent vs, ShaderParent fs) {
        init("src/main/java/", vs, fs);
    }

    public void init(String srcDir, ShaderParent vs, ShaderParent fs) {
        srcDir = srcDir.replace("/", File.separator);
        this.srcDir = srcDir;
        initImpl(srcDir, vs, fs);
        newShader();
    }

    private void initImpl(String srcDir, ShaderParent vs, ShaderParent fs) {
        pvs = createProgram(srcDir, vs, "vs");
        pfs = createProgram(srcDir, fs, "fs");

        if (pvs.outputClass != pfs.inputClass) throw new Error("output of VS " + pvs.outputClass.getName() + " must be same as input to FS " + pfs.inputClass.getName());
        if (!StandardFSInput.class.isAssignableFrom(pvs.outputClass)) throw new Error("output of VS must extends StandardFSInput");
        if (!StandardFSOutput.class.isAssignableFrom(pfs.outputClass)) throw new Error("output of FS must be StandardFrame class");

        Map<String, String> seenAt = hm();
        for (VertexAttrib a : pvs.attributes) {
//            System.out.println("checking " + a.getName());
            String old = seenAt.put(a.getName(), "VS input");
            if (old != null) throw new Error("name clash for " + a.getName() + " at " + old + " and " + seenAt.get(a.getName()));
        }
        for (String a : pfs.varyingFS) {
//            System.out.println("checking " + a);
            String old = seenAt.put(a, "VS output");
            if (old != null) throw new Error("name clash for " + a + " at " + old + " and " + seenAt.get(a));
        }
        for (UniformVariable a : pvs.uniforms) {
//            System.out.println("checking " + a.name);
            String old = seenAt.put(a.name, "VS uniforms");
            if (old != null) {
                throw new Error("name clash for " + a.name + " at " + old + " and " + seenAt.get(a.name));
            }
        }
        for (Iterator<UniformVariable> iterator = pfs.uniforms.iterator(); iterator.hasNext(); ) {
            UniformVariable a = iterator.next();
//            System.out.println("checking " + a.name);
            String old = seenAt.put(a.name, "FS uniforms");
//            if (old != null) throw new Error("name clash for " + a.name + " at " + old + " and " + seenAt.get(a.name));
            if (old != null) {
                //TODO check same type
                //TODO prevent somehow from filling from FS
                iterator.remove();
            }
        }

        this.vs = (V) vs;
        this.fs = (F) fs;

    }

    private void newShader() {
        shader = new ShaderHandler();

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

    public static ProgramGenerator createProgram(String srcDir, ShaderParent vs, String programType) {
        String path1 = vs.getClass().getName();
        String resultPath = srcDir + path1.replace(".", "/") + ".groovy";
        String src;
        if (!new File(resultPath).exists()) {
            resultPath = path1.replace(".", "/") + ".groovy";
            src = IO.streamToString(vs.getClass().getResourceAsStream("/" + resultPath));
        } else {
            src = IO.readFile(resultPath);
        }


        return new ProgramGenerator(src, resultPath, vs, programType);
    }

    private YMap<Class, YList<AbstractArrayStructure>> type2structure = hm();
    private YList<AbstractArrayStructure> getShaderSpecificStructure(Class clazz) {
        YList<AbstractArrayStructure> result = type2structure.get(clazz);
        if (result == null) {
            result = al();
            Field[] fields = clazz.getDeclaredFields();
            int stride = ReflectionVBO.getSizeOfType(clazz);
            int offset = 0;
            YSet<String> hasFields = hs();
            for (Field field : fields) {
                if (Modifier.isTransient(field.getModifiers())) continue;
                hasFields.add(field.getName());
                VertexAttrib shaderAttrib = shader.getVertexAttrib(field.getName());
                if (shaderAttrib == null) throw new RuntimeException("shader has no attribute " + field.getName());

                result.add(new VBOVertexAttrib(shaderAttrib.getIndex(), shaderAttrib.getSize(), shaderAttrib.getType(), shaderAttrib.isNormalized(), stride, offset));

                if (field.getType() == float.class) {
                    assertType(shaderAttrib, 1, GL11.GL_FLOAT, field.getName());
                    offset += 1 * 4;
                } else if (field.getType() == Vec2f.class) {
                    assertType(shaderAttrib, 2, GL11.GL_FLOAT, field.getName());
                    offset += 2 * 4;
                } else if (field.getType() == Vec3f.class) {
                    assertType(shaderAttrib, 3, GL11.GL_FLOAT, field.getName());
                    offset += 3 * 4;
                } else if (field.getType() == Vec4f.class) {
                    assertType(shaderAttrib, 4, GL11.GL_FLOAT, field.getName());
                    offset += 4 * 4;
                } else throw BadException.die("unknown VS input field type: " + field.getType());
            }
            for (String attrib : shader.vertexAttribs.keySet()) if (!hasFields.contains(attrib)) throw new Error("buffer haven't field " + attrib);
            type2structure.put(clazz, result);
        }
        return result;
    }

    //TODO interface
    public void setInput(ReflectionVBO vbo) {
        currentVBO = vbo;
        currentStructure = getShaderSpecificStructure(vbo.inputType);
    }

    @Override
    public void enable() {
        boolean changed = fsWatcher != null && fsWatcher.isChanged() || vsWatcher != null && vsWatcher.isChanged();
        if (changed) {
            //TODO clean up on fails!
            GShader newShader = new GShader(srcDir, pvs.shaderGroovy, pfs.shaderGroovy);
            shader.deleteProgram();
            pvs = newShader.pvs;
            pfs = newShader.pfs;
            shader = newShader.shader;
            init(srcDir, pvs.shaderGroovy, pfs.shaderGroovy);
        }

        shader.enable();
        if (currentVBO != null) {//because we can use built in vertex attributes
            glBindBuffer(GL_ARRAY_BUFFER, currentVBO.bufferId);
            Util.checkGLError();
            for (int i = 0; i < currentStructure.size(); i++) currentStructure.get(i).turnOn();
            Util.checkGLError();
        }
    }

    @Override
    public void disable() {
        if (currentVBO != null) {
            for (int i = 0; i < currentStructure.size(); i++) currentStructure.get(i).turnOff();
            glBindBuffer(GL_ARRAY_BUFFER, 0);
        }
        shader.disable();
    }

    @Override
    public void release() {
        shader.deleteProgram();
    }
}
