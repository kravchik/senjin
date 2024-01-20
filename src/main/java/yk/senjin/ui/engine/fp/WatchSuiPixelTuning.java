package yk.senjin.ui.engine.fp;

import yk.jcommon.fastgeom.Vec4f;
import yk.senjin.ui.core.SuiPanelDbgRect;
import yk.senjin.ui.core.SuiPositions;
import yk.senjin.util.GlWindow1;

import static yk.jcommon.fastgeom.Vec4f.v4;

/**
 * Created by Yuri Kravchik on 15.12.17.
 */
public class WatchSuiPixelTuning {
    private SuiEngineFp aui = new SuiEngineFp();
    private GlWindow1 window = new GlWindow1()
            .setSize(800, 800)
            .stopOnEsc()
            .onWindowReady(wh -> aui.init(wh))
            .onFirstFrame(this::onFirstPass)
            .onTick(aui);

    public static void main(String[] args) {
        System.out.println(System.getProperty("java.library.path"));
        new WatchSuiPixelTuning().window.start(1);
    }

    public void onFirstPass() {
        for (int i = 0; i < 20; i++) {
            addDbg(10 + i * 21, 100, i, i, v4(1, 1, 1, 1), null);
            addDbg(10 + i * 21, 130, i + 0.5f, i + 0.5f, v4(1, 1, 1, 1), null);
        }
        for (int i = 0; i < 20; i++) {
            addDbg(10 + i * 21, 160, i, i, null, v4(1, 1, 1, 1));
            addDbg(10 + i * 21, 190, i + 0.5f, i + 0.5f, null, v4(1, 1, 1, 1));
        }
    }

    public void addDbg(float x, float y, float w, float h, final Vec4f bodyCol, final Vec4f borderCol) {
        float s = 5;
        aui.getTopPanel().add(new SuiPanelDbgRect() {{
            pos = new SuiPositions() {{
                left = x - 1 - s + 1;
                top = y - 1 - s + 1;
                W = s;
                H = s;
            }};
            borderColor = v4(0, 1, 0, 1);
        }});
        aui.getTopPanel().add(new SuiPanelDbgRect() {{
            pos = new SuiPositions() {{
                left = x;
                top = y;
                W = w;
                H = h;
            }};
            bodyColor = bodyCol;
            borderColor = borderCol;
        }});
        aui.getTopPanel().add(new SuiPanelDbgRect() {{
            pos = new SuiPositions() {{
                left = x + w;
                top = y + h;
                W = s;
                H = s;
            }};
            borderColor = v4(0, 1, 0, 1);
        }});

    }
}
