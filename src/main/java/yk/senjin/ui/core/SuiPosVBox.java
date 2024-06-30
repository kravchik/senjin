package yk.senjin.ui.core;

import static yk.jcommon.utils.MyMath.max;

public class SuiPosVBox extends SuiPositions {
    public float interval;

    public SuiPosVBox interval(float interval) {
        this.interval = interval;
        return this;
    }

    public static SuiPosVBox vbox() {
        return new SuiPosVBox();
    }

    @Override
    public void calcSize(SuiPanel panel) {
        float resW = 0;
        float resH = 0;
        for (SuiPanel child : panel.children) {
            child.calcSize();

            resW = max(resW, child.pos.resultW);
            resH += child.pos.resultH + interval;
        }
        resH -= interval;
        if (resultW == null) resultW = resW + padding * 2;
        //TODO reuse percentW, etc
        if (resultH == null) resultH = resH + padding * 2;
    }

    @Override
    public void calcPos(SuiPanel panel) {
        float bot = 0;
        for (SuiPanel child : panel.children) {
            if (child.pos.resultLocalY == null) {
                child.pos.calcPos(child);
                child.pos.resultLocalY += bot;
            }
            bot += child.pos.resultH + interval;
        }
        calcPosSelf(panel);
    }

}
