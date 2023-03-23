package yk.senjin.examples.simple;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;

import static org.lwjgl.opengl.GL11.*;
import static yk.jcommon.utils.Threads.sleep;

/**
 * Created by Yuri Kravchik on 16.11.17.
 */
public class SimpleLwjglRoutine {
    public int w = 600;
    public int h = 600;
    public long sleepMs;
    public boolean checkExit = true;
    public boolean stopRenderThread;

    public static void main(String[] args) {
        SimpleLwjglRoutine routine = new SimpleLwjglRoutine();
        routine.w = 1200;
        routine.start();
    }

    public void start() {
        new Thread(() -> {
            try {
                onFirstPass();
                long lastTick = System.currentTimeMillis();
                while (!stopRenderThread) {
                    long curTime = System.currentTimeMillis();
                    onTick((curTime - lastTick) / 1000f);
                    lastTick = curTime;
                    sleep(sleepMs);
                }
            } catch (Throwable t) {
                System.err.println("Error occurred");
                t.printStackTrace();
            }
        }).start();
    }

    public void onFirstPass() {
        //create at first pass, must be thread local!
        try {
            Display.setDisplayMode(new DisplayMode(w, h));
            Display.create(new PixelFormat());
            Display.makeCurrent();
        } catch (LWJGLException e) {
            throw new RuntimeException(e);
        }

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    public void onTick(float dt) {
        Display.update();
        if (checkExit && ((Keyboard.isKeyDown(Keyboard.KEY_LMENU) && Keyboard.isKeyDown(Keyboard.KEY_F4))
                || Display.isCloseRequested())) stopRenderThread = true;
    }

}
