package yk.senjin.ui.core;

import yk.jcommon.collections.YList;
import yk.jcommon.fastgeom.Vec2f;
import yk.jcommon.utils.BadException;
import yk.senjin.util.Tickable;

import java.util.function.Consumer;

import static yk.jcommon.collections.YArrayList.al;

/**
 * Created by Yuri Kravchik on 15.12.17.
 */
public class SuiPanel {
    public static boolean dirtyPos;//TODO not static
    public SuiPanel parent;
    public YList<SuiPanel> children = al();
    public SuiPanel skinUnder;
    public SuiPanel skinOver;
    public Sui sui;

    public boolean posChanged;
    public SuiPositions pos = new SuiPositions();
    public float resultGlobalX;
    public float resultGlobalY;
    public float resultW;
    public float resultH;

    public boolean visible = true;

    public Object renderingData;

    public boolean childrenCanBeOutside;

    public YList<Tickable> onTick = al();//called if mouse is above (every frame, can be no action)
    public YList<Consumer<SuiMouseState>> onMouse = al();//called if mouse is above (every frame, can be no action)

    public YList<Consumer<SuiMouseState>> onMouseEnter = al();
    public YList<Consumer<SuiMouseState>> onMouseLeave = al();
    public YList<Consumer<SuiMouseState>> onMouseUp = al();
    public YList<Consumer<SuiMouseState>> onMouseDown = al();
    public YList<Consumer<SuiMouseState>> onWheel = al();
    public YList<Consumer<SuiMouseState>> onMouseMove = al();

    public boolean mouseEnabled = true;
    public boolean mouseEnabledInChildren = true;

    public boolean mouseHovers;
    public boolean seen;

    public boolean isInside(Vec2f globalMousePos) {
        return globalMousePos.x >= resultGlobalX && globalMousePos.x < resultGlobalX + resultW && globalMousePos.y >= resultGlobalY && globalMousePos.y < resultGlobalY + resultH;
    }

    public SuiPanel() {
    }

    public SuiPanel(SuiPositions pos) {
        this.pos = pos;
    }

    public void setSuiInChildren() {
        if (sui != null) {
            for (SuiPanel child : children) {
                child.sui = sui;
                child.setSuiInChildren();
            }
        }
    }

    public SuiPanel add(SuiPanel... pp) {
        for (SuiPanel p : pp) {
            if (p.parent != null) if (!p.parent.children.remove(p)) BadException.die("inconsistency");
            children.add(p);
            p.parent = this;
            p.dirtyPos = true;
        }
        setSuiInChildren();
        return this;
    }

    public SuiPanel add(YList<? extends SuiPanel> pp) {
        for (SuiPanel p : pp) {
            if (p.parent != null) if (!p.parent.children.remove(p)) BadException.die("inconsistency");
            children.add(p);
            p.parent = this;
            p.dirtyPos = true;
        }
        setSuiInChildren();
        return this;
    }

    public void updateSkin() {
        if (skinUnder == null) return;
        skinUnder.resultW = resultW;
        skinUnder.resultH = resultH;
        skinUnder.resultGlobalX = resultGlobalX;
        skinUnder.resultGlobalY = resultGlobalY;
    }

    public void removeChildren() {
        for (int i = 0; i < children.size(); i++) children.get(i).parent = null;
        children.clear();
    }

    public SuiPanel setGlobalSize(float w, float h) {
        pos.W = w;
        pos.H = h;
        return this;
    }

    public SuiPanel setLocalPos(float x, float y) {
        pos.left = x;
        pos.top = y;
        return this;
    }
}
