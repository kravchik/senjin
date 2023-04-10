package yk.senjin.ui.core;

import yk.jcommon.collections.YList;
import yk.jcommon.fastgeom.Vec2f;

import static yk.jcommon.collections.YArrayList.al;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 26/02/16
 * Time: 17:50
 */
public class SuiMouseState {
    public Vec2f globalPos = new Vec2f(0, 0);
    public Vec2f localPos = new Vec2f(0, 0);
    public boolean positionChanged;
    public boolean justEntered;//mouse entered in this frame
    public boolean justLeft;//mouse left in this frame
    public Vec2f lastGlobal;

    public YList<SuiMouseKey> keys = al();
    public boolean somethingDown;
    public boolean somethingUp;
    public int wheel;

    public SuiMouseState() {
    }

    @Override
    public String toString() {
        return "AuiMouseMoveEvent{" +
               "justEntered=" + justEntered +
               ", justLeaved=" + justLeft +
               ", lastGlobal=" + lastGlobal +
               "} " + super.toString();
    }
}
