package yk.senjin.ui.engine.fp;

import yk.senjin.ui.core.SuiPanel;
import yk.senjin.ui.core.SuiPanelImage;
import yk.senjin.ui.core.SuiPanelString;
import yk.senjin.viewers.GlWindow1;
import yk.ycollections.YList;

import java.awt.*;

import static yk.jcommon.utils.MyMath.max;
import static yk.jcommon.utils.MyMath.min;
import static yk.senjin.ui.core.SuiPanel.panel;
import static yk.senjin.ui.core.SuiPosHBox.hbox;
import static yk.senjin.ui.core.SuiPosVBox.vbox;
import static yk.senjin.ui.core.SuiPositions.pos;
import static yk.senjin.util.Utils.forThis;
import static yk.ycollections.YArrayList.al;

/**
 * Created by Yuri Kravchik on 2023.03.20
 *
 *
 * <a href="https://stackoverflow.com/questions/76362308/create-png-without-text-anti-aliasing-in-java-11-on-mac-os-x">Looks like you can't disable antialiasing on Mac</a>
 */
public class WatchSuiFontsRendering {
    //TODO font selection
    private SuiEngineFp sui = new SuiEngineFp();
    public GlWindow1 window = new GlWindow1()
            .setUxSize(800, 800)
            .stopOnEsc()
            .onWindowReady(wh -> sui.init(wh))
            .onFirstFrame(this::onFirstPass)
            .onTick(sui);


    private int sizeSelected = 14;
    private SuiPanelString sizeString;
    private YList<Float> scales = al(0.1f, 0.3f, 0.5f, 0.9f, 1f, 1.1f, 1.5f, 2f, 4f);
    private int scaleSelected = scales.indexOf(1f);
    private SuiPanelString scaleString;
    private boolean aaSelected = true;
    private boolean mipmapSelected = false;
    private float imagesDisplacement;

    private SuiPanel stringsVbox;
    private SuiPanel fontsVBox;

    public static void main(String[] args) {
        new WatchSuiFontsRendering().window.start(10);
    }

    public void onFirstPass() {
        sui.tick(0);//because fonts should be already available
        stringsVbox = panel(vbox().interval(5));
        sui.getTopPanel().add(panel(hbox()).add(stringsVbox));

        sui.getTopPanel().add(
                panel(vbox().right(10f).top(10f)).add(
                        // size << 24 >> |
                        panel(hbox().right(0f)).add(
                                new SuiPanelString("size << ") {{
                                    onMouseUp.add(ams -> {
                                        sizeSelected = max(sizeSelected - 1, 2);
                                        recreate();
                                    });
                                }},
                                sizeString = new SuiPanelString(sizeSelected + ""),
                                new SuiPanelString(" >> |") {{
                                    onMouseUp.add(ams -> {
                                        sizeSelected = min(sizeSelected + 1, 64);
                                        recreate();
                                    });
                                }}
                        ),
                        // scale << 1 >> |
                        panel(hbox().right(0f)).add(
                                new SuiPanelString("scale << ") {{
                                    onMouseUp.add(ams -> {
                                        scaleSelected = max(scaleSelected - 1, 0);
                                        recreate();
                                    });
                                }},
                                scaleString = new SuiPanelString(scales.get(scaleSelected) + ""),
                                new SuiPanelString(" >> |") {{
                                    onMouseUp.add(ams -> {
                                        scaleSelected = min(scaleSelected + 1, scales.size() - 1);
                                        recreate();
                                    });
                                }}
                        ),
                        //AA
                        new SuiPanelString(pos().right(0f), "AA |") {{
                            onMouseUp.add(ams -> {
                                aaSelected = !aaSelected;
                                setString(aaSelected ? "AA |" : "aa |");
                                recreate();
                            });
                        }},
                        //mipmap
                        new SuiPanelString(pos().right(0f), "mipmap |") {{
                            onMouseUp.add(ams -> {
                                mipmapSelected = !mipmapSelected;
                                setString(mipmapSelected ? "MIPMAP |" : "mipmap |");
                                recreate();
                            });
                        }},
                        // fonts << 1 >> |
                        panel(hbox().right(0f)).add(
                                new SuiPanelString("fonts << ") {{
                                    onMouseUp.add(ams -> {
                                        imagesDisplacement -= 500;
                                        recreate();
                                    });
                                }},
                                new SuiPanelString(" >> |") {{
                                    onMouseUp.add(ams -> {
                                        imagesDisplacement += 500;
                                        recreate();
                                    });
                                }}
                        )
                )
        );

        sui.getTopPanel().add(fontsVBox = panel(vbox().interval(5f).bottom(5f).percentWidth(1f)));
        recreate();
    }
                             
    private void recreate() {
        sizeString.setString(sizeSelected + "");
        scaleString.setString(scales.get(scaleSelected) + "");
        stringsVbox.children.clear();
        fontsVBox.children.clear();
        stringsVbox.add(
                createString(false, false),
                createString(false, true),
                createString(true, false),
                createString(true, true));
        fontsVBox.add(stringsVbox.children.map(child -> forThis(new SuiPanelImage(), p -> {
            p.pos.left = imagesDisplacement;
            p.scale = 4;
            p.setImage(sui.sui.fonts.getFont((SuiPanelString) child).bi);
        })));
        sui.recalcLayout();
    }

    private SuiPanel createString(boolean bold, boolean italic) {
        int fs = 0;
        YList<String> additional = al();

        if (bold) {fs |= Font.BOLD; additional.add("bold");}
        if (italic) {fs |= Font.ITALIC; additional.add("italic");}

        int finalFs = fs;
        SuiPanelString suiPanelString = new SuiPanelString("Test: " + ((char) 33) + ((char) 125) + "ATWjJ T W Z awz 09_|/\\"
                + " " + (sizeSelected)
                + " " + (scales.get(scaleSelected))
                + " " + additional.toString("|")
        );
        suiPanelString.fontName = "Ubuntu Mono";
        suiPanelString.fontSize = sizeSelected;
        suiPanelString.fontStyle = finalFs;
        suiPanelString.scale = scales.get(scaleSelected);
        suiPanelString.fontMipmap = mipmapSelected;
        suiPanelString.fontAntialiased = aaSelected;
        return suiPanelString;
    }
}
