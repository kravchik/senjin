package yk.senjin.shaders.gshader.analysis

import yk.jcommon.fastgeom.Vec2f
import yk.jcommon.fastgeom.Vec4f
import yk.senjin.examples.blend.BlendFi
import yk.senjin.shaders.gshader.FragmentShaderParent
import yk.senjin.shaders.gshader.Sampler2D
import yk.senjin.shaders.gshader.StandardFSOutput

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 23/10/15
 * Time: 20:46
 */
//initially based on http://habrahabr.ru/post/239085/
class BlendF extends FragmentShaderParent<BlendFi, StandardFSOutput> {

    final int MAX_KOEFF_SIZE = 32; //максимальный размер ядра (массива коэффициентов)

    //TODO fix without initialization ProgramGenerator fails
    public Sampler2D txt = new Sampler2D(); //размываемая текстура
    public int kSize; //размер ядра
    public float[] koeff = new float[MAX_KOEFF_SIZE]; //коэффициенты
    public Vec2f direction = new Vec2f(0.003f, 0f); //направление размытия с учетом радиуса размытия и aspect ratio, например (0.003, 0.0) - горизонтальное и (0.0, 0.002) - вертикальное

    @Override
    void main(BlendFi blendFi, StandardFSOutput o) {

        testFoo(blendFi.vTexCoord)
        testFoo(direction)
        kSize = 5
        txt.name = ""

        Vec4f sum = Vec4f(0.0); //результирующий цвет
//        Vec2f startDir = foo(direction)
        Vec2f startDir = foo(direction) * 0.5f
        for (int i=0; i<kSize; i++) //проходимся по всем коэффициентам
            sum += texture2D(txt, blendFi.vTexCoord + startDir + direction*(float)i) * koeff[i]; //суммируем выборки
        o.gl_FragColor = sum;
    }

    private void testFoo(Vec2f modified) {
        modified = new Vec2f()

        modified.x = 0


    }

    private Vec2f foo(Vec2f d) {
        if (1 == 2) return direction
        if (1 == 2) return d

        return -d * (kSize - 1)
    }
}
