package yk.senjin.ui.engine.fp;

import yk.senjin.ui.core.SuiPanel;
import yk.senjin.ui.core.SuiPanelImage;
import yk.senjin.util.GlWindow1;

import static yk.jcommon.utils.IO.readImage;

/**
 * Created by yuri at 2023.03.14
 */
public class WatchGuiWithGlsl {
    private SuiEngineFp ui = new SuiEngineFp(new SuiPanel().add(
            new SuiPanelImage().setImage(readImage("/Users/ykravchik/Screenshots/Screenshot 2023-10-30 at 20.07.11.png"))));

    private GlWindow1 window = new GlWindow1()
            .setSize(800, 800)
            .stopOnEsc()
            .onWindowReady(wh -> ui.init(wh))
            .onTick(ui);

    public static void main(String[] args) {
        new WatchGuiWithGlsl().window.start(1);
    }
}
