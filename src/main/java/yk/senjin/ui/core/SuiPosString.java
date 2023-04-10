package yk.senjin.ui.core;

import yk.senjin.ui.engine.fp.SuiFont;

/**
 * Created by yuri at 2023.03.22
 */
public class SuiPosString extends SuiPositions {

    @Override
    public void calcSize(SuiPanel panel) {
        SuiPanelString as = (SuiPanelString) panel;
        if (as.isChanged) {
            SuiFont font = as.sui.fonts.getFont(as);
            as.pos.resultW = (float)font.getWidth(as.getString()) * as.scale;
            as.pos.resultH = (float)font.getHeight() * as.scale;
            as.isChanged = false;
        }
    }
}
