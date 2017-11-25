package yk.senjin.shaders;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import yk.jcommon.utils.BadException;
import yk.senjin.AbstractState;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

/**
 * Created by: Yuri Kravchik Date: 2/11/2007 Time: 11:29:31
 */
public class ShaderHandler extends AbstractState {
    public static ShaderHandler currentShader;

    public ShaderHandler() {
    }


    public ShaderHandler(String v, String f) {
        createProgram(v, f);
    }



    private static ByteBuffer getProgramCode(final String filename) {
        final ClassLoader fileLoader = ShaderHandler.class.getClassLoader();
        InputStream fileInputStream = fileLoader.getResourceAsStream(filename);

        try {
            if (fileInputStream == null) fileInputStream = new FileInputStream(filename);
            return streamToByteBuffer(fileInputStream);
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
        ARBShaderObjects.glGetObjectParameterARB(
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

        Util.checkGLError();
    }

    public static int createFragmentShader(final ByteBuffer program) {
        final int fs = glCreateShader(GL20.GL_FRAGMENT_SHADER);
        GL20.glShaderSource(fs, program);
        GL20.glCompileShader(fs);
        printLogInfo(fs);
        return fs;
    }

    public static int createVertexShader(final ByteBuffer program) {
        final int vs = ARBShaderObjects.glCreateShaderObjectARB(ARBVertexShader.GL_VERTEX_SHADER_ARB);
        ARBShaderObjects.glShaderSourceARB(vs, program);
        ARBShaderObjects.glCompileShaderARB(vs);
        printLogInfo(vs);
        return vs;
    }

    public static int createGeometryShader(final ByteBuffer program) {
        int vs = glCreateShader(ARBGeometryShader4.GL_GEOMETRY_SHADER_ARB);
        if (vs == 0) BadException.die("0");
        glShaderSource(vs, program);
        glCompileShader(vs);
        printLogInfo(vs);
        return vs;
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

    public int program;
    public final List<UniformVariable> uniforms = new ArrayList<UniformVariable>();
    public final Map<String, VertexAttrib> vertexAttribs = new HashMap<String, VertexAttrib>();

    public void initVariables() {
        for (final UniformVariable u4f : uniforms) {
            u4f.initForProgram(program);
        }
        for (final VertexAttrib a : vertexAttribs.values()) {
            a.initForProgram(program);
        }
    }

    public void addVariables(final UniformVariable... variables) {
        for (final UniformVariable v : variables) {
            uniforms.add(v);
        }
    }

    public void addVertexAttrib(final VertexAttrib attrib) {
        vertexAttribs.put(attrib.getName(), attrib);
    }

    public void createProgram(String vss, String fss) {
        createProgram(new String[]{vss}, new String[]{fss});
    }


    public String geometryShaderString;
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
            final int shader = createGeometryShader(stringToBuffer(geometryShaderString));
            GL20.glAttachShader(program, shader);
            GL20.glDeleteShader(shader);
        }
        GL20.glLinkProgram(program);
        printLogInfo(program);

        initVariables();
    }

    public void createFromSrc(String vsrc, String fsrc) {
        Util.checkGLError();
        program = GL20.glCreateProgram();
        Util.checkGLError();

        if (geometryShaderString != null) {
            int gshader = createGeometryShader(stringToBuffer(geometryShaderString));
            GL20.glAttachShader(program, gshader);
            GL20.glDeleteShader(gshader);
        }


        int vshader = createVertexShader(stringToBuffer(vsrc));
        GL20.glAttachShader(program, vshader);
        GL20.glDeleteShader(vshader);

        int fshader = createFragmentShader(stringToBuffer(fsrc));
        GL20.glAttachShader(program, fshader);
        GL20.glDeleteShader(fshader);

        GL20.glLinkProgram(program);
        printLogInfo(program);

        initVariables();
    }

    public void createFromIndices(int vIndex, int fIndex) {
        //if (vIndex == -1) BadException.shouldNeverReachHere();
        //if (fIndex == -1) BadException.shouldNeverReachHere();
        program = GL20.glCreateProgram();
        Util.checkGLError();
//        printLogInfo(program);

        if (geometryShaderString != null) {
            int gshader = createGeometryShader(stringToBuffer(geometryShaderString));
            GL20.glAttachShader(program, gshader);
            GL20.glDeleteShader(gshader);
        }

        Util.checkGLError();
        if (vIndex != -1) {
            GL20.glAttachShader(program, vIndex);
            Util.checkGLError();
        }
        if (fIndex != -1) {
            GL20.glAttachShader(program, fIndex);
            Util.checkGLError();
        }
        GL20.glLinkProgram(program);
        printLogInfo(program);
        initVariables();
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
