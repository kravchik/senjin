package yk.senjin.ui.engine.fp;

import yk.senjin.ui.core.SuiPanelString;
import yk.senjin.viewers.GlWindow1;

import static yk.senjin.ui.core.SuiPanel.panel;
import static yk.senjin.ui.core.SuiPosVBox.vbox;

/**
 * Created by Yuri Kravchik on 2023.03.23
 *
 * Also to make sure that string length is calculated right
 */
public class WatchSuiMonoFont {
    private final SuiEngineFp sui = new SuiEngineFp(panel(vbox()).add(
        createString("12345678 1 2345678 2 2345678 3 2345678 4"),
        createString("1234123412341234124jveiowjfvew09jf390jfr"),
        createString("---WWW----------------------------------"),
        createString(" ------------------------------WWW-----"),
        createString("  ------------------------------------"),
        //createString("!!!!!!!!!\33[1m!!!!!!!!!!!!!!!!\33[0m!!!!!!!!!!!!!!!"),
        //createString("!!!!!!!!!\33[3m!!!!!!!!!!!!!!!!\33[0m!!!!!!!!!!!!!!!"),
        //createString("!!!!!!!!!\33[38;2;255;0;0m!!!!!!!!!!!!!!!!\33[0m!!!!!!!!!!!!!!!"),
        createString("!     WWWWWWW    .                     !"),
        createString("!      iiiiiii   .                     !"),
        createString("!       .......  .                     !"),
        createString("!                .                     !")
    ));
    private GlWindow1 window = new GlWindow1()
            .stopOnEsc()
            .onWindowReady(wh -> sui.init(wh))
            .onTick(sui);

    public static void main(String[] args) {
        WatchSuiMonoFont app = new WatchSuiMonoFont();
        app.sui.recalcLayout();
        app.window.setUxSize(app.sui.getTopPanel().pos.resultW.intValue(), app.sui.getTopPanel().pos.resultH.intValue());
        app.window.start(1);
    }

    private static SuiPanelString createString(String fileName) {
        SuiPanelString result = new SuiPanelString(fileName);
        //result.fontName = "Ubuntu Mono";
        result.fontName = "Monospaced";
        return result;
    }

}
