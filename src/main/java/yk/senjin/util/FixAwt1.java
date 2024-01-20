package yk.senjin.util;

import java.awt.image.BufferedImage;

public class FixAwt1 {
    public static void fix() {
        new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).createGraphics();
    }
}
