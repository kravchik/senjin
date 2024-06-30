package yk.senjin.examples.game_start.game;

import yk.jcommon.utils.BadException;
import yk.senjin.ui.core.SuiPanel;
import yk.senjin.ui.core.SuiPanelDbgRect;
import yk.senjin.ui.core.SuiPositions;
import yk.senjin.ui.engine.fp.SuiEngineFp;
import yk.ycollections.YMap;

import java.util.Map;
import java.util.function.Consumer;

import static yk.jcommon.fastgeom.Vec4f.v4;
import static yk.senjin.ui.core.SuiPositions.pos;
import static yk.ycollections.YHashMap.hm;

/**
 * 19.06.2024
 */
public class ExampleGameScreenViaFp {

    public SuiPanel panel = new SuiPanel();
    public ExampleGameScreen screen;
    YMap<ExampleGameObject, SuiPanel> go2panel = hm();
    public Runnable onMenu;
    public boolean isFocused;
    private final Consumer<String> keyListener;
    private SuiPanel parent;

    public ExampleGameScreenViaFp(ExampleGameScreen screen) {
        this.screen = screen;
        keyListener = key -> {
            if (!isFocused) return;
            if (key.equals("TAB") && onMenu != null) onMenu.run();
        };
    }

    public void reg(SuiEngineFp engine, SuiPanel parent) {
        this.parent = parent;
        parent.add(panel);
        engine.input.keyboard.onKeyDown.add(keyListener);
        isFocused = true;
    }

    public void unreg(SuiEngineFp engine) {
        engine.input.keyboard.onKeyDown.remove(keyListener);
        parent.children.remove(panel);
    }

    public void tick(float dt) {
        if (isFocused) screen.tick(dt);
        for (ExampleGameObject go : screen.oo) if (!go2panel.containsKey(go)) addGo(go);
        for (ExampleGameObject go : go2panel.keySet()) if (!screen.oo.contains(go)) removeGo(go);

        for (Map.Entry<ExampleGameObject, SuiPanel> entry : go2panel.entrySet()) {
            update(entry.getKey(), (SuiPanelDbgRect) entry.getValue());
        }
    }

    public void addGo(ExampleGameObject go) {
        SuiPositions pos = pos();
        SuiPanelDbgRect dbg = new SuiPanelDbgRect(pos, v4(1, 0, 0, 1));

        update(go, dbg);

        go2panel.put(go, dbg);
        panel.add(dbg);
    }

    private void update(ExampleGameObject go, SuiPanelDbgRect dbg) {
        dbg.pos.resultLocalX = go.pos.x;
        dbg.pos.resultLocalY = go.pos.y;
        dbg.pos.size(10, 10);
    }

    public void removeGo(ExampleGameObject go) {
        SuiPanel goPanel = go2panel.remove(go);
        if (goPanel == null) BadException.shouldNeverReachHere();
        panel.children.remove(goPanel);
    }
}
