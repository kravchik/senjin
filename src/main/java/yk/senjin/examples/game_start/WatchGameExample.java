package yk.senjin.examples.game_start;

import yk.jcommon.utils.Rnd;
import yk.senjin.examples.game_start.game.ExampleGameObject;
import yk.senjin.examples.game_start.game.ExampleGameScreen;
import yk.senjin.examples.game_start.game.ExampleGameScreenViaFp;
import yk.senjin.ui.engine.fp.SuiEngineFp;
import yk.senjin.viewers.GlWindow1;

/**
 * 20.06.2024
 */
public class WatchGameExample {
    private final SuiEngineFp engine = new SuiEngineFp();

    private final GlWindow1 window = new GlWindow1()
        .setUxSize(800, 800)
        .stopOnEsc()
        .onWindowReady(wh -> engine.init(wh))
        .onTick(engine)
        .onTick(this::tick);

    public static void main(String[] args) {
        new WatchGameExample().window.start(1);
    }
    public ExampleGameScreenViaFp gameRenderer = new ExampleGameScreenViaFp(new ExampleGameScreen());
    {
        gameRenderer.reg(engine, engine.getTopPanel());
        for (int i = 0; i < 1000; i++) gameRenderer.screen.oo.add(new ExampleGameObject(Rnd.instance.nextVec2f().mul(800)));
    }
    private void tick(float dt) {
        if (gameRenderer != null) gameRenderer.tick(dt);
    }

}
