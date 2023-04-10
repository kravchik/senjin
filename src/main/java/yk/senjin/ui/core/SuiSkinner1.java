package yk.senjin.ui.core;

import static yk.jcommon.fastgeom.Vec4f.v4;

/**
 * Created by Yuri Kravchik on 15.03.18.
 */
public class SuiSkinner1 {
    public void skin(SuiPanel panel) {
        for (SuiPanel child : panel.children) {
            skin(child);
        }
        if (panel instanceof SuiPanelFixingButton) {
            SuiPanelFixingButton b = (SuiPanelFixingButton) panel;
            b.downSkin = new SuiPanelDbgRect(v4(0.2f, 0.2f, 1, 1));
            b.upSkin = new SuiPanelDbgRect(v4(0.2f, 0.2f, 0.2f, 1));
        }

    }
}
