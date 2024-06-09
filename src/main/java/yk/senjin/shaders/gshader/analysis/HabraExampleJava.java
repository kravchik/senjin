package yk.senjin.shaders.gshader.analysis;

import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.builder.AstBuilder;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.control.CompilePhase;
import yk.jcommon.fastgeom.Vec3f;
import yk.jcommon.match2.Matcher;
import yk.jcommon.utils.IO;
import yk.ycollections.YMap;

import static yk.jcommon.match2.MatcherShortNames.*;
import static yk.senjin.shaders.gshader.analysis.GglslAnalyzer.G_BODY_ACCESSORS;
import static yk.senjin.shaders.gshader.analysis.GglslAnalyzer.G_METHOD_ACCESSORS;
import static yk.ycollections.YHashMap.hm;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 04/11/15
 * Time: 12:17
 */
public class HabraExampleJava {

    public static final Object G_FIELD_AS_ARG_PATTERN = stairs(
            //на какой-то глубине
            deeper(G_BODY_ACCESSORS),
            //есть объект такого класса
            obj(MethodCallExpression.class,
                    //с такими параметрами
                    "getMethod", obj("getText", var("METHOD_NAME")),
                    //а метод getArguments возвращает...
                    "getArguments"),
            // такой объект, у которого есть поле expressions, которое...
            obj(ArgumentListExpression.class, "expressions"),
            //является массивом, а по индексу (который мы запомним) находится...
            i(var("ARG_INDEX")),
            //такой объект
            obj(PropertyExpression.class,
                    //с каким-то ответвлением
                    "getObjectExpression", obj(VariableExpression.class,
                            "variable", var("OBJ_NAME")),
                    //и с ещё одним
                    "getProperty", obj("value", var("FIELD_NAME"))));

    //    public static final Object G_WRITE_FIELD_PATTERN = new Match2Deeper(G_BODY_ACCESSORS,
//            obj(BinaryExpression.class, "operation", obj("text", "="),
//                    "leftExpression", obj(PropertyExpression.class, "objectExpression", obj(VariableExpression.class, "variable", ARG_NAME_USAGE), "property", obj("value", var("value")))
//            ));


    public static void main(String[] args) {

        Object varWritePattern = stairs(
                deeper(G_BODY_ACCESSORS),
                obj(BinaryExpression.class, "operation", obj("text", "="), "leftExpression"),
                obj(VariableExpression.class, "variable", var("VAR_NAME")));

        String src = IO.readFile("src/main/java/yk/senjin/shaders/gshader/analysis/HabraExample.groovy");
        //parse kotlin file
        Object node = new AstBuilder().buildFromString(CompilePhase.INSTRUCTION_SELECTION, src);

        //select method "foo"
        for (YMap<String, Object> m : new Matcher().match(node, stairs(deeper(G_METHOD_ACCESSORS), var("method"), obj(MethodNode.class, "name"), "foo"))) {
            //getting methodNode object from select result
            Object methodNode = m.get("method");
            System.out.println("all variables are free:");
            System.out.println(new Matcher().match(methodNode, G_FIELD_AS_ARG_PATTERN).toString("\n"));
            System.out.println("\nfixed OBJ_NAME:");
            System.out.println(new Matcher().match(methodNode, G_FIELD_AS_ARG_PATTERN, hm("OBJ_NAME", "vecB")).toString("\n"));
            System.out.println("\nfixed ARG_INDEX:");
            System.out.println(new Matcher().match(methodNode, G_FIELD_AS_ARG_PATTERN, hm("ARG_INDEX", 0)).toString("\n"));
        }

        System.out.println("\nsimple array\n");
        Vec3f[] vv = new Vec3f[]{new Vec3f(0, 0, 0), new Vec3f(1, 1, 1)};
        System.out.println(new Matcher().match(vv, i(obj("x", var("X_VALUE")))));
        System.out.println(new Matcher().match(vv, i(var("OBJ_INDEX"), obj("x", var("X_VALUE")))));



    }

}
