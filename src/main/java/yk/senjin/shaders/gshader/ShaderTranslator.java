package yk.senjin.shaders.gshader;

import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.builder.AstBuilder;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.*;
import org.codehaus.groovy.control.CompilePhase;
import yk.jcommon.collections.YHashMap;
import yk.jcommon.collections.YList;
import yk.jcommon.collections.YMap;
import yk.jcommon.collections.YSet;
import yk.jcommon.utils.BadException;
import yk.jcommon.utils.Reflector;
import yk.jcommon.utils.Tab;
import yk.senjin.shaders.VertexAttrib;
import yk.senjin.shaders.gshader.analysis.GglslAnalyzer;
import yk.senjin.shaders.uniforms.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_INT;
import static yk.jcommon.collections.YArrayList.al;
import static yk.jcommon.collections.YArrayList.toYList;
import static yk.jcommon.collections.YHashMap.hm;
import static yk.jcommon.collections.YHashSet.hs;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 14/12/14 (ShaderGenerator on Groovy)
 * Time: 17:10
 * 
 * Translated to Java by Yuri Kravchik on 27.08.2020
 */
@SuppressWarnings("StringConcatenationInLoop")
public class ShaderTranslator {
    public final ShaderParent shaderGroovy;
    public final String gSrc;
    public String resultSrc;

    public String inputSuffix = "";
    public String outputSuffix = "";

    public YList<UniformVariable> uniforms = al();
    public YList<VertexAttrib> attributes = al();

    public YList<String> varyingFS = al();
    public Class inputClass;
    public Class outputClass;
    private String inputName;
    private String outputName;

    private ClassNode mainClassNode;
    private YMap<String, YSet<String>> caller2callee = hm();

    //TODO fill from ShaderParent by reflection
    private Set<String> glNames = hs("texture");

    public String shaderType;

    public ShaderTranslator(String gSrc, String srcPath, ShaderParent shaderGroovy, String shaderType, String inputSuffix, String outputSuffix) {
        this.gSrc = gSrc;
        this.shaderGroovy = shaderGroovy;
        this.shaderType = shaderType;
        this.inputSuffix = inputSuffix;
        this.outputSuffix = outputSuffix;
        this.resultSrc = translate(srcPath);
    }

    private String translate(String srcPath) {
        if (outputSuffix == null) throw BadException.die("outputSuffix should be defined at this time");
        if (inputSuffix == null) throw BadException.die("inputSuffix should be defined at this time");

        int version = shaderType.equals("gs") ? 400 : 130;
        String result = "#version " + version + "\n";
        result += "\n//autogenerated from " + srcPath + "\n\n";

        if (shaderType.equals("gs")) {//TODO from the code
            result += "layout(triangles) in;\n";
            result += "layout (triangle_strip, max_vertices=3) out;\n";
        }

        List<ASTNode> nodes;
        try {
            nodes = new AstBuilder().buildFromString(CompilePhase.INSTRUCTION_SELECTION, gSrc);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing");
        }

        mainClassNode = (ClassNode) nodes.get(1);

        for (MethodNode m : this.mainClassNode.getDeclaredMethods("main")) {
            if (Modifier.isStatic(m.getModifiers()) || (m.getModifiers() & 0x00001000) != 0) continue;
            Parameter input = m.getParameters()[0];
            inputName = input.getName();
            Parameter output = m.getParameters()[1];
            outputName = output.getName();
            inputClass = Reflector.classForName(input.getType().getName());
            outputClass = Reflector.classForName(output.getType().getName());
        }
        if (shaderType.equals("fs") && !StandardFSOutput.class.isAssignableFrom(outputClass)) throw new Error("Fragment shader must have ${StandardFSOutput.class.getSimpleName()} output type, but it is " + outputClass);


        if (inputClass.isArray()) {
            for (Field fn : getFieldsForData(inputClass)) {
                if (Modifier.isStatic(fn.getModifiers())) continue;
                if (Modifier.isTransient(fn.getModifiers())) continue;
                if (glNames.contains(fn.getName())) throw new Error("clash with gl names: " + fn.getName() + " in input data for " + shaderType);
                String type = translateType(fn.getType().getName());
                String name = withInputSuffix(fn.getName());
                result += "in " + type + " " + name + "[];\n";
            }
        } else

        if (inputClass != StandardVertexData.class) for (Field fn : getFieldsForData(inputClass)) {
            if (Modifier.isStatic(fn.getModifiers())) continue;
            if (Modifier.isTransient(fn.getModifiers())) continue;
            if (glNames.contains(fn.getName())) throw new Error("clash with gl names: " + fn.getName() + " in input data for " + shaderType);
            String type = translateType(fn.getType().getName());
            String name = withInputSuffix(fn.getName());
            result += "in " + type + " " + name + ";\n";
            if ("vs".equals(shaderType)) {
                if (type.equals("vec4")) attributes.add(new VertexAttrib(name, GL_FLOAT, 4));
                else if (type.equals("vec3")) attributes.add(new VertexAttrib(name, GL_FLOAT, 3));
                else if (type.equals("vec2")) attributes.add(new VertexAttrib(name, GL_FLOAT, 2));
                else if (type.equals("float")) attributes.add(new VertexAttrib(name, GL_FLOAT, 1));
                else if (type.equals("int")) attributes.add(new VertexAttrib(name, GL_INT, 1));
                else throw new RuntimeException("unknown attribute type " + type);
            } else if ("fs".equals(shaderType)) {
                varyingFS.add(name);
            } else throw BadException.shouldNeverReachHere();
        }

        if (outputClass != StandardFragmentData.class) for (Field fn : outputClass.getDeclaredFields()) {
            if (Modifier.isStatic(fn.getModifiers())) continue;
            if (Modifier.isTransient(fn.getModifiers())) continue;
            if (glNames.contains(fn.getName())) throw new Error("clash with gl names: " + fn.getName() + " in output data for " + shaderType);
            String type = translateType(fn.getType().getName());
            //if ("fs".equals(shaderType)) {
            //    result += "out " + type + " " + fn.getName() + ";\n";
            //} else if ("vs".equals(shaderType)) {
            //    result += "out " + type + " " + fn.getName() + "_fi;\n";
            //} else throw BadException.shouldNeverReachHere();
            result += "out " + type + " " + withOutputSuffix(fn.getName()) + ";\n";
        }

        for (FieldNode fn : this.mainClassNode.getFields()) {
            if (fn.getName().contains("$") || fn.getName().startsWith("__timeStamp") || fn.getName().equals("metaClass")) continue;
            if (glNames.contains(fn.getName())) throw new Error("clash with gl names: " + fn.getName() + " in uniforms for " + shaderType);

            String type = translateType(fn.getType().getName());
            result += stringForUniform(type, fn);

            if (type.equals("sampler2D")) {
                Sampler2D sampler = Reflector.get(shaderGroovy, fn.getName());
                sampler.name = fn.getName();
                uniforms.add(sampler);
            }
            else if (type.equals("vec2")) uniforms.add(new UniformRefVec2f(fn.getName(), shaderGroovy, fn.getName()));
            else if (type.equals("vec3")) uniforms.add(new UniformRefVec3f(fn.getName(), shaderGroovy, fn.getName()));
            else if (type.equals("vec4")) uniforms.add(new UniformRefVec4f(fn.getName(), shaderGroovy, fn.getName()));
            else if (type.equals("int")) uniforms.add(new UniformRefInt(fn.getName(), shaderGroovy, fn.getName()));
            else if (type.equals("float")) uniforms.add(new UniformRefFloat(fn.getName(), shaderGroovy, fn.getName()));
            else if (type.equals("int[]")) uniforms.add(new UniformRefIntArray(fn.getName(), shaderGroovy, fn.getName()));
            else if (type.equals("float[]")) uniforms.add(new UniformRefFloatArray(fn.getName(), shaderGroovy, fn.getName()));
            else if (type.equals("mat3")) uniforms.add(new UniformRefMatrix3(fn.getName(), shaderGroovy, fn.getName()));
            else if (type.equals("mat4")) uniforms.add(new UniformRefMatrix4(fn.getName(), shaderGroovy, fn.getName()));
            else throw new RuntimeException("unknown uniform type " + type);
        }

        YMap<String, String> method2body = hm();
        YSet<String> systemMethods = hs();
        for (Method m : ShaderParent.class.getDeclaredMethods()) {
            if (Modifier.isStatic(m.getModifiers()) && Modifier.isPublic(m.getModifiers())) systemMethods.add(m.getName());
        }
        caller2callee.put("'OpenGL'", hs("main"));

        YSet<String> watched = hs();
        while(true) {
            String toConvert = null;
            for (Map.Entry<String, YSet<String>> entry : caller2callee.entrySet()) {
                toConvert = entry.getValue().first(m -> !systemMethods.contains(m) && !watched.contains(m));
                if (toConvert != null) break;
            }
            if (toConvert == null) break;
            watched.add(toConvert);
            MethodNode methodNode = findByShortDesc(toConvert);
            if (methodNode == null) throw BadException.die("can't find method " + toConvert);
            caller2callee.putAll(GglslAnalyzer.calcCallers(methodNode));
        }

        YSet<MethodNode> methods = hs();
        methods.addAll(caller2callee.values().flatMap(cc -> cc).filter(cc -> !systemMethods.contains(cc)).map(c -> findByShortDesc(c)));

        //takes too long, TODO fix
        YHashMap<String, YSet<String>> modifiersMap = hm();
//        YHashMap<String, YSet<String>> modifiersMap = GglslAnalyzer.inferInOutModifiers(methods.toArray())
//        for (MethodNode method  : methods) {
//            def asserts = GglslAnalyzer.gAsserts(method, modifiersMap)
//            if (asserts.notEmpty()) {
//                println asserts.toString("\n")
//                BadException.die(asserts.toString())
//            }
//        }

        //println "analyzing 3: " + sw.stop()

        for (MethodNode methodNode : methods) {
            String body = "";
            if (methodNode.getName().equals("main")) {
                body += "\nvoid main(void) ";
            } else {

                YList<String> pp = al();
                for (int i = 0; i < methodNode.getParameters().length; i++) {//TODO patterns
                    Parameter p = methodNode.getParameters()[i];
                    YSet<String> modifiers = modifiersMap.get(methodNode.getName() + ":" + i);
                    String type = translateType(p.getType().getName());
                    pp.add("" + (modifiers == null ? "" : modifiers.toString(" ")) + " " + type + " " + p.getName());
                }

                body += "\n" + translateType(methodNode.getReturnType().getName()) + " " + methodNode.getName() + "(" + pp.toString(", ") + ") ";
            }
            body += translateExpression(methodNode.getCode());
            body += ("\n");
            method2body.put(methodNode.getName(), body);
        }

        YList<String> ordered = Orderer.orderMethods("main", method2body, caller2callee);
        if (ordered == null) throw BadException.die("can't find proper order (check long recursions)");//TODO show best guess cycle

        for (String m : ordered.reverse()) result += method2body.get(m) + "\n";

        return result;
    }

    private static YMap<Class, YList<Field>> CACHE = hm(StandardFragmentData.class, al(), Object.class, al(), StandardVertexData.class, al());
    public static YList<Field> getFieldsForData(Class inputClass) {
        YList<Field> result = CACHE.get(inputClass);
        if (inputClass.isArray()) {
            return getFieldsForData(inputClass.getComponentType());
        }
        if (result == null) {
            result = getFieldsForData(inputClass.getSuperclass()).with(inputClass.getDeclaredFields());
            CACHE.put(inputClass, result);
        }
        return result;
    }

    private MethodNode findByShortDesc(String s) {
        //TODO from imports
        for (MethodNode m : mainClassNode.getMethods()) {
            if ((m.getModifiers() & 0x00001000) != 0) continue;
            if (s.equals("main") && Modifier.isStatic(m.getModifiers())) continue;
            if (m.getName().equals(s)) return m;
        }
        return null;
    }

    private String stringForUniform(String type, FieldNode fieldNode) {
        String result = Modifier.isFinal(fieldNode.getModifiers()) ? "const " : "uniform ";
        if (type.endsWith("[]")) {
            String sizeExpression = translateExpression(((ArrayExpression) fieldNode.getInitialExpression()).getSizeExpression().get(0));
            result += type.substring(0, type.length() - 2) + " " + fieldNode.getName() + "[" + sizeExpression + "]";
        } else {
            result += type + " " + fieldNode.getName();
        }
        if (Modifier.isFinal(fieldNode.getModifiers())) result += " = " + translateExpression(fieldNode.getInitialExpression());
        return result + ";\n";
    }

    private Tab tab = new Tab();

    private String translateExpression(Object o) {
        if (o instanceof ConstantExpression) return translateExpression(((ConstantExpression)o));
        if (o instanceof DeclarationExpression) return translateExpression(((DeclarationExpression)o));
        if (o instanceof ClassExpression) return translateExpression(((ClassExpression)o));
        if (o instanceof BlockStatement) return translateExpression(((BlockStatement)o));
        if (o instanceof BooleanExpression) return translateExpression(((BooleanExpression)o));
        if (o instanceof CastExpression) return ((CastExpression)o).getType().getName() + "(" + translateExpression(((CastExpression)o).getExpression()) + ")";
        if (o instanceof ForStatement) return translateExpression(((ForStatement)o));
        if (o instanceof PostfixExpression) return translateExpression(((PostfixExpression)o).getExpression()) + ((PostfixExpression)o).getOperation().getText();
        if (o instanceof IfStatement) return translateExpression(((IfStatement)o));
        if (o instanceof VariableExpression) return ((VariableExpression)o).getName();
        if (o instanceof BinaryExpression) return translateExpression(((BinaryExpression)o));
        if (o instanceof UnaryMinusExpression) return "-" + translateExpression(((UnaryMinusExpression)o).getExpression());
        if (o instanceof BreakStatement) return "break";
        if (o instanceof EmptyExpression) return "";
        if (o instanceof ExpressionStatement) return translateExpression(((ExpressionStatement)o));
        if (o instanceof ReturnStatement) return "return " + translateExpression(((ReturnStatement)o).getExpression());
        if (o instanceof ArgumentListExpression) return toYList(((ArgumentListExpression)o).getExpressions()).map(ee -> translateExpression(ee)).toString(", ");
        if (o instanceof MethodCallExpression) return translateType(((MethodCallExpression)o).getMethodAsString()) + "(" + translateExpression(((MethodCallExpression)o).getArguments()) + ")";
        if (o instanceof StaticMethodCallExpression) return translateType(((StaticMethodCallExpression)o).getMethodAsString()) + "(" + translateExpression(((StaticMethodCallExpression)o).getArguments()) + ")";
        if (o instanceof PropertyExpression) return translateExpression(((PropertyExpression)o));

        return ":unknown:" + o;
    }

    private String translateExpression(ConstantExpression e) {
        return convertions.containsKey(e.getText()) ? convertions.get(e.getText()) : e.getText();
    }

    private String translateExpression(DeclarationExpression e) {
        String result = translateVarDecl((VariableExpression) e.getLeftExpression());
        if (!(e.getRightExpression() instanceof EmptyExpression)) result += " = " + translateExpression(e.getRightExpression());
        return result;
    }

    private String translateExpression(ClassExpression e) {
        if (e.getType().getName().endsWith("myengine.optiseq.states.shaders.gshader.ShaderFunctions")) return "";
        throw new Error("don't know what to do with " + e);
    }

    private String translateExpression(BlockStatement e) {
        String result = "{\n";
        tab.inc();
        for (Statement s : e.getStatements()) {
            result += tab.toString() + translateExpression(s);
            if (!(s instanceof IfStatement)) result += ";";
            result += "\n";
        }
        tab.dec();
        result += tab.toString() + "}";
        return result;
    }

    private String translateExpression(BooleanExpression e) {
        return translateExpression(e.getExpression());
    }

    private String translateExpression(ForStatement e) {
        List<Expression> expressions = ((ClosureListExpression) (e.getCollectionExpression())).getExpressions();
        String result = "for (" + translateExpression(expressions.get(0)) + "; " + translateExpression(expressions.get(1)) + "; " + translateExpression(expressions.get(2)) + ") {\n";
        result += tab.toString() + translateExpression(e.getLoopBlock());
        result += ";\n" + tab + "}";
        return result;
    }

    private String translateExpression(IfStatement e) {
        String result = "if (" + translateExpression(e.getBooleanExpression()) + ") " + translateExpression(e.getIfBlock());
        if (!(e.getIfBlock() instanceof BlockStatement)) result += ";";
        if (!(e.getElseBlock() instanceof EmptyStatement)) {
            result += " else " + translateExpression(e.getElseBlock());
            if (!(e.getElseBlock() instanceof BlockStatement)) result += ";";
        }
        return result;
    }

    private String translateVarDecl(VariableExpression e) {
        return translateType(e.getType().getName()) + " " + e.getName();
    }

    private String translateExpression(BinaryExpression e) {
        String opName = e.getOperation().getText();
        if (opName.equals("[")) return translateExpression(e.getLeftExpression()) + "[" + translateExpression(e.getRightExpression()) + "]";
        //TODO skip redundant ()
        if (opName.equals("=")) return translateExpression(e.getLeftExpression()) + " " + opName + " " + translateExpression(e.getRightExpression());
        return "(" + translateExpression(e.getLeftExpression()) + " " + opName + " " + translateExpression(e.getRightExpression()) + ")";
    }

    //TODO Groovy can 'return' simple expressions, but glsl can't. Fix it (for ex: void foo() {Vec4f(0)} - valid in Groovy but not valid in glsl and I didn't fixed it
    private String translateExpression(ExpressionStatement e) {
        return translateExpression(e.getExpression());
    }

    private String translateExpression(PropertyExpression e) {
        //access [] for geometry shader
        if (e.getObjectExpression() instanceof BinaryExpression) {
            BinaryExpression be = (BinaryExpression) e.getObjectExpression();
            if (be.getOperation().getText().equals("[")) {
                String left = translateExpression(be.getLeftExpression());
                if (left.equals(inputName)) {
                    return withInputSuffix(e.getPropertyAsString()) + "[" + translateExpression(be.getRightExpression()) + "]";
                }
            }
        }


        String obj = translateExpression(e.getObjectExpression());
        //TODO fix for "not in main case"
        //if (obj.equals(outputName) && shaderType.equals("vs")) return withOutputSuffix(e.getPropertyAsString());
        if (obj.equals(outputName)) return withOutputSuffix(e.getPropertyAsString());
        //TODO fix for "not in main case"
        //if (obj.equals(inputName) && shaderType.equals("fs")) return withOutputSuffix(e.getPropertyAsString());
        if (obj.equals(inputName)) return withInputSuffix(e.getPropertyAsString());
        //if (obj.equals(outputName)) return e.getPropertyAsString();
        //if (obj.equals(inputName)) return e.getPropertyAsString();
        if (obj.equals("")) return e.getPropertyAsString();
        if ((e.getObjectExpression() instanceof BinaryExpression)) return "(" + obj + ")." + e.getPropertyAsString();
        return obj + "." + e.getPropertyAsString();
    }

    private String withOutputSuffix(String prop) {
        for (Field f : StandardFragmentData.class.getDeclaredFields()) if (f.getName().equals(prop)) return prop;
        return prop + outputSuffix;
    }

    private String withInputSuffix(String prop) {
        for (Field f : StandardFragmentData.class.getDeclaredFields()) if (f.getName().equals(prop)) return prop;
        return prop + inputSuffix;
    }

    public static final YList<String> PRIMITIVES = al("int", "float");
    public static boolean isPrimitive(String oglType) {
        return PRIMITIVES.contains(oglType);
    }

    private static Map<String, String> convertions = hm(
            "[I", "int[]",
            "[F", "float[]",
            "Integer", "int",
            "Float", "float",
            "Matrix3", "mat3",
            "Matrix4", "mat4",
            "Vec2f", "vec2",
            "Vec3f", "vec3",
            "Vec4f", "vec4",
            "Sampler2D", "sampler2D"
    );
    public static String translateType(String groovyType) {
        String t = al(groovyType.split("\\.")).last();
        if (!convertions.containsKey(t)) return groovyType;
        return convertions.get(t);
    }


}
