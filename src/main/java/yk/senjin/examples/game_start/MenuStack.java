package yk.senjin.examples.game_start;

import yk.ycollections.YList;

import static yk.ycollections.YArrayList.al;

/**
 * 14.06.2024
 */
public class MenuStack {
    public YList<MenuMenu> menuStack;

    public MenuStack(MenuMenu topMenu) {
        this.menuStack = al(topMenu);
    }

    private void switchTo(MenuMenu sm) {
        menuStack.last().isActive = false;
        menuStack.add(sm);
        sm.isActive = true;
    }

    private void back() {
        MenuMenu cur = menuStack.last();
        if (cur.onBackAction != null) {
            cur.onBackAction.run();
        } else if (cur.onBackMenu != null) {
            switchTo(cur.onBackMenu.get());
        } else if (menuStack.size() > 1) {
            cur.isActive = false;
            menuStack.remove(menuStack.size() - 1);
            menuStack.last().isActive = true;
        }
    }

    public void up() {
        menuStack.last().onUp();
    }

    public void down() {
        menuStack.last().onDown();
    }

    public void act() {
        MenuMenu menu = menuStack.last();
        MenuItem item = menu.items.get(menu.selectedIndex);
        if (!item.available) return;
        if (item.action != null) item.action.run();
        if (item.nextMenu != null) switchTo(item.nextMenu.get());
        if (item.isBack) back();
    }

    public void cancel() {
        back();
    }
}
