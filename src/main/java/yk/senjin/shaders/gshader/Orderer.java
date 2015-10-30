package yk.senjin.shaders.gshader;

import org.junit.Test;
import yk.jcommon.collections.YList;
import yk.jcommon.collections.YMap;
import yk.jcommon.collections.YSet;
import yk.jcommon.search.SSearch;
import yk.jcommon.utils.BadException;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static yk.jcommon.collections.YArrayList.al;
import static yk.jcommon.collections.YHashMap.hm;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 30/10/15
 * Time: 14:26
 */
public class Orderer {

    @Test
    public void tests() {
        assertEquals(al("foo1", "foo2", "foo3"), orderMethods("foo1", hm("foo1", "foo1b", "foo2", "foo2b", "foo3", "foo3b"), hm("foo1", al("foo2", "foo3"), "foo2", al(), "foo3", al())));
        assertEquals(al("foo1", "foo3", "foo2"), orderMethods("foo1", hm("foo1", "foo1b", "foo2", "foo2b", "foo3", "foo3b"), hm("foo1", al("foo2", "foo3"), "foo2", al(), "foo3", al("foo2"))));
        try {
            assertEquals(al("foo1", "foo3", "foo2"), orderMethods("foo1", hm("foo1", "foo1b", "foo2", "foo2b", "foo3", "foo3b"), hm("foo1", al("foo2", "foo3"), "foo2", al(), "foo3", al("foo3"))));
            fail();
        } catch(BadException ignore) {}
        assertEquals(null, orderMethods("foo1", hm("foo1", "foo1b", "foo2", "foo2b", "foo3", "foo3b"), hm("foo1", al("foo2", "foo3"), "foo2", al(), "foo3", al("foo2", "foo1"))));

        assertEquals(al("main"), orderMethods("main", hm("main", "", "vec2", "", "vec3", "", "vec4", ""), hm("'OpenGL'", al("main"))));
        assertEquals(al("main", "texture2D"), orderMethods("main", hm("main", "", "vec2", "", "vec3", "", "vec4", "", "texture2D", "", "reflect", "", "dot", "", "pow", "", "min", "", "max", "", "normalize", ""), hm("'OpenGL'", al("main"), "main", al("texture2D"))));

        YMap<String, YList<String>> c2c = hm(
                "'OpenGL'", al("main"), "main", al("texture2D", "vec3", "vec3", "max", "dot", "normalize", "reflect", "normalize", "normalize", "pow", "max", "dot", "normalize", "vec4"));
        YMap<String, String> bodies = hm("main", "");
        assertEquals(al("main"), orderMethods("main", bodies, c2c));

    }

    public static YList<String> orderMethods(String start, YMap<String, String> all, YMap<String, YList<String>> caller2callee) {
        for (Map.Entry<String, YList<String>> entry : caller2callee.entrySet()) {
            if (entry.getValue().contains(entry.getKey())) BadException.die("method " + entry.getKey() + " calls itself, but recursion is prohibited");
        }

        SSearch<YList<String>> search = new SSearch<YList<String>>(al()) {
            @Override
            public List<YList<String>> generate(Node<YList<String>> node) {
                List<YList<String>> result = al();
                YSet<String> candidates = all.keySet().filter(m -> !node.state.contains(m)
                        && all.containsKey(m)
                        && !node.state.any(added -> caller2callee.getOr(m, al()).contains(added))
                        && (m.equals(start) || node.state.any(ordered -> caller2callee.containsKey(ordered) && caller2callee.get(ordered).contains(m))));
                result.addAll(candidates.map(c -> node.state.with(c)));
                return result;
            }

            @Override
            public boolean isSolution(Node<YList<String>> node) {
                for (YList<String> callee : caller2callee.values()) if (callee.any(c -> all.containsKey(c) && !node.state.contains(c))) return false;
                return true;
            }
        };
        SSearch.Node<YList<String>> solution = search.nextSolution(100);
        return solution == null ? null : solution.state;
    }


}
