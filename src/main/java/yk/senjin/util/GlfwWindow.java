package yk.senjin.util;

import yk.jcommon.fastgeom.Vec2i;

import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
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
        int[] w = new int[1], h = new int[1];
        glfwGetWindowSize(windowHandle, w, h);
        return v2i(w[0], h[0]);
    }

    public static Vec2i getWindowsPixelSize(long windowHandle) {
        int[] w = new int[1], h = new int[1];
        glfwGetFramebufferSize(windowHandle, w, h);
        return v2i(w[0], h[0]);
    }

}
