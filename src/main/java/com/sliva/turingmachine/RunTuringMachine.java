package com.sliva.turingmachine;

import static com.sliva.turingmachine.Transition.STARTING_STATE;
import static com.sliva.turingmachine.Transition.SYMBOL_ONE;
import static com.sliva.turingmachine.Transition.SYMBOL_ZERO;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author whost
 */
public class RunTuringMachine {

    private static final int STEP_LIMIT = 100_000_000;
    private static final int TAPE_LIMIT = 30_000;

    public static void main(String[] args) throws Exception {
        String bb4 = """
                      \tA\tB\tC\tD
                      0\t1RB\t1LA\t1RH\t1RD
                      1\t1LB\t0LC\t1LD\t0RA
                      """;
        String bb5 = """
                     \tA\tB\tC\tD\tE
                     0\t1RB\t1RC\t1RD\t1LA\t1RH
                     1\t1LC\t1RB\t0LE\t1LD\t0LA
                     """;
        TMProgram tmProgram = readProgram(bb5);
        PrintUtils.printTransisitons(tmProgram, System.out);

        TMState tmState = new TMState(tmProgram, new byte[TAPE_LIMIT * 2], STEP_LIMIT, 0, STARTING_STATE, TAPE_LIMIT);

        try ( PrintStream filePrintStream = new PrintStream(new FileOutputStream("output.txt"))) {
            PrintUtils.printTransisitons(tmProgram, filePrintStream);
            TMState tmState2 = tmState.copy();
            runAndPrintResultTape(tmState2, filePrintStream);
            int totalSteps = tmState2.getStep();
            //print tape for the first and last 2000 steps
            PrintUtils.runAndPrint(tmState, filePrintStream, tms -> tms.getStep() < 2000 || tms.getStep() > totalSteps - 2000);
        }
    }

    private static void runAndPrintResultTape(TMState tms, PrintStream out) {
        int minPos = Integer.MAX_VALUE;
        int maxPos = Integer.MIN_VALUE;
        while (!tms.isHaltState()) {
            tms.applyTransition();
            if (tms.getPosTape() < minPos) {
                minPos = tms.getPosTape();
            }
            if (tms.getPosTape() > maxPos) {
                maxPos = tms.getPosTape();
            }
        }
        out.println("MinMax: (" + minPos + "," + maxPos + "), tapeSize=" + (maxPos - minPos + 1) + ", steps=" + tms.getStep());
        out.println("Final Tape:");
        out.println(PrintUtils.toStringTape(tms.getTape(), minPos, maxPos, tms.getPosTape()));
    }

    private static TMProgram readProgram(String str) {
        String[] lines = str.split("\n");
        int rows = lines.length;
        int cols = lines[0].split("\t").length;
        String[][] table = new String[rows][cols];
        for (int i = 0; i < rows; i++) {
            String[] cells = lines[i].split("\t");
            for (int j = 0; j < cols; j++) {
                table[i][j] = cells[j].trim();
            }
        }
        if (!"".equals(table[0][0]) || !"0".equals(table[1][0]) || !"1".equals(table[2][0]) || table[0].length != table[1].length || table[1].length != table[2].length) {
            throw new IllegalArgumentException("Unexpected input: " + str);
        }
        Map<Character, Integer> statesMap = new HashMap<>();
        for (int i = 1; i < table[0].length; i++) {
            statesMap.put(table[0][i].charAt(0), i);
        }
        System.out.println("statesMap:" + statesMap);
        Transition[] transitions = new Transition[statesMap.size() * 2];
        for (int i = 1; i < table[1].length; i++) {
            transitions[i * 2 - 2] = readTransition(table[1][i], statesMap);
            transitions[i * 2 - 1] = readTransition(table[2][i], statesMap);
        }
        return new TMProgram(transitions);
    }

    private static Transition readTransition(String str, Map<Character, Integer> statesMap) {
        if (str == null || str.length() != 3 || (str.charAt(0) != '0' && str.charAt(0) != '1') || (str.charAt(1) != 'L' && str.charAt(1) != 'R')) {
            throw new IllegalArgumentException("Incorrect transition: " + str);
        }
        return str.charAt(2) == 'H' ? Transition.HALT
                : new Transition(str.charAt(0) == '0' ? SYMBOL_ZERO : SYMBOL_ONE,
                        str.charAt(1) == 'L' ? Direction.LEFT : Direction.RIGHT,
                        statesMap.get(str.charAt(2)));
    }
}
