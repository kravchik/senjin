package yk.senjin.ui.core.text;

public class SuiFontGlyph {

    public int pixelX;
    public int pixelY;
    public int pixelW;
    public int pixelH;
    public int borderW;
    public float relLeft;
    public float relRight;
    public float relTop;
    public float relBottom;

    public SuiFontGlyph(int pixelX, int pixelY, int pixelW, int pixelH, int textureWidth, int textureHeight, int borderW) {
        //-1 - is to far side to be included
        this.pixelW = pixelW - 1;
        this.pixelH = pixelH - 1;
        this.pixelX = pixelX;
        this.pixelY = pixelY;
        this.borderW = borderW;

        relLeft = (float) pixelX / textureWidth;
        relRight = (float) (pixelX + pixelW) / textureWidth;
        relTop = (float) (pixelY + pixelH) / textureHeight;
        relBottom = (float) pixelY / textureHeight;
    }}
