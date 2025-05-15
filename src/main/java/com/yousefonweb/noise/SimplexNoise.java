package com.yousefonweb.noise;

import static com.yousefonweb.noise.NoiseConstants.*;

public class SimplexNoise {

    private static final PermutationTable DEFAULT_PERMUTATION_TABLE = new PermutationTable();
    private static final double DEFAULT_PERSISTENCE = 0.5;
    private static final double DEFAULT_LACUNARITY = 2.0;

    // Simplex noise 2D
    private static double noise2(double x, double y, PermutationTable permTable) {
        double n = 0.0;
        double s = (x + y) * F2;
        int i = (int) Math.floor(x + s);
        int j = (int) Math.floor(y + s);
        double t = (i + j) * G2;
        double x0 = x - (i - t);
        double y0 = y - (j - t);

        int i1, j1;
        if (x0 > y0) {
            i1 = 1; j1 = 0;
        } else {
            i1 = 0; j1 = 1;
        }

        double x1 = x0 - i1 + G2;
        double y1 = y0 - j1 + G2;
        double x2 = x0 - 1.0 + 2.0 * G2;
        double y2 = y0 - 1.0 + 2.0 * G2;

        int ii = i % permTable.getPeriod(); if (ii < 0) ii += permTable.getPeriod();
        int jj = j % permTable.getPeriod(); if (jj < 0) jj += permTable.getPeriod();

        int gi0 = permTable.getPerm(ii + permTable.getPerm(jj)) % 12;
        int gi1 = permTable.getPerm(ii + i1 + permTable.getPerm(jj + j1)) % 12;
        int gi2 = permTable.getPerm(ii + 1 + permTable.getPerm(jj + 1)) % 12;
        
        double t0 = 0.5 - x0 * x0 - y0 * y0;
        if (t0 > 0) {
            t0 *= t0;
            n += t0 * t0 * dot(GRAD3[gi0], x0, y0);
        }

        double t1 = 0.5 - x1 * x1 - y1 * y1;
        if (t1 > 0) {
            t1 *= t1;
            n += t1 * t1 * dot(GRAD3[gi1], x1, y1);
        }

        double t2 = 0.5 - x2 * x2 - y2 * y2;
        if (t2 > 0) {
            t2 *= t2;
            n += t2 * t2 * dot(GRAD3[gi2], x2, y2);
        }
        return 70.0 * n; // Scale to cover [-1, 1]
    }

    // Simplex noise 3D
    private static double noise3(double x, double y, double z, PermutationTable permTable) {
        double n = 0.0;
        double s = (x + y + z) * F3;
        int i = (int) Math.floor(x + s);
        int j = (int) Math.floor(y + s);
        int k = (int) Math.floor(z + s);
        double t = (i + j + k) * G3;
        double x0 = x - (i - t);
        double y0 = y - (j - t);
        double z0 = z - (k - t);

        int i1, j1, k1;
        int i2, j2, k2;

        if (x0 >= y0) {
            if (y0 >= z0) { i1 = 1; j1 = 0; k1 = 0; i2 = 1; j2 = 1; k2 = 0; }
            else if (x0 >= z0) { i1 = 1; j1 = 0; k1 = 0; i2 = 1; j2 = 0; k2 = 1; }
            else { i1 = 0; j1 = 0; k1 = 1; i2 = 1; j2 = 0; k2 = 1; }
        } else { // x0 < y0
            if (y0 < z0) { i1 = 0; j1 = 0; k1 = 1; i2 = 0; j2 = 1; k2 = 1; }
            else if (x0 < z0) { i1 = 0; j1 = 1; k1 = 0; i2 = 0; j2 = 1; k2 = 1; }
            else { i1 = 0; j1 = 1; k1 = 0; i2 = 1; j2 = 1; k2 = 0; }
        }

        double x1 = x0 - i1 + G3;
        double y1 = y0 - j1 + G3;
        double z1 = z0 - k1 + G3;
        double x2 = x0 - i2 + 2.0 * G3;
        double y2 = y0 - j2 + 2.0 * G3;
        double z2 = z0 - k2 + 2.0 * G3;
        double x3 = x0 - 1.0 + 3.0 * G3;
        double y3 = y0 - 1.0 + 3.0 * G3;
        double z3 = z0 - 1.0 + 3.0 * G3;

        int ii = i % permTable.getPeriod(); if (ii < 0) ii += permTable.getPeriod();
        int jj = j % permTable.getPeriod(); if (jj < 0) jj += permTable.getPeriod();
        int kk = k % permTable.getPeriod(); if (kk < 0) kk += permTable.getPeriod();
        
        int gi0 = permTable.getPerm(ii + permTable.getPerm(jj + permTable.getPerm(kk))) % 12;
        int gi1 = permTable.getPerm(ii + i1 + permTable.getPerm(jj + j1 + permTable.getPerm(kk + k1))) % 12;
        int gi2 = permTable.getPerm(ii + i2 + permTable.getPerm(jj + j2 + permTable.getPerm(kk + k2))) % 12;
        int gi3 = permTable.getPerm(ii + 1 + permTable.getPerm(jj + 1 + permTable.getPerm(kk + 1))) % 12;

        double t0 = 0.6 - x0 * x0 - y0 * y0 - z0 * z0;
        if (t0 > 0) {
            t0 *= t0;
            n += t0 * t0 * dot(GRAD3[gi0], x0, y0, z0);
        }
        double t1 = 0.6 - x1 * x1 - y1 * y1 - z1 * z1;
        if (t1 > 0) {
            t1 *= t1;
            n += t1 * t1 * dot(GRAD3[gi1], x1, y1, z1);
        }
        double t2 = 0.6 - x2 * x2 - y2 * y2 - z2 * z2;
        if (t2 > 0) {
            t2 *= t2;
            n += t2 * t2 * dot(GRAD3[gi2], x2, y2, z2);
        }
        double t3 = 0.6 - x3 * x3 - y3 * y3 - z3 * z3;
        if (t3 > 0) {
            t3 *= t3;
            n += t3 * t3 * dot(GRAD3[gi3], x3, y3, z3);
        }
        return 32.0 * n; // Scale to cover [-1, 1]
    }

    // Simplex noise 4D (standard algorithm, constants F4, G4, GRAD4, SIMPLEX)
    private static double noise4(double x, double y, double z, double w, PermutationTable permTable) {
        double n0, n1, n2, n3, n4; // Noise contributions from five corners
        
        // Skew the (x,y,z,w) space to determine which cell of 24 simplices we're in
        double s = (x + y + z + w) * F4; // Factor for 4D skewing
        int i = (int)Math.floor(x + s);
        int j = (int)Math.floor(y + s);
        int k = (int)Math.floor(z + s);
        int l = (int)Math.floor(w + s);
        
        double t = (i + j + k + l) * G4; // Factor for 4D unskewing
        double X0 = i - t; // Unskewed grid origin
        double Y0 = j - t;
        double Z0 = k - t;
        double W0 = l - t;
        
        double x0 = x - X0; // The x,y,z,w distances from the grid origin
        double y0 = y - Y0;
        double z0 = z - Z0;
        double w0 = w - W0;
        
        // For the 4D case, the simplex is a 4D shape called a pentachoron.
        // It has 5 vertices. Identify the 4 simplices surrounding the input point.
        // Determine which simplex we are in.
        int c = (x0 > y0 ? 32 : 0) + (x0 > z0 ? 16 : 0) + (y0 > z0 ? 8 : 0) + (x0 > w0 ? 4 : 0) + (y0 > w0 ? 2 : 0) + (z0 > w0 ? 1 : 0);
        
        int i1 = SIMPLEX[c][0] >= 3 ? 1 : 0; 
        int j1 = SIMPLEX[c][1] >= 3 ? 1 : 0; 
        int k1 = SIMPLEX[c][2] >= 3 ? 1 : 0; 
        int l1 = SIMPLEX[c][3] >= 3 ? 1 : 0;
        
        int i2 = SIMPLEX[c][0] >= 2 ? 1 : 0; 
        int j2 = SIMPLEX[c][1] >= 2 ? 1 : 0; 
        int k2 = SIMPLEX[c][2] >= 2 ? 1 : 0; 
        int l2 = SIMPLEX[c][3] >= 2 ? 1 : 0;
        
        int i3 = SIMPLEX[c][0] >= 1 ? 1 : 0; 
        int j3 = SIMPLEX[c][1] >= 1 ? 1 : 0; 
        int k3 = SIMPLEX[c][2] >= 1 ? 1 : 0; 
        int l3 = SIMPLEX[c][3] >= 1 ? 1 : 0;
        
        // simplex[c] is a 4-vector with values in 0,1,2,3 indicating the order of axes.
        // For example, if simplex[c] = (2,3,1,0), then x_offset_order = (k,l,j,i)
        // The code above (i1,j1...i3,j3...) is a common way to determine offsets for the other 4 vertices.
        // This specific mapping might need verification against a known working 4D simplex source.
        // A more direct interpretation:
        // int c1 = simplex[c][0]; int c2 = simplex[c][1]; int c3 = simplex[c][2]; int c4 = simplex[c][3];

        double x1 = x0 - i1 + G4;
        double y1 = y0 - j1 + G4;
        double z1 = z0 - k1 + G4;
        double w1 = w0 - l1 + G4;
        double x2 = x0 - i2 + 2.0 * G4;
        double y2 = y0 - j2 + 2.0 * G4;
        double z2 = z0 - k2 + 2.0 * G4;
        double w2 = w0 - l2 + 2.0 * G4;
        double x3 = x0 - i3 + 3.0 * G4;
        double y3 = y0 - j3 + 3.0 * G4;
        double z3 = z0 - k3 + 3.0 * G4;
        double w3 = w0 - l3 + 3.0 * G4;
        double x4 = x0 - 1.0 + 4.0 * G4;
        double y4 = y0 - 1.0 + 4.0 * G4;
        double z4 = z0 - 1.0 + 4.0 * G4;
        double w4 = w0 - 1.0 + 4.0 * G4;
        
        // Calculate the hashed gradient indices of the five simplex corners
        int ii = i % permTable.getPeriod(); if (ii < 0) ii += permTable.getPeriod();
        int jj = j % permTable.getPeriod(); if (jj < 0) jj += permTable.getPeriod();
        int kk = k % permTable.getPeriod(); if (kk < 0) kk += permTable.getPeriod();
        int ll = l % permTable.getPeriod(); if (ll < 0) ll += permTable.getPeriod();
        
        int gi0 = permTable.getPerm(ii + permTable.getPerm(jj + permTable.getPerm(kk + permTable.getPerm(ll)))) % 32;
        int gi1 = permTable.getPerm(ii + i1 + permTable.getPerm(jj + j1 + permTable.getPerm(kk + k1 + permTable.getPerm(ll + l1)))) % 32;
        int gi2 = permTable.getPerm(ii + i2 + permTable.getPerm(jj + j2 + permTable.getPerm(kk + k2 + permTable.getPerm(ll + l2)))) % 32;
        int gi3 = permTable.getPerm(ii + i3 + permTable.getPerm(jj + j3 + permTable.getPerm(kk + k3 + permTable.getPerm(ll + l3)))) % 32;
        int gi4 = permTable.getPerm(ii + 1 + permTable.getPerm(jj + 1 + permTable.getPerm(kk + 1 + permTable.getPerm(ll + 1)))) % 32;
        
        // Calculate the contribution from the five corners
        double t0 = 0.6 - x0*x0 - y0*y0 - z0*z0 - w0*w0;
        if (t0 < 0) n0 = 0.0;
        else {
            t0 *= t0;
            n0 = t0 * t0 * dot(GRAD4[gi0], x0, y0, z0, w0);
        }
        
        double t1 = 0.6 - x1*x1 - y1*y1 - z1*z1 - w1*w1;
        if (t1 < 0) n1 = 0.0;
        else {
            t1 *= t1;
            n1 = t1 * t1 * dot(GRAD4[gi1], x1, y1, z1, w1);
        }
        
        double t2 = 0.6 - x2*x2 - y2*y2 - z2*z2 - w2*w2;
        if (t2 < 0) n2 = 0.0;
        else {
            t2 *= t2;
            n2 = t2 * t2 * dot(GRAD4[gi2], x2, y2, z2, w2);
        }
        
        double t3 = 0.6 - x3*x3 - y3*y3 - z3*z3 - w3*w3;
        if (t3 < 0) n3 = 0.0;
        else {
            t3 *= t3;
            n3 = t3 * t3 * dot(GRAD4[gi3], x3, y3, z3, w3);
        }
        
        double t4 = 0.6 - x4*x4 - y4*y4 - z4*z4 - w4*w4;
        if (t4 < 0) n4 = 0.0;
        else {
            t4 *= t4;
            n4 = t4 * t4 * dot(GRAD4[gi4], x4, y4, z4, w4);
        }
        
        // Sum contributions from the five corners
        return 27.0 * (n0 + n1 + n2 + n3 + n4); // Arbitrary scaling factor. May need adjustment for [-1,1]
    }


    // Public Simplex noise functions
    public static double snoise2(double x, double y, int octaves, double persistence, double lacunarity, PermutationTable permTable) {
        double total = 0;
        double frequency = 1;
        double amplitude = 1;
        double maxValue = 0;

        for (int i = 0; i < octaves; i++) {
            total += noise2(x * frequency, y * frequency, permTable) * amplitude;
            maxValue += amplitude;
            amplitude *= persistence;
            frequency *= lacunarity;
        }
        // Simplex noise (properly scaled) should already be in a range close to [-1,1] per octave.
        // The fBm sum might exceed this. Normalization by maxValue ensures it.
        return total / maxValue;
    }
    public static double snoise2(double x, double y, int octaves) {
        return snoise2(x, y, octaves, DEFAULT_PERSISTENCE, DEFAULT_LACUNARITY, DEFAULT_PERMUTATION_TABLE);
    }
    public static double snoise2(double x, double y) {
        return snoise2(x,y,1);
    }


    public static double snoise3(double x, double y, double z, int octaves, double persistence, double lacunarity, PermutationTable permTable) {
        double total = 0;
        double frequency = 1;
        double amplitude = 1;
        double maxValue = 0;

        for (int i = 0; i < octaves; i++) {
            total += noise3(x * frequency, y * frequency, z * frequency, permTable) * amplitude;
            maxValue += amplitude;
            amplitude *= persistence;
            frequency *= lacunarity;
        }
        return total / maxValue;
    }
    public static double snoise3(double x, double y, double z, int octaves) {
        return snoise3(x,y,z,octaves, DEFAULT_PERSISTENCE, DEFAULT_LACUNARITY, DEFAULT_PERMUTATION_TABLE);
    }
    public static double snoise3(double x, double y, double z) {
        return snoise3(x,y,z,1);
    }

    public static double snoise4(double x, double y, double z, double w, int octaves, double persistence, double lacunarity, PermutationTable permTable) {
        double total = 0;
        double frequency = 1;
        double amplitude = 1;
        double maxValue = 0;

        for (int i = 0; i < octaves; i++) {
            total += noise4(x * frequency, y * frequency, z * frequency, w * frequency, permTable) * amplitude;
            maxValue += amplitude;
            amplitude *= persistence;
            frequency *= lacunarity;
        }
        return total / maxValue;
    }
     public static double snoise4(double x, double y, double z, double w, int octaves) {
        return snoise4(x,y,z,w,octaves, DEFAULT_PERSISTENCE, DEFAULT_LACUNARITY, DEFAULT_PERMUTATION_TABLE);
    }
    public static double snoise4(double x, double y, double z, double w) {
        return snoise4(x,y,z,w,1);
    }
}