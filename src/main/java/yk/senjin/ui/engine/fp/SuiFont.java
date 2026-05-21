package yk.senjin.ui.engine.fp;


import yk.senjin.SomeTexture;
import yk.senjin.ui.core.text.SuiFontGlyph;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NEAREST;

public class SuiFont {//TODO separate core and engine!

    private FontMetrics fontMetrics;
    public Map<Character, SuiFontGlyph> glyphs;
    private SomeTexture oglTexture;//TODO Object renderData
    public BufferedImage bi;//TODO remove?
    //clear borders around a symbol
    private int borderW;
    private int borderH;
    private boolean mipmap;

    //TODO UTF symbols by demand
    //TODO different layout types
    public SuiFont(Font font, boolean antiAlias, boolean mipmap) {
        this.mipmap = mipmap;
        glyphs = new HashMap<>();
        Graphics2D tempG = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).createGraphics();
        setupFontRendering(tempG, antiAlias);
        tempG.setFont(font);
        fontMetrics = tempG.getFontMetrics();
        tempG.dispose();
        borderW = font.isItalic() ? 5 : 2;
        borderH = 0;
        initBufferedImage(font, antiAlias);
    }

    //TODO fix for italics
    public static void main(String[] args) {

        GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
        for(String font:e.getAvailableFontFamilyNames()) {
            System.out.println(font);
        }

        BufferedImage tempImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = tempImage.createGraphics();
        //if (antiAlias) tempG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Font awtFont = new Font("Times New Roman", Font.ITALIC | Font.BOLD, 24);
        g.setFont(awtFont);
        FontMetrics fontMetrics = g.getFontMetrics();

        System.out.println("width:      " + fontMetrics.charWidth('0'));
        System.out.println("width:      " + fontMetrics.stringWidth("0"));
        System.out.println("max bounds: " + fontMetrics.getStringBounds("0", g));
        System.out.println("max bounds: " + fontMetrics.stringWidth("0"));

    }

    private void initOglTexture(boolean mipmap) {
        oglTexture = new SomeTexture();
        oglTexture.magFilter = GL_NEAREST;
        oglTexture.minFilter = mipmap ? GL_LINEAR_MIPMAP_LINEAR : GL_NEAREST;
        oglTexture.uploadImage(bi);
    }

    private void initBufferedImage(Font font, boolean antiAlias) {
        int textureWidth = 0;
        for (int i = 32; i < 126; i++) textureWidth += fontMetrics.charWidth(i) + borderW * 2 + 2;
        int textureHeight = fontMetrics.getHeight() + borderH * 2 + 2;

        bi = new BufferedImage(textureWidth, textureHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gAll = bi.createGraphics();
        gAll.setBackground(new Color(255, 255, 255, 0));
        gAll.clearRect(0, 0, textureWidth, textureHeight);

        int x = 0;
        for (int i = 32; i < 126; i++) {
            char c = (char) i;
            BufferedImage biChar = createCharImage(font, c, antiAlias);
            if (biChar == null) continue;
            gAll.drawImage(biChar, x, 0, null);
            SuiFontGlyph glyph = new SuiFontGlyph(x, 0, biChar.getWidth(), biChar.getHeight(),
                    textureWidth, textureHeight, borderW);
            glyphs.put(c, glyph);
            //+1 is to make guaranteed empty space between symbols
            //  as despite the borderW, a symbol still can 'touch' the edge
            //  and when it touches, it can leak to the neighbour (because texture coords are not int pixels)
            //  can be seen on large font size (given all symbols are rendered in one line)
            //  on '0', 'i' because of their neighbors
            x += biChar.getWidth() + 1;
        }
    }

    private BufferedImage createCharImage(Font font, char c, boolean antiAlias) {
        int w = fontMetrics.charWidth(c) + borderW * 2;
        int h = fontMetrics.getHeight() + borderH * 2;
        if (w == 0) return null;
        BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        drawCharImage(result, font, c, antiAlias);
        if (!antiAlias) {
            if (!hasInk(result)) drawCharImage(result, font, c, true);
            hardenAlpha(result);
        }
        return result;
    }

    private void drawCharImage(BufferedImage result, Font font, char c, boolean antiAlias) {
        Graphics2D g = result.createGraphics();
        //for debugging
        //g.setBackground(new Color(100, 0, 0, 255));
        g.setBackground(new Color(255, 255, 255, 0));
        g.clearRect(0, 0, result.getWidth(), result.getHeight());
        setupFontRendering(g, antiAlias);
        //Toolkit tk = Toolkit.getDefaultToolkit();
        //Map desktopHints = (Map) (tk.getDesktopProperty("awt.font.desktophints"));
        //if (desktopHints != null) {
        //    tempG.addRenderingHints(desktopHints);
        //}

        g.setFont(font);
        g.setPaint(Color.WHITE);
        // Draw the text using Graphics2D
        g.drawString(String.valueOf(c), borderW, fontMetrics.getAscent() + borderH);

        //for debugging
        //g.setPaint(Color.blue);
        //g.drawLine(0,0,0, 3);
        //g.drawLine(0,15,0, 18);

        // Draw the text using TextLayout
        //TextLayout layout = new TextLayout(String.valueOf(c), font, g.getFontRenderContext());
        //layout.draw(g, borderW, fontMetrics.getAscent() + borderH);

        g.dispose();
    }

    private static void setupFontRendering(Graphics2D g, boolean antiAlias) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antiAlias
                ? RenderingHints.VALUE_ANTIALIAS_ON
                : RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, antiAlias
                ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON
                : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
    }

    private static boolean hasInk(BufferedImage image) {
        int w = image.getWidth();
        int h = image.getHeight();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if ((image.getRGB(x, y) >>> 24) != 0) return true;
            }
        }
        return false;
    }

    private static void hardenAlpha(BufferedImage image) {
        int w = image.getWidth();
        int h = image.getHeight();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int argb = image.getRGB(x, y);
                int alpha = argb >>> 24;
                image.setRGB(x, y, alpha < 128 ? 0 : 0xffffffff);
            }
        }
    }

    //WARNING! Doesn't count console modifications! (like \33[3m)
    public int getWidth(String s) {
        return fontMetrics.stringWidth(s);
    }

    public int getHeight() {
        return fontMetrics.getHeight();
    }

    public SomeTexture getOglTexture() {
        if (oglTexture == null) initOglTexture(mipmap);
        return oglTexture;
    }
}
