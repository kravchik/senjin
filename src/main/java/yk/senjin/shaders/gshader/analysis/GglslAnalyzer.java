package yk.senjin.shaders.gshader.analysis;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.builder.AstBuilder;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.IfStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.control.CompilePhase;
import org.junit.Test;
import yk.jcommon.match2.*;
import yk.jcommon.probe.Probe;
import yk.jcommon.probe.State;
import yk.jcommon.search.SSearch;
import yk.jcommon.utils.IO;
import yk.ycollections.*;

import static org.junit.Assert.assertEquals;
import static yk.jcommon.match2.MatcherShortNames.*;
import static yk.senjin.shaders.gshader.ShaderTranslator.isPrimitive;
import static yk.senjin.shaders.gshader.ShaderTranslator.translateType;
import static yk.ycollections.YArrayList.al;
import static yk.ycollections.YHashMap.hm;
import static yk.ycollections.YHashSet.hs;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 28/10/15
 * Time: 16:38
 */
public class GglslAnalyzer {

    //primitive parameters CAN be overwritten
    //primitive parameters ALL in

    //in vector
    // can't rewrite  (could, but its strange if we rewrote it but still can't modify, and I don't want do flow analysis)
    // can't modify
    // can't return (make explicit copy)

    //out vector forbidden

    //inout vector
    // can't rewrite (use explicit copyFrom)
    // can modify
    // can't return  (use explicit copy)

    //copyFrom -> glsl -> translate as '='
    //return explicit copy -> glsl -> translate as 'return without copy'


    //SUMMARY
    //primitives - do nothing
    //vector param
    // can't be rewritten
    // modified ? inout : in
    // can't be returned

    public static final YArrayList<Object> G_BODY_ACCESSORS = al(
            i(var("access")),
            p("methodsList", var("access")),
            p(MethodNode.class, "code", var("access")),
            p(MethodCallExpression.class, "getReceiver", var("access")),
            p(BlockStatement.class, "getStatements", var("access")),
            p(ExpressionStatement.class, "expression", var("access")),
            p(BinaryExpression.class, "leftExpression", var("access")),
            p(BinaryExpression.class, "rightExpression", var("access")),
            p(DeclarationExpression.class, "getLeftExpression", var("access")),
            p(DeclarationExpression.class, "getRightExpression", var("access")),
            p(UnaryMinusExpression.class, "getExpression", var("access")),
            p(UnaryPlusExpression.class, "getExpression", var("access")),
            p(ReturnStatement.class, "getExpression", var("access")),
            p(ConstructorCallExpression.class, "arguments", p("expressions", i(var("access")))),
            p(IfStatement.class, "booleanExpression", var("access")),
            p(IfStatement.class, "ifBlock", var("access")),
            p(IfStatement.class, "elseBlock", var("access"))
    );
    public static final YList<Object> G_METHOD_ACCESSORS = al(i(var("access")), p("methodsList", var("access")));

    public static final Object G_AS_ARG_PATTERN = stairs(
            deeper(G_BODY_ACCESSORS),
            p(MethodCallExpression.class, "getMethod", p("getText", var("callMethodName")), "getArguments"),
            p(ArgumentListExpression.class, "expressions"),
            i(var("argIndex")),
            p("variable", var("name")));

    public static final Object G_FIELD_AS_ARG_PATTERN = stairs(
            deeper(G_BODY_ACCESSORS),
            p(MethodCallExpression.class,
                    "getMethod", p("getText", var("METHOD_NAME")),
                    "getArguments"),
            p(ArgumentListExpression.class, "expressions"),
            i(var("ARG_INDEX")),
            p(PropertyExpression.class,
                    "getObjectExpression", p(VariableExpression.class,
                            "variable", var("OBJ_NAME")),
                    "getProperty", p("value", var("FIELD_NAME"))));

    public static final Object G_WRITE_FIELD_PATTERN = stairs(
            deeper(G_BODY_ACCESSORS),
            p(BinaryExpression.class, "operation", p("text", "="), "leftExpression"),
            p(PropertyExpression.class, "objectExpression"),
            p(VariableExpression.class, "variable", "OBJ_NAME"));

    @Test
    public void testCalcCallers() {
        String src = IO.readFile("src/main/java/yk/senjin/shaders/gshader/analysis/TestGroovyClass.groovy");
        assertEquals("{foo=[foo1], foo1=[foo2]}", calcCallers(new AstBuilder().buildFromString(CompilePhase.INSTRUCTION_SELECTION, src)).toString());

        src = IO.readFile("/home/yuri/1/public/senjin/src/main/java/yk/senjin/shaders/gshader/examples/blend/BlendF.groovy");

        Probe probe = new Probe(new AstBuilder().buildFromString(CompilePhase.INSTRUCTION_SELECTION, src), p(MethodCallExpression.class, "getMethod", p("getText", "foo"))){{
            skipMethods = hs("getMethodAsString", "getFirstStatement", "getVariableScope", "getDeclaringClass");
        }};
        SSearch.Node<State> node = probe.nextSolution(100000);
        if (node != null) System.out.println("!" + node.state.stackTrace.toString("\n"));

        src = IO.readFile("src/main/java/yk/senjin/shaders/gshader/analysis/TestGroovyClass2.groovy");
        assertEquals("{foo=[foo1]}", calcCallers(new AstBuilder().buildFromString(CompilePhase.INSTRUCTION_SELECTION, src)).toString());
    }

    public static YMap<String, YSet<String>> calcCallers(Object data) {
        YMap<String, YSet<String>> result = hm();
        YSet<YMap<String, Object>> methods = new Matcher().match(data, stairs(deeper(G_METHOD_ACCESSORS), var("method"), p(MethodNode.class, "name"), var("methodName")));
        for (YMap<String, Object> mp : methods) {
            Object methodCalls = stairs(deeper(G_BODY_ACCESSORS), p(MethodCallExpression.class, "getMethod"), p("getText"), var("callMethodName"));
            for (YMap<String, Object> mcp : new Matcher().match(mp.get("method"), methodCalls)) {
                result.put((String) mp.get("methodName"), result.getOr((String) mp.get("methodName"), hs()).with((String) mcp.get("callMethodName")));
            }

            //TODO statics in other class
            methodCalls = stairs(deeper(G_BODY_ACCESSORS), p(StaticMethodCallExpression.class, "getMethod"), var("callMethodName"));
            for (YMap<String, Object> mcp : new Matcher().match(mp.get("method"), methodCalls)) {
                result.put((String) mp.get("methodName"), result.getOr((String) mp.get("methodName"), hs()).with((String) mcp.get("callMethodName")));
            }
        }
        return result;
    }

    @Test
    public void testArgModifiersCollect() {
        String src = IO.readFile("src/main/java/yk/senjin/shaders/gshader/analysis/BlendF.groovy");
        YHashMap<String, YSet<String>> modifiers = inferInOutModifiers(new AstBuilder().buildFromString(CompilePhase.INSTRUCTION_SELECTION, src));
        assertEquals("{main:1=[inout], testFoo:0=[inout]}", modifiers.toString());
    }

    //SUMMARY
    //primitives - do nothing
    //vector param
    // can't be rewritten     //done in gAsserts
    // can't be returned      //in gAsserts
    // modified ?  =>   inout : in

    public static YHashMap<String, YSet<String>> inferInOutModifiers(Object nodes) {
        YList<Object> accessors = YArrayList.al(new MatchByIndex(new MatchVar("access")));
        YList<Object> accessors2 = accessors.with(MatchProperty.p("methodsList", new MatchVar("access")));
        YSet<YMap<String, Object>> method = new Matcher().match(nodes, new MatchDeeper(accessors2, var("method", p(MethodNode.class, "name", var("methodName")))));
        YHashMap<String, YSet<String>> modifiers = hm();
        int oldModifiersCount = 0;
        while (true) {

            for (YMap<String, Object> m : method) {
//                System.out.println("method: " + m.get("methodName"));
                getArgsModifiers((MethodNode) m.get("method"), modifiers);
//                System.out.println(modifiers);
            }
            int newModifiersCount = modifiers.values().reduce(0, (i, ss) -> i + ss.size());
            if (newModifiersCount == oldModifiersCount) break;
//            System.out.println("AND AGAIN");
            oldModifiersCount = newModifiersCount;
        }
        return modifiers;
    }

    public static YMap<String, YSet<String>> getArgsModifiers(MethodNode methodNode, YMap<String, YSet<String>> result) {

        Object fieldWritePattern = new MatchDeeper(G_BODY_ACCESSORS,
                p(BinaryExpression.class, "operation", p("text", "="),
                        "leftExpression", p(PropertyExpression.class, "objectExpression", p(VariableExpression.class, "variable", var("name")), "property", p("value", var("value")))
                ));

        YSet<YMap<String, Object>> paramMm = new Matcher().match(methodNode, p("parameters", i(var("paramIndex"), p("name", var("paramName")))));
        for (YMap<String, Object> paramM : paramMm) if (!isPrimitive(translateType((String) paramM.get("paramName")))) {
            String paramName = (String) paramM.get("paramName");
            String paramFullName = methodNode.getName() + ":" + paramM.get("paramIndex");
            if (new Matcher().match(methodNode, fieldWritePattern, hm("name", paramName)).notEmpty()) result.put(paramFullName, result.getOr(paramFullName, hs()).with("inout"));

            YSet<YMap<String, Object>> asArgMm = new Matcher().match(methodNode, G_AS_ARG_PATTERN, hm("name", paramName));
            for (YMap<String, Object> asArgM : asArgMm) {
                Object callMethodName = asArgM.get("callMethodName");
                Object asArgIndex = asArgM.get("argIndex");
                result.put(paramFullName, result.getOr(paramFullName, hs()).withAll(result.getOr(callMethodName + ":" + asArgIndex, hs())));
            }
        }
        return result;
    }

    @Test
    public void testAsserts() {
        String src = IO.readFile("src/main/java/yk/senjin/shaders/gshader/analysis/BlendF.groovy");
        assertEquals("(method main) field 'kSize' is rewritten, but it's forbidden in gglsl\n" +
                        "(method main) field 'direction' is used as inout arg, but it's forbidden in gglsl\n" +
                        "(method testFoo) parameter 'modified' is rewritten, but it's forbidden in gglsl (use explicity copyFrom)\n" +
                        "(method foo) parameter 'd' is used as return value, but it's forbidden in gglsl (use explicity copy)\n" +
                        "(method foo) field 'direction' is used as return value, but it's forbidden in gglsl",
                gAsserts(new AstBuilder().buildFromString(CompilePhase.INSTRUCTION_SELECTION, src), hm("testFoo:0", hs("in", "inout"))).toString("\n"));

    }

    //ShaderInOut can't write         //done in gAsserts
    //ShaderInOut can't read
    //ShaderInOut can't as arg        //TODO just forbid non-glsl types
    //ShaderIn.x  can't write
    //ShaderIn.x  can't as inout-arg

    //ShaderOut.x can write
    //ShaderOut.x can as inout-arg

    public static YList<String> gAssertsVarying(MethodNode methodNode, YMap<String, YSet<String>> modifiers) {


        Object refReadPattern = stairs(
                deeper(G_BODY_ACCESSORS),
                p(BinaryExpression.class, "operation", p("text", "="), "rightExpression"),
                deeper(G_BODY_ACCESSORS),
                p(VariableExpression.class, "variable", var("name")));

        Object refReadPattern2 = stairs(
                deeper(G_BODY_ACCESSORS),
                p(ReturnStatement.class, "getExpression"),
                deeper(G_BODY_ACCESSORS),
                p(VariableExpression.class, "variable", var("name")));

        Object fieldReadPattern = new MatchDeeper(G_BODY_ACCESSORS,
                p("operation", p("text", "="), "rightExpression", new MatchDeeper(G_BODY_ACCESSORS,
                        p("objectExpression", p("variable", var("name"))))));

        return null;
    }

    public static YList<String> gAsserts(Object mainClass, YMap<String, YSet<String>> modifiers) {
        YList<String> errors = al();

        Object varWritePattern = stairs(
                deeper(G_BODY_ACCESSORS),
                p(BinaryExpression.class, "operation", p("text", "="), "leftExpression"),
                p(VariableExpression.class, "variable", var("VAR_NAME")));

        Object varReturnPattern = stairs(
                deeper(G_BODY_ACCESSORS),
                p(ReturnStatement.class, "getExpression"),
                p(VariableExpression.class, "variable", var("VAR_NAME")));

        for (YMap<String, Object> m : new Matcher().match(mainClass, stairs(deeper(G_METHOD_ACCESSORS), var("method"), p(MethodNode.class, "name"), var("methodName")))) {
            Object methodName = m.get("methodName");
            String prefix = "(method " + methodName + ") ";
            for (Parameter parameter : ((MethodNode)m.get("method")).getParameters()) if (!isPrimitive(translateType(parameter.getType().getName()))) {//TODO match type
                //can't rewrite vector (use explicit copyFrom)
                if (new Matcher().match(m.get("method"), varWritePattern, hm("VAR_NAME", parameter.getName())).notEmpty()) {
                    errors.add(prefix + "parameter '" + parameter.getName() + " " + translateType(parameter.getType().getName()) + "' is rewritten, but it's forbidden in gglsl (use explicity copyFrom)");
                }
                //can't return vector (use explicit copy)
                if (new Matcher().match(m.get("method"), varReturnPattern, hm("VAR_NAME", parameter.getName())).notEmpty()) {
                    errors.add(prefix + "parameter '" + parameter.getName() + "' is used as return value, but it's forbidden in gglsl (use explicity copy)");
                }
            }

            YSet<YMap<String, Object>> fieldsMm = new Matcher().match(mainClass, stairs(i(), p(ClassNode.class, "getFields"), i(), p(FieldNode.class, "getName"), var("fieldName")));
            for (YMap<String, Object> fieldM : fieldsMm) {

                String fieldName = (String) fieldM.get("fieldName");

                //can't return uniform (use explicit copy)
                if (new Matcher().match(m.get("method"), varReturnPattern, hm("VAR_NAME", fieldName)).notEmpty()) {
                    errors.add(prefix + "field '" + fieldName + "' is used as return value, but it's forbidden in gglsl");
                }

                //uniform.x as inout-arg
                //seems like couldn't be (because all fields are primitive)
                for (YMap<String, Object> asArgM : new Matcher().match(m.get("method"), G_FIELD_AS_ARG_PATTERN, hm("OBJ_NAME", fieldName))) {
                    String argIndexedName = asArgM.get("callMethodName") + ":" + asArgM.get("argIndex");
                    YSet<String> argQualifiers = modifiers.getOr(argIndexedName, hs());
                    if (argQualifiers.contains("inout")) {
                        errors.add(prefix + "" + fieldName + "." + asArgM.get("FIELD_NAME") + " is used as inout arg, but it's forbidden in gglsl");
                    }
                }

                //uniform as inout-arg
                for (YMap<String, Object> asArgM : new Matcher().match(m.get("method"), G_AS_ARG_PATTERN, hm("name", fieldName))) {
                    String argIndexedName = asArgM.get("callMethodName") + ":" + asArgM.get("argIndex");
                    YSet<String> argQualifiers = modifiers.getOr(argIndexedName, hs());
                    if (argQualifiers.contains("inout")) {
                        errors.add(prefix + "field '" + fieldName + "' is used as inout arg, but it's forbidden in gglsl");
                    }
                }

                //uniform write
                if (new Matcher().match(m.get("method"), varWritePattern, hm("VAR_NAME", fieldName)).notEmpty()) {
                    errors.add(prefix + "field '" + fieldName + "' is rewritten, but it's forbidden in gglsl");
                }

                //uniform.x write
                if (new Matcher().match(m.get("method"), G_WRITE_FIELD_PATTERN, hm("OBJ_NAME", fieldName)).notEmpty()) {
                    errors.add(prefix + "field '" + fieldName + "' is modified, but it's forbidden in gglsl");
                }
            }

        }
        return errors;
    }
}



