package yk.senjin.examples.game_start;

import yk.jcommon.utils.MyMath;
import yk.senjin.ui.core.SuiPanel;
import yk.senjin.ui.core.SuiPanelString;
import yk.senjin.ui.engine.fp.SuiEngineFp;

import java.util.function.Consumer;

import static yk.jcommon.fastgeom.Vec4f.v4;
import static yk.senjin.ui.core.SuiPanel.panel;
import static yk.senjin.ui.core.SuiPosVBox.vbox;
import static yk.senjin.ui.core.SuiPositions.pos;

/**
 * 19.06.2024
 */
public class MenuStackViaSui {
    private SuiPanel parent;
    private final SuiPanel panel;
    private final Consumer<String> keyListener;
    private MenuMenu lastMenu;
    private final MenuStack menu;

    public MenuStackViaSui(MenuStack menu) {
        this(menu, panel(pos().centerX(0f).centerY(0f).widthRatio(1f).heightRatio(1f)));
    }
    public MenuStackViaSui(MenuStack menu, SuiPanel panel) {
        this.menu = menu;
        this.panel = panel;

        keyListener = s -> {
            switch (s) {
                case "DOWN": menu.down();break;
                case "UP": menu.up();break;
                case "ENTER": menu.act();break;
                case "RIGHT": menu.act();break;
                case "LEFT": menu.cancel();break;
                case "ESCAPE": menu.cancel();break;
            }
        };
    }

    public MenuStackViaSui reg(SuiEngineFp engine, SuiPanel in) {
        parent = in;
        parent.add(panel);
        engine.input.keyboard.onKeyDown.add(keyListener);
        return this;
    }

    public void unreg(SuiEngineFp engine) {
        parent.children.remove(panel);
        parent = null;
        engine.input.keyboard.onKeyDown.remove(keyListener);
    }

    public void tick() {
        MenuMenu curMenu = menu.menuStack.last();
        if (lastMenu != curMenu) {
            panel.children.clear();
            panel.add(createSuiSkin(menu, curMenu));
        }
        lastMenu = curMenu;

    }

    public static SuiPanel createSuiSkin(MenuStack menuTree, MenuMenu menu) {
        return panel(vbox().centerY(0f).centerX(0f),
            new SuiPanelString(menu.label){{color = v4(0, 1f, 0.5f, 1);}},
            new SuiPanel(pos().H(20f)),
            panel(vbox().centerX(0f), menu.items
                .mapWithIndex((i, item) -> new SuiPanel(pos().centerX(0f),
                    new SuiPanelString(pos().centerX(0f).centerY(0f), item.text) {
                        private float targetScale = 1;
                        private float targetCenterX = 0;
                        private float targetCenterY = 0;

                        {
                            onTick.add(dt -> {
                                targetScale = item.isSelected ? 1.2f : 0.9f;
                                color = item.isSelected ? v4(1, 0.2f, 0.2f, 1) : v4(1, 1, 1, 1);
                                if (!item.available) color = v4(0.5f, 0.5f, 0.5f, 0.5f);
                                //targetCenterX = i == menu.selectedIndex ? 0 : 2f / (i - menu.selectedIndex);
                                targetCenterY = i == menu.selectedIndex ? 0 : -5f / (i - menu.selectedIndex);

                                scale = MyMath.mix(scale, targetScale, dt * 10);
                                pos.centerX = MyMath.mix(pos.centerX, targetCenterX, dt * 20);
                                pos.centerY = MyMath.mix(pos.centerY, targetCenterY, dt * 20);
                                pos.resultW = null;
                                pos.resultH = null;
                                pos.resultLocalX = null;
                                pos.resultLocalY = null;
                                isChanged = true;
                                calcSize();
                                pos.calcPosSelf(this);
                            });
                        }
                    })
                    .onMouseEnter(sms -> menu.setSelectedIndex(item))
                ))).onMouseDown(sms -> menuTree.act());
    }
}
