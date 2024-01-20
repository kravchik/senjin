package yk.senjin.util;

import org.lwjgl.system.MemoryStack;
import yk.jcommon.fastgeom.Vec2i;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.system.MemoryStack.stackPush;
import static yk.jcommon.fastgeom.Vec2i.v2i;

public class GlfwWindow {
    public final long handle;
    public final Vec2i sizeUx;
    public final Vec2i sizePixels;

    public GlfwWindow(long handle) {
        this.handle = handle;
        sizeUx = getWindowsUxSize(handle);
        sizePixels = getWindowsPixelSize(handle);
    }

    public static Vec2i getWindowsUxSize(long windowHandle) {
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            glfwGetWindowSize(windowHandle, pWidth, pHeight);
            glfwGetWindowSize(windowHandle, pWidth, pHeight);
            return v2i(pWidth.get(0), pHeight.get(0));
        }
    }

    public static Vec2i getWindowsPixelSize(long windowHandle) {
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            glfwGetFramebufferSize(windowHandle, pWidth, pHeight);
            return v2i(pWidth.get(0), pHeight.get(0));
        }
    }

}
