package yk.senjin.ui.core;

import yk.ycollections.YList;

import java.util.function.Consumer;

import static yk.ycollections.YArrayList.al;

/**
 * Created by Yuri Kravchik on 15.03.18.
 */
public class SuiPanelFixingButton extends SuiPanel {//TODO rename
    public boolean isPressed;
    public SuiPanel downSkin;
    public SuiPanel upSkin;
    public boolean canSelfRelease = true;

    public YList<Consumer<SuiPanelFixingButton>> onPress = al();
    public YList<Consumer<SuiPanelFixingButton>> onRelease = al();

    public SuiPanelFixingButton() {
        onMouseDown.add(m -> change());
    }

    public SuiPanelFixingButton(SuiPositions pos) {
        super(pos);
        onMouseDown.add(m -> change());
    }

    public void change() {
        if (isPressed) {if (canSelfRelease) release();}
        else press();
    }

    public void press() {
        boolean oldValue = isPressed;
        isPressed = true;
        if (!oldValue) for (int i = 0; i < onPress.size(); i++) onPress.get(i).accept(this);
    }

    public void release() {
        boolean oldValue = isPressed;
        isPressed = false;
        if (oldValue) for (int i = 0; i < onRelease.size(); i++) onRelease.get(i).accept(this);
    }
}

