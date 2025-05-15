package com.yousefonweb.noise;

/**
 * Noise functions for procedural generation of content.
 * Contains implementations of Perlin improved noise (with
 * fBm capabilities) and Perlin simplex noise. Also contains helpers
 * for fast implementation in GLSL for execution in shaders.
 *
 * Based on Python package 'noise' (https://github.com/caseman/noise).
 */
public class Noise {

    public static final String VERSION = "1.0.0";

    // Default permutation table for all noise functions if not specified
    private static final PermutationTable defaultPermutationTable = new PermutationTable();
    // One can create specific PermutationTable instances and pass to noise
    // functions for varied results or specific seeds.

    // Simplex Noise
    public static double snoise2(double x, double y) {
        return SimplexNoise.snoise2(x, y, 1, 0.5, 2.0, defaultPermutationTable);
    }

    public static double snoise2(double x, double y, int octaves) {
        return SimplexNoise.snoise2(x, y, octaves, 0.5, 2.0, defaultPermutationTable);
    }
    // more overloads to be added if custom persistence, lacunarity, or perm_table is needed
    // at this level

    public static double snoise3(double x, double y, double z) {
        return SimplexNoise.snoise3(x, y, z, 1, 0.5, 2.0, defaultPermutationTable);
    }

    public static double snoise3(double x, double y, double z, int octaves) {
        return SimplexNoise.snoise3(x, y, z, octaves, 0.5, 2.0, defaultPermutationTable);
    }

    public static double snoise4(double x, double y, double z, double w) {
        return SimplexNoise.snoise4(x, y, z, w, 1, 0.5, 2.0, defaultPermutationTable);
    }

    public static double snoise4(double x, double y, double z, double w, int octaves) {
        return SimplexNoise.snoise4(x, y, z, w, octaves, 0.5, 2.0, defaultPermutationTable);
    }

    // Perlin Noise
    public static double pnoise1(double x) {
        return PerlinNoise.pnoise1(x, 1, 0.5, 2.0, 0, defaultPermutationTable);
    }

    public static double pnoise1(double x, int octaves) {
        return PerlinNoise.pnoise1(x, octaves, 0.5, 2.0, 0, defaultPermutationTable);
    }

    public static double pnoise1(double x, int octaves, int base) {
        return PerlinNoise.pnoise1(x, octaves, 0.5, 2.0, base, defaultPermutationTable);
    }

    public static double pnoise1(double x, int octaves, double persistence, double lacunarity, int base) {
        return PerlinNoise.pnoise1(x, octaves, persistence, lacunarity, base, defaultPermutationTable);
    }

    public static double pnoise2(double x, double y) {
        return PerlinNoise.pnoise2(x, y, 1, 0.5, 2.0, 0, defaultPermutationTable);
    }

    public static double pnoise2(double x, double y, int octaves) {
        return PerlinNoise.pnoise2(x, y, octaves, 0.5, 2.0, 0, defaultPermutationTable);
    }

    public static double pnoise2(double x, double y, int octaves, int base) {
        return PerlinNoise.pnoise2(x, y, octaves, 0.5, 2.0, base, defaultPermutationTable);
    }

    public static double pnoise2(double x, double y, int octaves, double persistence, double lacunarity, int base) {
        return PerlinNoise.pnoise2(x, y, octaves, persistence, lacunarity, base, defaultPermutationTable);
    }

    public static double pnoise3(double x, double y, double z) {
        return PerlinNoise.pnoise3(x, y, z, 1, 0.5, 2.0, 0, defaultPermutationTable, 0, 0, 0);
    }

    public static double pnoise3(double x, double y, double z, int octaves) {
        return PerlinNoise.pnoise3(x, y, z, octaves, 0.5, 2.0, 0, defaultPermutationTable, 0, 0, 0);
    }

    public static double pnoise3(double x, double y, double z, int octaves, int base) {
        return PerlinNoise.pnoise3(x, y, z, octaves, 0.5, 2.0, base, defaultPermutationTable, 0, 0, 0);
    }

    // Tiling version for ShaderNoiseTexture
    public static double pnoise3(double x, double y, double z, int repeatX, int repeatY, int repeatZ, int base) {
        return PerlinNoise.pnoise3(x, y, z, 1, 0.5, 2.0, base, defaultPermutationTable, repeatX, repeatY, repeatZ);
    }

    public static double pnoise3(double x, double y, double z, int octaves, double persistence, double lacunarity,
            int base) {
        return PerlinNoise.pnoise3(x, y, z, octaves, persistence, lacunarity, base, defaultPermutationTable, 0, 0, 0);
    }

    // Entry point to get a new permutation table for custom seeding, if needed.
    public static PermutationTable createPermutationTable(int period) {
        return new PermutationTable(period);
    }

    public static PermutationTable createPermutationTable(int[] customTable) {
        return new PermutationTable(customTable);
    }
}