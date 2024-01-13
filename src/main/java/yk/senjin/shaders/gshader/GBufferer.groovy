package yk.senjin.shaders.gshader
import yk.jcommon.fastgeom.Vec2f
import yk.jcommon.fastgeom.Vec3f
import yk.jcommon.utils.IO

import java.lang.reflect.Field
/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 18/12/14
 * Time: 23:08
 */
class GBufferer {

    public static createFile(Class clazz) {
        IO.writeFile("secret/src/gen/${clazz.getSimpleName()}VBO.java", gen(clazz))
    }

    public static String gen(Class clazz) {
        def className = clazz.getSimpleName()
        Field[] fields = clazz.getDeclaredFields();

        String result = ""
        
        result += "    public void upload() {\n"
        result += "        for (${clazz.getSimpleName()} item : data) {\n"

        int size = 0;
        for (Field field : fields) {
            if (field.getType() == Vec3f.class) {
                result += "            buffer.putFloat(item." + field.name + ".x);\n"
                result += "            buffer.putFloat(item." + field.name + ".y);\n"
                result += "            buffer.putFloat(item." + field.name + ".z);\n"
                size += 3 * 4;
            }
            if (field.getType() == Vec2f.class) {
                result += "            buffer.putFloat(item." + field.name + ".x);\n"
                result += "            buffer.putFloat(item." + field.name + ".y);\n"
                size += 2 * 4;
            }
        }
        result += "        }\n"
        result += "        buffer.rewind();\n"
        result += "        glBindBuffer(GL_ARRAY_BUFFER, bufferId);\n"
        result += "        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);\n"
        result += "        glBindBuffer(GL_ARRAY_BUFFER, 0);\n"
        result += "    }\n"
        result += "\n"

        result += "    public void bindToShader(GShader shader) {\n" +
                "        shader.vbo2structure.put(this, new VertexStructureState(shader.shader, ${className}.class, bufferId));" +
                "    }\n" +
                "}\n"

        result = "package gen;\n" +
                "\n" +
                "import ${clazz.getName()};\n" +
                "import myengine.optiseq.states.VertexStructureState;\n" +
                "import myengine.optiseq.states.shaders.gshader.GShader;\n" +
                "import org.lwjgl.BufferUtils;\n" +
                "import yk.ycollections.YArrayList;\n" +
                "\n" +
                "import java.nio.ByteBuffer;\n" +
                "import java.util.List;\n" +
                "\n" +
                "import static org.lwjgl.opengl.GL15.*;\n" +
                "\n" +
                "public class ${className}VBO {\n" +
                "    public List<${className}> data;\n" +
                "    public ByteBuffer buffer;\n" +
                "    public int bufferId = glGenBuffers();\n" +
                "\n" +
                "    public void setData(List<${className}> data) {\n" +
                "        this.data = data;\n" +
                "        buffer = BufferUtils.createByteBuffer(${size} * data.size());\n" +
                "    }\n" +
                "\n" +
                "    public void setData(int size) {\n" +
                "        this.data = new YArrayList<>();\n" +
                "        for (int i = 0; i < size; i++) this.data.add(new ${className}());\n" +
                "        buffer = BufferUtils.createByteBuffer(size * data.size());\n" +
                "    }\n" +
                result

        return result


//        int attribProgIndex = 0 //is this prog index for sure?
//        int offset = 0
//        for (Field field : fields) {
//            println "    glEnableVertexAttribArray(${attribProgIndex});"
//            if (field.getType() == Vec3.class) {
//                println "    glVertexAttribPointer(${attribProgIndex}, 3, GL_FLOAT, GL_FALSE, ${size}, ${offset});   //The starting point of the VBO, for the vertices"
//                offset += 3 * 4
//            }
//            if (field.getType() == Vec2.class) {
//                println "    glVertexAttribPointer(${attribProgIndex}, 2, GL_FLOAT, GL_FALSE, ${size}, ${offset});   //The starting point of the VBO, for the vertices"
//                offset += 2 * 4
//            }
//
//            attribProgIndex += 1
//
//        }

    }
    
    

}
