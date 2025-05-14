package com.example.noise;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PerlinNoiseTest {

    private static final double DELTA = 1e-9; // For float comparisons
    private static final int RANGE_CHECK_ITERATIONS_SMALL = 50; // For octave tests
    private static final int RANGE_CHECK_ITERATIONS_LARGE = 100; // For single octave tests
    private static final int OCTAVE_COUNT_MAX = 5; // Max octaves to test to keep it reasonably fast

    // --- 1D Perlin Noise Tests ---
    @Test
    void testPerlin1DRange() {
        for (int i = -RANGE_CHECK_ITERATIONS_LARGE; i <= RANGE_CHECK_ITERATIONS_LARGE; i++) {
            double x = i * 0.49;
            double n = Noise.pnoise1(x);
            assertTrue(n >= -1.0 - DELTA && n <= 1.0 + DELTA, "Value out of range [-1,1]: " + n + " for x=" + x);
        }
    }

    @Test
    void testPerlin1DOctavesRange() {
        for (int o = 1; o <= OCTAVE_COUNT_MAX; o++) {
            for (int i = -RANGE_CHECK_ITERATIONS_SMALL; i <= RANGE_CHECK_ITERATIONS_SMALL; i++) {
                double x = i * 0.49;
                double n = Noise.pnoise1(x, o);
                assertTrue(n >= -1.0 - DELTA && n <= 1.0 + DELTA,
                        "Value out of range [-1,1]: " + n + " for x=" + x + ", octaves=" + o);
            }
        }
    }

    @Test
    void testPerlin1DBase() {
        double x = 0.5;
        // Using the Noise facade which defaults octaves if not specified
        double n_base0_default = Noise.pnoise1(x); // base defaults to 0
        double n_base0_explicit = Noise.pnoise1(x, 1, 0); // 1 octave, base 0
        double n_base5 = Noise.pnoise1(x, 1, 5);
        double n_base1 = Noise.pnoise1(x, 1, 1);

        assertEquals(n_base0_default, n_base0_explicit, DELTA,
                "Default base (1-octave) should be equivalent to base=0 (1-octave)");
        assertNotEquals(n_base0_explicit, n_base5, DELTA, "Value for base 0 and base 5 should differ");
        assertNotEquals(n_base5, n_base1, DELTA, "Value for base 5 and base 1 should differ");
    }

    // --- 2D Perlin Noise Tests ---
    @Test
    void testPerlin2DRange() {
        for (int i = -RANGE_CHECK_ITERATIONS_LARGE; i <= RANGE_CHECK_ITERATIONS_LARGE; i++) {
            double x = i * 0.49;
            double y = -i * 0.67;
            double n = Noise.pnoise2(x, y);
            assertTrue(n >= -1.0 - DELTA && n <= 1.0 + DELTA,
                    "Value out of range [-1,1]: " + n + " for (x,y)=(" + x + "," + y + ")");
        }
    }

    @Test
    void testPerlin2DOctavesRange() {
        for (int o = 1; o <= OCTAVE_COUNT_MAX; o++) {
            for (int i = -RANGE_CHECK_ITERATIONS_SMALL; i <= RANGE_CHECK_ITERATIONS_SMALL; i++) {
                double x = -i * 0.49;
                double y = i * 0.67;
                double n = Noise.pnoise2(x, y, o);
                assertTrue(n >= -1.0 - DELTA && n <= 1.0 + DELTA,
                        "Value out of range [-1,1]: " + n + " for (x,y)=(" + x + "," + y + "), octaves=" + o);
            }
        }
    }

    @Test
    void testPerlin2DBase() {
        double x = 0.73, y = 0.27;
        double n_base0_default = Noise.pnoise2(x, y); // base defaults to 0
        double n_base0_explicit = Noise.pnoise2(x, y, 1, 0); // 1 octave, base 0
        double n_base5 = Noise.pnoise2(x, y, 1, 5);
        double n_base1 = Noise.pnoise2(x, y, 1, 1);

        assertEquals(n_base0_default, n_base0_explicit, DELTA,
                "Default base (1-octave) should be equivalent to base=0 (1-octave)");
        assertNotEquals(n_base0_explicit, n_base5, DELTA, "Value for base 0 and base 5 should differ");
        assertNotEquals(n_base5, n_base1, DELTA, "Value for base 5 and base 1 should differ");
    }

    // --- 3D Perlin Noise Tests ---
    @Test
    void testPerlin3DRange() {
        for (int i = -RANGE_CHECK_ITERATIONS_LARGE; i <= RANGE_CHECK_ITERATIONS_LARGE; i++) {
            double x = -i * 0.49;
            double y = i * 0.67;
            double z = -i * 0.727;
            double n = Noise.pnoise3(x, y, z);
            assertTrue(n >= -1.0 - DELTA && n <= 1.0 + DELTA,
                    "Value out of range [-1,1]: " + n + " for (x,y,z)=(" + x + "," + y + "," + z + ")");
        }
    }

    @Test
    void testPerlin3DOctavesRange() {
        for (int o = 1; o <= OCTAVE_COUNT_MAX; o++) {
            for (int i = -RANGE_CHECK_ITERATIONS_SMALL; i <= RANGE_CHECK_ITERATIONS_SMALL; i++) {
                double x = i * 0.22;
                double y = -i * 0.77;
                double z = -i * 0.17;
                double n = Noise.pnoise3(x, y, z, o);
                assertTrue(n >= -1.0 - DELTA && n <= 1.0 + DELTA, "Value out of range [-1,1]: " + n + " for (x,y,z)=("
                        + x + "," + y + "," + z + "), octaves=" + o);
            }
        }
    }

    @Test
    void testPerlin3DBase() {
        double x = 0.1, y = 0.7, z = 0.33;
        double n_base0_default = Noise.pnoise3(x, y, z); // base defaults to 0
        double n_base0_explicit = Noise.pnoise3(x, y, z, 1, 0); // 1 octave, base 0
        double n_base5 = Noise.pnoise3(x, y, z, 1, 5);
        double n_base1 = Noise.pnoise3(x, y, z, 1, 1);

        assertEquals(n_base0_default, n_base0_explicit, DELTA,
                "Default base (1-octave) should be equivalent to base=0 (1-octave)");
        assertNotEquals(n_base0_explicit, n_base5, DELTA, "Value for base 0 and base 5 should differ");
        assertNotEquals(n_base5, n_base1, DELTA, "Value for base 5 and base 1 should differ");
    }

    @Test
    void testPerlin3DTiling() {
        // Test that pnoise3 with repeat parameters tiles correctly
        // Uses a specific PermutationTable instance to ensure consistency for the
        // tiling check
        int repeat = 8;
        double x = 0.1, y = 0.2, z = 0.3;

        // Test with a randomly initialized (but shared for the test) permutation table
        PermutationTable randomPermTable = Noise.createPermutationTable(256); // period 256
        double n1_random = PerlinNoise.pnoise3(x, y, z, 1, 0.5, 2.0, 0, randomPermTable, repeat, repeat, repeat);
        double n2_random_tiledX = PerlinNoise.pnoise3(x + repeat, y, z, 1, 0.5, 2.0, 0, randomPermTable, repeat, repeat,
                repeat);
        double n3_random_tiledY = PerlinNoise.pnoise3(x, y + repeat, z, 1, 0.5, 2.0, 0, randomPermTable, repeat, repeat,
                repeat);
        double n4_random_tiledZ = PerlinNoise.pnoise3(x, y, z + repeat, 1, 0.5, 2.0, 0, randomPermTable, repeat, repeat,
                repeat);

        assertEquals(n1_random, n2_random_tiledX, 1e-5, "Tiling failed for X axis (random perm table)");
        assertEquals(n1_random, n3_random_tiledY, 1e-5, "Tiling failed for Y axis (random perm table)");
        assertEquals(n1_random, n4_random_tiledZ, 1e-5, "Tiling failed for Z axis (random perm table)");

        // Test with the default permutation table (which is fixed)
        PermutationTable defaultPermTable = new PermutationTable(); // Uses the static default array
        double n1_default = PerlinNoise.pnoise3(x, y, z, 1, 0.5, 2.0, 0, defaultPermTable, repeat, repeat, repeat);
        double n2_default_tiledX = PerlinNoise.pnoise3(x + repeat, y, z, 1, 0.5, 2.0, 0, defaultPermTable, repeat,
                repeat, repeat);
        double n3_default_tiledY = PerlinNoise.pnoise3(x, y + repeat, z, 1, 0.5, 2.0, 0, defaultPermTable, repeat,
                repeat, repeat);
        double n4_default_tiledZ = PerlinNoise.pnoise3(x, y, z + repeat, 1, 0.5, 2.0, 0, defaultPermTable, repeat,
                repeat, repeat);

        assertEquals(n1_default, n2_default_tiledX, 1e-5, "Tiling failed for X axis (default perm table)");
        assertEquals(n1_default, n3_default_tiledY, 1e-5, "Tiling failed for Y axis (default perm table)");
        assertEquals(n1_default, n4_default_tiledZ, 1e-5, "Tiling failed for Z axis (default perm table)");
    }
}