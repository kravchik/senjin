package yk.senjin.ui.engine.fp;

import yk.jcommon.collections.YList;
import yk.senjin.ui.core.*;
import yk.senjin.util.GlWindow1;

import java.awt.*;

import static yk.jcommon.collections.YArrayList.al;
import static yk.jcommon.utils.MyMath.max;
import static yk.jcommon.utils.MyMath.min;
import static yk.senjin.util.Utils.forThis;

/**
 * Created by Yuri Kravchik on 2023.03.20
 */
public class WatchSuiFontsRendering {
    //TODO font selection
    private SuiEngineFp sui = new SuiEngineFp();
    private GlWindow1 window = new GlWindow1()
            .setSize(800, 800)
            .stopOnEsc()
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
        new WatchSuiFontsRendering().window.start(1);
    }

    public void onFirstPass() {
        sui.tick(0);//because fonts should be already available
        stringsVbox = new SuiPanel(){{pos = new SuiPosVBox(){{interval=5;}};}};
        sui.getTopPanel().add(new SuiPanel(new SuiPosHBox()).add(stringsVbox));

        sui.getTopPanel().add(
                new SuiPanel(new SuiPosVBox(){{right=10f;top=10f;}}).add(
                        // size << 24 >> |
                        new SuiPanel(new SuiPosHBox(){{right=0f;}}).add(
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
                        new SuiPanel(new SuiPosHBox(){{right=0f;}}).add(
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
                        new SuiPanelString("AA |") {{
                            pos.right = 0f;
                            onMouseUp.add(ams -> {
                                aaSelected = !aaSelected;
                                setString(aaSelected ? "AA |" : "aa |");
                                recreate();
                            });
                        }},
                        //mipmap
                        new SuiPanelString("mipmap |") {{
                            pos.right=0f;
                            onMouseUp.add(ams -> {
                                mipmapSelected = !mipmapSelected;
                                setString(mipmapSelected ? "MIPMAP |" : "mipmap |");
                                recreate();
                            });
                        }},
                        // fonts << 1 >> |
                        new SuiPanel(new SuiPosHBox(){{right=0f;}}).add(
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

        sui.getTopPanel().add(fontsVBox = new SuiPanel(new SuiPosVBox(){{bottom = 5f;interval=5f;percentWidth = 1f;}}));
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
