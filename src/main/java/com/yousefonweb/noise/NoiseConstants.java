package com.yousefonweb.noise;

public class NoiseConstants {

    // For Simplex Noise
    public static final double F2 = 0.5 * (Math.sqrt(3.0) - 1.0);
    public static final double G2 = (3.0 - Math.sqrt(3.0)) / 6.0;
    public static final double F3 = 1.0 / 3.0;
    public static final double G3 = 1.0 / 6.0;
    // F4 and G4 for 4D Simplex, common values:
    public static final double F4 = (Math.sqrt(5.0) - 1.0) / 4.0;
    public static final double G4 = (5.0 - Math.sqrt(5.0)) / 20.0;


    // Gradient vectors
    public static final int[][] GRAD3 = {
            {1, 1, 0}, {-1, 1, 0}, {1, -1, 0}, {-1, -1, 0},
            {1, 0, 1}, {-1, 0, 1}, {1, 0, -1}, {-1, 0, -1},
            {0, 1, 1}, {0, -1, 1}, {0, 1, -1}, {0, -1, -1},
            // We'll use the first 12 for Simplex and allow a larger set for Perlin if needed.
            {1,1,0}, {0,-1,1}, {-1,1,0}, {0,-1,-1} // these are 12, 13, 14, 15th if table size is 16
    };
    
    // For Perlin improved noise, gradients are often simpler (12 vectors)
    // e.g. (1,1,0),(-1,1,0),(1,-1,0),(-1,-1,0),(1,0,1),(-1,0,1),(1,0,-1),(-1,0,-1),(0,1,1),(0,-1,1),(0,1,-1),(0,-1,-1)
    public static final int[][] GRAD4 = { 
            {0,1,1,1}, {0,1,1,-1}, {0,1,-1,1}, {0,1,-1,-1},
            {0,-1,1,1}, {0,-1,1,-1}, {0,-1,-1,1}, {0,-1,-1,-1},
            {1,0,1,1}, {1,0,1,-1}, {1,0,-1,1}, {1,0,-1,-1},
            {-1,0,1,1}, {-1,0,1,-1}, {-1,0,-1,1}, {-1,0,-1,-1},
            {1,1,0,1}, {1,1,0,-1}, {1,-1,0,1}, {1,-1,0,-1},
            {-1,1,0,1}, {-1,1,0,-1}, {-1,-1,0,1}, {-1,-1,0,-1},
            {1,1,1,0}, {1,1,-1,0}, {1,-1,1,0}, {1,-1,-1,0},
            {-1,1,1,0}, {-1,1,-1,0}, {-1,-1,1,0}, {-1,-1,-1,0}
    }; // 32 vectors

    // Simplex table (for 4D, though not used in its SimplexNoise class)
    public static final int[][] SIMPLEX = {
            {0,1,2,3},{0,1,3,2},{0,0,0,0},{0,2,3,1},{0,0,0,0},{0,0,0,0},{0,0,0,0},{1,2,3,0},
            {0,2,1,3},{0,0,0,0},{0,3,1,2},{0,3,2,1},{0,0,0,0},{0,0,0,0},{0,0,0,0},{1,3,2,0},
            {0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0},
            {1,2,0,3},{0,0,0,0},{1,3,0,2},{0,0,0,0},{0,0,0,0},{0,0,0,0},{2,3,0,1},{2,3,1,0},
            {1,0,2,3},{1,0,3,2},{0,0,0,0},{0,0,0,0},{0,0,0,0},{2,0,3,1},{0,0,0,0},{2,1,3,0},
            {0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0},
            {2,0,1,3},{0,0,0,0},{0,0,0,0},{0,0,0,0},{3,0,1,2},{3,0,2,1},{0,0,0,0},{3,1,2,0},
            {2,1,0,3},{0,0,0,0},{0,0,0,0},{0,0,0,0},{3,1,0,2},{0,0,0,0},{3,2,0,1},{3,2,1,0}
    }; // 64 entries

    public static double dot(int[] grad, double x, double y) {
        return grad[0] * x + grad[1] * y;
    }

    public static double dot(int[] grad, double x, double y, double z) {
        return grad[0] * x + grad[1] * y + grad[2] * z;
    }

    public static double dot(int[] grad, double x, double y, double z, double w) {
        return grad[0] * x + grad[1] * y + grad[2] * z + grad[3] * w;
    }

    // Fade function for Perlin Improved Noise
    public static double fade(double t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    // Linear interpolation
    public static double lerp(double t, double a, double b) {
        return a + t * (b - a);
    }
}