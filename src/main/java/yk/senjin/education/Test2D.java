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
        drawCircle(14,14,3);
        drawLine(12,13,15,13);
        drawCircle(12,15,0.3);
        drawPoint(12,15);
        drawCircle(15,15,0.3);
        drawPoint(15,15);

        drawPoint(1, 1);







    }
}
