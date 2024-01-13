package yk.senjin.ui.core;

import yk.jcommon.fastgeom.Vec2f;
import yk.senjin.ui.engine.fp.OglKeyboard;
import yk.senjin.ui.engine.fp.OglMouseController;
import yk.ycollections.YList;

import java.util.Iterator;
import java.util.function.Consumer;

import static yk.ycollections.YArrayList.al;

/**
 * Created by Yuri Kravchik on 23.02.18.
 */
public class SuiMouseControl {
    public OglMouseController mouse = new OglMouseController();
    public OglKeyboard keyboard = new OglKeyboard();

    public SuiPanel top;

    YList<SuiPanel> mouseHovers = al();
    public SuiMouseState mouseState = new SuiMouseState();

    public SuiMouseControl() {
        mouse.onWheelListeners.add(w -> mouseState.wheel = w / 120);
        //should set them by event, or can miss rapid press-release
        mouse.onMousePressedListeners.add(w -> { if (w < mouseState.keys.size()) {
            mouseState.keys.get(w).justPressed = true;
        } });
        mouse.onMouseReleasedListeners.add(w -> { if (w < mouseState.keys.size()) mouseState.keys.get(w).justReleased = true;});

        mouseState.keys.add(new SuiMouseKey("left"));
        mouseState.keys.add(new SuiMouseKey("right"));
    }

    public void tick(float dt) {
        for (SuiMouseKey key : mouseState.keys) {
            key.justReleased = false;
            key.justPressed = false;
        }

        keyboard.tick(dt);
        mouseState.wheel = 0;
        mouse.tick(dt);

        mouseState.keys.get(0).isPressed = mouse.lDown;
        mouseState.keys.get(1).isPressed = mouse.rDown;

        mouseState.lastGlobal = mouseState.globalPos;
        mouseState.globalPos = new Vec2f(mouse.current.x, mouse.current.y);
        mouseState.positionChanged = mouseState.lastGlobal.distanceSquared(mouseState.globalPos) > 0.00001f;

        mouseState.somethingDown = mouseState.keys.isAny(k -> k.justPressed);
        mouseState.somethingUp = mouseState.keys.isAny(k -> k.justReleased);

        workHierarchy();
        workTick(top, dt);
    }

    public void workHierarchy() {
        for (SuiPanel inProgress : mouseHovers) inProgress.seen = false;

        workHierarchy(top, new Vec2f(mouse.current.x, mouse.current.y));

        mouseState.justLeft = true;
        for (Iterator<SuiPanel> iterator = mouseHovers.iterator(); iterator.hasNext(); ) {
            SuiPanel inProgress = iterator.next();
            if (!inProgress.seen) {
                inProgress.mouseHovers = false;
                iterator.remove();
                for (Consumer<SuiMouseState> c : inProgress.onMouseLeave) c.accept(mouseState);
            }
        }
        mouseState.justLeft = false;
    }

    public void workTick(SuiPanel panel, float f) {
        for (SuiPanel child : panel.children) workTick(child, f);
        for (int i = 0; i < panel.onTick.size(); i++) {
            panel.onTick.get(i).tick(f);
        }
    }

    public void workHierarchy(SuiPanel panel, Vec2f globalPos) {
        if (!panel.mouseEnabledInChildren && !panel.mouseEnabled) return;
        boolean isInside = (!panel.mouseEnabled && panel.childrenCanBeOutside) || panel.isInside(globalPos);
        if (panel.mouseEnabledInChildren && (isInside || panel.childrenCanBeOutside)) {
            for (SuiPanel child : panel.children) workHierarchy(child, globalPos);
        }
        if (panel.mouseEnabled && isInside) moveEvent(panel);
    }

    private void moveEvent(SuiPanel panel) {
        mouseState.justEntered = !panel.mouseHovers;
        if (mouseState.justEntered) mouseHovers.add(panel);
        panel.mouseHovers = true;
        panel.seen = true;
        mouseState.localPos = mouseState.globalPos.sub(panel.resultGlobalX, panel.resultGlobalY);

        for (Consumer<SuiMouseState> consumer : panel.onMouse) consumer.accept(mouseState);
        if (mouseState.justEntered) for (Consumer<SuiMouseState> c : panel.onMouseEnter) c.accept(mouseState);
        if (mouseState.positionChanged) for (Consumer<SuiMouseState> c : panel.onMouseMove) c.accept(mouseState);
        if (mouseState.somethingDown) for (Consumer<SuiMouseState> consumer : panel.onMouseDown) consumer.accept(mouseState);
        if (mouseState.somethingUp) for (Consumer<SuiMouseState> consumer : panel.onMouseUp) consumer.accept(mouseState);
        if (mouseState.wheel != 0) for (Consumer<SuiMouseState> consumer : panel.onWheel) consumer.accept(mouseState);
    }

}
