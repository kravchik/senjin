package yk.senjin.viewers;

import yk.senjin.ui.engine.fp.OglKeyboard;
import yk.senjin.ui.engine.fp.OglMouse;
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
public class SuiWindow {
    private int uxW = 600;
    private int uxH = 600;
    private boolean stopRenderThread;
    private GlfwWindow glfwWindow;
    private final YList<Tickable> tickable = al();
    private final YList<Consumer<SuiWindow>> onInit = al();
    private final YList<Runnable> onFirstFrame = al();
    private boolean startHidden = false;

    public final OglMouse mouse = new OglMouse();
    public final OglKeyboard keyboard = new OglKeyboard();

    public final void start(long sleepMs) {
        tickerNotThread(sleepMs, () -> firstFrame(), dt -> eachFrame(dt));
    }

    public void firstFrame() {
        glfwWindow = initWindow(uxW, uxH, "Hello World!", false, !startHidden);
        uxW = glfwWindow.sizePixels.x;
        uxH = glfwWindow.sizePixels.y;
        mouse.init(glfwWindow.handle);
        mouse.height = glfwWindow.sizeUx.y;
        keyboard.init(glfwWindow.handle);
        for (Consumer<SuiWindow> consumer : onInit) consumer.accept(this);
        for (Runnable r : onFirstFrame) r.run();
    }

    //Should be in the same thread as initializations, as is using thread locals.
    public final boolean eachFrame(float dt) {
        keyboard.clearStates();
        glfwPollEvents();
        mouse.tick(dt);
        for (Tickable r : tickable) r.tick(dt);
        tick(dt);
        glfwSwapBuffers(glfwWindow.handle); // swap the color buffers
        return !(stopRenderThread || glfwWindowShouldClose(glfwWindow.handle));
    }
    //Should be in the same thread as initializations, as is using thread locals.
    public void tick(float dt) {
    }
    public final void requestStop() {
        this.stopRenderThread = true;
    }

    public final SuiWindow onWindowReady(Consumer<SuiWindow> onFirstFrame) {
        this.onInit.add(onFirstFrame);
        return this;
    }

    public final SuiWindow onFirstFrame(Runnable onFirstFrame) {
        this.onFirstFrame.add(onFirstFrame);
        return this;
    }

    public final SuiWindow onTick(Tickable tickable) {
        this.tickable.add(tickable);
        return this;
    }

    public final SuiWindow stopOnEsc() {
        //TODO use OglKeyboard
        return onTick(dt -> {
            if (glfwGetKey(glfwWindow.handle, GLFW_KEY_ESCAPE) == GLFW_PRESS) requestStop();
        });
    }

    public final SuiWindow setUxSize(int w, int h) {
        this.uxW = w;
        this.uxH = h;
        return this;
    }

    public final int getWidth() {return uxW;}

    public final int getHeight() {return uxH;}

    public SuiWindow startHidden() {
        this.startHidden = true;
        return this;
    }

    public GlfwWindow getGlfwWindow() {
        return glfwWindow;
    }
}
