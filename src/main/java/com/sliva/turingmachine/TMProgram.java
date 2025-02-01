package com.sliva.turingmachine;

import static com.sliva.turingmachine.Transition.SYMBOL_ONE;
import static com.sliva.turingmachine.Transition.SYMBOL_ZERO;
import java.util.Arrays;

/**
 *
 * @author whost
 */
public class TMProgram {

    private final TMInstruction[] program;

    public TMProgram(int numStates) {
        program = new TMInstruction[numStates];
    }

    public TMProgram(TMInstruction[] program) {
        this.program = program;
    }

    public TMProgram(Transition[] transitions) {
        this(transitions.length / 2);
        for (int i = 0; i < transitions.length; i++) {
            setTransition(i / 2 + 1, i % 2 == 0 ? SYMBOL_ZERO : SYMBOL_ONE, transitions[i]);
        }
    }

    public TMInstruction getInstruction(int state) {
        validateState(state);
        return program[state - 1];
    }

    public Transition getTransition(int state, byte symbol) {
        TMInstruction inst = getInstruction(state);
        if (inst != null) {
            return inst.getTransition(symbol);
        }
        return null;
    }

    public int getSize() {
        return program.length;
    }

    public boolean hasInstruction(int state) {
        return getInstruction(state) != null;
    }

    public final void setTransition(int state, byte symbol, Transition t) {
        TMInstruction inst = getInstruction(state);
        program[state - 1] = (inst == null) ? new TMInstruction(symbol, t) : inst.transition(symbol, t);
    }

    public TMProgram copy() {
        return new TMProgram(Arrays.copyOf(program, program.length));
    }

    public Transition[] toTransitionsArray() {
        Transition[] transitions = new Transition[program.length * 2];
        for (int state = 1; state <= program.length; state++) {
            for (byte symbol = SYMBOL_ZERO; symbol <= SYMBOL_ONE; symbol++) {
                transitions[state * 2 - 2 + symbol] = getTransition(state, symbol);
            }
        }
        return transitions;
    }

    private void validateState(int state) {
        if (state < 1 || state > program.length) {
            throw new IllegalArgumentException("state value out of range:" + state);
        }
    }
}
