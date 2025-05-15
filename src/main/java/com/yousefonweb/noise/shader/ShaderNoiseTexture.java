package com.yousefonweb.noise.shader;

import com.yousefonweb.noise.Noise;
import com.yousefonweb.noise.gl.GLUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import org.lwjgl.BufferUtils; // For allocateDirect ShortBuffer more easily

public class ShaderNoiseTexture {
    private int freq;
    private int width;
    private ShortBuffer textureData; // R, G channels for two noise values

    public ShaderNoiseTexture(int freq, int width) {
        this.freq = freq;
        this.width = width;

        if ((width & (width - 1)) != 0 && width != 0) {
            System.err.println("Warning: ShaderNoiseTexture width " + width + " is not a power of two. Some GL targets might prefer power-of-two textures.");
        }
        
        double scale = (double) freq / width;
        int texelCount = width * width * width;
        // Each texel has 2 short values (R, G)
        this.textureData = BufferUtils.createShortBuffer(texelCount * 2); // LWJGL BufferUtils for direct buffer

        for (int z = 0; z < width; z++) {
            for (int y = 0; y < width; y++) {
                for (int x = 0; x < width; x++) {
                    // Noise channel 1 (goes to Red channel of texture)
                    double n1_val = Noise.pnoise3(
                        x * scale, y * scale, z * scale,
                        freq, freq, freq, // repeatX, repeatY, repeatZ
                        0 // base for permutation for first channel
                    );
                    // Noise channel 2 (goes to Green channel of texture)
                    double n2_val = Noise.pnoise3(
                        x * scale, y * scale, z * scale,
                        freq, freq, freq, // repeatX, repeatY, repeatZ
                        freq + 1 // base for permutation for second channel
                    );

                    // Map [-1,1] noise to [0, 65535] for unsigned short
                    short r_channel = (short) ((n1_val + 1.0) * 32767.5);
                    short g_channel = (short) ((n2_val + 1.0) * 32767.5);
                    
                    textureData.put(r_channel);
                    textureData.put(g_channel);
                }
            }
        }
        this.textureData.flip(); // Prepare buffer for reading
    }

    public void loadToGL(int textureTarget) { // e.g., GLUtil.GL_TEXTURE_3D
        // Assumes a texture ID is already bound or generated and bound outside.
        GLUtil.glTexImage3D(textureTarget, 0, GLUtil.GL_INTERNAL_FORMAT_SHADER_NOISE_TEX,
                width, width, width, 0, GLUtil.GL_FORMAT_SHADER_NOISE_TEX,
                GLUtil.GL_TYPE_SHADER_NOISE_TEX, textureData);
    }

    public void configureSamplerParameters(int textureTarget) {
        GLUtil.glTexParameteri(textureTarget, GLUtil.GL_TEXTURE_WRAP_S, GLUtil.GL_REPEAT);
        GLUtil.glTexParameteri(textureTarget, GLUtil.GL_TEXTURE_WRAP_T, GLUtil.GL_REPEAT);
        GLUtil.glTexParameteri(textureTarget, GLUtil.GL_TEXTURE_WRAP_R, GLUtil.GL_REPEAT);
        GLUtil.glTexParameteri(textureTarget, GLUtil.GL_TEXTURE_MAG_FILTER, GLUtil.GL_LINEAR);
        GLUtil.glTexParameteri(textureTarget, GLUtil.GL_TEXTURE_MIN_FILTER, GLUtil.GL_LINEAR);
    }

    public void enableTextureUnitState(int textureTarget) { // Renamed to avoid confusion with glActiveTexture
        GLUtil.glEnable(textureTarget); // e.g. GL_TEXTURE_3D
    }
    
    public ShortBuffer getTextureData() {
        return textureData;
    }

    public int getWidth() {
        return width;
    }
}