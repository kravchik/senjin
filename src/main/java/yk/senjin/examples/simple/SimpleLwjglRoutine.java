package yk.senjin.examples.simple;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.opengl.Util;
import yk.jcommon.utils.Threads;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Yuri Kravchik on 16.11.17.
 */
public class SimpleLwjglRoutine {
    private boolean firstPass = true;
    public int w = 600;
    public int h = 600;
    public boolean stopRenderThread;

    public static void main(String[] args) throws Exception {
        new SimpleLwjglRoutine().main();
    }

    public void main() throws Exception {
        Threads.tick(new Threads.Tickable() {
            @Override
            public void tick(float dt) throws Exception {
                if (firstPass) onFirstPass();
                firstPass = false;
                onTick(dt);
                exit = stopRenderThread;
            }
        }, 1);
    }

    public void onFirstPass() {
        //create at first pass, must be thread local
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
        Util.checkGLError();
        Display.update();
        checkExit();
    }

    public void checkExit() {
        if ((Keyboard.isKeyDown(Keyboard.KEY_LMENU) && Keyboard.isKeyDown(Keyboard.KEY_F4)) || Display.isCloseRequested()) stopRenderThread = true;
    }

}
