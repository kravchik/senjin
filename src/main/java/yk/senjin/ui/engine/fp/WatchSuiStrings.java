package yk.senjin.ui.engine.fp;

import yk.senjin.ui.core.SuiPanelDbgRect;
import yk.senjin.ui.core.SuiPanelString;
import yk.senjin.ui.core.SuiPositions;
import yk.senjin.viewers.GlWindow1;

import java.awt.*;

import static yk.jcommon.fastgeom.Vec4f.v4;
import static yk.senjin.ui.core.SuiPanel.panel;
import static yk.senjin.ui.core.SuiPosHBox.hbox;
import static yk.senjin.ui.core.SuiPosVBox.vbox;
import static yk.senjin.ui.core.SuiPositions.pos;

/**
 * Created by Yuri Kravchik on 15.12.17.
 */
public class WatchSuiStrings {
    private SuiEngineFp aui = new SuiEngineFp();
    private GlWindow1 window = new GlWindow1()
            .setUxSize(800, 800)
            .stopOnEsc()
            .onWindowReady(wh -> aui.init(wh))
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
                panel(hbox().interval(10).centerY(0f).right(5f),
                    new SuiPanelDbgRect(pos().W(50f).H(50f), v4(0.5f, 0.5f, 0.8f, 1)),
                    new SuiPanelDbgRect(pos().W(50f).H(50f), v4(0.5f, 0.5f, 0.8f, 1)),
                    new SuiPanelDbgRect(pos().W(50f).H(50f), v4(0.5f, 0.5f, 0.8f, 1))
                ),
                panel(vbox().interval(10).centerX(0f).bottom(5f),
                        new SuiPanelDbgRect(pos().W(50f).H(50f), v4(1, 0, 0, 1)),
                        new SuiPanelDbgRect(pos().W(50f).H(50f), v4(1, 0, 0, 1)),
                        new SuiPanelDbgRect(pos().W(50f).H(50f), v4(1, 0, 0, 1))
                ),

                new SuiPanelDbgRect(pos().left(50f).W(100f).H(600f), v4(1, 0.4f, 0, 1)),

                new SuiPanelString(pos().left(10f).top(0f), "Hello bold world! :)") {{
                    fontStyle= Font.BOLD;}},

                //TODO fix for Mac (render on another platform?)
                new SuiPanelString(pos().left(10f).top(40f), "Hello pixel world! :)") {{
                    fontAntialiased=false;fontSize=18;scale=3;fontName="Ubuntu Condensed";}},

                new SuiPanelString(pos().left(10f).top(100f), "Hello antialiased world! :)") {{
                    fontSize=24;}},
                new SuiPanelString(pos().left(10f).top(150f), "Hello aliased world! :)") {{
                    fontAntialiased=false;fontSize=24;}},

                new SuiPanelString(pos().left(10f).top(190f), "Scale with mipmap! :)") {{
                    scale=0.5f;fontMipmap=true;}},
                new SuiPanelString(pos().left(10f).top(210f), "Scale without mipmap! :)") {{
                    scale=0.5f;fontAntialiased=false;}},
                new SuiPanelString(pos().left(10f).top(230f), "Scale with mipmap! :)") {{
                    scale=0.3f;fontMipmap=true;}},
                new SuiPanelString(pos().left(10f).top(240f), "Scale without mipmap! :)") {{
                    scale=0.3f;fontAntialiased=false;}},
                rect
        );

    }
}
