package yk.senjin.ui.core;

import yk.jcommon.fastgeom.Vec4f;

/**
 * Created by Yuri Kravchik on 01.03.18.
 */
public class SuiPanelDbgRect extends SuiPanel {
    public Vec4f bodyColor = null;
    public Vec4f borderColor = null;
    public boolean isSelected;

    public SuiPanelDbgRect() {
    }

    public SuiPanelDbgRect(SuiPositions pos, Vec4f bodyColor) {
        super(pos);
        this.bodyColor = bodyColor;
    }

    public SuiPanelDbgRect(SuiPositions pos, Vec4f bodyColor, Vec4f borderColor) {
        super(pos);
        this.bodyColor = bodyColor;
        this.borderColor = borderColor;
    }

    public SuiPanelDbgRect(Vec4f bodyColor) {
        this.bodyColor = bodyColor;
    }
    public SuiPanelDbgRect(Vec4f bodyColor, Vec4f borderColor) {
        this.bodyColor = bodyColor;
        this.borderColor = borderColor;
    }
}
