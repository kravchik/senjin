package yk.senjin.shaders.gshader.analysis;

import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.builder.AstBuilder;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.control.CompilePhase;
import org.junit.Test;
import yk.jcommon.match2.*;
import yk.jcommon.utils.IO;
import yk.ycollections.*;

import java.util.List;

import static yk.jcommon.match2.MatcherShortNames.p;
import static yk.jcommon.match2.MatcherShortNames.var;
import static yk.senjin.shaders.gshader.ShaderTranslator.isPrimitive;
import static yk.senjin.shaders.gshader.ShaderTranslator.translateType;
import static yk.ycollections.YArrayList.al;
import static yk.ycollections.YHashMap.hm;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 03/11/15
 * Time: 12:29
 */
public class Visitors {

    @Test
    public void test1() {
        String src = IO.readFile("src/main/java/yk/senjin/shaders/gshader/analysis/BlendF.groovy");
        List<ASTNode> nodes = new AstBuilder().buildFromString(CompilePhase.INSTRUCTION_SELECTION, src);
        ClassNode classNode = (ClassNode) nodes.get(1);
        for (MethodNode methodNode : classNode.getMethods()) {
            System.out.println("looking at " + methodNode.getName());
            getArgsModifiers(methodNode, null);
        }
    }

    public static YHashMap<String, YSet<String>> inferInOutModifiers(Object nodes) {
        YList<Object> accessors = YArrayList.al(new MatchByIndex(new MatchVar("access")));
        YList<Object> accessors2 = accessors.with(MatchProperty.p("methodsList", new MatchVar("access")));
        YSet<YMap<String, Object>> method = new Matcher().match(nodes, new MatchDeeper(accessors2, var("method", p(MethodNode.class, "name", var("methodName")))));
        YHashMap<String, YSet<String>> modifiers = hm();
        int oldModifiersCount = 0;
        while (true) {

            for (YMap<String, Object> m : method) {
                System.out.println("method: " + m.get("methodName"));
                getArgsModifiers((MethodNode) m.get("method"), modifiers);
                System.out.println(modifiers);
            }
            int newModifiersCount = modifiers.values().reduce(0, (i, ss) -> i + ss.size());
            if (newModifiersCount == oldModifiersCount) break;
            System.out.println("AND AGAIN");
            oldModifiersCount = newModifiersCount;
        }
        return modifiers;
    }

    public static YMap<String, YSet<String>> getArgsModifiers(MethodNode methodNode, YMap<String, YSet<String>> result) {

        for (Parameter parameter : methodNode.getParameters()) if (!isPrimitive(translateType(parameter.getText()))) {

            YList<Boolean> rewritten = al(false);
            CodeVisitorSupport detectRewrittenVisitor = new CodeVisitorSupport() {

                @Override
                public void visitBinaryExpression(BinaryExpression expression) {
                    if (expression.getOperation().getText().equals("=")) {
                        Expression left = expression.getLeftExpression();
                        if (left instanceof VariableExpression) {
                            if (((VariableExpression) left).getName().equals(parameter.getName())) {
                                rewritten.set(0, true);
                            }
                        }
                    }
                    super.visitBinaryExpression(expression);
                }
            };
            detectRewrittenVisitor.visitBlockStatement((BlockStatement) methodNode.getCode());
            if (rewritten.get(0)) System.out.println(parameter.getName() + " rewritten!");
        }
        return result;
    }

}
