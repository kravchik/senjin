package yk.senjin.ui.engine.fp;

import yk.senjin.ui.core.KeyboardCodeToString;
import yk.senjin.util.Tickable;
import yk.ycollections.YList;
import yk.ycollections.YMap;
import yk.ycollections.YSet;

import java.util.function.Consumer;

import static org.lwjgl.glfw.GLFW.*;
import static yk.ycollections.YArrayList.al;
import static yk.ycollections.YHashMap.hm;
import static yk.ycollections.YHashSet.hs;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 11/02/16
 * Time: 20:42
 *
 * Should be one-per window, as it consumes key events (sets handlers).
 */
public class OglKeyboard implements Tickable {
    public final YMap<String, YList<Runnable>> onKeyPressed = hm();
    public final YList<Consumer<String>> onAnyKeyPressed = al();

    public final YList<Consumer<String>> onAnyKeyDown = al();
    public final YList<Consumer<String>> onAnyKeyUp = al();
    private final YSet<String> pressed = hs();

    public final YSet<String> justPressed = hs();
    public final YSet<String> justReleased = hs();

    private String keyToRepeat;
    private float repeatTimer;
    private boolean repeatFirst;
    private final float repeatFirstTime = 0.5f;
    private final float repeatContinuousTime = 0.1f;

    public OglKeyboard() {
    }

    public OglKeyboard(Consumer<String> onAnyKeyDown, Consumer<String> onAnyKeyUp) {
        if (onAnyKeyDown != null) this.onAnyKeyDown.add(onAnyKeyDown);
        if (onAnyKeyUp != null) this.onAnyKeyUp.add(onAnyKeyUp);
    }

    public OglKeyboard init(long windowHandle) {
        glfwSetKeyCallback(windowHandle, (win, key, scancode, action, mods) -> {
            String keyName = KeyboardCodeToString.getString(key);
            if (action == GLFW_PRESS) {
                pressed.add(keyName);
                justPressed.add(keyName);
                for (Consumer<String> consumer : onAnyKeyDown) consumer.accept(keyName);

                if (onKeyPressed.containsKey(keyName)) for (Runnable listener : onKeyPressed.get(keyName)) listener.run();
                for (Consumer<String> listener : onAnyKeyPressed) listener.accept(keyName);
                keyToRepeat = keyName;
                repeatFirst = true;
                repeatTimer = 0;
            }
            if (action == GLFW_RELEASE) {
                pressed.remove(keyName);
                justReleased.add(keyName);
                for (Consumer<String> consumer : onAnyKeyUp) consumer.accept(keyName);
                if (keyName.equals(keyToRepeat)) keyToRepeat = null;
            }
        });
        return this;
    }

    public boolean isPressed(String key) {
        return pressed.contains(key);
    }

    @Override
    public void tick(float dt) {
        if (keyToRepeat != null) {
            repeatTimer += dt;
            float check = repeatFirst ? repeatFirstTime : repeatContinuousTime;
            if (repeatTimer > check) {
                repeatFirst = false;
                if (onKeyPressed.containsKey(keyToRepeat)) for (Runnable listener : onKeyPressed.get(keyToRepeat)) listener.run();
                for (Consumer<String> consumer : onAnyKeyPressed) consumer.accept(keyToRepeat);
                repeatTimer = repeatTimer - check;
                if (repeatTimer > check) repeatTimer %= check;
            }
        }
    }

    public void clearStates() {
        justPressed.clear();
        justReleased.clear();
    }

    public OglKeyboard onKeyPressed(String key, Runnable listener) {
        onKeyPressed.computeIfAbsent(key, k -> al()).add(listener);
        return this;
    }

    public OglKeyboard removeOnKeyPressed(String key, Runnable listener) {
        onKeyPressed.computeIfAbsent(key, k -> al()).remove(listener);
        return this;
    }
}
