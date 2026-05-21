package yk.senjin.ui.engine.fp;

import yk.jcommon.fastgeom.Vec2i;
import yk.ycollections.YList;

import java.util.function.Consumer;

import static org.lwjgl.glfw.GLFW.*;
import static yk.ycollections.YArrayList.al;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 27/02/16
 * Time: 12:29
 */
public class OglMouse {
    public Vec2i posFromTop;//pos, 0,0 is left-top
    public Vec2i posFromBot;//pos, 0,0 is left-bot
    public int height;

    public boolean lDown;
    public boolean rDown;
    public int dWheel;

    public YList<Consumer<Integer>> onMousePressedListeners = al();
    public YList<Consumer<Integer>> onMouseReleasedListeners = al();
    public YList<Consumer<Integer>> onWheelListeners = al();

    private final double[] db_x = new double[]{0};
    private final double[] db_y = new double[]{0};
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
        glfwGetCursorPos(windowHandle, db_x, db_y);

        int y = (int)db_y[0];
        int x = (int) db_x[0];
        posFromTop = new Vec2i(x, y);
        posFromBot = new Vec2i(x, height - y - 1);

        lDown = glfwGetMouseButton(windowHandle, GLFW_MOUSE_BUTTON_1) == GLFW_PRESS;
        rDown = glfwGetMouseButton(windowHandle, GLFW_MOUSE_BUTTON_2) == GLFW_PRESS;

        if (dWheel != 0) for (Consumer<Integer> listener : onWheelListeners) listener.accept(dWheel);
    }

}
