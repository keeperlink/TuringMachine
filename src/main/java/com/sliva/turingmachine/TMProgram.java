package com.sliva.turingmachine;

import static com.sliva.turingmachine.Transition.SYMBOL_ONE;
import static com.sliva.turingmachine.Transition.SYMBOL_ZERO;
import java.util.Arrays;

public class TMProgram {

    private final Transition[] program;

    public TMProgram(int numStates) {
        program = new Transition[numStates * 2];
    }

    public TMProgram(Transition[] transitions) {
        this.program = transitions;
    }

    public Transition getTransition(int state, byte symbol) {
        validateState(state);
        return program[state * 2 - 2 + symbol];
    }

    public int getSize() {
        return program.length / 2;
    }

    public boolean hasInstruction(int state) {
        return getTransition(state, SYMBOL_ZERO) != null || getTransition(state, SYMBOL_ONE) != null;
    }

    public final void setTransition(int state, byte symbol, Transition t) {
        validateState(state);
        program[state * 2 - 2 + symbol] = t;
    }

    public int getLastState() {
        int s = getSize();
        while (s > 0 && !hasInstruction(s)) {
            s--;
        }
        return s;
    }

    public int getTransitionsCount() {
        int s = 0;
        for (Transition t : program) {
            if (t != null) {
                s++;
            }
        }
        return s;
    }

    public TMProgram copy() {
        return new TMProgram(Arrays.copyOf(program, program.length));
    }

    public Transition[] toTransitionsArray() {
        return program;
    }

    public void fillFrom(TMProgram p) {
        System.arraycopy(p.program, 0, this.program, 0, p.program.length);
    }

    private void validateState(int state) {
        if (state < 1 || state > program.length / 2) {
            throw new IllegalArgumentException("state value out of range:" + state);
        }
    }
}
