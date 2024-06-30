package yk.senjin.examples.game_start;

import java.util.function.Supplier;

/**
 * 15.06.2024
 */
public class MenuItem {
    public String text;
    public Runnable action;
    public Supplier<MenuMenu> nextMenu;
    public boolean isBack;
    //TO DO onMouseEnter, onMouseExit, onSelected, onDeselected
    public boolean available;
    public boolean isSelected;

    public MenuItem(String text, boolean available) {
        this.text = text;
        this.available = available;
    }

    public static MenuItem back(String text) {
        MenuItem result = new MenuItem(text, true);
        result.isBack = true;
        return result;
    }

    public static MenuItem action(String text, boolean available, Runnable action) {
        MenuItem result = new MenuItem(text, available);
        result.action = action;
        return result;
    }

    public static MenuItem submenu(String text, boolean available, Supplier<MenuMenu> nextMenu) {
        MenuItem result = new MenuItem(text, available);
        result.nextMenu = nextMenu;
        return result;
    }
}
