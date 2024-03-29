package yk.senjin.shaders.gshader.analysis

import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.control.CompilePhase
import yk.jcommon.fastgeom.Vec3f
import yk.jcommon.match2.MatchByIndex
import yk.jcommon.match2.MatchDeeper
import yk.jcommon.match2.MatchVar
import yk.jcommon.utils.IO
import yk.ycollections.YMap
import yk.ycollections.YSet

import static GglslAnalyzer.G_BODY_ACCESSORS
import static yk.jcommon.match2.MatcherShortNames.*

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 04/11/15
 * Time: 11:16
 */
class HabraExample {


    public void foo(Vec3f vecA, Vec3f vecB) {
        bar(vecA.x, vecA.y)
        bar(vecA.x, vecB.x)
    }

    public void bar(float a, float b) {
    }


    public static void main(String[] args) {

//        Object varWritePattern = new Match2Deeper(G_BODY_ACCESSORS,
//                p(BinaryExpression.class, "operation", p("text", "="),
//                        "leftExpression", p(VariableExpression.class, "variable", argNameUsage)));

        Object varWritePattern = stairs(
                deeper(G_BODY_ACCESSORS),
                p(BinaryExpression.class, "operation", p("text", "="), "leftExpression"),
                p(VariableExpression.class, "variable", var("VAR_NAME")));

//        Object methodNode = null;
        String varName = "";

        Object rest = null;
        new MatchVar("VAR_NAME", rest);
        new MatchByIndex(3, rest);
        new MatchDeeper(G_BODY_ACCESSORS, rest);

//        boolean wasWritten = match(methodNode, varWritePattern, hm("VAR_NAME", varName)).notEmpty();

        String src = IO.readFile("src/main/java/yk/senjin/shaders/gshader/analysis/HabraExample.groovy");
        //parse kotlin file
        Object node = new AstBuilder().buildFromString(CompilePhase.INSTRUCTION_SELECTION, src)

        YSet<YMap<String, Object>> match1 = match(node, varWritePattern);
        YSet<YMap<String, Object>> match2 = match(node, varWritePattern, hm("VAR_NAME", varName));



    }
}
