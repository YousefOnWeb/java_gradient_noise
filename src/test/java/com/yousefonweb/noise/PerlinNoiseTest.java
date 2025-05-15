package com.yousefonweb.noise;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Random; // For testing randomize effects

public class PerlinNoiseTest {

    private static final double DELTA = 1e-9; // For float comparisons
    private static final double GRID_POINT_DELTA = 1e-7; // For checking values at grid points (should be close to 0)
    private static final int RANGE_CHECK_ITERATIONS_SMALL = 50; // For octave tests
    private static final int RANGE_CHECK_ITERATIONS_LARGE = 100; // For single octave tests
    private static final int OCTAVE_COUNT_MAX = 5; // Max octaves to test to keep it reasonably fast

    // --- Helper Function Tests ---
    @Test
    void testLerp() {
        assertEquals(10.0, NoiseConstants.lerp(0.0, 10.0, 20.0), DELTA, "lerp(0, a, b) should be a");
        assertEquals(20.0, NoiseConstants.lerp(1.0, 10.0, 20.0), DELTA, "lerp(1, a, b) should be b");
        assertEquals(15.0, NoiseConstants.lerp(0.5, 10.0, 20.0), DELTA, "lerp(0.5, a, b) should be (a+b)/2");
        assertEquals(5.0, NoiseConstants.lerp(-0.5, 10.0, 20.0), DELTA, "lerp extrapolates for t < 0");
        assertEquals(25.0, NoiseConstants.lerp(1.5, 10.0, 20.0), DELTA, "lerp extrapolates for t > 1");
        assertEquals(10.0, NoiseConstants.lerp(0.0, 10.0, 10.0), DELTA, "lerp with a==b");
    }

    @Test
    void testFadeFunction() {
        // The fade function x^3*(x*(x*6−15)+10)
        // This is NoiseConstants.fade()
        assertEquals(0.0, NoiseConstants.fade(0.0), DELTA, "fade(0) should be 0");
        assertEquals(1.0, NoiseConstants.fade(1.0), DELTA, "fade(1) should be 1");

        double t = 0.5;
        double expectedFadeAt05 = t * t * t * (t * (t * 6 - 15) + 10);
        assertEquals(expectedFadeAt05, NoiseConstants.fade(t), DELTA, "fade(0.5) calculation mismatch");
        assertEquals(0.5, NoiseConstants.fade(0.5), DELTA, "fade(0.5) should indeed be 0.5");

        // Derivatives at 0 and 1 should be 0 (characteristic of Perlin's improved noise fade)
        // Approximate derivative: (fade(epsilon) - fade(0)) / epsilon
        double epsilon = 1e-6;
        assertTrue((NoiseConstants.fade(epsilon) - NoiseConstants.fade(0.0)) / epsilon < 1e-5,
                "Fade derivative at 0 should be close to 0");
        assertTrue((NoiseConstants.fade(1.0) - NoiseConstants.fade(1.0 - epsilon)) / epsilon < 1e-5,
                "Fade derivative at 1 should be close to 0");
    }

    // --- Indirect Gradient Logic Tests ---
    @Test
    void testPerlinNoiseAtIntegerGridPointsShouldBeZero() {
        // For 1-octave Perlin noise, the value at integer grid points should be 0.
        // This indirectly tests that gradients and fade function are working correctly
        // together.
        PermutationTable pt = new PermutationTable(); // Default permutation table

        // Test pnoise1
        for (int i = -5; i <= 5; i++) {
            double n1 = PerlinNoise.pnoise1((double) i, 1, 0.5, 2.0, 0, pt); // 1 octave
            assertEquals(0.0, n1, GRID_POINT_DELTA, "pnoise1 at integer grid point " + i + " should be 0");
        }

        // Test pnoise2
        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                double n2 = PerlinNoise.pnoise2((double) i, (double) j, 1, 0.5, 2.0, 0, pt); // 1 octave
                assertEquals(0.0, n2, GRID_POINT_DELTA,
                        "pnoise2 at integer grid point (" + i + "," + j + ") should be 0");
            }
        }

        // Test pnoise3
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                for (int k = -1; k <= 1; k++) {
                    double n3 = PerlinNoise.pnoise3((double) i, (double) j, (double) k, 1, 0.5, 2.0, 0, pt, 0, 0, 0); // 1 octave, no tiling
                    assertEquals(0.0, n3, GRID_POINT_DELTA,
                            "pnoise3 at integer grid point (" + i + "," + j + "," + k + ") should be 0");
                }
            }
        }

        // Test pnoise3 with Tiling (should also be 0 at grid points relative to its own
        // period,
        // but the interaction of tiling complicates this simple assertion directly on
        // global integer coords.
        // However, if x,y,z are integer multiples of the repeat period, the logic
        // should effectively wrap to (0,0,0) relative.
        // More simply, the local fractional coordinates passed to the core noise
        // function would be 0.
        int repeat = 8;
        double n_tiled_grid = PerlinNoise.pnoise3(0.0, 0.0, 0.0, 1, 0.5, 2.0, 0, pt, repeat, repeat, repeat);
        assertEquals(0.0, n_tiled_grid, GRID_POINT_DELTA, "Tiled pnoise3 at (0,0,0) should be 0");
        double n_tiled_grid_offset = PerlinNoise.pnoise3((double) repeat, (double) repeat, (double) repeat, 1, 0.5, 2.0,
                0, pt, repeat, repeat, repeat);
        assertEquals(0.0, n_tiled_grid_offset, GRID_POINT_DELTA,
                "Tiled pnoise3 at integer multiple of repeat period should be 0");

    }

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

    @Test
    void testPerlin1DReproducibility() {
        double x = 0.789;
        // Assumes default permutation table is used by Noise.pnoise1 by default
        double val1 = Noise.pnoise1(x);
        double val2 = Noise.pnoise1(x);
        assertEquals(val1, val2, DELTA, "Repeated calls with default permutation should yield identical results.");

        // If we could directly access and change the default PermutationTable instance used by PerlinNoise/Noise,
        // this test would be more direct. For now, we test that different 'base' values
        // (which effectively use different parts/seeds of the permutation logic) produce different results.
        double val_base0 = Noise.pnoise1(x, 1, 0); // 1 octave, base 0
        double val_base1 = Noise.pnoise1(x, 1, 1); // 1 octave, base 1
        assertNotEquals(val_base0, val_base1, DELTA, "Different base values should change output.");
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

    @Test
    void testPerlin2DReproducibility() {
        double x = 0.123, y = 0.456;
        double val1 = Noise.pnoise2(x, y);
        double val2 = Noise.pnoise2(x, y);
        assertEquals(val1, val2, DELTA, "Repeated calls should yield identical results.");

        double val_base0 = Noise.pnoise2(x, y, 1, 0);
        double val_base1 = Noise.pnoise2(x, y, 1, 1);
        assertNotEquals(val_base0, val_base1, DELTA, "Different base values should change output.");
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
    void testPerlin3DReproducibility() {
        double x = 0.7, y = 0.8, z = 0.9;
        double val1 = Noise.pnoise3(x, y, z);
        double val2 = Noise.pnoise3(x, y, z);
        assertEquals(val1, val2, DELTA, "Repeated calls should yield identical results.");

        double val_base0 = Noise.pnoise3(x, y, z, 1, 0);
        double val_base1 = Noise.pnoise3(x, y, z, 1, 1);
        assertNotEquals(val_base0, val_base1, DELTA, "Different base values should change output.");
    }

    @Test
    void testPerlin3DTiling() { 
        int repeat = 8;
        double x = 0.1, y = 0.2, z = 0.3;

        PermutationTable defaultPermTable = new PermutationTable(); // Default fixed table
        int baseForPermutation = 0; // 'base' for pnoise3, influences permutation lookup

        // Test tiling with default permutation table
        double n1_default = PerlinNoise.pnoise3(x, y, z, 1, 0.5, 2.0, baseForPermutation, defaultPermTable, repeat,
                repeat, repeat);
        double n2_tiledX = PerlinNoise.pnoise3(x + repeat, y, z, 1, 0.5, 2.0, baseForPermutation, defaultPermTable,
                repeat, repeat, repeat);
        double n3_tiledY = PerlinNoise.pnoise3(x, y + repeat, z, 1, 0.5, 2.0, baseForPermutation, defaultPermTable,
                repeat, repeat, repeat);
        double n4_tiledZ = PerlinNoise.pnoise3(x, y, z + repeat, 1, 0.5, 2.0, baseForPermutation, defaultPermTable,
                repeat, repeat, repeat);

        assertEquals(n1_default, n2_tiledX, 1e-5, "Tiling failed for X axis (default perm table)");
        assertEquals(n1_default, n3_tiledY, 1e-5, "Tiling failed for Y axis (default perm table)");
        assertEquals(n1_default, n4_tiledZ, 1e-5, "Tiling failed for Z axis (default perm table)");

        // Test that a different 'baseForPermutation' produces different noise but still tiles
        int differentBaseForPermutation = 5;
        double n1_diffBase = PerlinNoise.pnoise3(x, y, z, 1, 0.5, 2.0, differentBaseForPermutation, defaultPermTable,
                repeat, repeat, repeat);
        double n2_diffBase_tiledX = PerlinNoise.pnoise3(x + repeat, y, z, 1, 0.5, 2.0, differentBaseForPermutation,
                defaultPermTable, repeat, repeat, repeat);

        assertNotEquals(n1_default, n1_diffBase, 1e-5, "Different baseForPermutation should produce different noise.");
        assertEquals(n1_diffBase, n2_diffBase_tiledX, 1e-5,
                "Tiling should still work with a different baseForPermutation.");
    }
}