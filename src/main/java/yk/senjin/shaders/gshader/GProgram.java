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
import yk.senjin.shaders.ShaderHandler2;
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
    public static final String SRC_MAIN_JAVA = "src/main/java/";
    public V vs;
    public F fs;
    public ShaderParent gs;

    public ShaderHandler2 shaderState;

    private GShader pvs;
    private GShader pfs;
    private GShader pgs;

    //TODO get rid of both in favor of ShaderUser
    private AVbo currentVBO;
    private YList<AbstractArrayStructure> currentStructure;

    public <V extends VertexShaderParent, F extends FragmentShaderParent> GProgram<V, F> runtimeReload() {
        if (pvs != null && pvs.singleOwner) pvs.runtimeReload();
        if (pfs != null && pfs.singleOwner) pfs.runtimeReload();
        if (pgs != null && pgs.singleOwner) pgs.runtimeReload();
        return (GProgram<V, F>) this;
    }

    public static GProgram initFragmentShaderOnly(String srcPath, FragmentShaderParent fs) {
        return new GProgram().addFragmentShader(srcPath, fs).link();
    }

    public static GProgram initFromSrcMainJava(ShaderParent vs, ShaderParent fs) {
        return new GProgram(vs, fs).link();
    }

    public static GProgram initFromSrc(ShaderParent vs, ShaderParent fs) {
        return new GProgram("src/", vs, fs).link();
    }

    public static GProgram initFrom(String src, ShaderParent vs, ShaderParent fs) {
        return new GProgram(src, vs, fs).link();
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
        init(SRC_MAIN_JAVA, vs, fs);
        return this;
    }

    public GProgram<V, F> init(String srcDir, ShaderParent vs, ShaderParent fs) {
        pvs = new GShader(srcDir, this.vs = (V) vs, "vs");
        pfs = new GShader(srcDir, this.fs = (F) fs, "fs");
        return this;
    }

    public GProgram<V, F> addFragmentShader(FragmentShaderParent fs) {
        return addFragmentShader(SRC_MAIN_JAVA, fs);
    }

    public GProgram<V, F> addFragmentShader(String srcDir, FragmentShaderParent fs) {
        pfs = new GShader(srcDir, this.fs = (F) fs, "fs");
        return this;
    }

    public GProgram<V, F> addShader(VertexShaderParent vs) {
        return addVertexShader(vs);
    }

    public GProgram<V, F> addShader(FragmentShaderParent fs) {
        return addFragmentShader(fs);
    }

    public GProgram<V, F> addShader(GeometryShaderParent gs) {
        return addGeometryShader(gs);
    }

    public GProgram<V, F> addVertexShader(VertexShaderParent vs) {
        return addVertexShader(SRC_MAIN_JAVA, vs);
    }

    public GProgram<V, F> addVertexShader(String srcDir, VertexShaderParent vs) {
        pvs = new GShader(srcDir, this.vs = (V) vs, "vs");
        return this;
    }

    public GProgram<V, F> addGeometryShader(GeometryShaderParent gs) {
        return addGeometryShader(SRC_MAIN_JAVA, gs);
    }

    public GProgram<V, F> addGeometryShader(String srcDir, GeometryShaderParent gs) {
        pgs = new GShader(srcDir, this.gs = gs, "gs");
        return this;
    }

    public GProgram<V, F> link() {
        String suffix = "";
        if (pvs != null) {
            pvs.inputSuffix = suffix;
            suffix = "_vo";
            pvs.outputSuffix = suffix;
        }
        if (pgs != null) {
            pgs.inputSuffix = suffix;
            suffix = "_go";
            pgs.outputSuffix = suffix;
        }
        if (pfs != null) {
            pfs.inputSuffix = suffix;
            suffix = "";
            pfs.outputSuffix = suffix;
        }

        if (pvs != null) pvs.translate();
        if (pgs != null) pgs.translate();
        if (pfs != null) pfs.translate();

        asserts();
        shaderState = newShaderProgram();
        return this;
    }

    public GProgram<V, F> init(GShader pvs, GShader pfs) {
        this.pvs = pvs;
        this.pfs = pfs;
        asserts();

        this.vs = (V) pvs.generator.shaderGroovy;
        this.fs = (F) pfs.generator.shaderGroovy;

        shaderState = newShaderProgram();
        return this;
    }

    private void asserts() {
        //TODO assert VS->GS->FS data types
        Map<String, String> seenAt = hm();
        if (pvs != null) {
            //if (//geometryShaderString == null &&
            //        //TODO assert reflectively by names and types
            //        (pfs == null && pvs.generator.outputClass != StandardFragmentData.class
            //                || pfs != null && pvs.generator.outputClass != pfs.generator.inputClass)) {
            //    throw new Error("output of VS " + pvs.generator.outputClass.getName() + " must be same as input to FS " + pfs.generator.inputClass.getName());
            //}
            if (!StandardFragmentData.class.isAssignableFrom(pvs.generator.outputClass)) throw new Error("output of VS must extends StandardFSInput");
            for (VertexAttrib a : pvs.generator.attributes) {
                String old = seenAt.put(a.getName(), "VS input");
                if (old != null) throw new Error("name clash for " + a.getName() + " at " + old + " and " + seenAt.get(a.getName()));
            }
        }
        if (pfs != null) {
            if (!StandardFSOutput.class.isAssignableFrom(pfs.generator.outputClass)) throw new Error("output of FS must be " + StandardFSOutput.class);
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

    //public String geometryShaderString;
    private ShaderHandler2 newShaderProgram() {
        ShaderHandler2 shader = new ShaderHandler2();

        if (pvs != null) {
            for (UniformVariable u : pvs.generator.uniforms) shader.addVariables(u);
            for (VertexAttrib v : pvs.generator.attributes) shader.addVertexAttrib(v);
        }

        if (pfs != null) for (UniformVariable u : pfs.generator.uniforms) shader.addVariables(u);

        shader.createProgram();
        if (pvs != null) shader.attachShader(pvs.shaderIndex);
        if (pgs != null) shader.attachShader(pgs.shaderIndex);
        if (pfs != null) shader.attachShader(pfs.shaderIndex);
        shader.linkProgram();
        return shader;
    }

    //input data -> structure that maps it to this shader
    private YMap<Class, YList<AbstractArrayStructure>> type2structure = hm();
    public YList<AbstractArrayStructure> getShaderSpecificStructure(Class clazz, Map<String, String> dataToShaderNames) {
        YList<AbstractArrayStructure> result = type2structure.get(clazz);
        if (result != null) return result;
        result = al();
        YList<Field> dataFields = ShaderTranslator.getFieldsForData(clazz);
        int stride = TypeUtils.getComplexTypeSize(clazz);
        int offset = 0;
        YSet<String> hasFields = hs();
        for (Field dataField : dataFields) {
            if (Modifier.isStatic(dataField.getModifiers())) continue;
            if (Modifier.isTransient(dataField.getModifiers())) continue;
            String dataFieldName = dataField.getName();
            String vertexFieldName = dataToShaderNames == null ? dataFieldName : dataToShaderNames.getOrDefault(dataFieldName, dataFieldName);

            hasFields.add(vertexFieldName);
            VertexAttrib shaderAttrib = shaderState.getVertexAttrib(vertexFieldName);

            if (shaderAttrib != null) result.add(new VBOVertexAttrib(shaderAttrib.getIndex(), shaderAttrib.getSize(), shaderAttrib.getType(), shaderAttrib.isNormalized(), stride, offset));

            if (dataField.getType() == float.class) {
                assertShaderAttribType(shaderAttrib, 1, GL11.GL_FLOAT, vertexFieldName);
                offset += 1 * 4;
            } else if (dataField.getType() == Vec2f.class) {
                assertShaderAttribType(shaderAttrib, 2, GL11.GL_FLOAT, vertexFieldName);
                offset += 2 * 4;
            } else if (dataField.getType() == Vec3f.class) {
                assertShaderAttribType(shaderAttrib, 3, GL11.GL_FLOAT, vertexFieldName);
                offset += 3 * 4;
            } else if (dataField.getType() == Vec4f.class) {
                assertShaderAttribType(shaderAttrib, 4, GL11.GL_FLOAT, vertexFieldName);
                offset += 4 * 4;
            } else throw BadException.die("unknown VS input field type: " + dataField.getType());
        }
        if (!hasFields.containsAll(shaderState.vertexAttribs.keySet())) throw new Error(String.format("buffer contains %s fields, but shader requires %s", hasFields, shaderState.vertexAttribs.keySet()));
        type2structure.put(clazz, result);
        return result;
    }

    public void setInput(AVboTyped vbo) {
        currentVBO = vbo;
        YList<AbstractArrayStructure> result = type2structure.get(vbo.getInputType());
        if (result != null) currentStructure = result;
        else currentStructure = getShaderSpecificStructure(vbo.getInputType(), null);
    }

    public void tick() {
        if (pvs != null && pvs.singleOwner) pvs.tick();
        if (pfs != null && pfs.singleOwner) pfs.tick();
        if ((pvs != null && pvs.oldGenerator != pvs.generator) || (pfs != null && pfs.oldGenerator != pfs.generator)) {
            //TODO asserts?
//            asserts();
            ShaderHandler2 np = newShaderProgram();
            shaderState.deleteProgram();
            shaderState = np;
            if (pvs != null) pvs.oldGenerator = pvs.generator;
            if (pfs != null) pfs.oldGenerator = pfs.generator;
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
        if (pvs.oldGenerator != pvs.generator || pfs.oldGenerator != pfs.generator) {
            //TODO asserts?
//            asserts();
            ShaderHandler2 np = newShaderProgram();
            shaderState.deleteProgram();
            shaderState = np;
            pvs.oldGenerator = pvs.generator;
            pfs.oldGenerator = pfs.generator;
        }

        GL20.glUseProgram(shaderState.program);
        ShaderHandler2.currentShader = shaderState;
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

    private static void assertShaderAttribType(VertexAttrib shaderAttrib, int size, int type, String name) {
        if (shaderAttrib == null) return;//no such attribute in the shader, ignoring
        if (shaderAttrib.getSize() != size) throw new Error("wrong type for " + name);
        if (shaderAttrib.getType() != type) throw new Error("wrong type for " + name);
    }

}
