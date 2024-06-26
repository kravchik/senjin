package yk.senjin.ui.core;

import yk.jcommon.fastgeom.Vec2f;
import yk.jcommon.utils.MyMath;

public class SuiPositions {
    public Float resultLocalX;
    public Float resultLocalY;
    public Float resultW;
    public Float resultH;

    public Float widthDif; //of parent
    public Float heightDif; //of parent
    public Float widthRatio; //of parent
    public Float heightRatio; //of parent
    public Float W;
    public Float H;

    public Float percentWidthByChildren;
    public Float percentHeightByChildren;

    public Float left;
    public Float top;
    public Float right;
    public Float bottom;
    public Float centerX;//0 means center, +10 means 10 pixels to the right of a center
    public Float centerY;//0 means center, +10 means 10 pixels below a center

    public float padding;

    public static SuiPositions pos() {
        return new SuiPositions();
    }

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

    public SuiPositions size(float w, float h) {this.W = w;this.H = h;return this;}
    public SuiPositions size(Vec2f v) {this.W = v.x;this.H = v.y;return this;}

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
        //absolute size
        if (resultW == null && W != null) resultW = W;
        if (resultH == null && H != null) resultH = H;

        //size based on parent
        if (panel.parent != null) {
            SuiPositions parent = panel.parent.pos;
            if (widthRatio != null && resultW == null) resultW = parent.resultW * widthRatio;
            if (heightRatio != null && resultH == null) resultH = parent.resultH * heightRatio;
            if (widthDif != null && resultW == null) resultW = parent.resultW - widthDif;
            if (heightDif != null && resultH == null) resultH = parent.resultH - heightDif;
        }

        //if (resultW == null) resultW = 100f;
        //if (resultH == null) resultH = 100f;
        //resultW -= padding * 2;TODO account padding in children
        //resultH -= padding * 2;
        float maxW = 0;
        float maxH = 0;
        for (SuiPanel child : panel.children) {
            child.calcSize();
            maxW = MyMath.max(child.pos.resultW, maxW);
            maxH = MyMath.max(child.pos.resultH, maxH);
        }

        //size by children
        if (resultW == null) resultW = maxW;
        if (resultH == null) resultH = maxH;
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
        if (resultLocalX == null && left != null) resultLocalX = left;
        if (resultLocalY == null && top != null) resultLocalY = top;

        if (panel.parent != null) {
            SuiPositions parent = panel.parent.pos;
            if (resultLocalX == null && centerX != null && resultW != null) resultLocalX = (parent.resultW - resultW) / 2 + centerX;
            if (resultLocalY == null && centerY != null && resultH != null) resultLocalY = (parent.resultH - resultH) / 2 + centerY;
            if (resultLocalX == null && right != null && resultW != null) resultLocalX = parent.resultW - right - resultW;
            if (resultLocalY == null && bottom != null && resultH != null) resultLocalY = parent.resultH - bottom - resultH;
        }
        if (resultLocalX == null) resultLocalX = 0f;
        if (resultLocalY == null) resultLocalY = 0f;
    }

    public SuiPositions W(Float w) {
        W = w;
        return this;
    }

    public SuiPositions H(Float h) {
        H = h;
        return this;
    }

    public SuiPositions left(Float left) {
        this.left = left;
        return this;
    }

    public SuiPositions top(Float top) {
        this.top = top;
        return this;
    }

    public SuiPositions right(Float right) {
        this.right = right;
        return this;
    }

    public SuiPositions bottom(Float bottom) {
        this.bottom = bottom;
        return this;
    }

    public SuiPositions centerX(Float rightCenter) {
        this.centerX = rightCenter;
        return this;
    }

    public SuiPositions centerY(Float belowCenter) {
        this.centerY = belowCenter;
        return this;
    }

    public SuiPositions padding(Float padding) {
        this.padding = padding;
        return this;
    }

    public SuiPositions widthRatio(Float percentWidth) {
        this.widthRatio = percentWidth;
        return this;
    }

    public SuiPositions heightRatio(Float percentHeight) {
        this.heightRatio = percentHeight;
        return this;
    }
}
