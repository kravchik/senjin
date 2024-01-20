package yk.senjin.ui.core;

import yk.jcommon.fastgeom.Vec4f;
import yk.senjin.ui.engine.fp.SuiFont;

import static yk.jcommon.fastgeom.Vec4f.v4;

/**
 * Created by Yuri Kravchik on 15.12.17.
 */
//TODO (only when use-case found) rendering hint CHANGES_RARELY for "drawing whole string on the texture, instead of by glyph"
public class SuiPanelString extends SuiPanel {
    public Vec4f color = v4(1, 1, 1, 1);
    private String string;
    private boolean isChanged;

    //System font (vector based) -> pixel font (image based)
    //  using fontSize, fontStyle and fontAntialiased (for possible system AA)
    public String fontName = "Times New Roman";
    public int fontSize = 24;
    public boolean fontAntialiased = true;
    public int fontStyle;//Font.PLAIN, Font.BOLD

    //pixel font (texture) -> rendering
    //  (which scale to use, if mipmapping should be used)
    public boolean fontMipmap;
    public float scale = 1;

    public SuiPanelString() {
        string = "";
    }
    
    public SuiPanelString(String string) {
        setString(string);
    }

    public SuiPanelString(SuiPositions pos, String string) {
        this.pos = pos;
        setString(string);
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        if (this.string == null || !this.string.equals(string)) {
            this.string = string;
            isChanged = true;
        }
    }

    @Override
    public void calcSize() {
        if (isChanged) {
            SuiFont font = sui.fonts.getFont(this);
            pos.W = (float)font.getWidth(getString()) * scale;
            pos.H = (float)font.getHeight() * scale;
            isChanged = false;
        }
        super.calcSize();
    }
}
