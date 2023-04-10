package yk.senjin.ui.core;

public class SuiPositions {
    public Float resultLocalX;
    public Float resultLocalY;
    public Float resultW;
    public Float resultH;

    public Float difWidth; //of parent
    public Float difHeight; //of parent
    public Float percentWidth; //of parent
    public Float percentHeight; //of parent
    public Float W;
    public Float H;

    public Float percentWidthByChildren;
    public Float percentHeightByChildren;

    public Float left;
    public Float top;
    public Float right;
    public Float bottom;
    public Float rightCenter;//0 means center, +10 means 10 pixels to the right of a center
    public Float belowCenter;//0 means center, +10 means 10 pixels below a center

    public float padding;

    public static SuiPositions absolute(float left, float top) {
        SuiPositions result = new SuiPositions();
        result.left = left;
        result.top = top;
        return result;
    }

    public static SuiPositions absolute(float left, float top, float w, float h) {
        SuiPositions result = new SuiPositions();
        result.left = left;
        result.top = top;
        result.W = w;
        result.H = h;
        return result;
    }

    public SuiPositions size(float w, float h) {
        this.W = w;
        this.H = h;
        return this;
    }

    public SuiPositions pos(float x, float y) {
        this.left = x;
        this.top = y;
        return this;
    }

    /**
     * Translates local positions to global.
     */
    public void localToGlobal(SuiPanel panel) {
        if (panel.parent == null) {
            panel.resultGlobalX = panel.pos.resultLocalX;
            panel.resultGlobalY = panel.pos.resultLocalY;
        } else {
            panel.resultGlobalX = panel.parent.resultGlobalX + panel.pos.resultLocalX;
            panel.resultGlobalY = panel.parent.resultGlobalY + panel.pos.resultLocalY;
        }
        panel.resultW = resultW;
        panel.resultH = resultH;
        for (SuiPanel child : panel.children) child.pos.localToGlobal(child);
    }

    public void calcSize(SuiPanel panel) {
        resultW = null;
        resultH = null;

        if (W != null) resultW = W;
        if (H != null) resultH = H;

        if (panel.parent != null) {
            SuiPositions parent = panel.parent.pos;
            if (percentWidth != null && resultW == null) resultW = parent.resultW * percentWidth;
            if (percentHeight != null && resultH == null) resultH = parent.resultH * percentHeight;
            if (difWidth != null && resultW == null) resultW = parent.resultW - difWidth;
            if (difHeight != null && resultH == null) resultH = parent.resultH - difHeight;
        }

        //if (resultW == null) resultW = 100f;
        //if (resultH == null) resultH = 100f;
        //resultW -= padding * 2;TODO account padding in children
        //resultH -= padding * 2;
        for (SuiPanel child : panel.children) child.pos.calcSize(child);
        //resultW += padding * 2;
        //resultH += padding * 2;

        //if (resultW == null && percentWidthByChildren != null) {
        //}
    }

    public void calcPos(SuiPanel panel) {
        calcPosSelf(panel);
        resultW -= padding * 2;
        resultH -= padding * 2;
        for (SuiPanel child : panel.children) {
            child.pos.calcPos(child);
            child.pos.resultLocalX += padding;
            child.pos.resultLocalY += padding;
        }
        resultW += padding * 2;
        resultH += padding * 2;
    }

    public void calcPosSelf(SuiPanel panel) {
        resultLocalX = null;
        resultLocalY = null;
        if (left != null) resultLocalX = left;
        if (top != null) resultLocalY = top;

        if (panel.parent != null) {
            SuiPositions parent = panel.parent.pos;
            if (rightCenter != null && resultW != null) resultLocalX = (parent.resultW - resultW) / 2 + rightCenter;
            if (belowCenter != null && resultH != null) resultLocalY = (parent.resultH - resultH) / 2 + belowCenter;
            if (right != null && resultW != null) resultLocalX = parent.resultW - right - resultW;
            if (bottom != null && resultH != null) resultLocalY = parent.resultH - bottom - resultH;
        }
        if (resultLocalX == null) resultLocalX = 0f;
        if (resultLocalY == null) resultLocalY = 0f;
    }
}
