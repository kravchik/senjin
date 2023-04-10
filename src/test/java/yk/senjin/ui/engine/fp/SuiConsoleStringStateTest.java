package yk.senjin.ui.engine.fp;

import org.junit.Test;
import yk.jcommon.fastgeom.Vec4f;

import static org.junit.Assert.assertEquals;

/**
 * Created by yuri at 2023.04.10
 */
public class SuiConsoleStringStateTest {
    @Test
    public void test1() {
        //System.out.println("\033[38;2;255;0;0m Hello!");
        assertEquals("scss{fontColor=w: 1.0 x: 0.0 y: 0.0 z: 0.0, bold=false, italic=false, curPos=4}",
                new SuiConsoleStringState(Vec4f.AXIS_W).parseModifier("\033[0m", 0).info());
        assertEquals("scss{fontColor=w: 1.0 x: 1.0 y: 0.0 z: 0.0, bold=false, italic=false, curPos=15}",
                new SuiConsoleStringState(Vec4f.AXIS_W).parseModifier("\033[38;2;255;0;0m Hello!", 0).info());
        assertEquals("scss{fontColor=w: 1.0 x: 1.0 y: 0.0 z: 0.0, bold=false, italic=true, curPos=17}",
                new SuiConsoleStringState(Vec4f.AXIS_W).parseModifier("\033[3;38;2;255;0;0m Hello!", 0).info());
        assertEquals("scss{fontColor=w: 1.0 x: 1.0 y: 0.0 z: 0.0, bold=true, italic=false, curPos=17}",
                new SuiConsoleStringState(Vec4f.AXIS_W).parseModifier("\033[38;2;255;0;0;1m Hello!", 0).info());
    }
}