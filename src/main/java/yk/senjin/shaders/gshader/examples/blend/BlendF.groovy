package yk.senjin.shaders.gshader.examples.blend

import yk.jcommon.fastgeom.Vec2f
import yk.jcommon.fastgeom.Vec4f
import yk.senjin.shaders.gshader.FragmentShaderParent
import yk.senjin.shaders.gshader.Sampler2D
import yk.senjin.shaders.gshader.StandardFrame

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 23/10/15
 * Time: 20:46
 */
class BlendF extends FragmentShaderParent<BlendFi> {

    final int MAX_KOEFF_SIZE = 32; //максимальный размер ядра (массива коэффициентов)

    //TODO fix without initialization ProgramGenerator fails
    public Sampler2D txt = new Sampler2D(); //размываемая текстура
    public float kSize; //размер ядра
    public float[] koeff = new float[MAX_KOEFF_SIZE]; //коэффициенты
    public Vec2f direction = new Vec2f(0.003f, 0f); //направление размытия с учетом радиуса размытия и aspect ratio, например (0.003, 0.0) - горизонтальное и (0.0, 0.002) - вертикальное


    @Override
    void main(BlendFi blendFi, StandardFrame o) {
        Vec4f sum = Vec4f(0.0); //результирующий цвет
        Vec2f startDir = -direction*(float)(kSize-1)*0.5;//  mysterious groovy parser bug
        for (int i=0; i<kSize; i++) //проходимся по всем коэффициентам
            sum += texture(txt, blendFi.vTexCoord + startDir + direction*(float)i) * koeff[i]; //суммируем выборки
        o.gl_FragColor = sum;
    }
}
