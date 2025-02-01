package com.sliva.turingmachine;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author whost
 */
public final class PrintUtils {

    public static void runAndPrint(TMState tms) {
        MinMax head = getMinMaxHeadPos(tms);

        System.out.println(" step  " + StringUtils.rightPad("tape", (head.getMax() - head.getMin() + 1) * 3) + "  state");
        do {
            tms.applyTransition();
            System.out.println(StringUtils.leftPad(Integer.toString(tms.getStep()), 5) + "  "
                    + toStringTape(tms.getTape(), head.getMin(), head.getMax(), tms.getPosTape()) + "  "
                    + (tms.isHaltState() ? "HALT" : Integer.toString(tms.getState())));
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
        return new MinMax(minPos, maxPos);
    }

    public static String toStringTape(byte[] tape, int minPos, int maxPos, int headPos) {
        StringBuilder sb = new StringBuilder();
        for (int i = minPos; i <= maxPos; i++) {
            if (i == headPos) {
                sb.append(tape[i] == 0 ? "___" : "_1_");
            } else {
                sb.append(tape[i] == 0 ? " . " : " 1 ");
            }
        }
        return sb.toString();
    }
}
