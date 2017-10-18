package yk.senjin.education;

import com.sun.javafx.geom.Vec3d;
import yk.jcommon.fastgeom.Vec2f;
import yk.jcommon.fastgeom.Vec3f;
import yk.jcommon.utils.Rnd;
import yk.senjin.LoadTickUnload;

import java.awt.*;

import static yk.jcommon.utils.MyMath.*;
import static yk.jcommon.utils.Util.sqr;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 20/11/15
 * Time: 11:46
 */
public class BaseEdu2D implements LoadTickUnload<Viewer> {
    private Rnd rnd = new Rnd();
    private Vec3d currentColor = new Vec3d(1, 1, 1);
    private Graphics2D g;
    private int bufferWidth;
    private int bufferHeight;

    public Vec2f axisTranslate = new Vec2f(150, 150);
    public float unitSize = 30;

    public void setColor(double r, double g, double b) {
        setColor(new Vec3d(r, g, b));
    }

    public void setColor(Vec3d c) {
        currentColor = c;
        updateColor();
    }

    public void updateColor() {
        g.setColor(new Color((int) (currentColor.x * 255), (int) (currentColor.y * 255), (int) (currentColor.z * 255)));
    }

    public Vec3f randomVector3() {
        return new Vec3f(rnd.nextFloat(), rnd.nextFloat(), rnd.nextFloat());
    }
    public Vec2f world2screen(double x, double y) {
        return world2screen(new Vec2f((float)x, (float)y));
    }
    public Vec2f world2screen(Vec2f pos) {
        return new Vec2f(pos.x*unitSize+axisTranslate.x, bufferHeight - (pos.y*unitSize + axisTranslate.y));
    }
    public Vec2f screen2world(Vec2f pos) {
        return new Vec2f((pos.x-axisTranslate.x)/unitSize, (bufferHeight - pos.y-axisTranslate.y)/unitSize);
    }

    public void drawPoint(double x1, double y1) {
        fillCircle(x1, y1, 0.1);
    }

    public void drawLine(Vec2f from, Vec2f to) {
        drawLine(from.x, from.y, to.x, to.y);
    }
    public void drawLine(double x1, double y1, double x2, double y2) {
        Vec2f from = world2screen(x1, y1);
        Vec2f to = world2screen(x2, y2);
        g.drawLine((int) from.x, (int) from.y, (int) to.x, (int) to.y);
    }

    public void drawRectangle(double x, double y, double width, double height) {
        Vec2f from = world2screen(x, y+height);
        g.drawRect((int) from.x, (int) from.y, (int) (width * unitSize), (int) (height * unitSize));
    }

    public void drawCircle(Vec2f pos, double radius) {
        drawCircle(pos.x, pos.y, radius);
    }
    public void drawCircle(double x, double y, double radius) {
        Vec2f from = world2screen(x-radius, y+radius);
        g.drawOval((int) from.x, (int) from.y, (int) (radius * 2 * unitSize), (int) (radius * 2 * unitSize));
    }

    public void drawEllipse(double x, double y, double radius1, double radius2) {
        Vec2f from = world2screen(x-radius1, y+radius2);
        g.drawOval((int) from.x, (int) from.y, (int) (radius1 * 2 * unitSize), (int) (radius2 * 2 * unitSize));
    }

    public void fillRectangle(double x, double y, double width, double height) {
        Vec2f from = world2screen(x, y+height);
        g.fillRect((int) from.x, (int) from.y, (int) (width * unitSize), (int) (height * unitSize));
    }

    public void fillCircle(double x, double y, double radius) {
        Vec2f from = world2screen(x-radius, y+radius);
        g.fillOval((int) from.x, (int) from.y, (int) (radius * 2 * unitSize), (int) (radius * 2 * unitSize));
    }

    public void fillEllipse(double x, double y, double radius1, double radius2) {
        Vec2f from = world2screen(x-radius1, y+radius2);
        g.fillOval((int) from.x, (int) from.y, (int) (radius1 * 2 * unitSize), (int) (radius2 * 2 * unitSize));
    }

    public void clean() {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, bufferWidth, bufferHeight);
        updateColor();
    }

    public void drawString(String s, double x, double y) {
        Vec2f pos = world2screen(x, y);
        g.drawString(s, pos.x, pos.y);
    }

    public void drawAxes(int r) {
        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                float progress = clamp((sqrt(sqr(x) + sqr(y))) / r, 0, 1);
                setColor(mix(0.5f, 1, progress), progress, progress);
                if (y == 0) {
                    drawLine(x, y, x + 1, y);
                    drawLine(x, y - 0.1f, x, y + 0.1f);
                }
                setColor(progress, mix(0.5f, 1, progress), progress);
                if (x == 0) {
                    drawLine(x, y, x, y + 1);
                    drawLine(x - 0.1f, y, x + 0.1f, y);
                }
            }
        }
    }

    public void drawGrid(int r) {
        for (int x = -r*2; x <= r*2; x++) {
            for (int y = -r*2; y <= r*2; y++) {
                {
                    double progress = Math.abs(y%2) == 0 ? 0 : 0.6f;
                    //double progress = clamp((sqrt(sqr(x/2f) + sqr(y/2f))) / r, 0, 1);
                    double gridColor = clamp((float) progress * 0.1f + 0.9f, 0, 1);
                    setColor(gridColor, gridColor, 1);
                    drawLine(x / 2f, y / 2f, x / 2f + 0.5f, y / 2f);
                }

                {
                    double progress = Math.abs(x%2) == 0 ? 0 : 0.6f;
                    //double progress = clamp((sqrt(sqr(x/2f) + sqr(y/2f))) / r, 0, 1);
                    double gridColor = clamp((float) progress * 0.1f + 0.9f, 0, 1);
                    setColor(gridColor, gridColor, 1);
                    drawLine(x / 2f, y / 2f, x / 2f, y / 2f + 0.5f);
                }
            }
        }
    }

    public void drawLegend() {
        setColor(0, 0, 0);
        drawString("0", -0.4f, -0.5f);
        drawString("1", 0.9f, -0.5f);
        drawString("1", -0.5f, 0.85f);
        drawString("-1", -1.2f, -0.5f);
        drawString("-1", -0.5f, -1.f);
        drawString("x", (bufferWidth - axisTranslate.x) / unitSize * 0.9f, -0.5f);
        drawString("y", -0.5f, (bufferHeight - axisTranslate.y) / unitSize * 0.9f);
        drawString("- x", (-axisTranslate.x/unitSize)*0.9f, -0.5f);
        drawString("- y", -0.7f, (-axisTranslate.y/unitSize)*0.9f);
    }

    @Override
    public void onLoad(Viewer watch) {
        g = (Graphics2D) watch.result.getGraphics();
        bufferWidth = watch.result.getWidth();
        bufferHeight = watch.result.getHeight();
    }

    public double mouseX;
    public double mouseY;

    private Viewer watch;

    @Override
    public void onTick(Viewer watch, float dt) {
        this.watch = watch;
        clean();
        drawGrid(20);
        drawAxes(20);
        drawLegend();


        drawMouseCoords();

    }

    public void drawMouseCoords() {
        Point mousePosition = watch.getMousePosition();
        if (mousePosition != null) {
            Vec2f s2w = screen2world(new Vec2f((float) mousePosition.getX(), (float) mousePosition.getY()));
            mouseX = s2w.x;
            mouseY = s2w.y;
        }
        drawString(String.format("mouse: x = %.2f   y = %.2f", mouseX, mouseY), 5, 20);
    }

    @Override
    public void onUnload() {
    }
}
