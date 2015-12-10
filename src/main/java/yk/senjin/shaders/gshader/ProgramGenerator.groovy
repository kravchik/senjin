package yk.senjin.shaders.gshader

import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.*
import org.codehaus.groovy.control.CompilePhase
import yk.jcommon.collections.YList
import yk.jcommon.collections.YMap
import yk.jcommon.collections.YSet
import yk.jcommon.utils.BadException
import yk.jcommon.utils.IO
import yk.jcommon.utils.Reflector
import yk.jcommon.utils.Tab
import yk.senjin.shaders.UniformVariable
import yk.senjin.shaders.VertexAttrib
import yk.senjin.shaders.gshader.analysis.GglslAnalyzer

import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier

import static org.lwjgl.opengl.GL11.GL_FLOAT
import static org.lwjgl.opengl.GL11.GL_INT
import static yk.jcommon.collections.YArrayList.al
import static yk.jcommon.collections.YArrayList.toYList
import static yk.jcommon.collections.YHashMap.hm
import static yk.jcommon.collections.YHashSet.hs

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 14/12/14
 * Time: 17:10
 */
class ProgramGenerator {
    public final ShaderParent shaderGroovy
    public final String srcPath
    public String resultSrc

    public YList<UniformVariable> uniforms = al()
    public YList<VertexAttrib> attributes = al()

    public YList<String> varyingFS = al()
    public Class inputClass
    public Class outputClass
    private String inputName
    private String outputName

    private mainClassNode
    private String currentMethod
    private YMap<String, YSet<String>> caller2callee = hm()

    ProgramGenerator(String srcPath, ShaderParent shaderGroovy, String programType) {
        this.shaderGroovy = shaderGroovy
        this.srcPath = srcPath
        this.resultSrc = translate(programType)

    }
    //TODO fill from ShaderParent by reflection
    private Set<String> glNames = hs("texture")

    private String programType;

    private String translate(String programType) {
        String result = "#version 130\n"
        result += "\n//autogenerated from " + srcPath + "\n\n"
        this.programType = programType;

        List<ASTNode> nodes = new AstBuilder().buildFromString(CompilePhase.INSTRUCTION_SELECTION, IO.readFile(srcPath))

        mainClassNode = (ClassNode) nodes[1]

        for (MethodNode m in this.mainClassNode.getDeclaredMethods("main")) {
            if (Modifier.isStatic(m.modifiers) || (m.modifiers & 0x00001000) != 0) continue
            Object input = m.parameters[0]
            inputName = input.name
            Object output = m.parameters[1]
            outputName = output.name
            inputClass = Class.forName(input.type.name)
            outputClass = Class.forName(output.type.name)
        }
        if (programType.equals("fs") && outputClass != StandardFrame.class) throw new Error("Fragment shader must have ${StandardFrame.class.getSimpleName()} output type, but it is " + outputClass)


        if (inputClass != StandardVSInput.class) for (fn in inputClass.getDeclaredFields()) {
            if (Modifier.isStatic(fn.getModifiers())) continue;
            if (Modifier.isTransient(fn.getModifiers())) continue
            if (glNames.contains(fn.name)) throw new Error("clash with gl names: " + fn.name + " in input data for " + programType)
            def type = translateType(fn.type.name)
            if ("vs".equals(programType)) {
                result += "in " + type + " " + fn.name + ";\n"
                if (type.equals("vec4")) attributes.add(new VertexAttrib(fn.name, GL_FLOAT, 4))
                else if (type.equals("vec3")) attributes.add(new VertexAttrib(fn.name, GL_FLOAT, 3))
                else if (type.equals("vec2")) attributes.add(new VertexAttrib(fn.name, GL_FLOAT, 2));
                else if (type.equals("float")) attributes.add(new VertexAttrib(fn.name, GL_FLOAT, 1));
                else if (type.equals("int")) attributes.add(new VertexAttrib(fn.name, GL_INT, 1));
                else throw new RuntimeException("unknown varying type " + type)
            } else if ("fs".equals(programType)) {
                result += "in " + type + " " + fn.name + "_fi;\n"
                varyingFS.add(fn.name + "_fi")
            } else BadException.shouldNeverReachHere()
        }

        for (fn in outputClass.getDeclaredFields()) {
            if (Modifier.isStatic(fn.getModifiers())) continue;
            if (Modifier.isTransient(fn.getModifiers())) continue
            if (glNames.contains(fn.name)) throw new Error("clash with gl names: " + fn.name + " in output data for " + programType)
            def type = translateType(fn.type.name)
            if ("fs".equals(programType)) {
                result += "out " + type + " " + fn.name + ";\n"
            } else if ("vs".equals(programType)) {
                result += "out " + type + " " + fn.name + "_fi;\n"
            } else BadException.shouldNeverReachHere()
        }

        for (o in this.mainClassNode.getFields()) {
            FieldNode fieldNode = o
            if (fieldNode.name.contains("\$") || fieldNode.name.startsWith("__timeStamp") || fieldNode.name.equals("metaClass")) continue
            if (glNames.contains(fieldNode.name)) throw new Error("clash with gl names: " + fieldNode.name + " in uniforms for " + programType)

            def type = translateType(fieldNode.type.name)
            result += stringForUniform(type, fieldNode)

            if (type.equals("sampler2D")) {
                def sampler = (Sampler2D) Reflector.get(shaderGroovy, fieldNode.name)
                sampler.name = fieldNode.name
                uniforms.add(sampler)
            }
            else if (type.equals("vec2")) uniforms.add(new UniformRefVec2f(fieldNode.name, shaderGroovy, fieldNode.name))
            else if (type.equals("vec3")) uniforms.add(new UniformRefVec3f(fieldNode.name, shaderGroovy, fieldNode.name))
            else if (type.equals("vec4")) uniforms.add(new UniformRefVec4f(fieldNode.name, shaderGroovy, fieldNode.name))
            else if (type.equals("int")) uniforms.add(new UniformRefInt(fieldNode.name, shaderGroovy, fieldNode.name))
            else if (type.equals("float")) uniforms.add(new UniformRefFloat(fieldNode.name, shaderGroovy, fieldNode.name))
            else if (type.equals("int[]")) uniforms.add(new UniformRefIntArray(fieldNode.name, shaderGroovy, fieldNode.name))
            else if (type.equals("float[]")) uniforms.add(new UniformRefFloatArray(fieldNode.name, shaderGroovy, fieldNode.name))
            else if (type.equals("mat3")) uniforms.add(new UniformRefMatrix3(fieldNode.name, shaderGroovy, fieldNode.name))
            else if (type.equals("mat4")) uniforms.add(new UniformRefMatrix4(fieldNode.name, shaderGroovy, fieldNode.name))
            else throw new RuntimeException("unknown uniform type " + type)
        }

        YMap<String, String> method2body = hm();
        YSet<String> systemMethods = hs();
        for (Method m : ShaderParent.getDeclaredMethods()) {
            if (Modifier.isStatic(m.modifiers) && Modifier.isPublic(m.modifiers)) systemMethods.add(m.name)
        }
        caller2callee.put("'OpenGL'", hs("main"))

        YSet<String> watched = hs();
        while(true) {
            String toConvert = null
            for (Map.Entry<String, YSet<String>> entry : caller2callee.entrySet()) {
                toConvert = entry.getValue().first({ m -> !systemMethods.contains(m) && !watched.contains(m) })
                if (toConvert != null) break
            }
            if (toConvert == null) break
            watched.add(toConvert)
            MethodNode methodNode = findByShortDesc(toConvert)
            if (methodNode == null) BadException.die("can't find method " + toConvert)
            caller2callee.putAll(GglslAnalyzer.calcCallers(methodNode))
        }

        YSet<MethodNode> methods = hs();
        methods.addAll(caller2callee.values().flatMap{cc -> cc}.filter{cc -> !systemMethods.contains(cc)}.map{c->findByShortDesc(c)})
        def modifiersMap = GglslAnalyzer.inferInOutModifiers(methods.toArray())

        for (MethodNode method  : methods) {
            def asserts = GglslAnalyzer.gAsserts(method, modifiersMap)
            if (asserts.notEmpty()) {
                println asserts.toString("\n")
                BadException.die(asserts.toString())
            }
        }


        for (MethodNode methodNode : methods) {
            String body = ""
            if (methodNode.name == "main") {
                body += "\nvoid main(void) "
            } else {

                YList<String> pp = al()
                for (int i = 0; i < methodNode.parameters.length; i++) {//TODO patterns
                    Parameter p = methodNode.parameters[i];
                    def modifiers = modifiersMap.get(methodNode.name + ":" + i)
                    def type = translateType(p.getType().name)
                    pp.add("" + (modifiers == null ? "" : modifiers.toString(" ")) + " " + type + " " + p.name)
                }

                body += "\n" + translateType(methodNode.returnType.name) + " " + methodNode.name + "(" + pp.toString(", ") + ") "
            }
            currentMethod = getMethodShortDesc(methodNode)
            body += translateExpression(methodNode.code)
            body += ("\n")
            method2body.put(methodNode.name, body)
        }

        def ordered = Orderer.orderMethods("main", method2body, caller2callee)
        if (ordered == null) BadException.die("can't find proper order (check long recursions)")//TODO show best guess cycle

        for (String m : ordered.reverse()) result += method2body.get(m) + "\n"

        result
    }

    private MethodNode findByShortDesc(String s) {
        //TODO from imports
        for (m in ((ClassNode)mainClassNode).getMethods()) {
            if ((m.modifiers & 0x00001000) != 0) continue
            if (s.equals("main") && Modifier.isStatic(m.modifiers)) continue
            if (m.name == s) return m
        }
        return null
    }

    private String stringForUniform(String type, FieldNode fieldNode) {
        String result = Modifier.isFinal(fieldNode.modifiers) ? "const " : "uniform "
        if (type.endsWith("[]")) {
            def sizeExpression = translateExpression(((ArrayExpression) fieldNode.initialExpression).sizeExpression.get(0))
            result += type.substring(0, type.length() - 2) + " " + fieldNode.name + "[" + sizeExpression + "]"
        } else {
            result += type + " " + fieldNode.name
        }
        if (Modifier.isFinal(fieldNode.modifiers)) result += " = " + translateExpression(((FieldNode) fieldNode).initialExpression)
        result + ";\n"
    }

    private Tab tab = new Tab()

    private String translateExpression(Object o) {//overloading resolved at runtime
        return ":unknown:"+o
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    private String translateExpression(ConstantExpression e) {
        return convertions.containsKey(e.text) ? convertions.get(e.text) : e.text
    }

    private String translateExpression(DeclarationExpression e) {
        def result = translateVarDecl((VariableExpression) e.leftExpression)
        if (!(e.rightExpression instanceof EmptyExpression)) result += " = " + translateExpression(e.rightExpression)
        return result
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    private String translateExpression(ClassExpression e) {
        if (e.type.name.endsWith("myengine.optiseq.states.shaders.gshader.ShaderFunctions")) return ""
        throw new Error("don't know what to do with " + e)
    }

    private String translateExpression(BlockStatement e) {
        String result = "{\n"
        tab.inc()
        for (Statement s : e.statements) {
            result += tab.toString() + translateExpression(s)
            if (!(s instanceof IfStatement)) result += ";"
            result += "\n"
        }
        tab.dec()
        result += tab.toString() + "}"
        return result
    }

    private String translateExpression(BooleanExpression e) {
        return translateExpression(e.expression)
    }

    private String translateExpression(CastExpression e) {
        return e.type.name + "(" + translateExpression(e.expression) + ")"
    }

    private String translateExpression(ForStatement e) {
        def expressions = ((ClosureListExpression) (e.collectionExpression)).expressions
        String result = "for (" + translateExpression(expressions.get(0)) + "; " + translateExpression(expressions.get(1)) + "; " + translateExpression(expressions.get(2)) + ") {\n"
        result += tab.toString() + translateExpression(e.loopBlock)
        result += ";\n" + tab + "}"
        return result
    }

    private String translateExpression(PostfixExpression e) {
        return translateExpression(e.expression) + e.operation.getText()
    }

    private String translateExpression(IfStatement e) {
        def result = "if (" + translateExpression(e.booleanExpression) + ") " + translateExpression(e.ifBlock)
        if (!(e.ifBlock instanceof BlockStatement)) result += ";"
        if (!(e.elseBlock instanceof EmptyStatement)) {
            result += " else " + translateExpression(e.elseBlock)
            if (!(e.elseBlock instanceof BlockStatement)) result += ";"
        }
        return result
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    private String translateExpression(VariableExpression e) {
        return e.name
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    private String translateVarDecl(VariableExpression e) {
        return translateType(e.getType().name) + " " + e.name
    }


    private String translateExpression(BinaryExpression e) {
        def opName = e.operation.getText()
        if (opName == "[") return translateExpression(e.leftExpression) + "[" + translateExpression(e.rightExpression) + "]"
        //TODO skip redundant ()
        if (opName == "=") return translateExpression(e.leftExpression) + " " + opName + " " + translateExpression(e.rightExpression)
        return "(" + translateExpression(e.leftExpression) + " " + opName + " " + translateExpression(e.rightExpression) + ")"
    }

    private String translateExpression(UnaryMinusExpression e) {
        return "-" + translateExpression(e.expression)
    }

    private String translateExpression(BreakStatement e) {
        return "break"
    }

    private String translateExpression(EmptyExpression e) {
        return ""
    }

    //TODO Groovy can 'return' simple expressions, but glsl can't. Fix it (for ex: void foo() {Vec4f(0)} - valid in Groovy but not valid in glsl and I didn't fixed it
    private String translateExpression(ExpressionStatement e) {
        return translateExpression(e.expression)
    }

    private String translateExpression(ReturnStatement e) {
        return "return " + translateExpression(e.expression)
    }

    private String translateExpression(ArgumentListExpression e) {
        return toYList(e.getExpressions()).map{ee -> translateExpression(ee)}.toString(", ")
    }

    private static String getMethodShortDesc(MethodCall e) {
        def name = translateType(e.methodAsString)
        //TODO args
        return name
    }

    private static String getMethodShortDesc(MethodNode e) {
        def name = translateType(e.name)
        //TODO args
        return name
    }

    private String translateExpression(MethodCallExpression e) {
        def name = translateType(e.methodAsString)
        return name + "(" + translateExpression(e.arguments) + ")"
    }

    private String translateExpression(StaticMethodCallExpression e) {
        def name = translateType(e.methodAsString)
        return name + "(" + translateExpression(e.arguments) + ")"
    }

    private String translateExpression(PropertyExpression e) {
        String obj = translateExpression(e.objectExpression)
        //TODO fix for "not in main case"
        if (obj.equals(outputName) && programType.equals("vs")) return fiName(e.propertyAsString)
        //TODO fix for "not in main case"
        if (obj.equals(inputName) && programType.equals("fs")) return fiName(e.propertyAsString)
        if (obj.equals(outputName)) return e.propertyAsString
        if (obj.equals(inputName)) return e.propertyAsString
        if (obj.equals("")) return e.propertyAsString
        if ((e.objectExpression instanceof BinaryExpression)) return "(" + obj + ")." + e.propertyAsString
        return obj + "." + e.propertyAsString
    }

    private static String fiName(String prop) {
        for (Field f : StandardFSInput.class.getDeclaredFields()) if (f.name.equals(prop)) return prop
        return prop + "_fi";
    }

    public static final YList<String> PRIMITIVES = al("int", "float")
    public static boolean isPrimitive(String oglType) {
        return PRIMITIVES.contains(oglType)
    }

    static Map<String, String> convertions = hm(
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
    )
    public static String translateType(String groovyType) {
        def t = groovyType.split("\\.").last()
        if (!convertions.containsKey(t)) return groovyType
//        if (!convertions.containsKey(t)) throw new RuntimeException("unknown type " + t)
        return convertions.get(t)
    }


}
