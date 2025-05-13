package com.example.noise;

import static com.example.noise.NoiseConstants.*;

public class PerlinNoise {

    private static final PermutationTable DEFAULT_PERMUTATION_TABLE = new PermutationTable();
    private static final double DEFAULT_PERSISTENCE = 0.5;
    private static final double DEFAULT_LACUNARITY = 2.0;

    // Helper for 1D, 2D, 3D Improved Perlin Noise (single octave)
    private static double noise(double x, double y, double z, int base, PermutationTable permTable) {
        int X = (int) Math.floor(x) & (permTable.getPeriod() -1); // &255 if period is 256
        int Y = (int) Math.floor(y) & (permTable.getPeriod() -1);
        int Z = (int) Math.floor(z) & (permTable.getPeriod() -1);

        if (base != 0) {
            X = (X + base) % permTable.getPeriod();
            Y = (Y + base) % permTable.getPeriod();
            Z = (Z + base) % permTable.getPeriod();
             // Ensure positive result for modulo
            if (X < 0) X += permTable.getPeriod();
            if (Y < 0) Y += permTable.getPeriod();
            if (Z < 0) Z += permTable.getPeriod();
        }

        x -= Math.floor(x);
        y -= Math.floor(y);
        z -= Math.floor(z);

        double u = fade(x);
        double v = fade(y);
        double w = fade(z);

        int A = permTable.getPerm(X) + Y;
        int AA = permTable.getPerm(A) + Z;
        int AB = permTable.getPerm(A + 1) + Z;
        int B = permTable.getPerm(X + 1) + Y;
        int BA = permTable.getPerm(B) + Z;
        int BB = permTable.getPerm(B + 1) + Z;
        
        // Using GRAD3 for Perlin Improved Noise (typically 12 distinct non-zero axis vectors)
        // The TileableNoise in perlin.py uses GRAD3 with hash % 16.
        // Ken Perlin's Java reference uses permTable.getPerm(...) % 12 for grad indices.
        // Let's use % 12 with the first 12 entries of GRAD3 for standard Perlin.
        // For tileable part, we will adapt.

        return lerp(w, lerp(v, lerp(u, dot(GRAD3[permTable.getPerm(AA) % 12], x, y, z),
                                        dot(GRAD3[permTable.getPerm(BA) % 12], x - 1, y, z)),
                                lerp(u, dot(GRAD3[permTable.getPerm(AB) % 12], x, y - 1, z),
                                        dot(GRAD3[permTable.getPerm(BB) % 12], x - 1, y - 1, z))),
                        lerp(v, lerp(u, dot(GRAD3[permTable.getPerm(AA + 1) % 12], x, y, z - 1),
                                        dot(GRAD3[permTable.getPerm(BA + 1) % 12], x - 1, y, z - 1)),
                                lerp(u, dot(GRAD3[permTable.getPerm(AB + 1) % 12], x, y - 1, z - 1),
                                        dot(GRAD3[permTable.getPerm(BB + 1) % 12], x - 1, y - 1, z - 1))));
    }
    
    // Noise1D (adapted from 3D)
     private static double noise(double x, int base, PermutationTable permTable) {
        int X = (int) Math.floor(x) & (permTable.getPeriod() -1);
        if (base != 0) {
            X = (X + base) % permTable.getPeriod();
            if (X < 0) X += permTable.getPeriod();
        }
        x -= Math.floor(x);
        double u = fade(x);

        // Simplified gradient dot products for 1D
        // grad1 = perm[X] % grad_table_size; grad2 = perm[X+1] % grad_table_size
        // Typically for 1D, gradients are just -1, 1.
        // Here, we use a component of GRAD3 for consistency or use dedicated 1D grads.
        // Using x component of GRAD3 vectors for simplicity:
        return lerp(u, GRAD3[permTable.getPerm(X) % 12][0] * x, 
                       GRAD3[permTable.getPerm(X+1) % 12][0] * (x-1) );
    }

    // Noise2D (adapted from 3D)
    private static double noise(double x, double y, int base, PermutationTable permTable) {
        int X = (int) Math.floor(x) & (permTable.getPeriod() -1);
        int Y = (int) Math.floor(y) & (permTable.getPeriod() -1);
         if (base != 0) {
            X = (X + base) % permTable.getPeriod();
            Y = (Y + base) % permTable.getPeriod();
            if (X < 0) X += permTable.getPeriod();
            if (Y < 0) Y += permTable.getPeriod();
        }

        x -= Math.floor(x);
        y -= Math.floor(y);
        double u = fade(x);
        double v = fade(y);

        int A = permTable.getPerm(X) + Y;
        int B = permTable.getPerm(X + 1) + Y;

        return lerp(v, lerp(u, dot(GRAD3[permTable.getPerm(A) % 12], x, y),
                                dot(GRAD3[permTable.getPerm(B) % 12], x - 1, y)),
                       lerp(u, dot(GRAD3[permTable.getPerm(A + 1) % 12], x, y - 1),
                                dot(GRAD3[permTable.getPerm(B + 1) % 12], x - 1, y - 1)));
    }


    // Public Perlin noise functions matching python package's pnoise calls
    public static double pnoise1(double x, int octaves, double persistence, double lacunarity, int base, PermutationTable permTable) {
        double total = 0;
        double frequency = 1;
        double amplitude = 1;
        double maxValue = 0; // Used for normalizing result to [-1,1]

        for (int i = 0; i < octaves; i++) {
            total += noise(x * frequency, base, permTable) * amplitude;
            maxValue += amplitude;
            amplitude *= persistence;
            frequency *= lacunarity;
        }
        return total / maxValue;
    }
    
    public static double pnoise1(double x, int octaves, int base) {
        return pnoise1(x, octaves, DEFAULT_PERSISTENCE, DEFAULT_LACUNARITY, base, DEFAULT_PERMUTATION_TABLE);
    }
    public static double pnoise1(double x, int octaves) {
        return pnoise1(x, octaves, 0);
    }
    public static double pnoise1(double x) {
        return pnoise1(x, 1, 0);
    }
     public static double pnoise1(double x, int octaves, double persistence, double lacunarity, int base) {
        return pnoise1(x, octaves, persistence, lacunarity, base, DEFAULT_PERMUTATION_TABLE);
    }


    public static double pnoise2(double x, double y, int octaves, double persistence, double lacunarity, int base, PermutationTable permTable) {
        double total = 0;
        double frequency = 1;
        double amplitude = 1;
        double maxValue = 0;

        for (int i = 0; i < octaves; i++) {
            total += noise(x * frequency, y * frequency, base, permTable) * amplitude;
            maxValue += amplitude;
            amplitude *= persistence;
            frequency *= lacunarity;
        }
        return total / maxValue;
    }

    public static double pnoise2(double x, double y, int octaves, int base) {
        return pnoise2(x, y, octaves, DEFAULT_PERSISTENCE, DEFAULT_LACUNARITY, base, DEFAULT_PERMUTATION_TABLE);
    }
    public static double pnoise2(double x, double y, int octaves) {
        return pnoise2(x,y,octaves,0);
    }
    public static double pnoise2(double x, double y) {
        return pnoise2(x,y,1,0);
    }
    public static double pnoise2(double x, double y, int octaves, double persistence, double lacunarity, int base) {
        return pnoise2(x, y, octaves, persistence, lacunarity, base, DEFAULT_PERMUTATION_TABLE);
    }

    // Special grad3 for tileable noise, as in perlin.py's TileableNoise
    private static double tileableGrad(int hash, double x, double y, double z, PermutationTable permTable) {
	    // Uses GRAD3 with hash % 16, matching perlin.py's TileableNoise.grad3
        // and its _GRAD3 definition which has 16 elements.
        int h = permTable.getPerm(hash) % 16; // permTable.getPerm(hash) IS the hash for grad selection
	    return dot(GRAD3[h], x, y, z);
    }

    // Tileable 3D Perlin noise, inspired by perlin.py's TileableNoise and ShaderNoiseTexture's usage
    private static double noise3Tileable(double x, double y, double z, int repeatX, int repeatY, int repeatZ, int base, PermutationTable permTable) {
        // Ensure coordinates are positive for modulo behavior consistent with Python's fmod(floor(coord), repeat)
        double fx = x % repeatX; if (fx < 0) fx += repeatX;
        double fy = y % repeatY; if (fy < 0) fy += repeatY;
        double fz = z % repeatZ; if (fz < 0) fz += repeatZ;

        int i = (int) Math.floor(fx);
        int j = (int) Math.floor(fy);
        int k = (int) Math.floor(fz);

        int ii = (i + 1) % repeatX;
        int jj = (j + 1) % repeatY;
        int kk = (k + 1) % repeatZ;
        
        // Apply base offset to indices for permutation lookup if non-zero
        // This needs to be carefully mapped. The python code adds base to i,j,k,ii,jj,kk before perm lookup
        // And perm is period-masked. So, the indices into perm table should be (idx + base) % period.
        // The PermutationTable handles its own period, repeatX/Y/Z are for coordinate wrapping.
        // The TileableNoise python code effectively uses perm[(actual_i + base) % period]
        // where actual_i depends on repeat.
        // The 'base' parameter in pnoise3 (from tests) seems different from TileableNoise's 'base'
        // The pnoise3 'base' shifts the permutation space. TileableNoise base adds to wrapped coords.
        // For now, this 'base' parameter refers to the permutation shifting 'base'.
        // The ShaderNoiseTexture has a 'base' parameter that shifts the whole coordinate system
        // for the second channel of noise. This is like pnoise3(..., base=different_value).

        int p_base = base; // base for permutation table selection

        // These indices are for perm[] lookups and should be masked by permTable.getPeriod()-1
        // if permTable access method doesn't do it.
        // For TileableNoise, the perm lookups use i,j,k which are already mod repeat.
        // The critical part is that perm is indexed by values related to repeat, not permTable.period.
        // This means the permTable must be large enough or indices carefully managed if repeat > period.
        // Perlin's original tileable noise implies perm table indices are effectively (coord % repeat)
        // and these are then used to index the permutation table.
        // If repeat is, say, 8, and permTable.period is 256, then only a small part of permTable is used.
        // The `base` parameter in python's TileableNoise is added to these small indices.

        // Let's adjust indices for perm table before applying base, to match TileableNoise.py
        // and assume permTable is large enough or indices are appropriately mapped.
        // The perm table itself might be randomized with a different seed (base).
        // This particular TileableNoise implementation logic:
        int p_i = i; int p_j = j; int p_k = k;
        int p_ii = ii; int p_jj = jj; int p_kk = kk;
        if (base != 0) { // This 'base' is for TileableNoise internal offset logic
             p_i = (p_i + base) % repeatX; p_j = (p_j + base) % repeatY; p_k = (p_k + base) % repeatZ;
             p_ii = (p_ii + base) % repeatX; p_jj = (p_jj + base) % repeatY; p_kk = (p_kk + base) % repeatZ;
        }


        double relX = fx - Math.floor(fx);
        double relY = fy - Math.floor(fy);
        double relZ = fz - Math.floor(fz);

        double sx = fade(relX);
        double sy = fade(relY);
        double sz = fade(relZ);
        
        // Permutation indices for TileableNoise in perlin.py are complex.
        // A = perm[i], AA = perm[A+j], AB = perm[A+jj], etc.
        // These indices i,j,k are modulo 'repeat', not 'period'.
        // This implies either period == repeat, or perm values are used carefully.
        // If perm values are < period, then A+j can be > period. Doubled perm array helps.
        // Assuming permTable.getPerm() can take indices up to 2*period.
        // And that values i,j,k etc. are used to form indices into perm.
        // The 'base' in shader_noise.py pnoise3(..., base=freq+1) is the permutation base.
        // The tileable noise needs 'repeat' for coord wrapping and for perm table indexing.
        // If the 'base' parameter passed to this method is the *permutation table base shift*, then:
        // We'd use a PermutationTable initialized/offset by 'base'.
        // The ShaderNoiseTexture likely uses two distinct permutation sets (base=0 and base=freq+1)
        // when calling pnoise3, where pnoise3 itself applies tiling.

        // Standard perlin.py TileableNoise logic:
        // perm values are 0 to period-1.
        // i,j,k are 0 to repeat-1.
        // If repeat == period, then it's straightforward.
        // If repeat < period, then perm[i] still makes sense.
        // It is assumed that repeat <= period. Usually repeat is a power of 2 like 32, 64, 
        // and period is 256.

        int A = permTable.getPerm(p_i);
        int AA = permTable.getPerm(A + p_j);
        int AB = permTable.getPerm(A + p_jj);
        int B = permTable.getPerm(p_ii);
        int BA = permTable.getPerm(B + p_j);
        int BB = permTable.getPerm(B + p_jj);

        return lerp(sz,
                lerp(sy,
                        lerp(sx, tileableGrad(AA + p_k, relX, relY, relZ, permTable),
                                tileableGrad(BA + p_k, relX - 1, relY, relZ, permTable)),
                        lerp(sx, tileableGrad(AB + p_k, relX, relY - 1, relZ, permTable),
                                tileableGrad(BB + p_k, relX - 1, relY - 1, relZ, permTable))),
                lerp(sy,
                        lerp(sx, tileableGrad(AA + p_kk, relX, relY, relZ - 1, permTable),
                                tileableGrad(BA + p_kk, relX - 1, relY, relZ - 1, permTable)),
                        lerp(sx, tileableGrad(AB + p_kk, relX, relY - 1, relZ - 1, permTable),
                                tileableGrad(BB + p_kk, relX - 1, relY - 1, relZ - 1, permTable))));
    }


    public static double pnoise3(double x, double y, double z, int octaves, double persistence, double lacunarity,
                                int base, PermutationTable permTable,
                                int repeatX, int repeatY, int repeatZ) {
        double total = 0;
        double frequency = 1;
        double amplitude = 1;
        double maxValue = 0;

        PermutationTable currentPermTable = permTable;
        if (base != 0 && permTable == DEFAULT_PERMUTATION_TABLE) {
            // If a base is specified and we are using the default table,
            // we should ideally use a permutation table shifted by 'base'.
            // This requires a mechanism to get differently seeded/based PermutationTables.
            // For now, we'll assume 'base' in the non-tileable 'noise' function handles this shift,
            // or the 'base' for tiling is used within tileableNoise3.
            // The pnoise3 tests use 'base' without 'repeat'. ShaderNoiseTexture uses 'base' with 'repeat'.
            // This suggests 'base' is a general permutation offset parameter.
        }


        for (int i = 0; i < octaves; i++) {
            double val;
            if (repeatX > 0 || repeatY > 0 || repeatZ > 0) { // Tiling enabled
                // The 'base' for tileableNoise3 refers to the internal offset like in Python's TileableNoise.
                // The 'base' for pnoise3 (permutation shift) should be handled by providing a different permTable.
                // For ShaderNoiseTexture, it calls pnoise3(..., base=offset_for_perm_table)
                // and pnoise3 internally handles tiling using repeatX parameters.
                val = noise3Tileable(x * frequency, y * frequency, z * frequency,
                                   (int)(repeatX / frequency), (int)(repeatY / frequency), (int)(repeatZ / frequency),
                                   0,  // TileableNoise internal base, 0 for now. Permutation base is via permTable.
                                   currentPermTable);
            } else { // Non-tileable
                val = noise(x * frequency, y * frequency, z * frequency, base, currentPermTable) * amplitude;
            }
            total += val * amplitude;
            maxValue += amplitude;
            amplitude *= persistence;
            frequency *= lacunarity;
        }
        return total / maxValue;
    }

    // Public overloads for pnoise3
    public static double pnoise3(double x, double y, double z, int octaves, int base) {
        return pnoise3(x, y, z, octaves, DEFAULT_PERSISTENCE, DEFAULT_LACUNARITY, base, DEFAULT_PERMUTATION_TABLE, 0,0,0);
    }
    public static double pnoise3(double x, double y, double z, int octaves) {
        return pnoise3(x,y,z,octaves,0);
    }
    public static double pnoise3(double x, double y, double z) {
        return pnoise3(x,y,z,1,0);
    }
    
    // For ShaderNoiseTexture: pnoise3(x,y,z, repeatx,repeaty,repeatz, base)
    // This signature implies octaves=1, default persistence/lacunarity.
    public static double pnoise3(double x, double y, double z,
                                 int repeatX, int repeatY, int repeatZ, int base, PermutationTable permTable) {
        return pnoise3(x, y, z, 1, DEFAULT_PERSISTENCE, DEFAULT_LACUNARITY, base, permTable, repeatX, repeatY, repeatZ);
    }
    public static double pnoise3(double x, double y, double z,
                                 int repeatX, int repeatY, int repeatZ, int base) {
        return pnoise3(x, y, z, 1, DEFAULT_PERSISTENCE, DEFAULT_LACUNARITY, base, DEFAULT_PERMUTATION_TABLE, repeatX, repeatY, repeatZ);
    }
     public static double pnoise3(double x, double y, double z, int octaves, double persistence, double lacunarity, int base) {
        return pnoise3(x, y, z, octaves, persistence, lacunarity, base, DEFAULT_PERMUTATION_TABLE, 0,0,0);
    }

}