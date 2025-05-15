package com.yousefonweb.noise.gl;

// GLUtil provides GL_VERTEX_SHADER directly from GL20 next import no longer needed
// import org.lwjgl.opengl.GL20; 

public class VertexShader extends Shader {
    public VertexShader(String name, String programSource) {
        super(name, programSource);
    }

    @Override
    public int getShaderType() {
        return GLUtil.GL_VERTEX_SHADER; // Which is GL20.GL_VERTEX_SHADER
    }
}