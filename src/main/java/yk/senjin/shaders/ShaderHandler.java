package yk.senjin.shaders;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;
import yk.jcommon.utils.BadException;
import yk.jcommon.utils.IO;
import yk.senjin.AbstractState;
import yk.senjin.shaders.uniforms.UniformVariable;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.*;

import static org.lwjgl.opengl.GL20.glCreateShader;
import static yk.jcommon.utils.BadException.shouldNeverReachHere;

/**
 * Created by: Yuri Kravchik Date: 2/11/2007 Time: 11:29:31
 */
public class ShaderHandler extends AbstractState {
    public static ShaderHandler currentShader;

    private boolean isLinked;
    public int program = -1;
    public final List<UniformVariable> uniforms = new ArrayList<>();
    public final Map<String, VertexAttrib> vertexAttribs = new HashMap<>();
    public String geometryShaderString;

    public ShaderHandler() {
    }


    public ShaderHandler(String v, String f) {
        createProgram(v, f);
    }



    private static String getProgramCode(final String filename) {
        final ClassLoader fileLoader = ShaderHandler.class.getClassLoader();
        InputStream fileInputStream = fileLoader.getResourceAsStream(filename);

        try {
            if (fileInputStream == null) fileInputStream = new FileInputStream(filename);
            return IO.streamToString(fileInputStream);
        } catch (final Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private static ByteBuffer streamToByteBuffer(InputStream fileInputStream) {
        try {
        final DataInputStream dataStream = new DataInputStream(fileInputStream);
        byte[] shaderCode = new byte[fileInputStream.available()];
        dataStream.readFully(shaderCode);
        fileInputStream.close();
        dataStream.close();
        final ByteBuffer shaderPro = BufferUtils.createByteBuffer(shaderCode.length);

        shaderPro.put(shaderCode);
        shaderPro.flip();
        return shaderPro;
        } catch (final Exception e) {
            System.out.println(e.getMessage());
            throw BadException.die(e);
        }
    }

    public static void printLogInfo(final int obj) {
        final IntBuffer iVal = BufferUtils.createIntBuffer(1);
        ARBShaderObjects.glGetObjectParameterivARB(
                obj,
                ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB,
                iVal);

        final int length = iVal.get();
        System.out.println("Info log length:" + length);
        String all = "";
        if (length > 0) {
            // We have some info we need to output.
            final ByteBuffer infoLog = BufferUtils.createByteBuffer(length);
            iVal.flip();
            ARBShaderObjects.glGetInfoLogARB(obj, iVal, infoLog);
            final byte[] infoBytes = new byte[length];
            infoLog.get(infoBytes);
            final String out = new String(infoBytes);

            System.out.println("Info log:\n" + out);
            all += "\n" + out;
        }
        if (all.contains(": error")) BadException.die("error on shader loading");

    }

    private static int createShader(String src, int shaderType) {
        final int shaderIndex = glCreateShader(shaderType);
        GL20.glShaderSource(shaderIndex, src);
        GL20.glCompileShader(shaderIndex);
        printLogInfo(shaderIndex);
        return shaderIndex;
    }

    public static int createFragmentShader(String src) {
        return createShader(src, GL20.GL_FRAGMENT_SHADER);
    }

    public static int createVertexShader(String src) {
        return createShader(src, GL20.GL_VERTEX_SHADER);
    }

    public static int createGeometryShader(String src) {
        return createShader(src, GL32.GL_GEOMETRY_SHADER);
    }

    public static ByteBuffer getBufferedString(final String s) {
        final ByteBuffer bb = BufferUtils.createByteBuffer(s.length() + 1);
        bb.put(s.getBytes());
        bb.put((byte) 0);
        bb.rewind();
        return bb;
    }

    public static int getUniformLocation(final int program, final ByteBuffer attributeName) {
        return GL20.glGetUniformLocation(program, attributeName);
    }

    public void initVariables() {
        for (final UniformVariable u4f : uniforms) {
            u4f.initForProgram(program);
        }
        for (final VertexAttrib a : vertexAttribs.values()) {
            a.initForProgram(program);
        }
    }

    public void addVariables(final UniformVariable... variables) {
        Collections.addAll(uniforms, variables);
    }

    public void addVertexAttrib(final VertexAttrib attrib) {
        vertexAttribs.put(attrib.getName(), attrib);
    }

    public void createProgram(String vss, String fss) {
        createProgram(new String[]{vss}, new String[]{fss});
    }


    public void createProgram(final String[] vss, final String[] fss) {
        program = GL20.glCreateProgram();
        for (final String s : vss) {
            final int shader = createVertexShader(getProgramCode(s));
            GL20.glAttachShader(program, shader);
            GL20.glDeleteShader(shader);
        }
        for (final String s : fss) {
            final int shader = createFragmentShader(getProgramCode(s));
            GL20.glAttachShader(program, shader);
            GL20.glDeleteShader(shader);
        }
        if (geometryShaderString != null) {
            final int shader = createGeometryShader(geometryShaderString);
            GL20.glAttachShader(program, shader);
            GL20.glDeleteShader(shader);
        }
        GL20.glLinkProgram(program);
        printLogInfo(program);

        initVariables();
    }

    public void createFromSrc(String vsrc, String fsrc) {
        program = GL20.glCreateProgram();

        if (geometryShaderString != null) {
            int gshader = createGeometryShader(geometryShaderString);
            GL20.glAttachShader(program, gshader);
            GL20.glDeleteShader(gshader);
        }


        int vshader = createVertexShader(vsrc);
        GL20.glAttachShader(program, vshader);
        GL20.glDeleteShader(vshader);

        int fshader = createFragmentShader(fsrc);
        GL20.glAttachShader(program, fshader);
        GL20.glDeleteShader(fshader);

        GL20.glLinkProgram(program);
        printLogInfo(program);

        initVariables();
    }

    public void createProgram() {
        if (isLinked) throw shouldNeverReachHere();
        if (program != -1) throw shouldNeverReachHere();
        program = GL20.glCreateProgram();
    }

    public void attachShader(int index) {
        if (isLinked) throw shouldNeverReachHere();
        if (program == -1) throw shouldNeverReachHere();
        GL20.glAttachShader(program, index);
    }

    public void linkProgram() {
        if (isLinked) throw shouldNeverReachHere();
        if (program == -1) throw shouldNeverReachHere();
        GL20.glLinkProgram(program);
        printLogInfo(program);
        initVariables();
        isLinked = true;
    }

    public static ByteBuffer stringToBuffer(String vsrc) {
        byte[] progBytes = vsrc.getBytes();
        final ByteBuffer progBuf = BufferUtils.createByteBuffer(progBytes.length);
        progBuf.put(progBytes);
        progBuf.flip();
        return progBuf;
    }

    @Override
    public void disable() {
        GL20.glUseProgram(0);
        currentShader = null;
    }

    @Override
    public void enable() {
        GL20.glUseProgram(program);
        for (int i = 0; i < uniforms.size(); i++) {
            uniforms.get(i).plug();
        }
        currentShader = this;
    }

    public VertexAttrib getVertexAttrib(final String name) {
        return vertexAttribs.get(name);
    }

    public static void setShader(ShaderHandler handler) {
        if (currentShader != handler) {
            if (currentShader != null) currentShader.disable();
            currentShader = handler;
            if (currentShader != null) currentShader.enable();
        }
    }

    public void deleteProgram() {
        if (program != -1) GL20.glDeleteProgram(program);
        else System.out.println("WARNING: already removed at " + yk.jcommon.utils.Util.stacktraceToString(new Exception()));
        program = -1;
    }
}
