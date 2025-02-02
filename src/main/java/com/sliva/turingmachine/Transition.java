package com.sliva.turingmachine;

import static com.sliva.turingmachine.PrintUtils.stateToLetter;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Transition {

    public static final byte SYMBOL_ZERO = 0;
    public static final byte SYMBOL_ONE = 1;
    public static final int HALT_STATE = 0;
    public static final int STARTING_STATE = 1;
    public static final Transition HALT = new Transition(SYMBOL_ZERO, Direction.LEFT, HALT_STATE);

    private final byte newSymbol;
    private final Direction direction;
    private final int nextState;

    public boolean isHalt() {
        return nextState == HALT_STATE;
    }

    public String toShortString() {
        return newSymbol + direction.name().substring(0, 1) + (char) (nextState == HALT_STATE ? 'H' : stateToLetter(nextState));
    }

    @Override
    public String toString() {
        return isHalt()
                ? "HALT"
                : newSymbol + "," + direction + "," + nextState;
    }

}
