package yk.senjin.examples.ssao.blurred

import yk.jcommon.fastgeom.Vec2f
import yk.jcommon.fastgeom.Vec4f
import yk.senjin.examples.blend.BlendFi
import yk.senjin.shaders.gshader.FragmentShaderParent
import yk.senjin.shaders.gshader.StandardFSOutput
import yk.senjin.shaders.uniforms.Sampler2D

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 24/02/16
 * Time: 15:13
 */
class MonoBlenderF extends FragmentShaderParent<BlendFi, StandardFSOutput> {
    //TODO account depth (not blend too different depths)

    final int MAX_KOEFF_SIZE = 32; //максимальный размер ядра (массива коэффициентов)

    //TODO fix without initialization ProgramGenerator fails
    public Sampler2D txt = new Sampler2D(); //размываемая текстура

    public Sampler2D depth = new Sampler2D(); //z-buffer


    public int kSize; //размер ядра
    public float[] koeff = new float[MAX_KOEFF_SIZE]; //коэффициенты
    public Vec2f direction = new Vec2f(0.003f, 0f); //направление размытия с учетом радиуса размытия и aspect ratio, например (0.003, 0.0) - горизонтальное и (0.0, 0.002) - вертикальное

    @Override
    void main(BlendFi blendFi, StandardFSOutput o) {
        Vec4f sum = Vec4f(0); //результирующий цвет
//        float sum = 1; //результирующий цвет
        Vec2f startDir = -0.5f*direction*(kSize)

        float d1 = texture2D(depth, blendFi.vTexCoord).z


        Vec4f center = texture2D(txt, blendFi.vTexCoord)
        for (int i=0; i<kSize; i++) {
            Vec2f newPos = blendFi.vTexCoord + startDir + direction * i
            Vec4f cur = texture2D(txt, newPos)

            float d2 = texture2D(depth, newPos).z


            if (d1 < d2) {
//                sum += mix(cur, center, 1) * koeff[i];
                sum += mix(cur, center, clamp(abs((d1 - d2)/d1) * 1000, 0, 1)) * koeff[i];
            } else

//            if (abs(d1 - d2) > 0.001f)
//                sum += min(center, cur) * koeff[i];
//            if (cur.x - 0.0f > center.x) sum += min(center, cur) * koeff[i];
//            else
            sum += cur * koeff[i];
        }
//        for (int i=0; i<kSize; i++) {
//            float cur = texture2D(txt, blendFi.vTexCoord + startDir + direction * i).x
////            sum += max(center*0.8f, cur) * koeff[i];
//            sum = sum + cur * koeff[i];
////            sum = min(sum, cur * koeff[i]);
//        }
        sum.w = 1

//        float d = texture2D(depth, Vec2f(blendFi.vTexCoord.x, blendFi.vTexCoord.y)).z
//        if (d <-6) sum.x = 0;

//        o.gl_FragColor = center;

//        if (d1 > -5) sum.x = 0;
        o.gl_FragColor = sum
//        o.gl_FragColor = Vec4f(sum, center.y, center.z, center.w);
//        o.gl_FragColor = max(texture2D(txt, blendFi.vTexCoord), sum);
    }
}