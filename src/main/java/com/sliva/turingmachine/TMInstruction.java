package com.sliva.turingmachine;

import static com.sliva.turingmachine.Transition.SYMBOL_ONE;
import static com.sliva.turingmachine.Transition.SYMBOL_ZERO;
import lombok.AllArgsConstructor;

/**
 *
 * @author whost
 */
@AllArgsConstructor
public class TMInstruction {

    private final Transition t0;
    private final Transition t1;

    public Transition getTransition(byte symbol) {
        validateSymbol(symbol);
        return symbol == SYMBOL_ZERO ? t0 : t1;
    }

    public TMInstruction(byte symbol, Transition t) {
        validateSymbol(symbol);
        this.t0 = symbol == SYMBOL_ZERO ? t : null;
        this.t1 = symbol == SYMBOL_ONE ? t : null;
    }

    public TMInstruction transition(byte symbol, Transition t) {
        validateSymbol(symbol);
        return new TMInstruction(symbol == 0 ? t : t0, symbol == 1 ? t : t1);
    }

    private static void validateSymbol(byte symbol) {
        if (symbol != SYMBOL_ZERO && symbol != SYMBOL_ONE) {
            throw new IllegalArgumentException("symbol value out of range:" + symbol);
        }
    }
}
