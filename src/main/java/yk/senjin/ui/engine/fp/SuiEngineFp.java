package yk.senjin.ui.engine.fp;

import yk.jcommon.fastgeom.Vec2i;
import yk.senjin.ui.core.Sui;
import yk.senjin.ui.core.SuiInputControl;
import yk.senjin.ui.core.SuiPanel;
import yk.senjin.ui.core.SuiSkinner1;
import yk.senjin.util.GlfwWindow;
import yk.senjin.util.Tickable;

/**
 * Created by Yuri Kravchik on 01.03.18.
 */
public class SuiEngineFp implements Tickable {
    public Sui sui;

    public SuiRendererFp auiRenderer;
    public SuiInputControl auiInput = new SuiInputControl();
    public SuiSkinner1 skinner1 = new SuiSkinner1();

    public SuiEngineFp() {
        this(new SuiPanel());
    }

    public SuiEngineFp(SuiPanel topPanel) {
        sui = new Sui();
        sui.topPanel = topPanel;
        auiRenderer = new SuiRendererFp();
        sui.fonts = auiRenderer.fonts;
        getTopPanel().sui = sui;
        getTopPanel().setSuiInChildren();
    }

    @Override
    public void tick(float dt) {
        recalcLayout();//TODO on changes only
        auiInput.tick(dt);
        auiRenderer.render(getTopPanel());
    }

    public SuiEngineFp init(GlfwWindow win) {
        //Vec2i wh = win.sizePixels;
        Vec2i wh = GlfwWindow.getWindowsUxSize(win.handle);


        auiInput.init(win.handle);
        auiInput.mouse.topBottom = true;
        auiInput.mouse.height = wh.y;
        auiInput.top = getTopPanel();
        getTopPanel().pos.W = (float)wh.x;
        getTopPanel().pos.H = (float)wh.y;
        return this;
    }

    public void recalcLayout() {
        if (auiRenderer == null) return;
        getTopPanel().calcSize();
        getTopPanel().pos.calcPos(getTopPanel());
        getTopPanel().pos.localToGlobal(getTopPanel());
    }

    public SuiPanel getTopPanel() {
        return sui.topPanel;
    }

    public int getWindowW() {
        return (int) sui.topPanel.resultW;
    }

    public int getWindowH() {
        return (int) sui.topPanel.resultH;
    }
}
