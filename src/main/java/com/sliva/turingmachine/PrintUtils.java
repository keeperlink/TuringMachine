package com.sliva.turingmachine;

import static com.sliva.turingmachine.Transition.STARTING_STATE;
import static com.sliva.turingmachine.Transition.SYMBOL_ONE;
import static com.sliva.turingmachine.Transition.SYMBOL_ZERO;
import java.io.PrintStream;
import java.util.function.Function;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author whost
 */
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
        System.out.println("MinMax: (" + minPos + "," + maxPos + "), tapeSize=" + (maxPos - minPos + 1) + ", steps=" + _tms.getStep());
        return new MinMax(minPos, maxPos);
    }

    public static void printTransisitons(TMProgramN tmProgram, PrintStream out) {
        for (int state = STARTING_STATE; state <= tmProgram.getNumStates(); state++) {
            for (byte symbol = 0; symbol < tmProgram.getNumSymbols(); symbol++) {
                out.println("" + state + ":" + symbol + "  " + tmProgram.getTransition(state, symbol));
            }
        }
    }

    public static void runAndPrint(TMStateN tms, PrintStream out, Function<TMStateN, Boolean> doPrint) {
        MinMax head = getMinMaxHeadPos(tms);

        out.println(" step  " + StringUtils.rightPad("tape", (head.getMax() - head.getMin() + 1) * 3) + "  state");
        tms.runLoop(t -> {
            if (doPrint == null || doPrint.apply(tms)) {
                out.println(StringUtils.leftPad(Integer.toString(tms.getStep()), 5) + "  "
                        + toStringTape(tms.getTape().getTape(), head.getMin(), head.getMax(), tms.getTape().getHead()) + "  "
                        + (tms.isHaltState() ? "HALT" : Integer.toString(tms.getState())));
            }
        });
    }

    public static MinMax getMinMaxHeadPos(TMStateN tms) {
        TMStateN _tms = tms.copy();
        _tms.runLoop(null);
        int minPos = _tms.getTape().getMinPos();
        int maxPos = _tms.getTape().getMaxPos();
        System.out.println("MinMax: (" + minPos + "," + maxPos + "), tapeSize=" + (maxPos - minPos + 1) + ", steps=" + _tms.getStep());
        return new MinMax(minPos, maxPos);
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
}
