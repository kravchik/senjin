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
import yk.jcommon.utils.StopWatch;
import yk.senjin.AbstractState;
import yk.senjin.shaders.ShaderHandler;
import yk.senjin.shaders.UniformVariable;
import yk.senjin.shaders.VertexAttrib;
import yk.senjin.shaders.arraystructure.AbstractArrayStructure;
import yk.senjin.shaders.arraystructure.VBOVertexAttrib;

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
public class GProgram<V extends VertexShaderParent, F extends FragmentShaderParent> extends AbstractState {
    public V vs;
    public F fs;

    private ShaderHandler shader;

    private ShaderGenerator oldVGen;
    private ShaderGenerator oldFGen;
    private GShaderNew pvs;
    private GShaderNew pfs;

    private ReflectionVBO currentVBO;
    private YList<AbstractArrayStructure> currentStructure;

    public <V extends VertexShaderParent, F extends FragmentShaderParent> GProgram<V, F> runtimeReload() {
        if (pvs.singleOwner) pvs.runtimeReload();
        if (pfs.singleOwner) pfs.runtimeReload();
        return (GProgram<V, F>) this;
    }

    public static GProgram initFromSrcMainJava(ShaderParent vs, ShaderParent fs) {
        return new GProgram(vs, fs);
    }

    public static GProgram initFromSrc(ShaderParent vs, ShaderParent fs) {
        return new GProgram("src/", vs, fs);
    }

    public static GProgram initFrom(String src, ShaderParent vs, ShaderParent fs) {
        return new GProgram(src, vs, fs);
    }

    public GProgram(ShaderParent vs, ShaderParent fs) {
        init(vs, fs);
    }

    public GProgram(String srcDir, ShaderParent vs, ShaderParent fs) {
        init(srcDir, vs, fs);
    }

    public GProgram() {
    }

    public GProgram<V, F> init(ShaderParent vs, ShaderParent fs) {
        init("src/main/java/", vs, fs);
        return this;
    }

    public GProgram<V, F> init(String srcDir, ShaderParent vs, ShaderParent fs) {
        pvs = GShaderNew.createShader(srcDir, vs, "vs");
        pfs = GShaderNew.createShader(srcDir, fs, "fs");
        oldVGen = pvs.generator;
        oldFGen = pfs.generator;
        asserts();

        this.vs = (V) vs;
        this.fs = (F) fs;

        shader = newShaderProgram();
        return this;
    }

    public GProgram<V, F> init(GShaderNew pvs, GShaderNew pfs) {
        this.pvs = pvs;
        this.pfs = pfs;
        oldVGen = pvs.generator;
        oldFGen = pfs.generator;
        asserts();

        this.vs = (V) pvs.generator.shaderGroovy;
        this.fs = (F) pfs.generator.shaderGroovy;

        shader = newShaderProgram();
        return this;
    }

    private void asserts() {
        if (geometryShaderString == null && pvs.generator.outputClass != pfs.generator.inputClass) {
            throw new Error("output of VS " + pvs.generator.outputClass.getName() + " must be same as input to FS " + pfs.generator.inputClass.getName());
        }
        if (!StandardFSInput.class.isAssignableFrom(pvs.generator.outputClass)) throw new Error("output of VS must extends StandardFSInput");
        if (!StandardFSOutput.class.isAssignableFrom(pfs.generator.outputClass)) throw new Error("output of FS must be StandardFrame class");

        Map<String, String> seenAt = hm();
        for (VertexAttrib a : pvs.generator.attributes) {
            String old = seenAt.put(a.getName(), "VS input");
            if (old != null) throw new Error("name clash for " + a.getName() + " at " + old + " and " + seenAt.get(a.getName()));
        }
        for (String a : pfs.generator.varyingFS) {
            String old = seenAt.put(a, "VS output");
            if (old != null) throw new Error("name clash for " + a + " at " + old + " and " + seenAt.get(a));
        }
        for (UniformVariable a : pvs.generator.uniforms) {
            String old = seenAt.put(a.name, "VS uniforms");
            if (old != null) {
                throw new Error("name clash for " + a.name + " at " + old + " and " + seenAt.get(a.name));
            }
        }
        for (Iterator<UniformVariable> iterator = pfs.generator.uniforms.iterator(); iterator.hasNext(); ) {
            UniformVariable a = iterator.next();
            String old = seenAt.put(a.name, "FS uniforms");
//            if (old != null) throw new Error("name clash for " + a.name + " at " + old + " and " + seenAt.get(a.name));
            if (old != null) {
                //TODO check same type
                //TODO prevent somehow from filling from FS
                iterator.remove();
            }
        }
    }

    public String geometryShaderString;
    private ShaderHandler newShaderProgram() {
        StopWatch sw = new StopWatch();


        ShaderHandler shader = new ShaderHandler();
        if (geometryShaderString != null) shader.geometryShaderString = geometryShaderString;

        String res = "";
        //for (String s : pfs.getVaryingFS()) res += s;
        //pvs.setResultSrc(res + pvs.getResultSrc());

        System.out.println(pvs.generator.resultSrc);
        System.out.println(pfs.generator.resultSrc);
        //
        for (UniformVariable u : pvs.generator.uniforms) shader.addVariables(u);
        for (VertexAttrib v : pvs.generator.attributes) shader.addVertexAttrib(v);

        for (UniformVariable u : pfs.generator.uniforms) shader.addVariables(u);
        //for (VertexAttrib v : pfs.getVarying()) shader.addVertexAttrib(v);

        shader.createFromIndices(pvs.shaderIndex, pfs.shaderIndex);

        System.out.println("linked in " + sw.stop());
        return shader;
    }

    private YMap<Class, YList<AbstractArrayStructure>> type2structure = hm();
    private YList<AbstractArrayStructure> getShaderSpecificStructure(Class clazz) {
        YList<AbstractArrayStructure> result = type2structure.get(clazz);
        if (result == null) {
            result = al();
            YList<Field> fields = ShaderGenerator.getFieldsForData(clazz);
            int stride = ReflectionVBO.getSizeOfType(clazz);
            int offset = 0;
            YSet<String> hasFields = hs();
            for (Field field : fields) {
                if (Modifier.isTransient(field.getModifiers())) continue;
                hasFields.add(field.getName());
                VertexAttrib shaderAttrib = shader.getVertexAttrib(field.getName());

//                if (shaderAttrib == null) throw new RuntimeException("shader has no attribute " + field.getName());

                if (shaderAttrib != null) result.add(new VBOVertexAttrib(shaderAttrib.getIndex(), shaderAttrib.getSize(), shaderAttrib.getType(), shaderAttrib.isNormalized(), stride, offset));

                if (field.getType() == float.class) {
                    if (shaderAttrib != null) assertType(shaderAttrib, 1, GL11.GL_FLOAT, field.getName());
                    offset += 1 * 4;
                } else if (field.getType() == Vec2f.class) {
                    if (shaderAttrib != null) assertType(shaderAttrib, 2, GL11.GL_FLOAT, field.getName());
                    offset += 2 * 4;
                } else if (field.getType() == Vec3f.class) {
                    if (shaderAttrib != null) assertType(shaderAttrib, 3, GL11.GL_FLOAT, field.getName());
                    offset += 3 * 4;
                } else if (field.getType() == Vec4f.class) {
                    if (shaderAttrib != null) assertType(shaderAttrib, 4, GL11.GL_FLOAT, field.getName());
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
        if (pvs.singleOwner) pvs.tick();
        if (pfs.singleOwner) pfs.tick();
        if (oldVGen != pvs.generator || oldFGen != pfs.generator) {
            //TODO asserts?
//            asserts();
            ShaderHandler np = newShaderProgram();
            shader.deleteProgram();
            shader = np;
            oldVGen = pvs.generator;
            oldFGen = pfs.generator;
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
        if (pvs.singleOwner) pvs.removeShaderFromCard();
        if (pfs.singleOwner) pfs.removeShaderFromCard();
        shader.deleteProgram();
    }
}
