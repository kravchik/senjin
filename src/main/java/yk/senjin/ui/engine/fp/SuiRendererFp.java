package yk.senjin.ui.engine.fp;

import org.lwjgl.opengl.GL11;
import yk.jcommon.fastgeom.Matrix4;
import yk.jcommon.fastgeom.Vec4f;
import yk.senjin.SomeTexture;
import yk.senjin.ui.core.SuiPanel;
import yk.senjin.ui.core.SuiPanelDbgRect;
import yk.senjin.ui.core.SuiPanelImage;
import yk.senjin.ui.core.SuiPanelString;
import yk.senjin.ui.core.text.SuiFontGlyph;
import yk.senjin.ui.core.text.SuiFontsCache;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static yk.jcommon.fastgeom.Matrix4.orthoPixel;
import static yk.jcommon.utils.MyMath.min;
import static yk.senjin.DDDUtils.glLoadMatrix;

/**
 * Created by Yuri Kravchik on 15.12.17.
 * TODO try to get rid of senjin dependency?
 * TODO or get rid of any renderer? Just make a package along side senjin?
 * TODO or just keep it in senjin and ignore dependencies issues for a while?
 */
public class SuiRendererFp {
    public boolean drawDebug;
    public SuiFontsCache fonts;

    public SuiRendererFp() {
        fonts = new SuiFontsCache();
    }

    public void render(SuiPanel topPanel) {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT);
        glMatrixMode(GL_MODELVIEW);
        glLoadMatrix(Matrix4.identity());//0,0 is LEFT BOTTOM
        glMatrixMode(GL_PROJECTION);
        glLoadMatrix(orthoPixel(0, topPanel.pos.resultW, topPanel.pos.resultH, 0, 0, 10));//0,0 is LEFT BOTTOM
        glDisable(GL_DEPTH_TEST);
        renderImpl(topPanel);
    }

    public void renderImpl(SuiPanel suiPanel) {
        if (!suiPanel.visible) return;
        suiPanel.updateSkin();
        if (suiPanel.skinUnder != null) renderImpl(suiPanel.skinUnder);

        float left = suiPanel.resultGlobalX;
        float top = suiPanel.resultGlobalY;
        float bot = top + suiPanel.resultH;
        float right = left + suiPanel.resultW;

        if (suiPanel instanceof SuiPanelImage) {
            SuiPanelImage im = (SuiPanelImage) suiPanel;

            if (im.isChanged) {
                im.isChanged = false;
                SomeTexture texture = new SomeTexture(im.bufferedImage);

                texture.magFilter = GL_NEAREST;
                //if (mipmap) result.minFilter = GL_LINEAR_MIPMAP_LINEAR;
                //else
                texture.minFilter = GL_NEAREST;
                //so that clear line gets repeated, not a part of the symbol
                texture.wrapS = GL_CLAMP_TO_EDGE;
                texture.wrapT = GL_CLAMP_TO_EDGE;


                if (im.data != null) ((SomeTexture) im.data).release();
                im.data = texture;
            }
            ((SomeTexture) im.data).enable(0);
            glColor4f(1, 1, 1, 1);

            //right/bot of texture is NOT included
            float tw = (im.bufferedImage.getWidth() - 1f) / im.bufferedImage.getWidth();
            float th = (im.bufferedImage.getHeight() - 1f) / im.bufferedImage.getHeight();
            
            glBegin(GL_QUADS);
            glTexCoord2f(0.0f, 0.0f); glVertex3f(left, top,  0f);
            glTexCoord2f(tw, 0.0f); glVertex3f(right,top,  0f);
            glTexCoord2f(tw, th); glVertex3f(right,bot,  0f);
            glTexCoord2f(0.0f, th); glVertex3f(left, bot,  0f);
            glEnd();

            ((SomeTexture) im.data).disable();
        }

        if (suiPanel instanceof SuiPanelString) {
            SuiPanelString ms = (SuiPanelString) suiPanel;
            drawText(ms, left, top, ms.getString(), ms.color.mul(1, 1, 1, 1), ms.scale);
        }

        if (suiPanel instanceof SuiPanelDbgRect) {
            SuiPanelDbgRect rect = (SuiPanelDbgRect) suiPanel;
            if (rect.bodyColor != null) {
                glColor4f(rect.bodyColor.x, rect.bodyColor.y, rect.bodyColor.z, rect.bodyColor.w);
                glBegin(GL11.GL_TRIANGLES);
                glVertex3f(left, top, 0);
                glVertex3f(right, bot, 0);
                glVertex3f(right, top, 0);
                glVertex3f(left, top, 0);
                glVertex3f(left, bot, 0);
                glVertex3f(right, bot, 0);
                glEnd();
            }

            if (rect.borderColor != null) {
                glColor4f(rect.borderColor.x, rect.borderColor.y, rect.borderColor.z, rect.borderColor.w);
                glLineWidth(1);//it doesn't paint last point in line
                glBegin(GL11.GL_LINES);
                glVertex3f(left, top, 0);
                glVertex3f(right+1, top, 0);
                glVertex3f(right, top, 0);
                glVertex3f(right, bot+1, 0);
                glVertex3f(right, bot, 0);
                glVertex3f(left-1, bot, 0);
                glVertex3f(left, bot, 0);
                glVertex3f(left, top-1, 0);
                glEnd();
            }
        }

        for (SuiPanel child : suiPanel.children) {
            renderImpl(child);
        }

        if (suiPanel.skinOver != null) renderImpl(suiPanel.skinOver);

        if (drawDebug) {
            if (suiPanel.mouseHovers) glColor4f(1, 0.5f, 0.5f, 1); else glColor4f(1, 1, 1, 0.5f);
            float x = suiPanel.mouseHovers ? 1 : 0;
            float l = left + x;
            float r = right - x;
            float b = bot - x;
            float t = top + x;
            glLineWidth(1);
            glBegin(GL11.GL_LINES);
            glVertex3f(l, t, 0);
            glVertex3f(r, t, 0);
            glVertex3f(r, t, 0);
            //float corner = 10;
            float corner = min(suiPanel.resultW, suiPanel.resultH) * 0.2f;
            glVertex3f(r, b - corner, 0);
            glVertex3f(r, b - corner, 0);
            glVertex3f(r - corner, b, 0);
            glVertex3f(r - corner, b, 0);
            glVertex3f(l, b, 0);
            glVertex3f(l, b, 0);
            glVertex3f(l, t, 0);
            glEnd();
        }
    }

    public void drawText(SuiPanelString sps, float x, float y, String string, Vec4f color, float scale) {
        if (sps.renderingData == null) sps.renderingData = fonts.getFont(sps);
        SuiFont font = (SuiFont) sps.renderingData;

        SuiConsoleStringState scss = new SuiConsoleStringState(color);

        font.getOglTexture().enable(0);
        glBegin(GL11.GL_QUADS);
        glColor4f(color.x, color.y, color.z, color.w);

        float curX = x;
        for (int i = 0; i < string.length(); i++) {
            //for debugging
            //Rnd rnd = new Rnd(i);
            //glColor4f(rnd.nextFloat(), rnd.nextFloat(), rnd.nextFloat(), color.w);

            char c = string.charAt(i);
            if (c == 27) {
                i = scss.parseModifier(string, i).getCurPos() - 1;

                if (scss.isFontChanged) {
                    glEnd();
                    font.getOglTexture().disable();
                    font = fonts.getFont(sps, (scss.bold ? Font.BOLD : 0) | (scss.italic ? Font.ITALIC : 0));
                    font.getOglTexture().enable(0);
                    glBegin(GL11.GL_QUADS);
                }

                if (scss.isColorChanged) {
                    glColor4f(scss.fontColor.x, scss.fontColor.y, scss.fontColor.z, scss.fontColor.w);
                }

                continue;
            }


            SuiFontGlyph glyph = font.glyphs.get(c);
            if (glyph == null) glyph = font.glyphs.get('#');

            float vx = curX - glyph.borderW * scale;

            float vw = glyph.pixelW * scale;
            float vh = glyph.pixelH * scale;

            glTexCoord2f(glyph.relLeft, glyph.relTop);
            glVertex3f(vx, y + vh, 0);

            glTexCoord2f(glyph.relRight, glyph.relTop);
            glVertex3f(vx + vw, y + vh, 0);

            glTexCoord2f(glyph.relRight, glyph.relBottom);
            glVertex3f(vx + vw, y, 0);

            glTexCoord2f(glyph.relLeft, glyph.relBottom);
            glVertex3f(vx, y, 0);

            curX += vw - glyph.borderW * 2 * scale + scale;//+ 1 * scale is to compensage "far side to be included"
        }

        glEnd();

        font.getOglTexture().disable();
    }


}
