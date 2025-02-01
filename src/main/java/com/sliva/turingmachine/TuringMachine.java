package com.sliva.turingmachine;

import static com.sliva.turingmachine.Transition.STARTING_STATE;
import static com.sliva.turingmachine.Transition.SYMBOL_ZERO;
import java.util.Arrays;
import java.util.function.Function;
import lombok.Setter;

/**
 *
 * @author whost
 */
public class TuringMachine {

    private final byte[] tape;
    @Setter
    private Function<TuringMachineState, Boolean> callback;

    public TuringMachine(int stepLimit) {
        tape = new byte[stepLimit * 2 + 2];
    }

    public int run(Transition[] transitions) {
        Arrays.fill(tape, SYMBOL_ZERO);
        TuringMachineState tms = new TuringMachineState(0, STARTING_STATE, tape.length / 2, tape);
//        int headPosition = tape.length / 2;
//        int currentState = STARTING_STATE;
//        int steps = 0;

        while (!tms.isHaltState()) {
            if (tms.isMaxStepsReached()) {
                return -1;
            }
            byte currentSymbol = tape[tms.getHeadPosition()];
            Transition t = transitions[tms.getCurrentState() * 2 - 2 + currentSymbol];

            // Write new symbol to tape
            tape[tms.getHeadPosition()] = t.getNewSymbol();

            // Move head
            tms.moveHead(t.getDirection());

            // Update state
            tms.setCurrentState(t.getNextState());

            // Increment step counter
            tms.incrementStep();

            if (callback != null) {
                if (!callback.apply(tms)) {
                    break;
                }
            }
        }
        return tms.getStep();
    }

}
