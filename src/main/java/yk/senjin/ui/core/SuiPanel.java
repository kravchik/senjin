package yk.senjin.ui.core;

import yk.jcommon.fastgeom.Vec2f;
import yk.jcommon.utils.BadException;
import yk.senjin.util.Tickable;
import yk.ycollections.YCollection;
import yk.ycollections.YList;

import java.util.function.Consumer;

import static yk.ycollections.YArrayList.al;

/**
 * Created by Yuri Kravchik on 15.12.17.
 */
public class SuiPanel {
    public SuiPanel parent;
    public YList<SuiPanel> children = al();
    public Sui sui;

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
    public SuiPanel onMouseEnter(Consumer<SuiMouseState> consumer) {onMouseEnter.add(consumer);return this;}
    public YList<Consumer<SuiMouseState>> onMouseLeave = al();
    public YList<Consumer<SuiMouseState>> onMouseUp = al();
    public YList<Consumer<SuiMouseState>> onMouseDown = al();
    public SuiPanel onMouseDown(Consumer<SuiMouseState> consumer) {onMouseDown.add(consumer);return this;}
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

    public SuiPanel(SuiPositions pos, SuiPanel... pp) {
        this.pos = pos;
        add(pp);
    }

    public SuiPanel(SuiPositions pos, YCollection<? extends SuiPanel> pp) {
        this.pos = pos;
        add(pp);
    }

    public static SuiPanel panel(SuiPositions pos, SuiPanel... pp) {
        return new SuiPanel(pos, pp);
    }

    public static SuiPanel panel(SuiPositions pos, YCollection<? extends SuiPanel> pp) {
        return new SuiPanel(pos, pp);
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
        }
        setSuiInChildren();
        return this;
    }

    public SuiPanel add(YCollection<? extends SuiPanel> pp) {
        for (SuiPanel p : pp) {
            if (p.parent != null) if (!p.parent.children.remove(p)) BadException.die("inconsistency");
            children.add(p);
            p.parent = this;
        }
        setSuiInChildren();
        return this;
    }

    public void remove(SuiPanel panel) {
        if (panel.parent != this) BadException.die("Trying to remove panel from a wrong parent");
        if (!children.remove(panel)) BadException.die("Removing, but it is absent");
    }

    public void removeChildren() {
        for (int i = 0; i < children.size(); i++) {
            SuiPanel c = children.get(i);
            if (c.parent != this) BadException.die("Trying to remove panel from a wrong parent");
            c.parent = null;
        }
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

    public void calcSize() {
        pos.calcSize(this);
    }
}
