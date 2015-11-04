package yk.jcommon.match2;

import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.builder.AstBuilder;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.IfStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.control.CompilePhase;
import org.junit.Test;
import yk.jcommon.collections.*;
import yk.jcommon.probe.Probe;
import yk.jcommon.probe.State;
import yk.jcommon.search.SSearch;
import yk.jcommon.utils.IO;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static yk.jcommon.collections.YArrayList.al;
import static yk.jcommon.collections.YHashMap.hm;
import static yk.jcommon.collections.YHashSet.hs;
import static yk.jcommon.match2.Matcher.match;
import static yk.jcommon.match2.ShortNames.*;
import static yk.senjin.shaders.gshader.ProgramGenerator.isPrimitive;
import static yk.senjin.shaders.gshader.ProgramGenerator.translateType;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 28/10/15
 * Time: 16:38
 */
public class Test1 {

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
            //на какой-то глубине
            deeper(G_BODY_ACCESSORS),
            //есть объект такого класса
            p(MethodCallExpression.class,
                    //с такими параметрами
                    "getMethod", p("getText", var("METHOD_NAME")),
                    //а метод getArguments возвращает...
                    "getArguments"),
            // такой объект, у которого есть поле expressions, которое...
            p(ArgumentListExpression.class, "expressions"),
            //является массивом, а по индексу (который мы запомним) находится...
            i(var("ARG_INDEX")),
            //такой объект
            p(PropertyExpression.class,
                    //с каким-то ответвлением
                    "getObjectExpression", p(VariableExpression.class,
                            "variable", "OBJ_NAME"),
                    //и с ещё одним
                    "getProperty", p("value", var("FIELD_NAME"))));

    public static final Object G_WRITE_FIELD_PATTERN = stairs(
            deeper(G_BODY_ACCESSORS),
            p(BinaryExpression.class, "operation", p("text", "="), "leftExpression"),
            p(PropertyExpression.class, "objectExpression"),
            p(VariableExpression.class, "variable", "OBJ_NAME"));

//    public static final Object G_WRITE_FIELD_PATTERN = new Match2Deeper(G_BODY_ACCESSORS,
//            p(BinaryExpression.class, "operation", p("text", "="),
//                    "leftExpression", p(PropertyExpression.class, "objectExpression", p(VariableExpression.class, "variable", ARG_NAME_USAGE), "property", p("value", var("value")))
//            ));



    public static void main(String[] args) {
        List<ASTNode> nodes = new AstBuilder().buildFromString(CompilePhase.INSTRUCTION_SELECTION, IO.readFile("/home/yuri/1/public/senjin/src/main/java/yk/senjin/shaders/gshader/examples/blend/BlendF.groovy"));
        ClassNode mainClass = (ClassNode) nodes.get(1);
        //mainClass.methodsList

//        Var ml = new Var();
        Object pattern = new ByIndex(1, new Property("methodsList", new ByIndex(new Var("method", new Property("name", "main", "modifiers", 1)))));
        System.out.println(match(nodes, pattern).toString("\n"));


        Object patternFields = new Property("fields", new ByIndex(new Var("field", new Property("modifiers", 1, "name", new Var("name")))));
        System.out.println(match(mainClass, patternFields).toString("\n"));


        System.out.println("done");
    }


    @Test
    public void test1() {
        List<ASTNode> nodes = new AstBuilder().buildFromString(CompilePhase.INSTRUCTION_SELECTION, IO.readFile("/home/yuri/1/public/senjin/src/main/java/yk/senjin/shaders/gshader/examples/blend/BlendF.groovy"));
        //find by accessor ByIndex
        YList<Object> accessors = al(new ByIndex(new Var("access")));
        System.out.println(match(nodes, new Match2Deeper(accessors, new Var("class", new Property("getClass", ClassNode.class)))));

        //find by accessor ByIndex and walk properties
        YList<Object> accessors2 = accessors.with(new Property("methodsList", new Var("access")));
        System.out.println(match(nodes, new Match2Deeper(accessors2, new Var("method", new Property("getClass", MethodNode.class)))));


    }

    @Test
    public void test2() {
        String src = IO.readFile("/home/yuri/1/public/senjin/src/main/java/yk/translator/TestClass.groovy");
        List<ASTNode> nodes = new AstBuilder().buildFromString(CompilePhase.INSTRUCTION_SELECTION, src);
        YList<Object> accessors = al(new ByIndex(new Var("access")));
        YList<Object> accessors2 = accessors.with(new Property("methodsList", new Var("access")));
        YSet<YMap<String, Object>> method = match(nodes, new Match2Deeper(accessors2, new Var("method", new Property("getClass", MethodNode.class))));
        System.out.println(method);

        YSet<YMap<String, Object>> params = match(method.first().get("method"), new Property("parameters", new ByIndex(new Property("name", new Var("varName")))));
        System.out.println(params);

        new Property("getClass", BlockStatement.class, "statements", "access");
        new Property("getClass", ExpressionStatement.class, "expresssion", "access");

        Object pattern = p("code", p(BlockStatement.class, "statements",
                i(p("expression",
                    p(BinaryExpression.class,
                        "leftExpression", p("getClass", var("wtf?"),                              //demo with "what is the name of that class?"
                             "objectExpression", p(VariableExpression.class, "variable", "a"),
                            "property", p("value", "x")),
                        "operation", p("text", "="))))));

        System.out.println(match(method.first().get("method"), pattern));

    }
    
    @Test
    public void test3() {
        String src = IO.readFile("/home/yuri/1/public/senjin/src/main/java/yk/translator/TestClass.groovy");
        List<ASTNode> nodes = new AstBuilder().buildFromString(CompilePhase.INSTRUCTION_SELECTION, src);
        YList<Object> accessors = al(new ByIndex(new Var("access")));
        YList<Object> accessors2 = accessors.with(new Property("methodsList", new Var("access")));
        YSet<YMap<String, Object>> method = match(nodes, new Match2Deeper(accessors2, new Var("method", new Property("getClass", MethodNode.class))));
        System.out.println(method);

        YSet<YMap<String, Object>> params = match(method.first().get("method"), new Property("parameters", new ByIndex(new Property("name", new Var("varName")))));
        System.out.println(params);

//block statement  - и сразу IfStatement! (польза Match2Deeperа)
//ExpressionStatement - содержит BinaryExpression в expression

        YArrayList<Object> accessors3 = al(
                i(var("access")),
                p(MethodNode.class, "code", var("access")),
                p(BlockStatement.class, "statements", var("access")),
                p(ExpressionStatement.class, "expression", var("access")),
                p(IfStatement.class, "booleanExpression", var("access")),
                p(IfStatement.class, "ifBlock", var("access")),
                p(IfStatement.class, "elseBlock", var("access"))
        );

        Object pattern = new Match2Deeper(accessors3,
                                p(BinaryExpression.class, "operation", p("text", "="),
                                        "leftExpression", p(PropertyExpression.class, "objectExpression", p(VariableExpression.class, "variable", "a"), "property", p("value", var("value")))
                                ));
//
        System.out.println(match(method.first().get("method"), pattern));
    }

    @Test
    public void testArgModifiers() {
        String src = IO.readFile("src/main/java/yk/jcommon/match2/TestGroovyClass.groovy");
        List<ASTNode> nodes = new AstBuilder().buildFromString(CompilePhase.INSTRUCTION_SELECTION, src);
        YList<Object> accessors = al(new ByIndex(new Var("access")));
        YList<Object> accessors2 = accessors.with(new Property("methodsList", new Var("access")));
        Property mmm = p(MethodNode.class, "name", var("methodName"));
        YSet<YMap<String, Object>> method = match(nodes, new Match2Deeper(accessors2, var("method", mmm)));
        for (YMap<String, Object> m : method) {
            System.out.println("method: " + m.get("methodName"));
            System.out.println(getArgsModifiers((MethodNode) m.get("method"), hm()));

//            YSet<YMap<String, Object>> mm = Matcher.match(m.get("method"), new Match2Deeper(G_BODY_ACCESSORS,
//                                    p(MethodCallExpression.class, "arguments", var("value"))));

            YSet<YMap<String, Object>> mm = match(m.get("method"), new Match2Deeper(G_BODY_ACCESSORS,
                    p(MethodCallExpression.class, "arguments", p(ArgumentListExpression.class, "expressions", i(p("variable", var("value")))))));
//
            for (YMap<String, Object> m1 : mm) {
                Object usedInMethodCall = m1.get("value");
                System.out.println("wtf " + usedInMethodCall);
                MethodCallExpression mce = (MethodCallExpression) mm.first().get("methodCallNode");
                System.out.println(mce);
            }


            Probe probe = new Probe(m.get("method"), "foo2"){{
                skipMethods = hs("getMethodAsString", "getFirstStatement", "getVariableScope", "getDeclaringClass");
            }};
            SSearch.Node<State> node = probe.nextSolution(100000);
            if (node != null) System.out.println("!" + node.state.stackTrace.toString("\n"));
        }
    }

    @Test
    public void testCalcCallers() {
        String src = IO.readFile("src/main/java/yk/jcommon/match2/TestGroovyClass.groovy");
        assertEquals("{foo=[foo1], foo1=[foo2]}", calcCallers(new AstBuilder().buildFromString(CompilePhase.INSTRUCTION_SELECTION, src)).toString());

        src = IO.readFile("/home/yuri/1/public/senjin/src/main/java/yk/senjin/shaders/gshader/examples/blend/BlendF.groovy");

        Probe probe = new Probe(new AstBuilder().buildFromString(CompilePhase.INSTRUCTION_SELECTION, src), p(MethodCallExpression.class, "getMethod", p("getText", "foo"))){{
            skipMethods = hs("getMethodAsString", "getFirstStatement", "getVariableScope", "getDeclaringClass");
        }};
        SSearch.Node<State> node = probe.nextSolution(100000);
        if (node != null) System.out.println("!" + node.state.stackTrace.toString("\n"));

        src = IO.readFile("src/main/java/yk/jcommon/match2/TestGroovyClass2.groovy");
        assertEquals("{foo=[foo1]}", calcCallers(new AstBuilder().buildFromString(CompilePhase.INSTRUCTION_SELECTION, src)).toString());
    }

    public static YMap<String, YSet<String>> calcCallers(Object data) {
        YMap<String, YSet<String>> result = hm();
        YSet<YMap<String, Object>> methods = match(data, stairs(deeper(G_METHOD_ACCESSORS), var("method"), p(MethodNode.class, "name"), var("methodName")));
        for (YMap<String, Object> mp : methods) {
            Object methodCalls = stairs(deeper(G_BODY_ACCESSORS), p(MethodCallExpression.class, "getMethod"), p("getText"), var("callMethodName"));
            for (YMap<String, Object> mcp : match(mp.get("method"), methodCalls)) {
                result.put((String) mp.get("methodName"), result.getOr((String) mp.get("methodName"), hs()).with((String) mcp.get("callMethodName")));
            }
        }
        return result;
    }

    public static YMap<String, YSet<String>> getArgsModifiers(MethodNode methodNode, YMap<String, YSet<String>> result) {
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

        Object fieldReadPattern = new Match2Deeper(G_BODY_ACCESSORS,
                p("operation", p("text", "="), "rightExpression", new Match2Deeper(G_BODY_ACCESSORS,
                                p("objectExpression", p("variable", var("name"))))));

        YSet<YMap<String, Object>> params = match(methodNode, p("parameters", i(var("argIndex"), p("name", var("argName")))));
        for (YMap<String, Object> foundArgs : params) {
            String argName = (String) foundArgs.get("argName");
            String argFullName = methodNode.getName() + ":" + foundArgs.get("argIndex");
//            String argName = (String) foundArgs.get("argName");
            if (match(methodNode, G_WRITE_FIELD_PATTERN, hm("OBJ_NAME", argName)).notEmpty()) result.put(argFullName, result.getOr(argFullName, hs()).with("inout"));

            if (match(methodNode, fieldReadPattern, hm("name", argName)).notEmpty()) result.put(argFullName, result.getOr(argFullName, hs()).with("in"));
            if (match(methodNode, refReadPattern, hm("name", argName)).notEmpty()) result.put(argFullName, result.getOr(argFullName, hs()).with("in"));
            if (match(methodNode, refReadPattern2, hm("name", argName)).notEmpty()) result.put(argFullName, result.getOr(argFullName, hs()).with("in"));

            Object asArgPattern = stairs(
                    deeper(G_BODY_ACCESSORS),
                    p(MethodCallExpression.class, "getMethod", p("getText", var("callMethodName")), "getArguments"),
                    p(ArgumentListExpression.class, "expressions"),
                    i(var("callVarIndex")),
                    p("variable"),
                    var("name"));

//        Object asArgPattern = new Match2Deeper(G_BODY_ACCESSORS,
//                p(MethodCallExpression.class, "arguments", p(ArgumentListExpression.class, "expressions", i(p("variable", var("value"))))));

            YSet<YMap<String, Object>> mm = match(methodNode, asArgPattern, hm("name", argName));
            for (YMap<String, Object> m1 : mm) {
//                System.out.println(m1);
                Object callMethodName = m1.get("callMethodName");
                Object callVarIndex = m1.get("callVarIndex");
//                System.out.println("usedInMethodCall " + callMethodName + "(...) " + argName + " " + callVarIndex);
//                System.out.println(result.get(callMethodName + ":" + callVarIndex));

                result.put(argFullName, result.getOr(argFullName, hs()).with(result.getOr(callMethodName + ":" + callVarIndex, hs())));

            }
        }
//        System.out.println("result: " + result);
        return result;
    }

    @Test
    public void testArgModifiersCollect() {
        String src = IO.readFile("src/main/java/yk/jcommon/match2/BlendF.groovy");
        YHashMap<String, YSet<String>> modifiers = inferInOutModifiers(new AstBuilder().buildFromString(CompilePhase.INSTRUCTION_SELECTION, src));
        assertEquals("{foo:0=[out, in], foo1:0=[out, in], foo2:0=[out]}", modifiers.toString());
    }

    //SUMMARY
    //primitives - do nothing
    //vector param
    // can't be rewritten     //done in gAsserts
    // can't be returned      //in gAsserts
    // modified ?  =>   inout : in

    public static YHashMap<String, YSet<String>> inferInOutModifiers(Object nodes) {
        YList<Object> accessors = al(new ByIndex(new Var("access")));
        YList<Object> accessors2 = accessors.with(new Property("methodsList", new Var("access")));
        YSet<YMap<String, Object>> method = match(nodes, new Match2Deeper(accessors2, var("method", p(MethodNode.class, "name", var("methodName")))));
        YHashMap<String, YSet<String>> modifiers = hm();
        int oldModifiersCount = 0;
        while (true) {

            for (YMap<String, Object> m : method) {
                System.out.println("method: " + m.get("methodName"));
                getArgsModifiers2((MethodNode) m.get("method"), modifiers);
                System.out.println(modifiers);
            }
            int newModifiersCount = modifiers.values().reduce(0, (i, ss) -> i + ss.size());
            if (newModifiersCount == oldModifiersCount) break;
            System.out.println("AND AGAIN");
            oldModifiersCount = newModifiersCount;
        }
        return modifiers;
    }

    public static YMap<String, YSet<String>> getArgsModifiers2(MethodNode methodNode, YMap<String, YSet<String>> result) {

        Object fieldWritePattern = new Match2Deeper(G_BODY_ACCESSORS,
                p(BinaryExpression.class, "operation", p("text", "="),
                        "leftExpression", p(PropertyExpression.class, "objectExpression", p(VariableExpression.class, "variable", var("name")), "property", p("value", var("value")))
                ));

        YSet<YMap<String, Object>> paramMm = match(methodNode, p("parameters", i(var("paramIndex"), p("name", var("paramName")))));
        for (YMap<String, Object> paramM : paramMm) if (!isPrimitive(translateType((String) paramM.get("paramName")))) {
            String paramName = (String) paramM.get("paramName");
            String paramFullName = methodNode.getName() + ":" + paramM.get("paramIndex");
            if (match(methodNode, fieldWritePattern, hm("name", paramName)).notEmpty()) result.put(paramFullName, result.getOr(paramFullName, hs()).with("inout"));

            YSet<YMap<String, Object>> asArgMm = match(methodNode, G_AS_ARG_PATTERN, hm("name", paramName));
            for (YMap<String, Object> asArgM : asArgMm) {
                Object callMethodName = asArgM.get("callMethodName");
                Object asArgIndex = asArgM.get("argIndex");
                result.put(paramFullName, result.getOr(paramFullName, hs()).with(result.getOr(callMethodName + ":" + asArgIndex, hs())));
            }
        }
        return result;
    }

    @Test
    public void testAsserts() {
        String src = IO.readFile("src/main/java/yk/jcommon/match2/BlendF.groovy");
        for (YMap<String, Object> m : match(
                new AstBuilder().buildFromString(CompilePhase.INSTRUCTION_SELECTION, src),
                new Match2Deeper(G_METHOD_ACCESSORS, var("method", p(MethodNode.class, "name", var("methodName")))))) {
            assertEquals("arg someVec is rewritten, but it's forbidden in gglsl", gAsserts((MethodNode) m.get("method"), null).toString("\n"));
        }
    }

    @Test
    public void testInOutPropagation() {
        String src = IO.readFile("src/main/java/yk/jcommon/match2/BlendF.groovy");
        System.out.println(gAsserts(new AstBuilder().buildFromString(CompilePhase.INSTRUCTION_SELECTION, src), hm("testFoo:0", hs("in", "inout"))).toString("\n"));

    }

    //ShaderInOut can't write
    //ShaderInOut can't read
    //ShaderInOut can't as arg
    //ShaderIn.x  can't write
    //ShaderIn.x  can't as inout-arg

    //ShaderOut.x can write
    //ShaderOut.x can as inout-arg

    public static YList<String> gAssertsVarying(MethodNode methodNode, YMap<String, YSet<String>> modifiers) {



        return null;
    }

    public static YList<String> gAsserts(Object mainClass, YMap<String, YSet<String>> modifiers) {
        YList<String> errors = al();

//        Object varWritePattern = new Match2Deeper(G_BODY_ACCESSORS,
//                p(BinaryExpression.class, "operation", p("text", "="),
//                        "leftExpression", p(VariableExpression.class, "variable", argNameUsage)));
        Object varWritePattern = stairs(
                deeper(G_BODY_ACCESSORS),
                p(BinaryExpression.class, "operation", p("text", "="), "leftExpression"),
                p(VariableExpression.class, "variable", var("VAR_NAME")));

        Object node = null;
        String varName = "";

        YSet<YMap<String, Object>> match1 = match(node, varWritePattern);
        YSet<YMap<String, Object>> match2 = match(node, varWritePattern, hm("VAR_NAME", varName));

        Object varReturnPattern = stairs(
                deeper(G_BODY_ACCESSORS),
                p(ReturnStatement.class, "getExpression"),
                p(VariableExpression.class, "variable", var("VAR_NAME")));

        Object rest = null;
//        new Property("getClass", ReturnStatement.class, "getExpression", var("EXPR"));
        new Var("VAR_NAME", rest);
        new ByIndex(3, rest);
        new Match2Deeper(G_BODY_ACCESSORS, rest);

        for (YMap<String, Object> m : match(mainClass, stairs(deeper(G_METHOD_ACCESSORS), var("method"), p(MethodNode.class, "name"), var("methodName")))) {
            Object methodName = m.get("methodName");
            String prefix = "(method " + methodName + ") ";
            System.out.println("method " + methodName);
            //can't rewrite vector (use explicit copyFrom)
            for (Parameter parameter : ((MethodNode)m.get("method")).getParameters()) if (!isPrimitive(translateType(parameter.getText()))) {
                boolean wasWritten = match(m.get("method"), varWritePattern, hm("VAR_NAME", parameter.getName())).notEmpty();
                if (match(m.get("method"), varWritePattern, hm("VAR_NAME", parameter.getName())).notEmpty()) {
                    errors.add(prefix + "parameter '" + parameter.getName() + "' is rewritten, but it's forbidden in gglsl");
                }
                //can't return vector (use explicit copy)
                if (match(m.get("method"), varReturnPattern, hm("VAR_NAME", parameter.getName())).notEmpty()) {
                    errors.add(prefix + "parameter '" + parameter.getName() + "' is used as return value, but it's forbidden in gglsl");
                }
            }


            YSet<YMap<String, Object>> fieldsMm = match(mainClass, stairs(i(), p(ClassNode.class, "getFields"), i(), p(FieldNode.class, "getName"), var("fieldName")));
            for (YMap<String, Object> fieldM : fieldsMm) {
                System.out.println("field: " + fieldM.get("fieldName"));

                String fieldName = (String) fieldM.get("fieldName");

                //can't return uniform (use explicit copy)
                if (match(m.get("method"), varReturnPattern, hm("VAR_NAME", fieldName)).notEmpty()) {
                    errors.add(prefix + "field '" + fieldName + "' is used as return value, but it's forbidden in gglsl");
                }

                        //uniform.x as inout-arg
                //seems like couldn't be (because all fields are primitive)
                for (YMap<String, Object> asArgM : match(m.get("method"), G_FIELD_AS_ARG_PATTERN, hm("OBJ_NAME", fieldName))) {
                    String argIndexedName = asArgM.get("callMethodName") + ":" + asArgM.get("argIndex");
                    YSet<String> argQualifiers = modifiers.getOr(argIndexedName, hs());
                    if (argQualifiers.contains("inout")) {
                        errors.add(prefix + "" + fieldName + "." + asArgM.get("FIELD_NAME") + " is used as inout arg, but it's forbidden in gglsl");
                    }
                }

                //uniform as inout-arg
                for (YMap<String, Object> asArgM : match(m.get("method"), G_AS_ARG_PATTERN, hm("name", fieldName))) {
                    String argIndexedName = asArgM.get("callMethodName") + ":" + asArgM.get("argIndex");
                    YSet<String> argQualifiers = modifiers.getOr(argIndexedName, hs());
                    if (argQualifiers.contains("inout")) {
                        errors.add(prefix + "field '" + fieldName + "' is used as inout arg, but it's forbidden in gglsl");
                    }
                }

                //uniform write
                if (match(m.get("method"), varWritePattern, hm("VAR_NAME", fieldName)).notEmpty()) {
                    errors.add(prefix + "field '" + fieldName + "' is rewritten, but it's forbidden in gglsl");
                }

                //uniform.x write
                if (match(m.get("method"), G_WRITE_FIELD_PATTERN, hm("OBJ_NAME", fieldName)).notEmpty()) {
                    errors.add(prefix + "field '" + fieldName + "' is modified, but it's forbidden in gglsl");
                }
            }

        }
        return errors;
    }

    //TODO assert last statement is returns (count ifs with elses)
}
























