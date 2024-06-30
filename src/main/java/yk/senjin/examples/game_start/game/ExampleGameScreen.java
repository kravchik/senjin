package yk.senjin.examples.game_start.game;

import yk.jcommon.utils.Rnd;
import yk.ycollections.YList;

import static yk.ycollections.YArrayList.al;

/**
 * 19.06.2024
 */
public class ExampleGameScreen {
    public YList<ExampleGameObject> oo = al();

    public void tick(float dt) {
        for (ExampleGameObject go : oo) {
            go.pos = Rnd.instance.nextVec2f().mul(800);
        }

    }
}
