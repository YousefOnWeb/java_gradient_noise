package com.yousefonweb.noise.gl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.lwjgl.opengl.GL11; // For GL_TRUE/GL_FALSE if used explicitly

public abstract class Shader {
    protected String name;
    protected String programSource; // single source string for this shader
    protected int shaderId = 0;
    protected boolean compiling = false;
    protected List<Shader> dependencies = new ArrayList<>();

    private static long currentTag = 0;
    private long instanceTag = -1;


    public Shader(String name, String programSource) {
        this.name = name;
        this.programSource = programSource;
    }

    public abstract int getShaderType(); // e.g., GLUtil.GL_VERTEX_SHADER

    protected List<String> getFullSourceList() {
        if (instanceTag == currentTag) return new ArrayList<>();
        instanceTag = currentTag;

        List<String> sources = new ArrayList<>();
        for (Shader dep : dependencies) {
            sources.addAll(dep.getFullSourceList());
        }
        sources.add(this.programSource);
        return sources;
    }
    
    protected String getCombinedSource() {
        currentTag++; 
        List<String> allSourcesList = getFullSourceList();
        return allSourcesList.stream().collect(Collectors.joining("\n"));
    }


    public void compile() {
        if (isCompiled()) return;
        for (Shader dep : dependencies) {
            dep.compile();
        }
        compileFlatLogic();
    }

    // less used that compileFlat is preferred
    protected void compileSelf() { 
        if (shaderId != 0 || compiling) return;
        compiling = true;

        shaderId = GLUtil.glCreateShader(getShaderType());
        if (shaderId == 0) {
            compiling = false;
            throw new GLSLException("Failed to create shader object for " + name);
        }

        GLUtil.glShaderSource(shaderId, this.programSource);
        GLUtil.glCompileShader(shaderId);
        compiling = false;

        if (GLUtil.glGetShaderi(shaderId, GLUtil.GL_COMPILE_STATUS) != GL11.GL_TRUE) {
            String log = GLUtil.glGetShaderInfoLog(shaderId);
            GLUtil.glDeleteShader(shaderId);
            shaderId = 0;
            throw new GLSLException("Failed to compile shader " + name, log);
        }
    }
    
    public void compileFlat() {
        compileFlatLogic();
    }

    private void compileFlatLogic() {
        if (isCompiled()) return;

        shaderId = GLUtil.glCreateShader(getShaderType());
        if (shaderId == 0) {
            throw new GLSLException("Failed to create shader object for " + name);
        }

        String combinedSource = getCombinedSource();
        GLUtil.glShaderSource(shaderId, combinedSource);
        GLUtil.glCompileShader(shaderId);

        if (GLUtil.glGetShaderi(shaderId, GLUtil.GL_COMPILE_STATUS) != GL11.GL_TRUE) {
            String log = GLUtil.glGetShaderInfoLog(shaderId);
            GLUtil.glDeleteShader(shaderId);
            shaderId = 0;
            throw new GLSLException("Failed to compile shader (flat) " + name, log);
        }
    }

    protected void attachToRecursive(int programHandle, long processingTag) {
        if (this.instanceTag == processingTag) return;
        this.instanceTag = processingTag;

        for (Shader dep : dependencies) {
            dep.attachToRecursive(programHandle, processingTag);
        }
        if (isCompiled()) {
            GLUtil.glAttachShader(programHandle, shaderId);
        }
    }


    public Shader addDependency(Shader shader) {
        dependencies.add(shader);
        return this;
    }

    public void destroy() {
        if (shaderId != 0) {
            GLUtil.glDeleteShader(shaderId);
            shaderId = 0;
        }
    }

    public boolean isCompiled() {
        return shaderId != 0;
    }
    
    public int getShaderId() {
        return shaderId;
    }
}