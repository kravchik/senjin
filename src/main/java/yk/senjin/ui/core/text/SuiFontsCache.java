package yk.senjin.ui.core.text;

import yk.jcommon.collections.YMap;
import yk.senjin.ui.core.SuiPanelString;
import yk.senjin.ui.engine.fp.SuiFont;

import java.awt.*;

import static yk.jcommon.collections.YHashMap.hm;

public class SuiFontsCache {
    public SuiFont defaultFont;
    public YMap<String, SuiFont> key2font = hm();

    public SuiFontsCache() {
        defaultFont = new SuiFont(new Font("Times New Roman", Font.PLAIN, 24), false, false);
    }

    private String getKey(SuiPanelString s, int fontStyle) {
        return s.fontName + " size: " + s.fontSize + " antialiased: " + s.fontAntialiased + " style: " + fontStyle + " mipmap: " + s.fontMipmap;
    }

    public SuiFont getFont(SuiPanelString s) {
        return getFont(s, s.fontStyle);
    }
    public SuiFont getFont(SuiPanelString s, int fontStyle) {
        String key = getKey(s, fontStyle);
        SuiFont font = key2font.get(key);
        if (font == null) {
            font = new SuiFont(new Font(s.fontName, fontStyle, s.fontSize), s.fontAntialiased, s.fontMipmap);
            key2font.put(key, font);
        }
        return font;
    }

}
