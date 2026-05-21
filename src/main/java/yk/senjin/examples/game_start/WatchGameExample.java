package yk.senjin.examples.game_start;

import yk.jcommon.utils.Rnd;
import yk.senjin.examples.game_start.game.ExampleGameObject;
import yk.senjin.examples.game_start.game.ExampleGameScreen;
import yk.senjin.examples.game_start.game.ExampleGameScreenViaFp;
import yk.senjin.ui.engine.fp.SuiEngineFp;

/**
 * 20.06.2024
 */
public class WatchGameExample {
    private final SuiEngineFp engine = new SuiEngineFp().winSetup(win -> win
            .setUxSize(800, 800)
            .stopOnEsc()
            .onFirstFrame(() -> onFirstPass())
            .onTick(this::tick)
    );

    private void onFirstPass() {
        gameRenderer.reg(engine, engine.getTopPanel());
        for (int i = 0; i < 1000; i++) gameRenderer.screen.oo.add(new ExampleGameObject(Rnd.instance.nextVec2f().mul(800)));
    }

    public static void main(String[] args) {
        new WatchGameExample().engine.suiWindow.start(1);
    }
    public ExampleGameScreenViaFp gameRenderer = new ExampleGameScreenViaFp(new ExampleGameScreen());
    private void tick(float dt) {
        if (gameRenderer != null) gameRenderer.tick(dt);
    }

}
