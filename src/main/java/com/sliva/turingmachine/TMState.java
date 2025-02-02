package com.sliva.turingmachine;

import static com.sliva.turingmachine.Transition.HALT_STATE;
import static com.sliva.turingmachine.Transition.STARTING_STATE;
import java.util.Arrays;
import lombok.Getter;

@Getter
public class TMState {

    private final TMProgram tmProgram;
    private final byte[] tape;
    private final int maxSteps;
    private int step;
    private int state;
    private int posTape;
    private byte oldSymbol;
    private int oldState;
    private int oldPosTape;

    public TMState(int numStates, int maxSteps, int maxTapeShift) {
        this.tmProgram = new TMProgram(numStates);
        this.tape = new byte[maxTapeShift * 2];
        this.maxSteps = maxSteps;
        this.step = 0;
        this.state = STARTING_STATE;
        this.posTape = maxTapeShift;
    }

    public TMState(TMProgram tmProgram, int maxSteps, int maxTapeShift) {
        this.tmProgram = tmProgram;
        this.tape = new byte[maxTapeShift * 2];
        this.maxSteps = maxSteps;
        this.step = 0;
        this.state = STARTING_STATE;
        this.posTape = maxTapeShift;
    }

    public TMState(TMProgram tmProgram, byte[] tape, int maxSteps, int step, int state, int posTape) {
        this.tmProgram = tmProgram;
        this.tape = tape;
        this.step = step;
        this.maxSteps = maxSteps;
        this.state = state;
        this.posTape = posTape;
    }

    public Transition getNextTransition() {
        return tmProgram.getTransition(state, tape[posTape]);
    }

    public void setNextTransition(Transition t) {
        tmProgram.setTransition(state, tape[posTape], t);
    }

    public boolean hasNextTransition() {
        return getNextTransition() != null;
    }

    public int getLastState() {
        return tmProgram.getLastState();
    }

    public int getTransitionsCount() {
        return tmProgram.getTransitionsCount();
    }

    public void applyTransition() {
        Transition t = getNextTransition();
        if (t == null) {
            throw new IllegalStateException("Next transition has not been defined");
        }
        oldSymbol = tape[posTape];
        oldPosTape = posTape;
        oldState = state;

        tape[posTape] = t.getNewSymbol();
        if (t.getDirection() == Direction.LEFT) {
            posTape--;
        } else {
            posTape++;
        }
        state = t.getNextState();
        step++;
    }

    public void reverseTransition() {
        if (oldState == HALT_STATE) {
            throw new IllegalStateException("Old state value is undefined");
        }
        state = oldState;
        posTape = oldPosTape;
        tape[posTape] = oldSymbol;
        step--;
        oldState = HALT_STATE;
    }

    public boolean isMaxStepsReached() {
        return step >= maxSteps || posTape < 0 || posTape >= tape.length;
    }

    public boolean isHaltState() {
        return state == HALT_STATE;
    }

    public TMState copy() {
        return new TMState(tmProgram.copy(), Arrays.copyOf(tape, tape.length), maxSteps, step, state, posTape);
    }

    public TMState fillFrom(TMState t) {
        tmProgram.fillFrom(t.getTmProgram());
        System.arraycopy(t.tape, 0, this.tape, 0, t.tape.length);
        this.step = t.step;
        this.state = t.state;
        this.posTape = t.posTape;
        this.oldState = HALT_STATE;
        return this;
    }
}
