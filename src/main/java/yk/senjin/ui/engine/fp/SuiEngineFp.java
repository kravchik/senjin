package yk.senjin.ui.engine.fp;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import yk.senjin.ui.core.Sui;
import yk.senjin.ui.core.SuiMouseControl;
import yk.senjin.ui.core.SuiPanel;
import yk.senjin.ui.core.SuiSkinner1;
import yk.senjin.util.Tickable;

/**
 * Created by Yuri Kravchik on 01.03.18.
 */
public class SuiEngineFp implements Tickable {
    public Sui sui;

    public SuiRendererFp auiRenderer;
    public SuiMouseControl auiInput = new SuiMouseControl();
    public SuiSkinner1 skinner1 = new SuiSkinner1();
    private boolean isInited;

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
        if (!isInited) init();
        recalcLayout();//TODO on changes only
        auiInput.tick(dt);
        auiRenderer.render(getTopPanel());
    }

    private void init() {
        //we can hard wire this because we depend on AuiRenderer1 anyway
        DisplayMode displayMode = Display.getDisplayMode();
        int w = displayMode.getWidth();
        int h = displayMode.getHeight();
        auiInput.mouse.topBottom = true;
        auiInput.mouse.height = h;
        auiInput.top = getTopPanel();
        getTopPanel().pos.W = (float)w;
        getTopPanel().pos.H = (float)h;
        isInited = true;
    }

    public void recalcLayout() {
        if (auiRenderer == null) return;
        getTopPanel().pos.calcSize(getTopPanel());
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
