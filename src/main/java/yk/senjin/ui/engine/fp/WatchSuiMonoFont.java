package yk.senjin.ui.engine.fp;

import yk.senjin.ui.core.SuiPanel;
import yk.senjin.ui.core.SuiPanelString;
import yk.senjin.ui.core.SuiPosVBox;
import yk.senjin.viewers.GlWindow1;

/**
 * Created by Yuri Kravchik on 2023.03.23
 *
 * Also to make sure that string length is calculated right
 */
public class WatchSuiMonoFont {
    private SuiEngineFp sui = new SuiEngineFp();
    private GlWindow1 window = new GlWindow1()
            .stopOnEsc()
            .onWindowReady(wh -> sui.init(wh))
            .onTick(sui);

    public static void main(String[] args) {
        WatchSuiMonoFont app = new WatchSuiMonoFont();

        app.sui.getTopPanel().add(new SuiPanel(new SuiPosVBox()).add(
                createString("1234123412341234124jveiowjfvew09jf390jfr"),
                createString("----------------------------------------"),
                createString("!!!!!!!!!\33[1m!!!!!!!!!!!!!!!!\33[0m!!!!!!!!!!!!!!!"),
                createString("!!!!!!!!!\33[3m!!!!!!!!!!!!!!!!\33[0m!!!!!!!!!!!!!!!"),
                createString("!!!!!!!!!\33[38;2;255;0;0m!!!!!!!!!!!!!!!!\33[0m!!!!!!!!!!!!!!!"),
                createString("!                .                     !")
        ));

        //TODO size based on children?
        SuiPanelString sps = (SuiPanelString) app.sui.getTopPanel().children.first().children.first();
        sps.calcSize();
        app.sui.getTopPanel().pos.W = sps.pos.resultW;
        app.sui.getTopPanel().pos.H = sps.pos.resultH * 20;
        app.sui.recalcLayout();

        app.window.setUxSize(app.sui.getWindowW(), app.sui.getWindowH())
                  .start(1);
    }

    private static SuiPanelString createString(String fileName) {
        SuiPanelString result = new SuiPanelString(fileName);
        result.fontName = "Ubuntu Mono";
        return result;
    }

}
