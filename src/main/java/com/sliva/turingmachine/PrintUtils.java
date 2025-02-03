package com.sliva.turingmachine;

import static com.sliva.turingmachine.Transition.STARTING_STATE;
import static com.sliva.turingmachine.Transition.SYMBOL_ONE;
import static com.sliva.turingmachine.Transition.SYMBOL_ZERO;
import java.io.PrintStream;
import java.util.function.Function;
import org.apache.commons.lang3.StringUtils;

public final class PrintUtils {

    public static void printTransisitons(TMProgram tmProgram, PrintStream out) {
        for (int state = STARTING_STATE; state <= tmProgram.getSize(); state++) {
            for (byte symbol = SYMBOL_ZERO; symbol <= SYMBOL_ONE; symbol++) {
                out.println("" + state + ":" + symbol + "  " + tmProgram.getTransition(state, symbol));
            }
        }
    }

    public static void runAndPrint(TMState tms, PrintStream out, Function<TMState, Boolean> doPrint) {
        MinMax head = getMinMaxHeadPos(tms);

        out.println(" step  " + StringUtils.rightPad("tape", (head.getMax() - head.getMin() + 1) * 3) + "  state");
        do {
            tms.applyTransition();
            if (doPrint == null || doPrint.apply(tms)) {
                out.println(StringUtils.leftPad(Integer.toString(tms.getStep()), 5) + "  "
                        + toStringTape(tms.getTape(), head.getMin(), head.getMax(), tms.getPosTape()) + "  "
                        + (tms.isHaltState() ? "HALT" : Integer.toString(tms.getState())));
            }
        } while (!tms.isHaltState());
    }

    public static MinMax getMinMaxHeadPos(TMState tms) {
        int minPos = Integer.MAX_VALUE;
        int maxPos = Integer.MIN_VALUE;
        TMState _tms = tms.copy();
        do {
            _tms.applyTransition();
            if (_tms.getPosTape() < minPos) {
                minPos = _tms.getPosTape();
            }
            if (_tms.getPosTape() > maxPos) {
                maxPos = _tms.getPosTape();
            }
        } while (!_tms.isHaltState());
        System.out.println("MinMax: (" + minPos + "," + maxPos + "), usedTapeSize=" + (maxPos - minPos + 1) + ", steps=" + _tms.getStep());
        return new MinMax(minPos, maxPos);
    }

    public static void printTransisitons(TMProgramN tmProgram, PrintStream out) {
        for (int state = STARTING_STATE; state <= tmProgram.getNumStates(); state++) {
            for (byte symbol = 0; symbol < tmProgram.getNumSymbols(); symbol++) {
                out.println("" + state + ":" + symbol + "  " + tmProgram.getTransition(state, symbol));
            }
        }
    }

    public static void printTransisitonsTable(TMProgramN tmProgram, PrintStream out) {
        for (int state = STARTING_STATE; state <= tmProgram.getNumStates(); state++) {
            out.print("\t" + stateToLetter(state));
        }
        for (byte symbol = 0; symbol < tmProgram.getNumSymbols(); symbol++) {
            out.println();
            out.print(symbol);
            for (int state = STARTING_STATE; state <= tmProgram.getNumStates(); state++) {
                out.print("\t" + tmProgram.getTransition(state, symbol).toShortString());
            }
        }
        out.println();
    }

    public static void runAndPrint(TMStateN tms, PrintStream out, Function<TMStateN, Boolean> doPrint) {
        Tape finalTape = getFinalTape(tms.copy());
        out.println("finalTape: " + finalTape);
        out.println();
        out.println("    step trans   " + StringUtils.rightPad("tape", finalTape.getUsedSize() * 3) + "  next");
        while (!tms.isFinshed()) {
            Transition t = tms.applyTransition();
            if (doPrint == null || doPrint.apply(tms)) {
                out.println(StringUtils.leftPad(Integer.toString(tms.getStep()), 8) + " "
                        + t.toShortString() + " "
                        + stateSymbolToString(tms.getOldState(), tms.getOldSymbol()) + "  "
                        + toStringTape(tms.getTape(), finalTape.getMinPos(), finalTape.getMaxPos()) + "  "
                        + (tms.getState() == 0 ? "" : tms.getNextTransition().toShortString() + " "
                        + stateSymbolToString(tms.getState(), tms.getTape().getSymbol())));
            }
        }
    }

    public static String stateSymbolToString(int state, int symbol) {
        return String.valueOf(stateToLetter(state)) + symbol;
    }

    public static char stateToLetter(int state) {
        return (char) ('A' + state - 1);
    }

    public static Tape getFinalTape(TMStateN tms) {
        tms.runLoop(null);
        return tms.getTape();
    }

    public static String toStringTape(byte[] tape, int minPos, int maxPos, int headPos) {
        StringBuilder sb = new StringBuilder();
        for (int i = minPos; i <= maxPos; i++) {
            if (i == headPos) {
                sb.append(tape[i] == 0 ? "___" : "_" + tape[i] + "_");
            } else {
                sb.append(tape[i] == 0 ? " . " : " " + tape[i] + " ");
            }
        }
        return sb.toString();
    }

    public static String toStringTape(Tape tape, int minPos, int maxPos) {
        return toStringTape(tape.getTape(), minPos, maxPos, tape.getHead());
    }

    public static String toStringTape(Tape tape) {
        return toStringTape(tape, tape.getMinPos(), tape.getMaxPos());
    }
}
