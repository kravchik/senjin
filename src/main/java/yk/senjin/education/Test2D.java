package yk.senjin.education;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 20/11/15
 * Time: 12:37
 */
public class Test2D extends BaseEdu2D {
    public static void main(String[] args) {
        new Viewer(new Test2D(), 800, 800);
    }

    //drawCircle(x, y, r)
    //drawLine(x1,y1,x2,y2)
    //drawEllipse(x, y, r1, r2)
    //drawString(s, x, y)
    //clean()


    @Override
    public void onTick(Viewer watch, float dt) {
        super.onTick(watch, dt);

        //drawCircle(3,0,3);
        //drawLine(3,1,2,2);
        //drawCircle(0,5,5);

        //smile
//        setColor(1,0,0);
//        fillEllipse(14,8,3,4);
//        drawLine(7,10,14,9);
//        drawLine(21,10,14,9);
//        setColor (1,1,0);//жёлтый
//        fillCircle(14,14,3);//голова
//        setColor(0,0,0);//чёрный
//        drawLine(12,13,15,13);//рот
//        setColor(1,1,1);
//        fillCircle(12,15,0.3);//левый глаз
//        setColor(0,0,0);
//        drawPoint(12,15);//левый зрачёк
//        setColor(1,1,1);
//        fillCircle(15,15,0.3);//правый глаз
//        setColor(1,1,1);
//        setColor(0,0,0);
//        drawPoint(15,15);//правый зрачёк
//        drawPoint(13.5,14);
//
//
//        //drawPoint(1, 1);
//        //setColor (1,1,0);
//
//        drawCircle(7,11,6);
//        drawCircle(17,0,2);
//        drawCircle(0,00,5);
//        drawCircle(17,0,1);
//        drawCircle(7,11,5);
//        drawPoint(7,11);
//        drawPoint(17,7);
//        //drawPoint(17,17);
        //drawLine(17,17,17,7);
        //drawLine(-3,17,15,19);


        double lol = 8.20;
        lol = 8;
        double kek = 12.03;
        kek = 8;
//        drawCircle(lol, kek, 7);
//        drawCircle(lol, kek, 6);
//        drawCircle(lol, kek, 0.7);
//        drawCircle(lol, kek, 5);
//        drawCircle(lol, kek, 4);
//        drawCircle(lol, kek, 3);
//        drawCircle(lol, kek, 2);
//        drawCircle(lol, kek, 1);
//        drawCircle(lol, kek, 0.9);
//        drawCircle(lol, kek, 0.8);
//        drawCircle(lol, kek, 0.6);
//        drawString("100", lol-0.4, kek-0.2);
//        drawString("80", lol-0.25, kek+1.3);
//        drawString("60", lol-0.25, kek+2.4);
//        drawString("40", lol-0.25, kek+3.4);
//        drawString("20", lol-0.25, kek+4.4);
//
//        drawString("80", lol-0.25, kek-1.6);
//        drawString("60", lol-0.25, kek-2.7);
//        drawString("40", lol-0.25, kek-3.8);
//        drawString("20", lol-0.25, kek-4.9);


//        setColor(0.5, 0.5, 0.5);
//        fillCircle(10, 10, 3);


        setColor(0.1, 0.1, 0.1);
        fillCircle(lol, kek, 4);
        setColor(0.7, 0.7, 0.7);
        fillCircle(lol, kek, 3);
        setColor(1.0, 1.0, 0.9);
        fillCircle(lol, kek, 2);
        setColor(0.0, 0.0, 0.0);
        fillCircle(lol, kek, 1);
        setColor(1.0, 1.0, 0.9);
        drawString("100", lol - 0.4, kek - 0.2);
        setColor(0.1, 0.1, 0.1);
        drawString("80", lol - 0.25, kek + 1.3);
        drawString("60", lol - 0.25, kek + 2.4);
        setColor(1.0, 1.0, 0.9);
        drawString("40", lol - 0.25, kek + 3.4);
        setColor(0.1, 0.1, 0.1);
        drawString("80", lol - 0.25, kek - 1.6);
        drawString("60", lol - 0.25, kek - 2.7);
        setColor(1.0, 1.0, 0.9);
        drawString("40", lol - 0.25, kek - 3.8);


        setColor(1.0, 0.0, 0.0);
        //левая линия
        drawLine(16, 14, 16, 10);
        //нижняя линия
        drawLine(16 , 10, 20, 10);
        //drawLine(19, 10,              16.33, 12);
        //правая линия
        drawLine(20, 14, 20, 10);
        //верхняя линия
        drawLine(20, 14  , 16, 14);
        drawPoint(18, 12);


        double cx = mouseX;
        double cy = mouseY;

        setColor(0,  0.5,  0);


        drawCircle(cx, cy, 1);
        drawPoint(cx, cy);
        //вертикальная линия
        drawLine(cx , cy - 1,  cx,  cy + 1);
        //горизонтальная линия
        drawLine(cx - 1,   cy ,  cx +1  ,  cy );



























    }
}