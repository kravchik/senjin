package yk.senjin.shaders.gshader;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.Util;
import yk.jcommon.utils.BadException;
import yk.jcommon.utils.FileWatcher2;
import yk.jcommon.utils.IO;

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
    public ShaderTranslator oldGenerator;
    public ShaderTranslator generator;
    public int shaderIndex = -1;

    private FileWatcher2 fileWatcher;
    private String gSrc;

    private final String srcDir;
    private String srcPath;
    private final String shaderType;
    private final ShaderParent groovyShader;

    public String inputSuffix;
    public String outputSuffix;

    public GShader(String srcDir, ShaderParent groovyShader, String shaderType) {
        this.srcDir = srcDir.replace("/", File.separator);
        this.shaderType = shaderType;
        this.groovyShader = groovyShader;
        loadSrc(groovyShader, this.srcDir);
    }

    public void translate() {
        generator = new ShaderTranslator(gSrc, srcPath, groovyShader, shaderType, inputSuffix, outputSuffix);
        oldGenerator = generator;
        sendShaderToCard();
    }

    private void loadSrc(ShaderParent groovyShader, String path) {
        String fileName = groovyShader.getClass().getName();
        srcPath = path + fileName.replace(".", "/") + ".groovy";
        if (!new File(this.srcPath).exists()) {
            System.out.println("src file not found (" + this.srcPath + ") getting shader src from resource");
            this.srcPath = fileName.replace(".", "/") + ".groovy";
            gSrc = IO.streamToString(groovyShader.getClass().getResourceAsStream("/" + this.srcPath));
        } else {
            gSrc = IO.readFile(this.srcPath);
        }
    }

    public GShader runtimeReload() {
        if (fileWatcher != null) BadException.die("already watching");
        fileWatcher = new FileWatcher2(srcPath);
        if (!fileWatcher.exists) {
            System.out.println("WARNING: runtime reload is enabled, but shader src is currently not exists (reloading could not be possible) " + srcPath);
        }
        return this;
    }

    public void tick() {
        boolean changed = fileWatcher != null && fileWatcher.isJustChanged();
        if (changed) {
            System.out.println("Shader file changed: " + srcPath);
            GShader newShader;
            try {
                newShader = new GShader(srcDir, generator.shaderGroovy, generator.shaderType);
                newShader.inputSuffix = this.inputSuffix;
                newShader.outputSuffix = this.outputSuffix;
                newShader.translate();
            } catch (Exception e) {
                if (e.getMessage().contains("Error parsing")) {
                    System.out.println("Error parsing shader, not reloaded");
                } else {
                    e.printStackTrace();
                }
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
        //System.out.println("Sending: ");
        //System.out.println(generator.resultSrc);

        if (generator.shaderType.equals("vs")) shaderIndex = createVertexShader(stringToBuffer(generator.resultSrc));
        else if (generator.shaderType.equals("fs")) shaderIndex = createFragmentShader(stringToBuffer(generator.resultSrc));
        else if (generator.shaderType.equals("gs")) shaderIndex = createGeometryShader(stringToBuffer(generator.resultSrc));
        else BadException.die("unknown shader type: " + generator.shaderType);
//        printLogInfo(shaderIndex);
        Util.checkGLError();
    }

    public void removeShaderFromCard() {
        if (shaderIndex == -1) throw BadException.shouldNeverReachHere();
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
