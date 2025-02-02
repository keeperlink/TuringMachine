package com.sliva.turingmachine;

import static com.sliva.turingmachine.Transition.HALT_STATE;
import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@AllArgsConstructor
@Getter
public class TuringMachineState {

    private int step;
    @Setter
    private int currentState;
    private int headPosition;
    private final byte[] tape;

    @Override
    public String toString() {
        return StringUtils.leftPad(Integer.toString(step), 5) + ": currentState=" + currentState + ", headPosition=" + headPosition + ", tape.len=" + tape.length + '}';
    }

    public int getMaxSteps() {
        return tape.length / 2 - 1;
    }

    public boolean isMaxStepsReached() {
        return step >= getMaxSteps();
    }

    public boolean isHaltState() {
        return currentState == HALT_STATE;
    }

    public void incrementStep() {
        step++;
    }

    public void moveHead(Direction d) {
        if (d == Direction.LEFT) {
            headPosition--;
        } else {
            headPosition++;
        }
    }

    public TuringMachineState copy() {
        return new TuringMachineState(step, currentState, headPosition, Arrays.copyOf(tape, tape.length));
    }
}
