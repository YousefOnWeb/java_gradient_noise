package com.example.noise.gl;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30; // For GL_RG16

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class GLUtil {

    // Selected GL Constants (LWJGL provides them directly)
    public static final int GL_TEXTURE_3D = GL12.GL_TEXTURE_3D;
    // For ShaderNoiseTexture: Use GL_RG16 for 2-channel 16-bit data
    public static final int GL_INTERNAL_FORMAT_SHADER_NOISE_TEX = GL30.GL_RG16; // Internal format
    public static final int GL_FORMAT_SHADER_NOISE_TEX = GL30.GL_RG;          // Format of pixel data
    public static final int GL_TYPE_SHADER_NOISE_TEX = GL11.GL_UNSIGNED_SHORT; // Type of pixel data

    public static final int GL_REPEAT = GL11.GL_REPEAT;
    public static final int GL_TEXTURE_WRAP_S = GL11.GL_TEXTURE_WRAP_S;
    public static final int GL_TEXTURE_WRAP_T = GL11.GL_TEXTURE_WRAP_T;
    public static final int GL_TEXTURE_WRAP_R = GL12.GL_TEXTURE_WRAP_R;
    public static final int GL_TEXTURE_MAG_FILTER = GL11.GL_TEXTURE_MAG_FILTER;
    public static final int GL_TEXTURE_MIN_FILTER = GL11.GL_TEXTURE_MIN_FILTER;
    public static final int GL_LINEAR = GL11.GL_LINEAR;

    public static final int GL_VERTEX_SHADER = GL20.GL_VERTEX_SHADER;
    public static final int GL_FRAGMENT_SHADER = GL20.GL_FRAGMENT_SHADER;
    public static final int GL_COMPILE_STATUS = GL20.GL_COMPILE_STATUS;
    public static final int GL_LINK_STATUS = GL20.GL_LINK_STATUS;
    public static final int GL_INFO_LOG_LENGTH = GL20.GL_INFO_LOG_LENGTH;

    public static final int GL_TRUE = GL11.GL_TRUE;
    public static final int GL_TEXTURE0 = GL13.GL_TEXTURE0;


    public static void glEnable(int cap) { GL11.glEnable(cap); }
    public static void glTexParameteri(int target, int pname, int param) { GL11.glTexParameteri(target, pname, param); }
    
    public static void glTexImage3D(int target, int level, int internalFormat, int width, int height, int depth, int border, int format, int type, ShortBuffer data) {
        GL12.glTexImage3D(target, level, internalFormat, width, height, depth, border, format, type, data);
    }
     public static void glTexImage3D(int target, int level, int internalFormat, int width, int height, int depth, int border, int format, int type, ByteBuffer data) {
        GL12.glTexImage3D(target, level, internalFormat, width, height, depth, border, format, type, data);
    }

    public static int glCreateShader(int shaderType) { return GL20.glCreateShader(shaderType); }
    public static void glShaderSource(int shader, CharSequence string) { GL20.glShaderSource(shader, string); }
    public static void glCompileShader(int shader) { GL20.glCompileShader(shader); }
    
    public static int glGetShaderi(int shader, int pname) {
        return GL20.glGetShaderi(shader, pname);
    }

    public static String glGetShaderInfoLog(int shader) {
        int maxLength = glGetShaderi(shader, GL_INFO_LOG_LENGTH);
        return GL20.glGetShaderInfoLog(shader, maxLength);
    }

    public static void glDeleteShader(int shader) { GL20.glDeleteShader(shader); }
    
    public static int glCreateProgram() { return GL20.glCreateProgram(); }
    public static void glAttachShader(int program, int shader) { GL20.glAttachShader(program, shader); }
    public static void glLinkProgram(int program) { GL20.glLinkProgram(program); }

    public static int glGetProgrami(int program, int pname) {
        return GL20.glGetProgrami(program, pname);
    }

    public static String glGetProgramInfoLog(int program) {
        int maxLength = glGetProgrami(program, GL_INFO_LOG_LENGTH);
        return GL20.glGetProgramInfoLog(program, maxLength);
    }

    public static void glUseProgram(int program) { GL20.glUseProgram(program); }
    
    public static int glGetUniformLocation(int program, CharSequence name) { return GL20.glGetUniformLocation(program, name); }
    
    public static void glUniform1f(int location, float v0) { GL20.glUniform1f(location, v0); }
    public static void glUniform2f(int location, float v0, float v1) { GL20.glUniform2f(location, v0, v1); }
    public static void glUniform3f(int location, float v0, float v1, float v2) { GL20.glUniform3f(location, v0, v1, v2); }
    public static void glUniform4f(int location, float v0, float v1, float v2, float v3) { GL20.glUniform4f(location, v0, v1, v2, v3); }
    public static void glUniform1i(int location, int v0) { GL20.glUniform1i(location, v0); }
    
    public static void glActiveTexture(int texture) { GL13.glActiveTexture(texture); }
    public static void glBindTexture(int target, int textureId) { GL11.glBindTexture(target, textureId); }

    public static void glDeleteProgram(int program) { GL20.glDeleteProgram(program); }
}