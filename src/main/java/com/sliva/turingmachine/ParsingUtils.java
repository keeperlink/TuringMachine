package com.sliva.turingmachine;

import java.util.HashMap;
import java.util.Map;

public final class ParsingUtils {

    public static TMProgramN readProgram(String str) {
        String[] lines = str.split("\n");
        int rows = lines.length;
        int cols = lines[0].split("\t").length;
        while (rows > 0 && lines[rows - 1].isBlank()) {
            //remove empty lines at the bottom
            rows--;
        }
        String[][] table = new String[rows][cols];
        for (int i = 0; i < rows; i++) {
            String[] cells = lines[i].split("\t");
            for (int j = 0; j < cols; j++) {
                table[i][j] = cells[j].trim();
            }
        }
        int numStates = cols - 1;
        int numSymbols = rows - 1;
        if (numSymbols < 2) {
            throw new IllegalArgumentException("Unexpected number of symbols. Expected at least 2, but parsed: " + numSymbols);
        }
        if (!"".equals(table[0][0]) || !"0".equals(table[1][0]) || !"1".equals(table[2][0]) || table[0].length != table[1].length || table[1].length != table[2].length) {
            throw new IllegalArgumentException("Unexpected input: " + str);
        }
        Map<Character, Integer> statesMap = new HashMap<>();
        for (int state = 1; state <= numStates; state++) {
            statesMap.put(table[0][state].charAt(0), state);
        }
        System.out.println("statesMap:" + statesMap);
        Transition[] transitions = new Transition[numStates * numSymbols];
        for (int state = 1; state <= numStates; state++) {
            for (int symbol = 0; symbol < numSymbols; symbol++) {
                transitions[(state - 1) * numSymbols + symbol] = readTransition(table[symbol + 1][state], statesMap, numSymbols);
            }
        }
        return new TMProgramN(transitions, numSymbols);
    }

    public static Transition readTransition(String str, Map<Character, Integer> statesMap, int numSymbols) {
        if (str == null || str.length() != 3 || str.charAt(0) < '0' || str.charAt(0) > ('0' + numSymbols) || (str.charAt(1) != 'L' && str.charAt(1) != 'R')) {
            throw new IllegalArgumentException("Incorrect transition: " + str);
        }
        return str.charAt(2) == 'H' ? Transition.HALT
                : new Transition((byte) (str.charAt(0) - '0'),
                        str.charAt(1) == 'L' ? Direction.LEFT : Direction.RIGHT,
                        statesMap.get(str.charAt(2)));
    }
}
