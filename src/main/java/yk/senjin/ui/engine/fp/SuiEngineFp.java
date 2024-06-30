package yk.senjin.ui.engine.fp;

import yk.jcommon.fastgeom.Vec2i;
import yk.jcommon.utils.BadException;
import yk.senjin.ui.core.Sui;
import yk.senjin.ui.core.SuiInputControl;
import yk.senjin.ui.core.SuiPanel;
import yk.senjin.util.GlfwWindow;
import yk.senjin.util.Tickable;

import java.util.function.Consumer;

/**
 * Created by Yuri Kravchik on 01.03.18.
 */
public class SuiEngineFp implements Tickable {
    public Sui sui;
    public SuiRendererFp renderer;
    public SuiInputControl input = new SuiInputControl();
    private GlfwWindow window;

    public SuiEngineFp() {
        this(new SuiPanel());
    }

    public SuiEngineFp(SuiPanel topPanel) {
        sui = new Sui();
        sui.topPanel = topPanel;
        renderer = new SuiRendererFp();
        sui.fonts = renderer.fonts;
        getTopPanel().sui = sui;
        getTopPanel().setSuiInChildren();
    }

    @Override
    public void tick(float dt) {
        recalcLayout();
        input.tick(dt);
        renderer.render(getTopPanel());
    }

    public SuiEngineFp init(GlfwWindow win) {
        if (window != null) BadException.shouldNeverReachHere();
        this.window = win;
        Vec2i wh = win.sizeUx;
        input.init(win.handle);
        input.mouse.topBottom = true;
        input.mouse.height = wh.y;
        input.top = getTopPanel();
        getTopPanel().pos.W = (float)wh.x;
        getTopPanel().pos.H = (float)wh.y;
        return this;
    }

    public void recalcLayout() {
        getTopPanel().calcSize();
        getTopPanel().pos.calcPos(getTopPanel());
        getTopPanel().pos.localToGlobal(getTopPanel());
    }

    public SuiPanel getTopPanel() {
        return sui.topPanel;
    }

    public SuiEngineFp forThis(Consumer<SuiEngineFp> consumer) {
        consumer.accept(this);
        return this;
    }
}
