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

        drawCircle(3,0,3);
        drawLine(3,1,2,2);
        drawCircle(0,5,5);

        //smile
        setColor(1,0,0);
        fillEllipse(14,8,3,4);
        drawLine(7,10,14,9);
        drawLine(21,10,14,9);
        setColor (1,1,0);//жёлтый
        fillCircle(14,14,3);//голова
        setColor(0,0,0);//чёрный
        drawLine(12,13,15,13);//рот
        setColor(1,1,1);
        fillCircle(12,15,0.3);//левый глаз
        setColor(0,0,0);
        drawPoint(12,15);//левый зрачёк
        setColor(1,1,1);
        fillCircle(15,15,0.3);//правый глаз
        setColor(1,1,1);
        setColor(0,0,0);
        drawPoint(15,15);//правый зрачёк
        drawPoint(13.5,14);


        drawPoint(1, 1);
        setColor (1,1,0);




    }
}
