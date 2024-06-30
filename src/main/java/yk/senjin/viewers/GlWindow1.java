package yk.senjin.viewers;

import yk.senjin.util.GlfwWindow;
import yk.senjin.util.Tickable;
import yk.ycollections.YList;

import java.util.function.Consumer;

import static org.lwjgl.glfw.GLFW.*;
import static yk.senjin.util.ThreadUtils.tickerNotThread;
import static yk.senjin.viewers.SimpleLwjglRoutine.initWindow;
import static yk.ycollections.YArrayList.al;

/**
 * Created by Yuri Kravchik on 2023.03.14
 *
 * A different approach taken here. We should use it via composition instead of inheritance.
 * Though inheritance is possible too
 *
 * Excessive finals placed to point at the right methods to (if) override
 */
public class GlWindow1 {
    private int uxW = 600;
    private int uxH = 600;
    private boolean stopRenderThread;
    private GlfwWindow win;
    private final YList<Tickable> tickable = al();
    private final YList<Consumer<GlfwWindow>> onInit = al();
    private final YList<Runnable> onFirstFrame = al();
    private boolean startHidden = false;

    public final void start(long sleepMs) {
        tickerNotThread(sleepMs, () -> firstFrame(), dt -> eachFrame(dt));
    }

    public void firstFrame() {
        win = initWindow(uxW, uxH, "Hello World!", false, !startHidden);
        uxW = win.sizePixels.x;
        uxH = win.sizePixels.y;
        for (Consumer<GlfwWindow> consumer : onInit) consumer.accept(win);
        for (Runnable r : onFirstFrame) r.run();
    }

    //Should be in the same thread as initializations, as is using thread locals.
    public final boolean eachFrame(float dt) {
        for (Tickable r : tickable) r.tick(dt);
        tick(dt);
        glfwSwapBuffers(win.handle); // swap the color buffers
        glfwPollEvents();
        return !(stopRenderThread || glfwWindowShouldClose(win.handle));
    }
    //Should be in the same thread as initializations, as is using thread locals.
    public void tick(float dt) {
    }
    public final void requestStop() {
        this.stopRenderThread = true;
    }

    public final GlWindow1 onWindowReady(Consumer<GlfwWindow> onFirstFrame) {
        this.onInit.add(onFirstFrame);
        return this;
    }

    public final GlWindow1 onFirstFrame(Runnable onFirstFrame) {
        this.onFirstFrame.add(onFirstFrame);
        return this;
    }

    public final GlWindow1 onTick(Tickable tickable) {
        this.tickable.add(tickable);
        return this;
    }

    public final GlWindow1 stopOnEsc() {
        return onTick(dt -> {
            if (glfwGetKey(win.handle, GLFW_KEY_ESCAPE) == GLFW_PRESS) requestStop();
        });
    }

    public final GlWindow1 stopOnAltF4() {
        return onTick(dt -> {
            if (glfwGetKey(win.handle, GLFW_KEY_F4) == GLFW_PRESS) requestStop();
        });
    }
    public final GlWindow1 setUxSize(int w, int h) {
        this.uxW = w;
        this.uxH = h;
        return this;
    }

    public final int getWidth() {return uxW;}

    public final int getHeight() {return uxH;}

    public GlWindow1 startHidden() {
        this.startHidden = true;
        return this;
    }
}
