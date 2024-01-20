package yk.senjin.ui.engine.fp;

import yk.senjin.ui.core.KeyboardCodeToString;
import yk.senjin.util.Tickable;
import yk.ycollections.YList;
import yk.ycollections.YSet;

import java.util.function.Consumer;

import static org.lwjgl.glfw.GLFW.*;
import static yk.ycollections.YArrayList.al;
import static yk.ycollections.YHashSet.hs;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 11/02/16
 * Time: 20:42
 */
public class OglKeyboard implements Tickable {
    private final YList<Consumer<String>> onKeyDown = al();
    private final YList<Consumer<String>> onKeyUp = al();
    private final YSet<String> pressed = hs();

    public OglKeyboard() {
    }

    public OglKeyboard(Consumer<String> onKeyDown, Consumer<String> onKeyUp) {
        if (onKeyDown != null) this.onKeyDown.add(onKeyDown);
        if (onKeyUp != null) this.onKeyUp.add(onKeyUp);
    }

    public OglKeyboard init(long windowHandle) {
        glfwSetKeyCallback(windowHandle, (win, key, scancode, action, mods) -> {
            String keyName = KeyboardCodeToString.getString(key);
            if (action == GLFW_PRESS) {
                pressed.add(keyName);
                for (Consumer<String> consumer : onKeyDown) consumer.accept(keyName);
            }
            if (action == GLFW_RELEASE) {
                pressed.remove(keyName);
                for (Consumer<String> consumer : onKeyUp) consumer.accept(keyName);
            }
        });
        return this;
    }

    public boolean isPressed(String key) {
        return pressed.contains(key);
    }

    @Override
    public void tick(float dt) {
    }
}
