package com.sliva.turingmachine;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import java.util.HashMap;
import java.util.Map;

public final class ParsingUtils {

    public static TMProgramN readProgram(String str) {
        checkNotNull(str);
        String[] lines = str.split("\n");
        int rows = lines.length;
        while (rows > 0 && lines[rows - 1].isBlank()) { //remove empty lines at the bottom
            rows--;
        }
        checkState(rows > 1, "Expected rows > 1, but was: %s", rows);
        int cols = lines[1].split("\t").length;
        checkState(cols > 1, "Expected columns > 1, but was: %s", cols);
        if (lines[0].split("\t").length < cols && !lines[0].startsWith("\t")) {
            lines[0] = "\t" + lines[0];
        }
        String[][] table = new String[rows][cols];
        for (int i = 0; i < rows; i++) {
            String[] cells = lines[i].split("\t");
            checkState(cells.length == cols, "Columns mismatch (%s <> %s) in line %s", cells, cols, i);
            for (int j = 0; j < cols; j++) {
                table[i][j] = cells[j].trim();
            }
        }
        boolean colsAreSymbols = "0".equals(table[1][0]);
        int numStates = (colsAreSymbols ? cols : rows) - 1;
        int numSymbols = (colsAreSymbols ? rows : cols) - 1;
        checkState(numSymbols > 1, "Unexpected number of symbols. Expected at least 2, but parsed: %s", numSymbols);
        Map<Character, Integer> statesMap = new HashMap<>();
        for (int state = 1; state <= numStates; state++) {
            statesMap.put(table[colsAreSymbols ? 0 : state][colsAreSymbols ? state : 0].charAt(0), state);
        }
        System.out.println("statesMap:" + statesMap);
        Transition[] transitions = new Transition[numStates * numSymbols];
        for (int state = 1; state <= numStates; state++) {
            for (int symbol = 0; symbol < numSymbols; symbol++) {
                checkState(Integer.toString(symbol).equals(table[colsAreSymbols ? symbol + 1 : 0][colsAreSymbols ? 0 : symbol + 1]), "Unexpected symbol. Expected %s, but was %s", symbol, table[colsAreSymbols ? symbol + 1 : 0][colsAreSymbols ? 0 : symbol + 1]);
                transitions[(state - 1) * numSymbols + symbol] = readTransition(table[colsAreSymbols ? symbol + 1 : state][colsAreSymbols ? state : symbol + 1], statesMap, numSymbols);
            }
        }
        return new TMProgramN(transitions, numSymbols);
    }

    public static Transition readTransition(String str, Map<Character, Integer> statesMap, int numSymbols) {
        if ("---".equals(str)) {
            return Transition.HALT;
        }
        checkArgument(str != null && str.length() == 3 && str.charAt(0) >= '0' && str.charAt(0) <= ('0' + numSymbols) && (str.charAt(1) == 'L' || str.charAt(1) == 'R'), "Incorrect transition: '%s'", str);
        return str.charAt(2) == 'H' ? Transition.HALT
                : new Transition((byte) (str.charAt(0) - '0'),
                        str.charAt(1) == 'L' ? Direction.LEFT : Direction.RIGHT,
                        statesMap.get(str.charAt(2)));
    }
}
