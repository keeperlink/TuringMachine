package com.sliva.turingmachine;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author whost
 */
public class TuringMachineWithHistory extends TuringMachine {

    private final List<TuringMachineState> statesHistory = new ArrayList<>();
    private int minPos;
    private int maxPos;

    public TuringMachineWithHistory(int stepLimit) {
        super(stepLimit);
        super.setCallback(this::processCallback);
    }

    @Override
    public int run(Transition[] transitions) {
        statesHistory.clear();
        minPos = Integer.MAX_VALUE;
        maxPos = Integer.MIN_VALUE;
        return super.run(transitions);
    }

    public void printHistory() {
        statesHistory.forEach(s -> {
            System.out.println(StringUtils.leftPad(Integer.toString(s.getStep()), 5) + "  " + toString(s.getTape(), minPos, maxPos, s.getHeadPosition()) + "  " + s.getCurrentState());
        });
    }

    private String toString(byte[] tape, int minPos, int maxPos, int headPos) {
        StringBuilder sb = new StringBuilder();
        for (int i = minPos; i <= maxPos; i++) {
            if (i == headPos) {
                sb.append(tape[i] == 0 ? 'o' : '+');
            } else {
                sb.append(tape[i] == 0 ? '0' : '1');
            }
        }
        return sb.toString();
    }

    private Boolean processCallback(TuringMachineState turingMachineState) {
        statesHistory.add(turingMachineState);
        if (turingMachineState.getHeadPosition() < minPos) {
            minPos = turingMachineState.getHeadPosition();
        }
        if (turingMachineState.getHeadPosition() > maxPos) {
            maxPos = turingMachineState.getHeadPosition();
        }
        return true;
    }
}
