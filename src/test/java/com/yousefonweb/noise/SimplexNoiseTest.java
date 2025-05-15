package com.yousefonweb.noise;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SimplexNoiseTest {

    private static final double DELTA = 1e-9; // For float comparisons
    private static final int RANGE_CHECK_ITERATIONS_SMALL = 50; // For octave tests
    private static final int RANGE_CHECK_ITERATIONS_LARGE = 100; // For single octave tests
    private static final int OCTAVE_COUNT_MAX = 5; // Max octaves to test

    // --- 2D Simplex Noise Tests ---
    @Test
    void testSimplex2DRange() {
        for (int i = -RANGE_CHECK_ITERATIONS_LARGE; i <= RANGE_CHECK_ITERATIONS_LARGE; i++) {
            double x = i * 0.49;
            double y = -i * 0.67;
            double n = Noise.snoise2(x, y); // Default: 1 octave
            assertTrue(n >= -1.0 - DELTA && n <= 1.0 + DELTA,
                    "Value out of range [-1,1]: " + n + " for (x,y)=(" + x + "," + y + ")");
        }
    }

    @Test
    void testSimplex2DOctavesRange() {
        for (int o = 1; o <= OCTAVE_COUNT_MAX; o++) {
            for (int i = -RANGE_CHECK_ITERATIONS_SMALL; i <= RANGE_CHECK_ITERATIONS_SMALL; i++) {
                double x = -i * 0.49;
                double y = i * 0.67;
                double n = Noise.snoise2(x, y, o);
                assertTrue(n >= -1.0 - DELTA && n <= 1.0 + DELTA,
                        "Value out of range [-1,1]: " + n + " for (x,y)=(" + x + "," + y + "), octaves=" + o);
            }
        }
    }

    // --- 3D Simplex Noise Tests ---
    @Test
    void testSimplex3DRange() {
        for (int i = -RANGE_CHECK_ITERATIONS_LARGE; i <= RANGE_CHECK_ITERATIONS_LARGE; i++) {
            double x = i * 0.31;
            double y = -i * 0.7;
            double z = i * 0.19;
            double n = Noise.snoise3(x, y, z); // Default: 1 octave
            assertTrue(n >= -1.0 - DELTA && n <= 1.0 + DELTA,
                    "Value out of range [-1,1]: " + n + " for (x,y,z)=(" + x + "," + y + "," + z + ")");
        }
    }

    @Test
    void testSimplex3DOctavesRange() {
        for (int o = 1; o <= OCTAVE_COUNT_MAX; o++) {
            for (int i = -RANGE_CHECK_ITERATIONS_SMALL; i <= RANGE_CHECK_ITERATIONS_SMALL; i++) {
                double x = -i * 0.12;
                double y = i * 0.55;
                double z = i * 0.34;
                double n = Noise.snoise3(x, y, z, o);
                assertTrue(n >= -1.0 - DELTA && n <= 1.0 + DELTA, "Value out of range [-1,1]: " + n + " for (x,y,z)=("
                        + x + "," + y + "," + z + "), octaves=" + o);
            }
        }
    }

    // --- 4D Simplex Noise Tests ---
    @Test
    void testSimplex4DRange() {
        for (int i = -RANGE_CHECK_ITERATIONS_LARGE; i <= RANGE_CHECK_ITERATIONS_LARGE; i++) {
            double x = i * 0.88;
            double y = -i * 0.11;
            double z = -i * 0.57;
            double w = i * 0.666;
            double n = Noise.snoise4(x, y, z, w); // Default: 1 octave
            assertTrue(n >= -1.0 - DELTA && n <= 1.0 + DELTA,
                    "Value out of range [-1,1]: " + n + " for (x,y,z,w)=(" + x + "," + y + "," + z + "," + w + ")");
        }
    }

    @Test
    void testSimplex4DOctavesRange() {
        for (int o = 1; o <= OCTAVE_COUNT_MAX; o++) {
            for (int i = -RANGE_CHECK_ITERATIONS_SMALL; i <= RANGE_CHECK_ITERATIONS_SMALL; i++) {
                double x = -i * 0.12;
                double y = i * 0.55;
                double z = i * 0.34;
                double w = i * 0.21;
                double n = Noise.snoise4(x, y, z, w, o);
                assertTrue(n >= -1.0 - DELTA && n <= 1.0 + DELTA, "Value out of range [-1,1]: " + n + " for (x,y,z,w)=("
                        + x + "," + y + "," + z + "," + w + "), octaves=" + o);
            }
        }
    }
}