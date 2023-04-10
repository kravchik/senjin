package yk.senjin.ui.core;

import java.awt.image.BufferedImage;

/**
 * Created by Yuri Kravchik on 06.10.2018
 */
public class SuiPanelImage extends SuiPanel {
    public float scale = 1;
    public BufferedImage bufferedImage;
    public boolean isChanged;
    public Object data;

    public SuiPanelImage() {
    }

    public SuiPanelImage(BufferedImage bufferedImage) {
        setImage(bufferedImage);
    }

    public SuiPanelImage setImage(BufferedImage bi) {
        this.bufferedImage = bi;
        pos.W = (float) bufferedImage.getWidth() * scale;
        pos.H = (float) bufferedImage.getHeight() * scale;
        isChanged = true;
        return this;
    }
}
