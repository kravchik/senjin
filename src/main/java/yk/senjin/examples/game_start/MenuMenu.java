package yk.senjin.examples.game_start;

import yk.jcommon.utils.MyMath;
import yk.ycollections.YList;

import java.util.function.Supplier;

/**
 * 13.06.2024
 */
public class MenuMenu {

    public String label;
    public int selectedIndex;
    public YList<MenuItem> items;
    private final boolean cycled;
    private boolean canSelectInactive;
    public boolean isActive;

    public Runnable onBackAction;
    public Supplier<MenuMenu> onBackMenu;

    public MenuMenu(String label, boolean cycled, YList<MenuItem> items) {
        this.label = label;
        this.items = items;
        this.cycled = cycled;
        selectedIndex = -1;
        selectInDir(1);
    }

    public void setSelectedIndex(MenuItem item) {
        if (!canSelectInactive && !item.available) return;
        this.selectedIndex = items.indexOf(item);
        update();
    }

    public void onDown() {
        selectInDir(1);
    }

    public void onUp() {
        selectInDir(-1);
    }

    private void selectInDir(int inc) {
        int tryIndex = selectedIndex;
        for (int i = 0; i < items.size(); i++) {
            tryIndex = cycled
                ? MyMath.cycle(tryIndex + inc, items.size())
                : MyMath.clamp(tryIndex + inc, 0, items.size() - 1);
            if (canSelectInactive || items.get(tryIndex).available) {
                selectedIndex = tryIndex;
                break;
            }
        }
        update();
    }

    public void update() {
        for (int i = 0; i < items.size(); i++) items.get(i).isSelected = selectedIndex == i;
    }


}
