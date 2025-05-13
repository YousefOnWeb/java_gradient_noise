package com.example.noise.gl;

import java.util.HashMap;
import java.util.Map;
import org.lwjgl.opengl.GL11; // For GL_TRUE/GL_FALSE if used explicitly

public class ShaderProgram {
    private VertexShader vertexShader;
    private FragmentShader fragmentShader;
    private int programId = 0;
    private Map<String, Integer> uniformLocations = new HashMap<>();
    
    private static long currentProcessingTag = 0;


    public ShaderProgram(VertexShader vertexShader, FragmentShader fragmentShader) {
        this.vertexShader = vertexShader;
        this.fragmentShader = fragmentShader;
    }
    public ShaderProgram() {}


    public void setShader(Shader shader) {
        if (shader instanceof VertexShader) {
            this.vertexShader = (VertexShader) shader;
        } else if (shader instanceof FragmentShader) {
            this.fragmentShader = (FragmentShader) shader;
        }
        if (programId != 0) {
            GLUtil.glDeleteProgram(programId);
            programId = 0;
            uniformLocations.clear();
        }
    }

    public void link() {
        if (programId != 0) return;

        if (vertexShader != null) vertexShader.compileFlat();
        if (fragmentShader != null) fragmentShader.compileFlat();

        programId = GLUtil.glCreateProgram();
        if (programId == 0) {
            throw new GLSLException("Failed to create shader program object");
        }

        if (vertexShader != null && vertexShader.isCompiled()) {
            GLUtil.glAttachShader(programId, vertexShader.getShaderId());
        }
        if (fragmentShader != null && fragmentShader.isCompiled()) {
            GLUtil.glAttachShader(programId, fragmentShader.getShaderId());
        }
        
        GLUtil.glLinkProgram(programId);

        if (GLUtil.glGetProgrami(programId, GLUtil.GL_LINK_STATUS) != GL11.GL_TRUE) {
            String log = GLUtil.glGetProgramInfoLog(programId);
            GLUtil.glDeleteProgram(programId);
            programId = 0;
            throw new GLSLException("Failed to link shader program", log);
        }
        uniformLocations.clear();
    }

    public int getProgramId() {
        if (programId == 0) {
            link();
        }
        return programId;
    }

    public void install() {
        GLUtil.glUseProgram(getProgramId());
    }

    public void uninstall() {
        GLUtil.glUseProgram(0);
    }

    public int getUniformLocation(String varName) {
        return uniformLocations.computeIfAbsent(varName, name -> {
            if (programId == 0) link();
            return GLUtil.glGetUniformLocation(programId, name);
        });
    }

    public void setUniform(String var, float x) { GLUtil.glUniform1f(getUniformLocation(var), x); }
    public void setUniform(String var, float x, float y) { GLUtil.glUniform2f(getUniformLocation(var), x, y); }
    public void setUniform(String var, float x, float y, float z) { GLUtil.glUniform3f(getUniformLocation(var), x, y, z); }
    public void setUniform(String var, float x, float y, float z, float w) { GLUtil.glUniform4f(getUniformLocation(var), x, y, z, w); }
    public void setUniform(String var, int x) { GLUtil.glUniform1i(getUniformLocation(var), x); }
    
    public void setTexture(String varName, int textureUnit, int textureTarget, int textureId) {
        GLUtil.glUniform1i(getUniformLocation(varName), textureUnit);
        GLUtil.glActiveTexture(GLUtil.GL_TEXTURE0 + textureUnit);
        GLUtil.glBindTexture(textureTarget, textureId);
    }


    public void destroy() {
        if (programId != 0) {
            GLUtil.glDeleteProgram(programId);
            programId = 0;
        }
        // Optional: if ShaderProgram owns the shaders
        // if (vertexShader != null) vertexShader.destroy();
        // if (fragmentShader != null) fragmentShader.destroy();
    }
}