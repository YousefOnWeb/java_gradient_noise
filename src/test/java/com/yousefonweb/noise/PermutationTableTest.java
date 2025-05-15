package com.yousefonweb.noise;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;

public class PermutationTableTest {

    @Test
    void constructorDefault() {
        PermutationTable pt = new PermutationTable();
        assertEquals(256, pt.getPeriod(), "Default period should be 256");
        // Check if the default table is loaded (first few values)
        assertEquals(151, pt.getPerm(0));
        assertEquals(160, pt.getPerm(1));
        assertEquals(PermutationTable.getDefaultPermutationTableLength() * 2, pt.getPermutationArrayLength(), "Internal array should be doubled");
    }

    @Test
    void constructorWithPeriod() {
        int customPeriod = 64;
        PermutationTable pt = new PermutationTable(customPeriod);
        assertEquals(customPeriod, pt.getPeriod(), "Period should match constructor argument");
        assertEquals(customPeriod * 2, pt.getPermutationArrayLength(), "Internal array should be doubled period");

        // Verify content of the randomized table for the first half
        Set<Integer> expectedValues = new HashSet<>();
        for (int i = 0; i < customPeriod; i++) {
            expectedValues.add(i);
        }
        Set<Integer> actualValues = new HashSet<>();
        for (int i = 0; i < customPeriod; i++) {
            actualValues.add(pt.getPerm(i));
        }
        assertEquals(expectedValues, actualValues, "Permutation table should contain all numbers from 0 to period-1 exactly once in the first half.");

        // Verify doubling
        for (int i = 0; i < customPeriod; i++) {
            assertEquals(pt.getPerm(i), pt.getPerm(i + customPeriod), "Second half should be a copy of the first");
        }
    }

    @Test
    void constructorWithCustomTable() {
        int[] customTableData = {2, 0, 1, 3}; // Not power of two, but valid
        PermutationTable pt = new PermutationTable(customTableData);
        assertEquals(customTableData.length, pt.getPeriod(), "Period should be length of custom table");
        assertEquals(customTableData.length * 2, pt.getPermutationArrayLength(), "Internal array should be doubled");

        for (int i = 0; i < customTableData.length; i++) {
            assertEquals(customTableData[i], pt.getPerm(i), "First half mismatch");
            assertEquals(customTableData[i], pt.getPerm(i + customTableData.length), "Second half mismatch (doubling)");
        }
    }

    @Test
    void constructorWithInvalidPeriod() {
        assertThrows(IllegalArgumentException.class, () -> new PermutationTable(0), "Period 0 should throw exception");
        assertThrows(IllegalArgumentException.class, () -> new PermutationTable(-5), "Negative period should throw exception");
    }
    
    @Test
    void constructorWithNullOrEmptyTable() {
        assertThrows(IllegalArgumentException.class, () -> new PermutationTable(null), "Null table should throw exception");
        assertThrows(IllegalArgumentException.class, () -> new PermutationTable(new int[0]), "Empty table should throw exception");
    }


    @Test
    void randomizeWithNewPeriodAndSeed() {
        PermutationTable pt = new PermutationTable(); // Initial period 256
        int oldPeriod = pt.getPeriod();
        int[] oldPermutationPart = Arrays.copyOfRange(pt.getPermutationArray(), 0, oldPeriod);

        int newPeriod = 32;
        Random fixedSeedRandom = new Random(12345L); // Fixed seed
        pt.randomize(newPeriod, fixedSeedRandom);

        assertEquals(newPeriod, pt.getPeriod(), "Period should update after randomize");
        assertEquals(newPeriod * 2, pt.getPermutationArrayLength(), "Internal array length should update");

        // Verify content with new period
        Set<Integer> expectedValues = IntStream.range(0, newPeriod).boxed().collect(HashSet::new, Set::add, Set::addAll);
        Set<Integer> actualValues = new HashSet<>();
        for (int i = 0; i < newPeriod; i++) {
            actualValues.add(pt.getPerm(i));
        }
        assertEquals(expectedValues, actualValues, "Randomized table should contain unique values from 0 to newPeriod-1");

        // Verify it's different from the old one (highly probable with different period and seed)
        if (newPeriod == oldPeriod) { // Only comparable if periods were same, which they are not here
             assertFalse(Arrays.equals(Arrays.copyOfRange(pt.getPermutationArray(), 0, newPeriod), oldPermutationPart),
                        "Permutation should change after randomize with different period/seed.");
        }
    }

    @Test
    void randomizeKeepsOldPeriodIfNull() {
        PermutationTable pt = new PermutationTable(128);
        int originalPeriod = pt.getPeriod();
        int[] originalPermutationFirstHalf = new int[originalPeriod];
        for(int i=0; i<originalPeriod; ++i) originalPermutationFirstHalf[i] = pt.getPerm(i);


        // Create a new Random instance for the second randomize call to ensure it *would* be different if period changed
        pt.randomize(originalPeriod, new Random(54321L)); // Randomize with same period but different seed

        assertEquals(originalPeriod, pt.getPeriod(), "Period should remain the same when randomize is called with null newPeriod");
        
        // Check if the permutation actually changed (due to new Random seed)
        boolean changed = false;
        for(int i=0; i<originalPeriod; ++i) {
            if(pt.getPerm(i) != originalPermutationFirstHalf[i]){
                changed = true;
                break;
            }
        }
        assertTrue(changed, "Permutation should change after randomize even with same period, if new Random is used.");
    }
}