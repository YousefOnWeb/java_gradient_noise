package com.example.noise.shader;

public class ShaderNoiseResources {
	public static final String SHADER_NOISE_GLSL = """
			/*
			 * GLSL Shader functions for fast fake Perlin 3D noise
			 *
			 * The required shader_noise_tex texture can be generated using the
			 * ShaderNoiseTexture class. It is a toroidal tiling 3D texture with each texel
			 * containing two 16-bit noise source channels (in R and G components).
			 * The shader permutes the source texture values by combining the channels
			 * such that the noise repeats at a much larger interval than the input texture.
			 */

			uniform sampler3D shader_noise_tex;
			const float twopi = 3.1415926 * 2.0;

			/* Simple perlin noise work-alike */
			float
			pnoise(vec3 position)
			{
				vec4 tex_val_hi = texture(shader_noise_tex, position.xyz);
			    // hi.r is noise1, hi.g is noise2 (formerly hi.a)
				vec2 hi = 2.0 * tex_val_hi.rg - 1.0;

			    vec4 tex_val_lo = texture(shader_noise_tex, position.xyz / 9.0);
			    // lo.r is noise1_low_freq, lo.g is noise2_low_freq
				vec2 lo = 2.0 * tex_val_lo.rg - 1.0;

			    // Use hi.x (formerly hi.r) for first channel, hi.y (formerly hi.a) for second channel
				return hi.x * cos(twopi * lo.x) + hi.y * sin(twopi * lo.x);
			}

			/* Multi-octave fractal brownian motion perlin noise */
			float
			fbmnoise(vec3 position, int octaves)
			{
				float m = 1.0;
				vec3 p = position;
				vec2 hi_sum = vec2(0.0); // Sum for two channels

				/* XXX Loops may not work correctly on all video cards if 'octaves' is not a compile-time const */
				for (int x = 0; x < octaves; x++) {
			        vec4 tex_val = texture(shader_noise_tex, p.xyz);
					hi_sum += (2.0 * tex_val.rg - 1.0) * m;
					p *= 2.0;
					m *= 0.5;
				}
				vec4 tex_val_lo = texture(shader_noise_tex, position.xyz / 9.0);
			    vec2 lo = 2.0 * tex_val_lo.rg - 1.0;
				return hi_sum.x * cos(twopi * lo.x) + hi_sum.y * sin(twopi * lo.x);
			}

			/* Multi-octave turbulent noise */
			float
			fbmturbulence(vec3 position, int octaves)
			{
				float m = 1.0;
				vec3 p = position;
				vec2 hi_sum_abs = vec2(0.0); // Sum of absolute values for two channels

				/* XXX Loops may not work correctly on all video cards if 'octaves' is not a compile-time const */
				for (int x = 0; x < octaves; x++) {
			        vec4 tex_val = texture(shader_noise_tex, p.xyz);
					hi_sum_abs += abs(2.0 * tex_val.rg - 1.0) * m;
					p *= 2.0;
					m *= 0.5;
				}
				vec4 tex_val_lo = texture(shader_noise_tex, position.xyz / 9.0);
			    // For mix factor, use one of the low-frequency channels, e.g., lo.r
				return 2.0 * mix(hi_sum_abs.x, hi_sum_abs.y, cos(twopi * tex_val_lo.r) * 0.5 + 0.5) - 1.0;
			}
			""";
}