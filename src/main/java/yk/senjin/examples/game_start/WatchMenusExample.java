package yk.senjin.examples.game_start;

import yk.senjin.ui.engine.fp.SuiEngineFp;
import yk.senjin.viewers.GlWindow1;

import java.util.function.Supplier;

import static yk.senjin.examples.game_start.MenuItem.*;
import static yk.ycollections.YArrayList.al;

/**
 * 09.06.2024
 */
public class WatchMenusExample {

    private final SuiEngineFp engine = new SuiEngineFp();

    private final GlWindow1 window = new GlWindow1()
        .setUxSize(800, 800)
        //.stopOnEsc()
        .onWindowReady(wh -> engine.init(wh))
        //.onFirstFrame(() -> onFirstPass())
        .onTick(engine)
        .onTick(dt -> tick());

    public static void main(String[] args) {
        new WatchMenusExample().window.start(1);
    }

    MenuStackViaSui mss = new MenuStackViaSui(
        new MenuStack(createMainMenu(() -> System.out.println("STARTING NEW GAME"), proposeExit(window))))
        .reg(engine, engine.getTopPanel());

    public void tick() {
        mss.tick();
    }


    public static MenuMenu createMainMenu(Runnable onStartGame, Supplier<MenuMenu> onBack) {
        MenuMenu result = new MenuMenu("Main Menu", false, al(
            action("New Game", true, onStartGame),
            submenu("Settings", true, () -> createSettingsMenu()),
            submenu("Exit", true, onBack)
        ));
        result.onBackMenu = onBack;
        return result;
    }

    public static Supplier<MenuMenu> proposeExit(GlWindow1 window) {
        return () -> createConfirmation("Exit?", () -> window.requestStop());
    }

    public static MenuMenu createConfirmation(String question, Runnable action) {
        return new MenuMenu(question, true, al(
            action("Yes", true, action),
            back("Back")));
    }

    public static MenuMenu createSettingsMenu() {
        return new MenuMenu("Settings", false, al(
            submenu("Display", false, () -> null),
            submenu("Controls", false, () -> null),
            back("Back")
        ));
    }

}
