package com.example.noise;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PerlinNoiseTest {

    private static final double DELTA = 1e-9; // For float comparisons

    @Test
    void testPerlin1DRange() {
        for (int i = -100; i <= 100; i++) { // Reduced range for faster test
            double x = i * 0.49;
            double n = Noise.pnoise1(x);
            assertTrue(n >= -1.0 && n <= 1.0, "Value out of range [-1,1]: " + n + " for x=" + x);
        }
    }

    @Test
    void testPerlin1DOctavesRange() {
        for (int o = 1; o <= 5; o++) { // Test a few octave counts
            for (int i = -50; i <= 50; i++) {
                double x = i * 0.49;
                double n = Noise.pnoise1(x, o);
                assertTrue(n >= -1.0 && n <= 1.0, "Value out of range [-1,1]: " + n + " for x=" + x + ", octaves=" + o);
            }
        }
    }

    @Test
    void testPerlin1DBase() {
        double x = 0.5;
        double n_base0 = Noise.pnoise1(x, 1, 0);
        double n_base0_default = Noise.pnoise1(x); // Should be same as base=0
        double n_base5 = Noise.pnoise1(x, 1, 5);
        double n_base1 = Noise.pnoise1(x, 1, 1);

        assertEquals(n_base0, n_base0_default, DELTA, "Default base should be 0");
        assertNotEquals(n_base0, n_base5, DELTA, "Value for base 0 and base 5 should differ");
        assertNotEquals(n_base5, n_base1, DELTA, "Value for base 5 and base 1 should differ");
    }

    // Similar tests for pnoise2 and pnoise3:
    // testPerlin2DRange, testPerlin2DOctavesRange, testPerlin2DBase
    // testPerlin3DRange, testPerlin3DOctavesRange, testPerlin3DBase
    
    @Test
    void testPerlin3DTiling() {
        // Test that pnoise3 with repeat parameters tiles correctly
        // e.g., Noise.pnoise3(0.1, 0.2, 0.3, 8,8,8, 0) should be very close to
        // Noise.pnoise3(0.1 + 8.0, 0.2, 0.3, 8,8,8, 0)
        int repeat = 8;
        double x=0.1, y=0.2, z=0.3;
        PermutationTable pt = new PermutationTable(); // Use a consistent perm table

        double n1 = PerlinNoise.pnoise3(x,y,z, 1, 0.5, 2.0, 0, pt, repeat, repeat, repeat);
        double n2 = PerlinNoise.pnoise3(x + repeat, y, z, 1, 0.5, 2.0, 0, pt, repeat, repeat, repeat);
        double n3 = PerlinNoise.pnoise3(x, y + repeat, z, 1, 0.5, 2.0, 0, pt, repeat, repeat, repeat);
        double n4 = PerlinNoise.pnoise3(x, y, z + repeat, 1, 0.5, 2.0, 0, pt, repeat, repeat, repeat);
        
        assertEquals(n1, n2, 1e-5, "Tiling failed for X axis"); // Expect some floating point noise
        assertEquals(n1, n3, 1e-5, "Tiling failed for Y axis");
        assertEquals(n1, n4, 1e-5, "Tiling failed for Z axis");
    }
}