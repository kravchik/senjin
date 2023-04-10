package yk.senjin.ui.engine.fp;

import yk.jcommon.fastgeom.Vec4f;
import yk.jcommon.utils.BadException;

/**
 * Created by yuri at 2023.04.10
 */
public class SuiConsoleStringState {
    public Vec4f defaultFontColor;

    public Vec4f fontColor = new Vec4f(0, 0, 0, 1);
    public Vec4f bgColor = new Vec4f(0, 0, 0, 0);
    public boolean bold;
    public boolean italic;

    public SuiConsoleStringState(Vec4f defaultFontColor) {
        this.defaultFontColor = defaultFontColor;
        this.fontColor = defaultFontColor;
    }

    private int curPos;
    private String s;

    public boolean isFontChanged;
    public boolean isColorChanged;

    public SuiConsoleStringState parseModifier(String s, int at) {
        isFontChanged = false;
        isColorChanged = false;
        boolean oldItalic = italic;
        boolean oldBold = bold;
        Vec4f oldFontColor = fontColor;

        this.s = s;
        this.curPos = at + 1;
        eat('[');
        while (true) {
            if (s.charAt(curPos) == 'm') {
                curPos++;
                break;
            }
            if (s.charAt(curPos) == ';') {
                curPos++;
                continue;
            }
            int value = eatNumber();
            if (value == 0) {
                fontColor = defaultFontColor;
                bold = false;
                italic = false;
            } else if (value == 1) {
                bold = true;
            } else if (value == 3) {
                italic = true;
            } else if (value == 38) {
                eat(';');
                int mode = eatNumber();
                if (mode != 2) BadException.die("Not implemented fc mode '%s' at %s in %s", mode, this.curPos, s);
                eat(';');
                int r = eatNumber();
                eat(';');
                int g = eatNumber();
                eat(';');
                int b = eatNumber();
                fontColor = new Vec4f(r / 255f, g / 255f, b / 255f, 1);
            } else if (value == 39) {
                fontColor = defaultFontColor;
            } else {
                BadException.die("Unexpected console mode: '%s' at %s in %s", value, curPos, s);
            }
        }

        isFontChanged = oldBold != bold || oldItalic != italic;
        isColorChanged = !oldFontColor.equals(fontColor);
        return this;
    }

    public void eat(char c) {
        if (s.charAt(curPos) != c) BadException.die("Expected char '%s' at %s in %s", c, curPos, s);
        curPos++;
    }

    public int eatNumber() {
        String n = "";
        while (true) {
            char c = s.charAt(curPos);
            if (c < '0' || c > '9') break;
            n += c;
            curPos++;
        }
        return Integer.parseInt(n);
    }

    public int getCurPos() {
        return curPos;
    }

    public String info() {
        return "scss{" +
                "fontColor=" + fontColor +
                ", bold=" + bold +
                ", italic=" + italic +
                ", curPos=" + curPos +
                '}';
    }
}
