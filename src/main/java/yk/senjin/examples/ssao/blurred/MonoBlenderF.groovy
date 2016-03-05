package yk.senjin.examples.ssao.blurred

import yk.jcommon.fastgeom.Vec2f
import yk.jcommon.fastgeom.Vec4f
import yk.senjin.examples.blend.BlendFi
import yk.senjin.shaders.gshader.FragmentShaderParent
import yk.senjin.shaders.gshader.Sampler2D
import yk.senjin.shaders.gshader.StandardFSOutput

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 24/02/16
 * Time: 15:13
 */
class MonoBlenderF extends FragmentShaderParent<BlendFi, StandardFSOutput> {

    final int MAX_KOEFF_SIZE = 32; //максимальный размер ядра (массива коэффициентов)

    //TODO fix without initialization ProgramGenerator fails
    public Sampler2D txt = new Sampler2D(); //размываемая текстура
    public int kSize; //размер ядра
    public float[] koeff = new float[MAX_KOEFF_SIZE]; //коэффициенты
    public Vec2f direction = new Vec2f(0.003f, 0f); //направление размытия с учетом радиуса размытия и aspect ratio, например (0.003, 0.0) - горизонтальное и (0.0, 0.002) - вертикальное

    @Override
    void main(BlendFi blendFi, StandardFSOutput o) {
        Vec4f sum = Vec4f(0); //результирующий цвет
//        float sum = 1; //результирующий цвет
        Vec2f startDir = -0.5f*direction*(kSize)

        Vec4f center = texture2D(txt, blendFi.vTexCoord)
        for (int i=0; i<kSize; i++) {
            Vec4f cur = texture2D(txt, blendFi.vTexCoord + startDir + direction * i)
            if (cur.x - 0.1f > center.x) sum += min(center, cur) * koeff[i];
            else
            sum += cur * koeff[i];
        }
//        for (int i=0; i<kSize; i++) {
//            float cur = texture2D(txt, blendFi.vTexCoord + startDir + direction * i).x
////            sum += max(center*0.8f, cur) * koeff[i];
//            sum = min(sum, cur * koeff[i]);
//        }
        sum.w = 1

//        o.gl_FragColor = center;
        o.gl_FragColor = sum;
//        o.gl_FragColor = Vec4f(sum, center.y, center.z, center.w);
//        o.gl_FragColor = max(texture2D(txt, blendFi.vTexCoord), sum);
    }
}