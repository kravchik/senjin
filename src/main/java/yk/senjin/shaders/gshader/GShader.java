package yk.senjin.shaders.gshader;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.Util;
import yk.jcommon.utils.BadException;
import yk.jcommon.utils.FileWatcher2;
import yk.jcommon.utils.IO;
import yk.jcommon.utils.StopWatch;

import java.io.File;

import static yk.senjin.shaders.ShaderHandler.*;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 16/03/16
 * Time: 09:57
 */
public class GShader {
    public boolean singleOwner = true;
    public ShaderGenerator generator;
    public int shaderIndex = -1;

    private FileWatcher2 fileWatcher;
    private String srcDir;

    public static GShader createShader(String srcDir, ShaderParent groovyShader, String shaderType) {
        return createShader(srcDir, groovyShader, shaderType, true);
    }

    public static GShader createShader(String srcDir, ShaderParent groovyShader, String shaderType, boolean send) {
        srcDir = srcDir.replace("/", File.separator);
        String path1 = groovyShader.getClass().getName();
        String resultPath = srcDir + path1.replace(".", "/") + ".groovy";
        String gSrc;
        if (!new File(resultPath).exists()) {
            System.out.println("src file not found (" + resultPath + ") getting shader src from resource");
            resultPath = path1.replace(".", "/") + ".groovy";
            gSrc = IO.streamToString(groovyShader.getClass().getResourceAsStream("/" + resultPath));
        } else {
            gSrc = IO.readFile(resultPath);
        }

        GShader result = new GShader();
        result.srcDir = srcDir;

        StopWatch sw = new StopWatch();
        result.generator = new ShaderGenerator(gSrc, resultPath, groovyShader, shaderType);
//        System.out.println(result.generator.resultSrc);
        sw = new StopWatch();
        if (send) result.sendShaderToCard();
        return result;
    }

    public GShader runtimeReload() {
        if (fileWatcher != null) BadException.die("already watching");
        fileWatcher = new FileWatcher2(generator.srcPath);
        if (!fileWatcher.exists) {
            System.out.println("WARNING: runtime reload is requested, but shader src is currently not exists (reloading could not be possible) " + generator.srcPath);
        }
        return this;
    }

    public void tick() {
        boolean changed = fileWatcher != null && fileWatcher.isJustChanged();
        if (changed) {
            System.out.println("Shader file changed: " + generator.srcPath);
            GShader newShader;
            try {
                newShader = createShader(srcDir, generator.shaderGroovy, generator.shaderType);
                newShader.sendShaderToCard();
            } catch (Exception e) {
                System.out.println("error while shader reloading");
                return;
            }
            generator = newShader.generator;
            removeShaderFromCard();
            shaderIndex = newShader.shaderIndex;
        }
    }

    //TODO handle fails

    /**<br><br><b>Don't forget to remove shader after programs compilation*/
    public void sendShaderToCard() {
        if (generator.shaderType.equals("vs")) shaderIndex = createVertexShader(stringToBuffer(generator.resultSrc));
        else if (generator.shaderType.equals("fs")) shaderIndex = createFragmentShader(stringToBuffer(generator.resultSrc));
        else BadException.die("unknown shader type: " + generator.shaderType);
//        printLogInfo(shaderIndex);
        Util.checkGLError();
    }

    public void removeShaderFromCard() {
        GL20.glDeleteShader(shaderIndex);
        shaderIndex = -1;
        Util.checkGLError();
    }

// concurrency
//    @Override
//    protected void finalize() throws Throwable {
//        super.finalize();
//        System.out.println("deleting shader in finilizer");
//        if (shaderIndex != -1) removeShaderFromCard();
//    }
}
