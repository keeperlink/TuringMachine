package com.sliva.turingmachine;

import java.util.Arrays;
import lombok.Getter;

/**
 *
 * @author whost
 */
public class TMProgramN {

    @Getter
    private final int numStates;
    @Getter
    private final int numSymbols;
    private final Transition[] program;

    public TMProgramN(int numStates, int numSymbols) {
        this.numStates = numStates;
        this.numSymbols = numSymbols;
        program = new Transition[numStates * numSymbols];
    }

    public TMProgramN(Transition[] transitions, int numStates) {
        this.numStates = numStates;
        this.numSymbols = transitions.length / numStates;
        this.program = transitions;
    }

    public Transition getTransition(int state, byte symbol) {
        validateState(state);
        validateSymbol(symbol);
        return program[(state - 1) * numSymbols + symbol];
    }

    public final void setTransition(int state, byte symbol, Transition t) {
        validateState(state);
        validateSymbol(symbol);
        program[(state - 1) * numSymbols + symbol] = t;
    }

    public boolean hasInstruction(int state) {
        for (byte symbol = 0; symbol < numSymbols; symbol++) {
            if (getTransition(state, symbol) != null) {
                return true;
            }
        }
        return false;
    }

    public int getLastState() {
        int s = numStates;
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

    public TMProgramN copy() {
        return new TMProgramN(Arrays.copyOf(program, program.length), numStates);
    }

    public Transition[] toTransitionsArray() {
        return program;
    }

    public void fillFrom(TMProgramN p) {
        if (this.numStates != p.numStates || this.numSymbols != p.numSymbols) {
            throw new IllegalArgumentException("Dimentions mismatch: this=" + this + ", argument=" + p);
        }
        System.arraycopy(p.program, 0, this.program, 0, p.program.length);
    }

    private void validateState(int state) {
        if (state < 1 || state > numStates) {
            throw new IllegalArgumentException("State value out of range:" + state);
        }
    }

    private void validateSymbol(int symbol) {
        if (symbol < 0 || symbol > numSymbols) {
            throw new IllegalArgumentException("Symbol value out of range:" + symbol);
        }
    }

    @Override
    public String toString() {
        return "TMProgramN{" + "numStates=" + numStates + ", numSymbols=" + numSymbols + '}';
    }
}
