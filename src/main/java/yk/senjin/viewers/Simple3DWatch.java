package yk.senjin.viewers;

import yk.jcommon.fastgeom.*;
import yk.senjin.Cam;
import yk.senjin.DDDUtils;
import yk.senjin.SimpleAntiAliasing2;
import yk.senjin.SkyBox;
import yk.senjin.ui.engine.fp.OglKeyboard;
import yk.senjin.ui.engine.fp.OglMouse;
import yk.senjin.util.FixAwt2;
import yk.senjin.util.GlfwWindow;
import yk.senjin.util.ThreadUtils;
import yk.ycollections.YHashMap;

import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static yk.jcommon.fastgeom.Matrix4.perspective;
import static yk.jcommon.fastgeom.Vec2i.v2i;
import static yk.senjin.viewers.SimpleLwjglRoutine.initWindow;
import static yk.ycollections.YHashMap.hm;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 4/3/14
 * Time: 8:43 PM
 */
public class Simple3DWatch {
    static {FixAwt2.fix();}

    public static final YHashMap<String, Vec3f> MOVEMENTS = hm(
        "W", new Vec3f(0, 0, -1),
        "S", new Vec3f(0, 0, 1),
        "A", new Vec3f(-1, 0, 0),
        "D", new Vec3f(1, 0, 0),
        "Q", new Vec3f(0, 1, 0),
        "Z", new Vec3f(0, -1, 0)
    );
    public boolean drawAxis = true;
    public boolean rotateByMouse = true;
    public Vec3f backgroundColor = new Vec3f(0, 0, 0);

    public final Cam cam = new Cam();
    public float fovy = 45f;
    public float zNear = 0.2f;
    public float zFar = 500;
    public Vec2i sizeUx;
    public Vec2i sizePixels;

    public Vec2f mousePressedAt;
    public float camPitch = 0;
    public float camYaw = 0;
    public float camMoveSpeed = 20;

    public Matrix4 perspectiveMatrix;
    public Matrix4 camModelViewMatrix;
    public Matrix4 camModelViewProjectionMatrix;
    public Matrix4 camNormalMatrix;

    //TODO implement as additions
    public SkyBox skyBox;
    public boolean SIMPLE_AA = false;
    public SimpleAntiAliasing2 simpleAA;

    private final OglMouse oglMouse = new OglMouse();
    {
        oglMouse.onMousePressedListeners.add(this::onMousePressed);
        oglMouse.onMouseReleasedListeners.add(this::onMouseReleased);
    }
    private final OglKeyboard oglKeyboard = new OglKeyboard();


    public Simple3DWatch() {
        this(800, 600);
    }

    private GlfwWindow win;

    public Simple3DWatch(int uxWidth, int uxHeight) {
        sizeUx = v2i(uxWidth, uxHeight);
    }

    public void run() {
        if (SIMPLE_AA) {
            simpleAA = new SimpleAntiAliasing2();
        }

        final Simple3DWatch THIS = this;
        cam.lookAt = new Vec3f(0, 0, 100);

        win = initWindow(sizeUx.x, sizeUx.y, "Hello World!", true);
        sizePixels = win.sizePixels;

        oglMouse.init(win.handle);
        oglKeyboard.init(win.handle);

        if (SIMPLE_AA) simpleAA.initAA(win.sizePixels.x, win.sizePixels.y);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        ThreadUtils.tickerNotThread(1, () -> {
            firstFrame();
        }, dt -> {
            commonTick(dt);
            THIS.tick(dt);
            if (SIMPLE_AA) simpleAA.renderFBO();

            glfwSwapBuffers(win.handle);
            glfwPollEvents();
            return !(oglKeyboard.isPressed("ESCAPE") || glfwWindowShouldClose(win.handle));
        });
    }

    protected void onRender(float dt) {
        //aa
        oglKeyboard.tick(dt);
        oglMouse.tick(dt);
        
        if (SIMPLE_AA) simpleAA.switchToFBO();
        Vec2f mouseCur = oglMouse.current.toVec2f();
        if (oglMouse.lDown) {
            if (mousePressedAt == null) {
                mousePressedAt = mouseCur;
            }
            if (rotateByMouse) rotateCam(
                -(mouseCur.y - mousePressedAt.y) * 0.004f,
                (mouseCur.x - mousePressedAt.x) * 0.004f);
            mousePressedAt = mouseCur;
        } else {
            mousePressedAt = null;
        }

        glAlphaFunc ( GL_GREATER, 0.1f) ;
        glEnable ( GL_ALPHA_TEST ) ;

        //if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) camMoveSpeed /= 20;
        for (Map.Entry<String, Vec3f> entry : MOVEMENTS.entrySet()) {
            if (oglKeyboard.isPressed(entry.getKey()))
                cam.lookAt = cam.lookAt.add(cam.lookRot.conjug().rotate(entry.getValue().mul(camMoveSpeed * dt)));
        }

        if (oglKeyboard.isPressed("UP")) rotateCam(-dt, 0);
        if (oglKeyboard.isPressed("DOWN")) rotateCam(dt, 0);
        if (oglKeyboard.isPressed("LEFT")) rotateCam(0, -dt);
        if (oglKeyboard.isPressed("RIGHT")) rotateCam(0, dt);

        updateCamLook();

        glClearColor(backgroundColor.x, backgroundColor.y, backgroundColor.z, 1);
        glClear(GL_COLOR_BUFFER_BIT);
        glClear(GL_DEPTH_BUFFER_BIT);
        glBindTexture(GL_TEXTURE_2D, 0);
        glDepthMask(true);
        recalcMatrices();

        if (skyBox != null) {
            glDisable(GL_DEPTH_TEST);
            skyBox.render(cam.lookAt);
        }
        glEnable(GL_DEPTH_TEST);
        if (drawAxis) drawAxis();
    }

    private void updateCamLook() {
        cam.lookRot = Quaternionf.fromAngleAxisFast(camPitch, Vec3f.AXIS_X)
                .mul(Quaternionf.fromAngleAxisFast(camYaw, Vec3f.AXIS_Y))
                .normalized();
    }

    private void recalcMatrices() {
        perspectiveMatrix = perspective(fovy, win.sizePixels.toVec2f().ratio(), zNear, zFar);
        camModelViewMatrix = cam.lookRot.toMatrix4().multiply(Matrix4.identity().translate(cam.lookAt.mul(-1)));
        camNormalMatrix = camModelViewMatrix.invert().transpose();
        camModelViewProjectionMatrix = perspectiveMatrix.multiply(camModelViewMatrix);
        resetMvp();
    }

    public void drawAxis() {
        float len = 10;
        glLineWidth(3);
        glBegin(GL_LINES);
        glColor3f(1, 0, 0);glVertex3f(0, 0, 0);glVertex3f(len, 0, 0);
        glColor3f(0, 1, 0);glVertex3f(0, 0, 0);glVertex3f(0, len, 0);
        glColor3f(0, 0, 1);glVertex3f(0, 0, 0);glVertex3f(0, 0, len);
        glEnd();
    }

    private void rotateCam(float pitch, float yaw) {
        camPitch += pitch;
        if (camPitch < -Math.PI / 2) camPitch = (float) (-Math.PI / 2);
        if (camPitch > Math.PI / 2) camPitch = (float) (Math.PI / 2);
        camYaw += yaw;
    }

    public void resetMvp() {
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        Matrix4 perspective = perspective(fovy, sizePixels.toVec2f().ratio(), zNear, zFar);
        DDDUtils.glLoadMatrix(perspective);
        resetMv();
    }

    public void resetMv() {
        glMatrixMode(GL_MODELVIEW);
        DDDUtils.glLoadMatrix(camModelViewMatrix);
    }

    public void tick(float dt) {
    }

    public void firstFrame() {
    }

    public void onMouseReleased(int button) {
    };

    protected void commonTick(float dt) {
        onRender(dt);
    }

    public void onMousePressed(int button) {
    };

}
