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
        System.out.println(" step  " + StringUtils.rightPad("tape", (maxPos - minPos + 1) * 3) + "  state");
        statesHistory.forEach(s -> {
            System.out.println(StringUtils.leftPad(Integer.toString(s.getStep()), 5) + "  "
                    + PrintUtils.toStringTape(s.getTape(), minPos, maxPos, s.getHeadPosition()) + "  "
                    + (s.getCurrentState() == 0 ? "HALT" : Integer.toString(s.getCurrentState())));
        });
    }

    private Boolean processCallback(TuringMachineState turingMachineState) {
        statesHistory.add(turingMachineState.copy());
        if (turingMachineState.getHeadPosition() < minPos) {
            minPos = turingMachineState.getHeadPosition();
        }
        if (turingMachineState.getHeadPosition() > maxPos) {
            maxPos = turingMachineState.getHeadPosition();
        }
        return true;
    }
}
