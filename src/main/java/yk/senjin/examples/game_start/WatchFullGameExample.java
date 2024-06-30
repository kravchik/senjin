package yk.senjin.examples.game_start;

import yk.jcommon.utils.Rnd;
import yk.senjin.examples.game_start.game.ExampleGameObject;
import yk.senjin.examples.game_start.game.ExampleGameScreen;
import yk.senjin.examples.game_start.game.ExampleGameScreenViaFp;
import yk.senjin.ui.core.SuiPanelDbgRect;
import yk.senjin.ui.engine.fp.SuiEngineFp;
import yk.senjin.viewers.GlWindow1;
import yk.ycollections.YList;

import static yk.jcommon.fastgeom.Vec4f.v4;
import static yk.senjin.examples.game_start.MenuItem.back;
import static yk.senjin.examples.game_start.MenuItem.submenu;
import static yk.senjin.examples.game_start.WatchMenusExample.*;
import static yk.senjin.ui.core.SuiPositions.pos;
import static yk.ycollections.YArrayList.al;

/**
 * 19.06.2024
 */
public class WatchFullGameExample {
    private final SuiEngineFp engine = new SuiEngineFp();

    private final GlWindow1 window = new GlWindow1()
        .setUxSize(800, 800)
        .stopOnEsc()
        .onWindowReady(wh -> engine.init(wh))
        .onTick(engine)
        .onTick(this::tick);

    private MenuStackViaSui mss = showMainMenu();
    public ExampleGameScreenViaFp gameRenderer;

    public static void main(String[] args) {
        new WatchFullGameExample().window.start(1);
    }

    private final YList<Runnable> laters = al();
    private Runnable later(Runnable r) {
        return () -> laters.add(r);
    }

    private void tick(float dt) {
        if (mss != null) mss.tick();
        if (gameRenderer != null) gameRenderer.tick(dt);

        for (Runnable later : laters) later.run();
        laters.clear();
    }

    private MenuStackViaSui showMainMenu() {
        return new MenuStackViaSui(new MenuStack(createMainMenu(later(() -> startGame()), proposeExit(window))))
            .reg(engine, engine.getTopPanel());
    }

    private void startGame() {
        mss.unreg(engine);
        mss = null;
        gameRenderer = new ExampleGameScreenViaFp(new ExampleGameScreen());
        gameRenderer.onMenu = later(() -> initInGameMenu());
        gameRenderer.reg(engine, engine.getTopPanel());
        for (int i = 0; i < 1000; i++) gameRenderer.screen.oo.add(new ExampleGameObject(Rnd.instance.nextVec2f().mul(800)));
    }

    private void initInGameMenu() {
        gameRenderer.isFocused = false;

        MenuMenu mm = new MenuMenu("Main Menu", false, al(
            back("Return"),
            submenu("Settings", true, () -> createSettingsMenu()),
            submenu("Exit", true, () -> createConfirmation("Exit game?", later(() -> {
                gameRenderer.unreg(engine);
                gameRenderer = null;
                mss.unreg(engine);
                mss = showMainMenu();
            })))
        ));
        mm.onBackAction = later(() -> {
            mss.unreg(engine);
            mss = null;
            gameRenderer.isFocused = true;
        });
        mss = new MenuStackViaSui(new MenuStack(mm),
                    new SuiPanelDbgRect(pos().widthRatio(1f).heightRatio(1f), v4(0, 0, 0, 0.8f)))
                .reg(engine, engine.getTopPanel());
    }


}
