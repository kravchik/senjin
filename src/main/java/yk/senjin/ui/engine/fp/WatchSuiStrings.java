package yk.senjin.ui.engine.fp;

import yk.senjin.ui.core.*;
import yk.senjin.util.GlWindow1;

import java.awt.*;

import static yk.jcommon.fastgeom.Vec4f.v4;

/**
 * Created by Yuri Kravchik on 15.12.17.
 */
public class WatchSuiStrings {
    private SuiEngineFp aui = new SuiEngineFp();
    private GlWindow1 window = new GlWindow1()
            .setSize(800, 800)
            .stopOnEsc()
            .onFirstFrame(this::onFirstPass)
            .onTick(aui);

    public static void main(String[] args) {
        //System.setProperty("awt.useSystemAAFontSettings","lcd"); wont fix font antialiasing on windows
        new WatchSuiStrings().window.start(1);
    }

    public void onFirstPass() {
        aui.getTopPanel().onMouseDown.add(m -> System.out.println("down " + m.keys.get(0).justPressed + " " + m.keys.get(1).justPressed));
        aui.getTopPanel().onMouseUp.add(m -> System.out.println(  "up   " + m.keys.get(0).justPressed + " " + m.keys.get(1).justPressed));

        SuiPanelDbgRect rect = new SuiPanelDbgRect();
        rect.bodyColor = v4(1, 0, 0, 1);
        rect.pos = new SuiPositions(){{right=50f;top=20f;W=50f;H=50f;}};
        aui.getTopPanel().add(rect);


        aui.getTopPanel().add(
                new SuiPanel(){{
                    pos = new SuiPosHBox(){{interval=10;
                        belowCenter =0f;right=5f;}};}}.add(
                        new SuiPanelDbgRect(){{bodyColor = v4(0.5f, 0.5f, 0.8f, 1);
                            pos = new SuiPositions(){{W=50f;H=50f;}};}},
                        new SuiPanelDbgRect(){{bodyColor = v4(0.5f, 0.5f, 0.8f, 1);
                            pos = new SuiPositions(){{W=50f;H=50f;}};}},
                        new SuiPanelDbgRect(){{bodyColor = v4(0.5f, 0.5f, 0.8f, 1);
                            pos = new SuiPositions(){{W=50f;H=50f;}};}}
                ),
                new SuiPanel(){{
                    pos = new SuiPosVBox(){{interval=10;
                        rightCenter =0f;bottom=5f;}};}}.add(
                        new SuiPanelDbgRect(){{bodyColor = v4(1, 0, 0, 1);
                            pos = new SuiPositions(){{W=50f;H=50f;}};}},
                        new SuiPanelDbgRect(){{bodyColor = v4(1, 0, 0, 1);
                            pos = new SuiPositions(){{W=50f;H=50f;}};}},
                        new SuiPanelDbgRect(){{bodyColor = v4(1, 0, 0, 1);
                            pos = new SuiPositions(){{W=50f;H=50f;}};}}
                ),

                new SuiPanelDbgRect(){{bodyColor = v4(1, 0.4f, 0, 1);
                    pos = new SuiPositions(){{left=50f;W=100f;H=600f;}};}},

                new SuiPanelString("Hello bold world! :)") {{
                    pos = new SuiPosString(){{left=10f;top=0f;}};fontStyle= Font.BOLD;}},
                new SuiPanelString("Hello pixel world! :)") {{
                    pos = new SuiPosString(){{
                    left=10f;top=40f;}};fontAntialiased=false;fontSize=18;scale=3;fontName="Ubuntu Condensed";}},

                new SuiPanelString("Hello antialiased world! :)") {{
                    pos = new SuiPosString(){{left=10f;top=100f;}};fontSize=24;}},
                new SuiPanelString("Hello aliased world! :)") {{
                    pos = new SuiPosString(){{left=10f;top=150f;}};fontAntialiased=false;fontSize=24;}},

                new SuiPanelString("Scale with mipmap! :)") {{
                    pos = new SuiPosString(){{left=10f;top=190f;}};fontSize=24;fontMipmap=true;scale=0.5f;}},
                new SuiPanelString("Scale without mipmap! :)") {{
                    pos = new SuiPosString(){{left=10f;top=210f;}};fontAntialiased=false;fontSize=24;scale=0.5f;}},
                new SuiPanelString("Scale with mipmap! :)") {{
                    pos = new SuiPosString(){{left=10f;top=230f;}};fontSize=24;fontMipmap=true;scale=0.3f;}},
                new SuiPanelString("Scale without mipmap! :)") {{
                    pos = new SuiPosString(){{left=10f;top=240f;}};fontAntialiased=false;fontSize=24;scale=0.3f;}},
                rect
        );

    }
}
