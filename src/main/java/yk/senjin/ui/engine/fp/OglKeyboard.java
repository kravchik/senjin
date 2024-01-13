package yk.senjin.ui.engine.fp;

import org.lwjgl.input.Keyboard;
import yk.senjin.ui.core.KeyboardCodeToString;
import yk.senjin.util.Tickable;
import yk.ycollections.YList;
import yk.ycollections.YSet;

import java.util.function.Consumer;

import static yk.ycollections.YArrayList.al;
import static yk.ycollections.YHashSet.hs;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 11/02/16
 * Time: 20:42
 */
//TODO remove (replace with newer keyboard with enum)
public class OglKeyboard implements Tickable {
    public YList<Consumer<String>> onKeyDown = al();
    public YList<Consumer<String>> onKeyUp = al();

    private YSet<String> pressed = hs();

    public OglKeyboard() {
    }

    public OglKeyboard(Consumer<String> onKeyDown, Consumer<String> onKeyUp) {
        if (onKeyDown != null) this.onKeyDown = al(onKeyDown);
        if (onKeyUp != null) this.onKeyUp = al(onKeyUp);
    }

    public boolean isPressed(String key) {
        return pressed.contains(key);
    }

    @Override
    public void tick(float dt) {
        while (Keyboard.next()) {
            String key = KeyboardCodeToString.getString(Keyboard.getEventKey());
            if (Keyboard.getEventKeyState()) {
                pressed.add(key);
                for (Consumer<String> consumer : onKeyDown) consumer.accept(key);
            } else {
                pressed.remove(key);
                for (Consumer<String> consumer : onKeyUp) consumer.accept(key);
            }
        }
    }
}
