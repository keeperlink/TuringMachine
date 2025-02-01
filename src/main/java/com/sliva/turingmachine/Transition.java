package com.sliva.turingmachine;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author whost
 */
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

    @Override
    public String toString() {
        return isHalt()
                ? "HALT"
                : newSymbol + "," + direction + "," + nextState;
    }

}
