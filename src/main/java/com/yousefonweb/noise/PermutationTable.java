package com.yousefonweb.noise;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PermutationTable {
    private int[] p; // Doubled permutation array
    private int period;

    private static final int[] DEFAULT_PERMUTATION_TABLE = {
            151, 160, 137, 91, 90, 15, 131, 13, 201, 95, 96, 53, 194, 233, 7, 225, 140, 36, 103, 30, 69, 142, 8, 99, 37,
            240, 21, 10, 23,
            190, 6, 148, 247, 120, 234, 75, 0, 26, 197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33, 88, 237,
            149, 56, 87, 174,
            20, 125, 136, 171, 168, 68, 175, 74, 165, 71, 134, 139, 48, 27, 166, 77, 146, 158, 231, 83, 111, 229, 122,
            60, 211, 133,
            230, 220, 105, 92, 41, 55, 46, 245, 40, 244, 102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132,
            187, 208, 89, 18,
            169, 200, 196, 135, 130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250, 124,
            123, 5, 202, 38,
            147, 118, 126, 255, 82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42, 223, 183, 170, 213,
            119, 248, 152,
            2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43, 172, 9, 129, 22, 39, 253, 9, 98, 108, 110, 79, 113, 224,
            232, 178, 185,
            112, 104, 218, 246, 97, 228, 251, 34, 242, 193, 238, 210, 144, 12, 191, 179, 162, 241, 81, 51, 145, 235,
            249, 14, 239,
            107, 49, 192, 214, 31, 181, 199, 106, 157, 184, 84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254, 138, 236,
            205, 93, 222,
            114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215, 61, 156, 180
    };

    public PermutationTable() {
        this(DEFAULT_PERMUTATION_TABLE);
    }

    public PermutationTable(int period) {
        if (period <= 0) {
            throw new IllegalArgumentException("Period must be positive.");
        }
        this.period = period;
        randomize(period, new Random());
    }

    public PermutationTable(int[] permutationTable) {
        if (permutationTable == null || permutationTable.length == 0) {
            throw new IllegalArgumentException("Permutation table cannot be null or empty.");
        }
        this.period = permutationTable.length;
        this.p = new int[this.period * 2];
        for (int i = 0; i < this.period; i++) {
            this.p[i] = permutationTable[i];
            this.p[i + this.period] = permutationTable[i];
        }
    }

    public void randomize(int newPeriod, Random random) {
        this.period = newPeriod;
        List<Integer> permList = new ArrayList<>(this.period);
        for (int i = 0; i < this.period; i++) {
            permList.add(i);
        }

        for (int i = 0; i < this.period; i++) {
            int j = random.nextInt(this.period - i) + i;
            Collections.swap(permList, i, j);
        }

        this.p = new int[this.period * 2];
        for (int i = 0; i < this.period; i++) {
            this.p[i] = permList.get(i);
            this.p[i + this.period] = permList.get(i);
        }
    }

    public int getPeriod() {
        return period;
    }

    public int get(int index) {
        return p[index & (p.length - 1)]; // Assumes p.length is power of 2, like 512 for period 256
                                          // Or, more generally for doubled array:
                                          // return p[index % p.length] but careful with negative results of %
                                          // The original python code seems to rely on p being already doubled.
                                          // and indexing like p[ii + p[jj]]
                                          // So direct indexing is fine if p is already doubled.
    }

    // Direct access for construction like p[ii + p[jj + p[kk]]]
    // This method handles the modulo for the outer index and relies on p being
    // doubled
    // to handle the inner sums without further modulos IF the sums stay within 0 to
    // 2*period-1.
    // The original Python code takes care of ii, jj, kk % period before using them.
    // p[ (base + p[ (base + p[ (base + kk) % period ] ) % period ] ) % period ]
    // Let's provide specific accessors based on how perlin.py uses it.
    // The p array is already doubled (length 512 for period 256).
    // Indexing like perm[ii + perm[jj]] is safe if ii and jj are already < period.
    // Max value of perm[x] is period-1. So ii + perm[jj] < period + (period-1) <
    // 2*period.
    // This means direct indexing into the doubled p array is fine.

    public int getPerm(int i) {
        return p[i];
    }

    public int getPermModPeriod(int i) {
        return p[i % period]; // If we need to ensure index is within the first half
    }

    /**
     * Returns a copy of the internal permutation array (which is doubled).
     * Primarily for testing or advanced use. Modifying the returned array
     * will not affect the internal state of this PermutationTable.
     * 
     * @return A copy of the doubled permutation array.
     */
    public int[] getPermutationArray() {
        return Arrays.copyOf(p, p.length);
    }

    /**
     * Gets the length of the internal (doubled) permutation array.
     * This will be 2 * getPeriod().
     * 
     * @return The length of the internal permutation array.
     */
    public int getPermutationArrayLength() {
        return p.length;
    }

    /**
     * Gets the length of the default permutation table.
     * 
     * @return The length of the default permutation table.
     */
    public static int getDefaultPermutationTableLength() {
        return DEFAULT_PERMUTATION_TABLE.length;
    }
}