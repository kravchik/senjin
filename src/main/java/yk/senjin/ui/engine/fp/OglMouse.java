package yk.senjin.ui.engine.fp;

import org.lwjgl.BufferUtils;
import yk.jcommon.fastgeom.Vec2i;
import yk.ycollections.YList;

import java.nio.DoubleBuffer;
import java.util.function.Consumer;

import static org.lwjgl.glfw.GLFW.*;
import static yk.ycollections.YArrayList.al;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 27/02/16
 * Time: 12:29
 * //TODO rename OglMouse
 */
public class OglMouse {
    public Vec2i current;
    public boolean topBottom;
    public int height;

    public boolean lDown;
    public boolean rDown;
    public int dWheel;

    public YList<Consumer<Integer>> onMousePressedListeners = al();
    public YList<Consumer<Integer>> onMouseReleasedListeners = al();
    public YList<Consumer<Integer>> onWheelListeners = al();

    private final DoubleBuffer db_x = BufferUtils.createDoubleBuffer(1);
    private final DoubleBuffer db_y = BufferUtils.createDoubleBuffer(1);
    private long windowHandle;


    public OglMouse init(long windowHandle) {
        this.windowHandle = windowHandle;
        glfwSetScrollCallback(windowHandle, (win, dx, dy) -> {
            for (Consumer<Integer> wl : onWheelListeners) wl.accept((int) dx);
        });

        glfwSetMouseButtonCallback(windowHandle, (win, button, action, mods) -> {
            if (action == GLFW_PRESS) for (Consumer<Integer> l : onMousePressedListeners) l.accept(button);
            if (action == GLFW_RELEASE) for (Consumer<Integer> l : onMouseReleasedListeners) l.accept(button);
        });
        return this;
    }

    public void tick(float dt) {
        db_x.rewind();
        db_y.rewind();
        glfwGetCursorPos(windowHandle, db_x, db_y);

        current = new Vec2i((int) db_x.get(), (int) (!topBottom ? height - db_y.get() - 1 : db_y.get()));

        lDown = glfwGetMouseButton(windowHandle, GLFW_MOUSE_BUTTON_1) == GLFW_PRESS;
        rDown = glfwGetMouseButton(windowHandle, GLFW_MOUSE_BUTTON_2) == GLFW_PRESS;

        if (dWheel != 0) for (Consumer<Integer> listener : onWheelListeners) listener.accept(dWheel);
    }

}
