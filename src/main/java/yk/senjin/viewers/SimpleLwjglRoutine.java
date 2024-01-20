package yk.senjin.viewers;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import yk.senjin.util.GlfwWindow;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.GL_SHADING_LANGUAGE_VERSION;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;
import static yk.jcommon.utils.Threads.sleep;

/**
 * Created by Yuri Kravchik on 16.11.17.
 */
public class SimpleLwjglRoutine {
    public long sleepMs;
    public GlfwWindow win;
    private int w;
    private int h;



    public static void main(String[] args) {
        SimpleLwjglRoutine routine = new SimpleLwjglRoutine();
        routine.start();
    }

    public void start() {
        start(600, 600);
    }
    public void start(int uxW, int uxH) {
        win = initWindow(uxW, uxH, "Hello World!", true);
        w = win.sizePixels.x;
        h = win.sizePixels.y;

        try {

            // Set the clear color
            glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

            onFirstPass();
            long lastTick = System.currentTimeMillis();
            while (!glfwWindowShouldClose(win.handle)) {
                //glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

                long curTime = System.currentTimeMillis();
                onTick((curTime - lastTick) / 1000f);
                lastTick = curTime;
                sleep(sleepMs);

                glfwSwapBuffers(win.handle); // swap the color buffers

                // Poll for window events. The key callback above will only be
                // invoked during this call.
                glfwPollEvents();
            }
        } catch (Throwable t) {
            System.err.println("Error occurred");
            t.printStackTrace();
        }
    }

    public static GlfwWindow initWindow(int width, int height, String title, boolean exitByEsc) {
        long windowHandle;
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        //glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        //glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        //glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        //glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        // Create the window
        windowHandle = glfwCreateWindow(width, height, title, NULL, NULL);
        if ( windowHandle == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        if (exitByEsc) glfwSetKeyCallback(windowHandle, (w, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(w, true); // We will detect this in the rendering loop
        });

        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(windowHandle, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                windowHandle,
                (vidmode.width() - pWidth.get(0)) / 2,
                (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(windowHandle);
        // Enable v-sync
        //glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(windowHandle);


        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        System.out.println("glsl version:   " + glGetString(GL_SHADING_LANGUAGE_VERSION));
        System.out.println("OpenGL version: " + glGetString(GL_VERSION));

        return new GlfwWindow(windowHandle);
    }

    public void onFirstPass() {
    }

    public void onTick(float dt) {
    }

    public int w() {
        return w;
    }

    public int h() {
        return h;
    }
}
