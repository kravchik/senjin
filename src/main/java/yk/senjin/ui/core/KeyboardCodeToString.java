package yk.senjin.ui.core;

import org.lwjgl.input.Keyboard;
import yk.ycollections.YMap;

import static yk.ycollections.YHashMap.hm;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 22/06/15
 * Time: 10:49
 */
public class KeyboardCodeToString {

    public static String getString(int keyCode) {
        if (!mapa.containsKey(keyCode)) return "unknown";
        //if (!mapa.containsKey(keyCode)) BadException.die("Can't find string for keycode " + keyCode);
        return mapa.get(keyCode);
    }

    public static YMap<Integer, String> mapa = hm(
            Keyboard.KEY_ESCAPE,              "ESCAPE",
            Keyboard.KEY_1,                   "1",
            Keyboard.KEY_2,                   "2",
            Keyboard.KEY_3,                   "3",
            Keyboard.KEY_4,                   "4",
            Keyboard.KEY_5,                   "5",
            Keyboard.KEY_6,                   "6",
            Keyboard.KEY_7,                   "7",
            Keyboard.KEY_8,                   "8",
            Keyboard.KEY_9,                   "9",
            Keyboard.KEY_0,                   "0",
            Keyboard.KEY_MINUS,               "MINUS",
            Keyboard.KEY_EQUALS,              "EQUALS",
            Keyboard.KEY_BACK,                "BACK",
            Keyboard.KEY_TAB,                 "TAB",
            Keyboard.KEY_Q,                   "Q",
            Keyboard.KEY_W,                   "W",
            Keyboard.KEY_E,                   "E",
            Keyboard.KEY_R,                   "R",
            Keyboard.KEY_T,                   "T",
            Keyboard.KEY_Y,                   "Y",
            Keyboard.KEY_U,                   "U",
            Keyboard.KEY_I,                   "I",
            Keyboard.KEY_O,                   "O",
            Keyboard.KEY_P,                   "P",
            Keyboard.KEY_LBRACKET,            "LBRACKET",
            Keyboard.KEY_RBRACKET,            "RBRACKET",
            Keyboard.KEY_RETURN,              "RETURN",
            Keyboard.KEY_LCONTROL,            "LCONTROL",
            Keyboard.KEY_A,                   "A",
            Keyboard.KEY_S,                   "S",
            Keyboard.KEY_D,                   "D",
            Keyboard.KEY_F,                   "F",
            Keyboard.KEY_G,                   "G",
            Keyboard.KEY_H,                   "H",
            Keyboard.KEY_J,                   "J",
            Keyboard.KEY_K,                   "K",
            Keyboard.KEY_L,                   "L",
            Keyboard.KEY_SEMICOLON,           "SEMICOLON",
            Keyboard.KEY_APOSTROPHE,          "APOSTROPHE",
            Keyboard.KEY_GRAVE,               "GRAVE",
            Keyboard.KEY_LSHIFT,              "LSHIFT",
            Keyboard.KEY_BACKSLASH,           "BACKSLASH",
            Keyboard.KEY_Z,                   "Z",
            Keyboard.KEY_X,                   "X",
            Keyboard.KEY_C,                   "C",
            Keyboard.KEY_V,                   "V",
            Keyboard.KEY_B,                   "B",
            Keyboard.KEY_N,                   "N",
            Keyboard.KEY_M,                   "M",
            Keyboard.KEY_COMMA,               "COMMA",
            Keyboard.KEY_PERIOD,              "PERIOD",
            Keyboard.KEY_SLASH,               "SLASH",
            Keyboard.KEY_RSHIFT,              "RSHIFT",
            Keyboard.KEY_MULTIPLY,            "MULTIPLY",
            Keyboard.KEY_LMENU,               "LMENU",
            Keyboard.KEY_SPACE,               "SPACE",
            Keyboard.KEY_CAPITAL,             "CAPITAL",
            Keyboard.KEY_F1,                  "F1",
            Keyboard.KEY_F2,                  "F2",
            Keyboard.KEY_F3,                  "F3",
            Keyboard.KEY_F4,                  "F4",
            Keyboard.KEY_F5,                  "F5",
            Keyboard.KEY_F6,                  "F6",
            Keyboard.KEY_F7,                  "F7",
            Keyboard.KEY_F8,                  "F8",
            Keyboard.KEY_F9,                  "F9",
            Keyboard.KEY_F10,                 "F10",
            Keyboard.KEY_NUMLOCK,             "NUMLOCK",
            Keyboard.KEY_SCROLL,              "SCROLL",
            Keyboard.KEY_NUMPAD7,             "NUMPAD7",
            Keyboard.KEY_NUMPAD8,             "NUMPAD8",
            Keyboard.KEY_NUMPAD9,             "NUMPAD9",
            Keyboard.KEY_SUBTRACT,            "SUBTRACT",
            Keyboard.KEY_NUMPAD4,             "NUMPAD4",
            Keyboard.KEY_NUMPAD5,             "NUMPAD5",
            Keyboard.KEY_NUMPAD6,             "NUMPAD6",
            Keyboard.KEY_ADD,                 "ADD",
            Keyboard.KEY_NUMPAD1,             "NUMPAD1",
            Keyboard.KEY_NUMPAD2,             "NUMPAD2",
            Keyboard.KEY_NUMPAD3,             "NUMPAD3",
            Keyboard.KEY_NUMPAD0,             "NUMPAD0",
            Keyboard.KEY_DECIMAL,             "DECIMAL",
            Keyboard.KEY_F11,                 "F11",
            Keyboard.KEY_F12,                 "F12",
            Keyboard.KEY_F13,                 "F13",
            Keyboard.KEY_F14,                 "F14",
            Keyboard.KEY_F15,                 "F15",
            Keyboard.KEY_KANA,                "KANA",
            Keyboard.KEY_CONVERT,             "CONVERT",
            Keyboard.KEY_NOCONVERT,           "NOCONVERT",
            Keyboard.KEY_YEN,                 "YEN",
            Keyboard.KEY_NUMPADEQUALS,        "NUMPADEQUALS",
            Keyboard.KEY_CIRCUMFLEX,          "CIRCUMFLEX",
            Keyboard.KEY_AT,                  "AT",
            Keyboard.KEY_COLON,               "COLON",
            Keyboard.KEY_UNDERLINE,           "UNDERLINE",
            Keyboard.KEY_KANJI,               "KANJI",
            Keyboard.KEY_STOP,                "STOP",
            Keyboard.KEY_AX,                  "AX",
            Keyboard.KEY_UNLABELED,           "UNLABELED",
            Keyboard.KEY_NUMPADENTER,         "NUMPADENTER",
            Keyboard.KEY_RCONTROL,            "RCONTROL",
            Keyboard.KEY_NUMPADCOMMA,         "NUMPADCOMMA",
            Keyboard.KEY_DIVIDE,              "DIVIDE",
            Keyboard.KEY_SYSRQ,               "SYSRQ",
            Keyboard.KEY_RMENU,               "RMENU",
            Keyboard.KEY_PAUSE,               "PAUSE",
            Keyboard.KEY_HOME,                "HOME",
            Keyboard.KEY_UP,                  "UP",
            Keyboard.KEY_PRIOR,               "PRIOR",
            Keyboard.KEY_LEFT,                "LEFT",
            Keyboard.KEY_RIGHT,               "RIGHT",
            Keyboard.KEY_END,                 "END",
            Keyboard.KEY_DOWN,                "DOWN",
            Keyboard.KEY_NEXT,                "NEXT",
            Keyboard.KEY_INSERT,              "INSERT",
            Keyboard.KEY_DELETE,              "DELETE",
            Keyboard.KEY_LMETA,               "LMETA"
    );
}
