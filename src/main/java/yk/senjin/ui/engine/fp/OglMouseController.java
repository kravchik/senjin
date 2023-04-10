package yk.senjin.ui.engine.fp;

import org.lwjgl.input.Mouse;
import yk.jcommon.collections.YList;
import yk.jcommon.fastgeom.Vec2i;

import java.util.function.Consumer;

import static yk.jcommon.collections.YArrayList.al;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 27/02/16
 * Time: 12:29
 */
public class OglMouseController {
    public Vec2i current;
    public boolean topBottom;
    public int height;

    public boolean lDown;
    public boolean rDown;
    public int dWheel;

    public YList<Consumer<Integer>> onMousePressedListeners = al();
    public YList<Consumer<Integer>> onMouseReleasedListeners = al();
    public YList<Consumer<Integer>> onWheelListeners = al();

    public void tick(float dt) {
        current = new Vec2i(Mouse.getX(), topBottom ? height - Mouse.getY() - 1 : Mouse.getY());
        lDown = Mouse.isButtonDown(0);
        rDown = Mouse.isButtonDown(1);
        dWheel = Mouse.getDWheel();

        if (dWheel != 0) for (Consumer<Integer> listener : onWheelListeners) listener.accept(dWheel);

        //can get press/unpress in sequence in one frame
        while (Mouse.next()){
            if (Mouse.getEventButtonState()) {
                int event = Mouse.getEventButton();
                if (event == 0) lDown = true;
                if (event == 1) rDown = true;

                for (Consumer<Integer> listener : onMousePressedListeners) {
                    listener.accept(event);
                }

            } else {
                int event = Mouse.getEventButton();
                if (event > -1) {

                    if (event == 0) lDown = false;
                    if (event == 1) rDown = false;

                    for (Consumer<Integer> listener : onMouseReleasedListeners) listener.accept(event);
                }
                //if (Mouse.getEventButton() == -1) //to do?
            }
        }

    }

}
