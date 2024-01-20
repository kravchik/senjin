package yk.senjin.ui.core;

import static yk.jcommon.utils.MyMath.max;

public class SuiPosHBox extends SuiPositions {
    public float interval;

    public static SuiPosHBox hbox() {
        return new SuiPosHBox();
    }

    public SuiPosHBox interval(float interval) {
        this.interval = interval;
        return this;
    }

    @Override
    public void calcSize(SuiPanel panel) {
        float resW = 0;
        float resH = 0;
        for (SuiPanel child : panel.children) {
            child.calcSize();

            resW += child.pos.resultW + interval;
            resH = max(resH, child.pos.resultH);
        }
        resW -= interval;
        resultW = resW + padding * 2;
        resultH = resH + padding * 2;
    }

    @Override
    public void calcPos(SuiPanel panel) {
        float right = 0;
        for (SuiPanel child : panel.children) {
            child.pos.calcPos(child);
            child.pos.resultLocalX += right;
            right += child.pos.resultW + interval;
        }
        calcPosSelf(panel);
    }

}
