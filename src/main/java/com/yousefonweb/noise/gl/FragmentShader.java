package com.yousefonweb.noise.gl;

// GLUtil provides GL_FRAGMENT_SHADER directly from GL20 so following import no longer needed
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