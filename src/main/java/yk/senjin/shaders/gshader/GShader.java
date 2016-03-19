package yk.senjin.shaders.gshader;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.Util;
import yk.jcommon.utils.BadException;
import yk.jcommon.utils.FileWatcher;
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

    private FileWatcher fileWatcher;
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
            resultPath = path1.replace(".", "/") + ".groovy";
            gSrc = IO.streamToString(groovyShader.getClass().getResourceAsStream("/" + resultPath));
        } else {
            gSrc = IO.readFile(resultPath);
        }

        GShader result = new GShader();
        result.srcDir = srcDir;

        StopWatch sw = new StopWatch();
        result.generator = new ShaderGenerator(gSrc, resultPath, groovyShader, shaderType);
        System.out.println("translated in " + sw.stop());
        sw = new StopWatch();
        if (send) result.sendShaderToCard();
        System.out.println("sent in " + sw.stop());
        return result;
    }

    public GShader runtimeReload() {
        if (fileWatcher != null) BadException.die("already watching");
        try {
            fileWatcher = new FileWatcher(generator.srcPath);
        } catch (Exception e) {
            System.out.println("warning: not watching for " + generator.srcPath);
        }
        return this;
    }

    public void tick() {
        boolean changed = fileWatcher != null && fileWatcher.isChanged();
        if (changed) {
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
