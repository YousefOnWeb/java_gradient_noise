package com.example.noise.gl;

// GLUtil now provides GL_FRAGMENT_SHADER directly from GL20
// import org.lwjgl.opengl.GL20;

public class FragmentShader extends Shader {
    public FragmentShader(String name, String programSource) {
        super(name, programSource);
    }

    @Override
    public int getShaderType() {
        return GLUtil.GL_FRAGMENT_SHADER; // Which is GL20.GL_FRAGMENT_SHADER
    }
}