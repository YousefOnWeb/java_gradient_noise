package com.example.noise;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SimplexNoiseTest {
    @Test
    void testSimplex2DRange() {
        for (int i = -100; i <= 100; i++) { // Reduced range
            double x = i * 0.49;
            double y = -i * 0.67;
            double n = Noise.snoise2(x, y);
            assertTrue(n >= -1.0 && n <= 1.0, "Value out of range [-1,1]: " + n + " for x=" + x + ", y=" + y);
        }
    }

    @Test
    void testSimplex2DOctavesRange() {
         for (int o = 1; o <= 5; o++) {
            for (int i = -50; i <= 50; i++) {
                double x = -i * 0.49;
                double y = i * 0.67;
                double n = Noise.snoise2(x, y, o);
                assertTrue(n >= -1.0 && n <= 1.0, "Value out of range [-1,1]: " + n + " for x=" + x + ", y=" + y + ", octaves=" + o);
            }
        }
    }
    
    // Similar tests for snoise3 and snoise4:
    // testSimplex3DRange, testSimplex3DOctavesRange
    // testSimplex4DRange, testSimplex4DOctavesRange
}