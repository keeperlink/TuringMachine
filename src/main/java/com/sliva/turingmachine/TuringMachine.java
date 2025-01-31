package com.sliva.turingmachine;

import java.util.Arrays;
import java.util.function.Function;
import lombok.Setter;

/**
 *
 * @author whost
 */
public class TuringMachine {

    private static final byte HALT_STATE = 0;
    private static final byte STARTING_STATE = 1;

    private final int stepLimit;
    private final byte[] tape;
    @Setter
    private Function<TuringMachineState, Boolean> callback;

    public TuringMachine(int stepLimit) {
        this.stepLimit = stepLimit;
        tape = new byte[stepLimit * 2 + 2];
    }

    public int run(Transition[] transitions) {
        int headPosition = tape.length / 2;
        byte currentState = STARTING_STATE;
        int steps = 0;
        Arrays.fill(tape, (byte) 0);

        while (currentState != HALT_STATE) {
            if (steps >= stepLimit) {
                return -1; // Assume non-halting
            }
            // Read symbol: true (1) if in set, false (0) otherwise
            byte currentSymbol = tape[headPosition];
            Transition t = transitions[currentState * 2 - 2 + currentSymbol];

            // Write new symbol to tape
            tape[headPosition] = t.newSymbol;

            // Move head
            headPosition += (t.direction == Direction.LEFT) ? -1 : 1;

            // Update state
            currentState = t.nextState;

            steps++;

            if (callback != null) {
                if (!callback.apply(new TuringMachineState(steps, currentState, headPosition, Arrays.copyOf(tape, tape.length)))) {
                    break;
                }
            }
        }
        return steps;
    }

}
