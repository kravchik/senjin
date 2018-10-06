package yk.senjin.shaders.gshader;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.Util;
import yk.jcommon.collections.YList;
import yk.jcommon.collections.YMap;
import yk.jcommon.collections.YSet;
import yk.jcommon.fastgeom.Vec2f;
import yk.jcommon.fastgeom.Vec3f;
import yk.jcommon.fastgeom.Vec4f;
import yk.jcommon.utils.BadException;
import yk.senjin.AbstractState;
import yk.senjin.shaders.ShaderHandler;
import yk.senjin.shaders.VertexAttrib;
import yk.senjin.shaders.arraystructure.AbstractArrayStructure;
import yk.senjin.shaders.arraystructure.VBOVertexAttrib;
import yk.senjin.shaders.uniforms.UniformVariable;
import yk.senjin.vbo.AVbo;
import yk.senjin.vbo.AVboTyped;
import yk.senjin.vbo.TypeUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.Map;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static yk.jcommon.collections.YArrayList.al;
import static yk.jcommon.collections.YHashMap.hm;
import static yk.jcommon.collections.YHashSet.hs;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 14/12/14
 * Time: 18:46
 */
public class GProgram<V extends VertexShaderParent, F extends FragmentShaderParent> extends AbstractState {
    public V vs;
    public F fs;

    public ShaderHandler shaderState;

    public ShaderGenerator oldVGen;
    private ShaderGenerator oldFGen;
    private GShader pvs;
    private GShader pfs;

    //TODO get rid of both in favor of ShaderUser
    private AVbo currentVBO;
    private YList<AbstractArrayStructure> currentStructure;

    public static void assertType(VertexAttrib shaderAttrib, int size, int type, String name) {
        if (shaderAttrib.getSize() != size) throw new Error("wrong type for " + name);
        if (shaderAttrib.getType() != type) throw new Error("wrong type for " + name);
    }

    public <V extends VertexShaderParent, F extends FragmentShaderParent> GProgram<V, F> runtimeReload() {
        if (pvs != null && pvs.singleOwner) pvs.runtimeReload();
        if (pfs != null && pfs.singleOwner) pfs.runtimeReload();
        return (GProgram<V, F>) this;
    }

    public static GProgram initFragmentShaderOnly(String srcPath, ShaderParent fs) {
        return new GProgram().addFragmentShader(srcPath, fs).link();
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
        pvs = GShader.createShader(srcDir, vs, "vs");
        pfs = GShader.createShader(srcDir, fs, "fs");
        oldVGen = pvs.generator;
        oldFGen = pfs.generator;

        this.vs = (V) vs;
        this.fs = (F) fs;

        return link();
    }

    public GProgram addFragmentShader(String srcDir, ShaderParent fs) {
        pfs = GShader.createShader(srcDir, fs, "fs");
        oldFGen = pfs.generator;
        this.fs = (F) fs;
        return this;
    }

    public GProgram addVertexShader(String srcDir, ShaderParent vs) {
        pvs = GShader.createShader(srcDir, vs, "vs");
        oldVGen = pvs.generator;
        this.vs = (V) vs;
        return this;
    }

    public GProgram<V, F> link() {
        asserts();
        shaderState = newShaderProgram();
        return this;
    }

    public GProgram<V, F> init(GShader pvs, GShader pfs) {
        this.pvs = pvs;
        this.pfs = pfs;
        oldVGen = pvs.generator;
        oldFGen = pfs.generator;
        asserts();

        this.vs = (V) pvs.generator.shaderGroovy;
        this.fs = (F) pfs.generator.shaderGroovy;

        shaderState = newShaderProgram();
        return this;
    }

    private void asserts() {
        Map<String, String> seenAt = hm();
        if (pvs != null) {
            if (geometryShaderString == null
                    && (pfs == null && pvs.generator.outputClass != StandardFragmentData.class || pfs != null && pvs.generator.outputClass != pfs.generator.inputClass)) {
                throw new Error("output of VS " + pvs.generator.outputClass.getName() + " must be same as input to FS " + pfs.generator.inputClass.getName());
            }
            if (!StandardFragmentData.class.isAssignableFrom(pvs.generator.outputClass)) throw new Error("output of VS must extends StandardFSInput");
            for (VertexAttrib a : pvs.generator.attributes) {
                String old = seenAt.put(a.getName(), "VS input");
                if (old != null) throw new Error("name clash for " + a.getName() + " at " + old + " and " + seenAt.get(a.getName()));
            }
        }
        if (pfs != null) {
            if (!StandardFSOutput.class.isAssignableFrom(pfs.generator.outputClass)) throw new Error("output of FS must be StandardFrame class");
            for (String a : pfs.generator.varyingFS) {
                String old = seenAt.put(a, "VS output");
                if (old != null) throw new Error("name clash for " + a + " at " + old + " and " + seenAt.get(a));
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

    }

    public String geometryShaderString;
    private ShaderHandler newShaderProgram() {
        ShaderHandler shader = new ShaderHandler();
        if (geometryShaderString != null) shader.geometryShaderString = geometryShaderString;

        String res = "";
        //for (String s : pfs.getVaryingFS()) res += s;
        //pvs.setResultSrc(res + pvs.getResultSrc());

        if (pvs != null) {
            for (UniformVariable u : pvs.generator.uniforms) shader.addVariables(u);
            for (VertexAttrib v : pvs.generator.attributes) shader.addVertexAttrib(v);
        }

        if (pfs != null) for (UniformVariable u : pfs.generator.uniforms) shader.addVariables(u);
        //for (VertexAttrib v : pfs.getVarying()) shader.addVertexAttrib(v);

        shader.createFromIndices(pvs == null ? -1 : pvs.shaderIndex, pfs == null ? -1 : pfs.shaderIndex);

        return shader;
    }

    private YMap<Class, YList<AbstractArrayStructure>> type2structure = hm();
    public YList<AbstractArrayStructure> getShaderSpecificStructure(Class clazz) {
        YList<AbstractArrayStructure> result = type2structure.get(clazz);
        if (result != null) return result;
        result = al();
        YList<Field> fields = ShaderGenerator.getFieldsForData(clazz);
        int stride = TypeUtils.getComplexTypeSize(clazz);
        int offset = 0;
        YSet<String> hasFields = hs();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) continue;
            if (Modifier.isTransient(field.getModifiers())) continue;
            hasFields.add(field.getName());
            VertexAttrib shaderAttrib = shaderState.getVertexAttrib(field.getName());

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
        for (String attrib : shaderState.vertexAttribs.keySet()) if (!hasFields.contains(attrib)) throw new Error("buffer haven't field " + attrib);
        type2structure.put(clazz, result);
        return result;
    }

    public void setInput(AVboTyped vbo) {
        currentVBO = vbo;
        setInputClass(vbo.getInputType());
    }

    public void setInputClass(Class c) {
        currentStructure = getShaderSpecificStructure(c);
    }

    public void tick() {
        if (pvs != null && pvs.singleOwner) pvs.tick();
        if (pfs != null && pfs.singleOwner) pfs.tick();
        if ((pvs != null && oldVGen != pvs.generator) || (pfs != null && oldFGen != pfs.generator)) {
            //TODO asserts?
//            asserts();
            ShaderHandler np = newShaderProgram();
            shaderState.deleteProgram();
            shaderState = np;
            if (pvs != null) oldVGen = pvs.generator;
            if (pfs != null) oldFGen = pfs.generator;
        }
    }

    @Override
    public void enable() {
        tick();
        shaderState.enable();
        if (currentVBO != null) {//because we can use built in vertex attributes
            currentVBO.enable();
            Util.checkGLError();
            for (int i = 0; i < currentStructure.size(); i++) currentStructure.get(i).turnOn();
            Util.checkGLError();
        }
    }

    public void enable1() {
        if (pvs.singleOwner) pvs.tick();
        if (pfs.singleOwner) pfs.tick();
        if (oldVGen != pvs.generator || oldFGen != pfs.generator) {
            //TODO asserts?
//            asserts();
            ShaderHandler np = newShaderProgram();
            shaderState.deleteProgram();
            shaderState = np;
            oldVGen = pvs.generator;
            oldFGen = pfs.generator;
        }

        GL20.glUseProgram(shaderState.program);
        ShaderHandler.currentShader = shaderState;
    }

    public void enable2() {
        for (int i1 = 0; i1 < shaderState.uniforms.size(); i1++) shaderState.uniforms.get(i1).plug();
        if (currentVBO != null) {//because we can use built in vertex attributes
            currentVBO.enable();
            //glBindBuffer(GL_ARRAY_BUFFER, currentVBO.bufferId);
            for (int i = 0; i < currentStructure.size(); i++) currentStructure.get(i).turnOn();
        }
    }

    @Override
    public void disable() {
        if (currentVBO != null) {
            for (int i = 0; i < currentStructure.size(); i++) currentStructure.get(i).turnOff();
            glBindBuffer(GL_ARRAY_BUFFER, 0);
        }
        shaderState.disable();
    }

    @Override
    public void release() {
        if (pvs.singleOwner) pvs.removeShaderFromCard();
        if (pfs.singleOwner) pfs.removeShaderFromCard();
        shaderState.deleteProgram();
    }
}
