package com.sliva.turingmachine;

import lombok.AllArgsConstructor;

/**
 *
 * @author whost
 */
@AllArgsConstructor
public class Transition {

    public final byte newSymbol;
    public final Direction direction;
    public final byte nextState;

    @Override
    public String toString() {
        return nextState == 0
                ? "HALT"
                : "" + newSymbol + "," + direction + "," + nextState;
    }

}
