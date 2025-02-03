package com.sliva.turingmachine;

import com.sliva.turingmachine.TMUtls.ProgramPosGroups;
import com.sliva.turingmachine.TMUtls.ProgramPosWithRange;
import java.util.ArrayList;
import java.util.List;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author whost
 */
public class TMUtlsTest {

    /**
     * Test of getRepeatingRanges method, of class TMUtls.
     */
    @Test
    public void testGetRepeatingRanges() {
        System.out.println("getRepeatingRanges");
        TMStateN tms = new TMStateEmulator(new int[]{0, 1, 1, 1, 1, 1, 2});
        int includeRepeats = 1;
        List<Range> expResult = new ArrayList<>();
        expResult.add(new Range(2, 4));
        List<Range> result = TMUtls.getRepeatingRanges(tms, includeRepeats);
        assertEquals(expResult, result);
    }

    /**
     * Test of getRepeatingGroups method, of class TMUtls.
     */
    @Test
    public void testGetRepeatingGroups() {
        System.out.println("getRepeatingGroups");
        //                                           0  1     2     3     4     5     6     7     8     9
        TMStateN tms = new TMStateEmulator(new int[]{0, 1, 1, 2, 2, 1, 1, 2, 2, 1, 1, 2, 2, 1, 1, 2, 2, 3});
        int includeRepeats = 1;
        List<ProgramPosGroups> expResult = new ArrayList<>();
        expResult.add(new ProgramPosGroups(List.of(1, 2), 1, 8, TMUtls.getCompactHistory(tms.copy())));
        List<ProgramPosGroups> result = TMUtls.getRepeatingGroups(tms, includeRepeats);
        assertEquals(expResult, result);
    }

    /**
     * Test of getRepeatingGroups method, of class TMUtls.
     */
    @Test
    public void testGetRepeatingGroups_3() {
        System.out.println("getRepeatingGroups");
        //                                           0  1     2     3  4     5     6  7     8     9  10    11    12 13
        TMStateN tms = new TMStateEmulator(new int[]{0, 1, 1, 2, 2, 3, 1, 1, 2, 2, 3, 1, 1, 2, 2, 3, 1, 1, 2, 2, 3, 0});
        int includeRepeats = 1;
        List<ProgramPosGroups> expResult = new ArrayList<>();
        expResult.add(new ProgramPosGroups(List.of(1, 2, 3), 1, 12, TMUtls.getCompactHistory(tms.copy())));
        List<ProgramPosGroups> result = TMUtls.getRepeatingGroups(tms, includeRepeats);
        assertEquals(expResult, result);
    }

    /**
     * Test of getRepeatingGroups method, of class TMUtls.
     */
    @Test
    public void testGetRepeatingGroups_3_part() {
        System.out.println("getRepeatingGroups");
        //                                           0  1     2     3  4     5     6  7     8     9  10    11    12    13    14
        TMStateN tms = new TMStateEmulator(new int[]{0, 1, 1, 2, 2, 3, 1, 1, 2, 2, 3, 1, 1, 2, 2, 3, 1, 1, 2, 2, 3, 3, 1, 1, 0});
        int includeRepeats = 1;
        List<ProgramPosGroups> expResult = new ArrayList<>();
        expResult.add(new ProgramPosGroups(List.of(1, 2, 3), 1, 12, TMUtls.getCompactHistory(tms.copy())));
        List<ProgramPosGroups> result = TMUtls.getRepeatingGroups(tms, includeRepeats);
        assertEquals(expResult, result);
    }

    /**
     * Test of getRepeatingGroups method, of class TMUtls.
     */
    @Test
    public void testGetRepeatingGroups_3_part2() {
        System.out.println("getRepeatingGroups");
        //                                           0  1     2     3  4     5     6  7     8     9  10    11    12    13    14    15
        TMStateN tms = new TMStateEmulator(new int[]{0, 1, 1, 2, 2, 3, 1, 1, 2, 2, 3, 1, 1, 2, 2, 3, 1, 1, 2, 2, 3, 3, 1, 1, 2, 2, 0});
        int includeRepeats = 1;
        List<ProgramPosGroups> expResult = new ArrayList<>();
        expResult.add(new ProgramPosGroups(List.of(1, 2, 3), 1, 12, TMUtls.getCompactHistory(tms.copy())));
        List<ProgramPosGroups> result = TMUtls.getRepeatingGroups(tms, includeRepeats);
        assertEquals(expResult, result);
    }

    /**
     * Test of getRepeatingGroups method, of class TMUtls.
     */
    @Test
    public void testGetRepeatingGroups_two_groups() {
        System.out.println("getRepeatingGroups");
        //                                           0  1     2     3     4     5     6     7     8     9  10    11    12    13    14    15    16    17    18
        TMStateN tms = new TMStateEmulator(new int[]{0, 1, 1, 2, 2, 1, 1, 2, 2, 1, 1, 2, 2, 1, 1, 2, 2, 3, 2, 2, 1, 1, 2, 2, 1, 1, 2, 2, 1, 1, 2, 2, 1, 1, 3});
        int includeRepeats = 1;
        List<ProgramPosGroups> expResult = new ArrayList<>();
        expResult.add(new ProgramPosGroups(List.of(1, 2), 1, 8, TMUtls.getCompactHistory(tms.copy())));
        expResult.add(new ProgramPosGroups(List.of(2, 1), 10, 17, TMUtls.getCompactHistory(tms.copy())));
        List<ProgramPosGroups> result = TMUtls.getRepeatingGroups(tms, includeRepeats);
        assertEquals(expResult, result);
    }

    /**
     * Test of getRepeatingGroups method, of class TMUtls.
     */
    @Test
    public void testGetRepeatingGroups_partia() {
        System.out.println("getRepeatingGroups");
        //                                           0  1     2     3     4     5     6     7     8     9     10
        TMStateN tms = new TMStateEmulator(new int[]{0, 1, 1, 2, 2, 1, 1, 2, 2, 1, 1, 2, 2, 1, 1, 2, 2, 1, 1, 3});
        int includeRepeats = 1;
        List<ProgramPosGroups> expResult = new ArrayList<>();
        expResult.add(new ProgramPosGroups(List.of(1, 2), 1, 8, TMUtls.getCompactHistory(tms.copy())));
        List<ProgramPosGroups> result = TMUtls.getRepeatingGroups(tms, includeRepeats);
        assertEquals(expResult, result);
    }

    /**
     * Test of getRepeatingGroups method, of class TMUtls.
     */
    @Test
    public void testGetRepeatingGroups_long() {
        System.out.println("getRepeatingGroups");
        //                                           0  1     2     3     4     5     6     7     8     9     10    11    12    13    14    15    16    17    18    19    20    21    22    23    24    25    26    27    28    29    30    21
        TMStateN tms = new TMStateEmulator(new int[]{0, 1, 1, 2, 2, 1, 1, 2, 2, 1, 1, 2, 2, 1, 1, 2, 2, 1, 1, 2, 2, 1, 1, 2, 2, 1, 1, 2, 2, 1, 1, 2, 2, 1, 1, 2, 2, 1, 1, 2, 2, 1, 1, 2, 2, 1, 1, 2, 2, 1, 1, 2, 2, 1, 1, 2, 2, 1, 1, 2, 2, 3});
        int includeRepeats = 1;
        List<ProgramPosGroups> expResult = new ArrayList<>();
        expResult.add(new ProgramPosGroups(List.of(1, 2), 1, 30, TMUtls.getCompactHistory(tms.copy())));
        List<ProgramPosGroups> result = TMUtls.getRepeatingGroups(tms, includeRepeats);
        assertEquals(expResult, result);
    }

    /**
     * Test of getCompactHistory method, of class TMUtls.
     */
    @Test
    public void testGetCompactHistory() {
        System.out.println("getCompactHistory");
        TMStateN tms = new TMStateEmulator(new int[]{0, 1, 1, 1, 1, 1, 2});
        List<ProgramPosWithRange> expResult = new ArrayList<>();
        expResult.add(new ProgramPosWithRange(0, 0, 0));
        expResult.add(new ProgramPosWithRange(1, 1, 5));
        expResult.add(new ProgramPosWithRange(2, 6, 6));
        List<ProgramPosWithRange> result = TMUtls.getCompactHistory(tms);
        assertEquals(expResult, result);
    }

}
