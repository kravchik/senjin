package yk.senjin.util;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;
import yk.ycollections.YList;

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
    private int w = 600;
    private int h = 600;
    private boolean stopRenderThread;

    private final YList<Tickable> tickable = al();
    private Runnable beforeFirstFrame;

    public final void start(long sleepMs) {
        ThreadUtils.ticker(sleepMs, () -> firstFrame(), dt -> eachFrame(dt));
    }

    //Makes initialisations. Should be in the right thread, as it is using thread locals.
    public void firstFrame() {
        try {
            Display.setDisplayMode(new DisplayMode(w, h));
            Display.create(new PixelFormat());
            Display.makeCurrent();
            if (beforeFirstFrame != null) beforeFirstFrame.run();
        } catch (LWJGLException e) {
            throw new RuntimeException(e);
        }
    }

    //Should be in the same thread as initializations, as is using thread locals.
    public final boolean eachFrame(float dt) {
        Display.update();
        if (Display.isCloseRequested()) requestStop();
        for (Tickable r : tickable) r.tick(dt);
        tick(dt);
        return !stopRenderThread;
    }

    //Should be in the same thread as initializations, as is using thread locals.
    public void tick(float dt) {
    }

    public final void requestStop() {
        this.stopRenderThread = true;
    }

    public final GlWindow1 onFirstFrame(Runnable onFirstPass) {
        this.beforeFirstFrame = onFirstPass;
        return this;
    }

    public final GlWindow1 onTick(Tickable tickable) {
        this.tickable.add(tickable);
        return this;
    }

    public final GlWindow1 stopOnEsc() {
        return onTick(dt -> {
            if (((Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)))) requestStop();
        });
    }
    public final GlWindow1 stopOnAltF4() {
        return onTick(dt -> {
            if (((Keyboard.isKeyDown(Keyboard.KEY_LMENU) && Keyboard.isKeyDown(Keyboard.KEY_F4)))) requestStop();
        });
    }

    public final GlWindow1 setSize(int w, int h) {
        this.w = w;
        this.h = h;
        return this;
    }

    public final int getWidth() {
        return w;
    }
    public final int getHeight() {
        return h;
    }
}
