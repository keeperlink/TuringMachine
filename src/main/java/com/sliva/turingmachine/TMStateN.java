package com.sliva.turingmachine;

import static com.sliva.turingmachine.Transition.HALT_STATE;
import static com.sliva.turingmachine.Transition.STARTING_STATE;
import java.util.function.Consumer;
import lombok.Getter;

@Getter
public class TMStateN {

    private final TMProgramN tmProgram;
    private final Tape tape;
    private final int maxSteps;
    private int step;
    private int state;
    private byte oldSymbol;
    private int oldState;
    private Direction lastDirection;

    public TMStateN(int numStates, int numSymbols, int maxSteps, int maxTapeShift) {
        this.tmProgram = new TMProgramN(numStates, numSymbols);
        this.tape = new Tape(maxTapeShift);
        this.maxSteps = maxSteps;
        this.step = 0;
        this.state = STARTING_STATE;
    }

    public TMStateN(TMProgramN tmProgram, int maxSteps, int maxTapeShift) {
        this.tmProgram = tmProgram;
        this.tape = new Tape(maxTapeShift);
        this.maxSteps = maxSteps;
        this.step = 0;
        this.state = STARTING_STATE;
    }

    private TMStateN(TMProgramN tmProgram, Tape tape, int maxSteps, int step, int state) {
        this.tmProgram = tmProgram;
        this.tape = tape;
        this.step = step;
        this.maxSteps = maxSteps;
        this.state = state;
    }

    public Transition getNextTransition() {
        return tmProgram.getTransition(state, tape.getSymbol());
    }

    public void setNextTransition(Transition t) {
        tmProgram.setTransition(state, tape.getSymbol(), t);
    }

    public int getProgramPos() {
        return isHaltState() ? -1 : tmProgram.getPos(state, tape.getSymbol());
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

    public Transition applyTransition() {
        Transition t = getNextTransition();
        if (t == null) {
            throw new IllegalStateException("Next transition has not been defined");
        }
        oldSymbol = tape.getSymbol();
        lastDirection = t.getDirection();
        oldState = state;

        tape.setSymbol(t.getNewSymbol());
        tape.moveHead(t.getDirection());
        state = t.getNextState();
        step++;
        return t;
    }

    public void runLoop(Consumer<Transition> consumer) {
        while (!isHaltState() && !isMaxStepsReached() && !isTapeOutOfRange()) {
            Transition t = applyTransition();
            if (consumer != null) {
                consumer.accept(t);
            }
        }
    }

    public void reverseTransition() {
        if (oldState == HALT_STATE) {
            throw new IllegalStateException("Old state value is undefined");
        }
        state = oldState;
        tape.moveHead(lastDirection == Direction.LEFT ? Direction.RIGHT : Direction.LEFT);
        tape.setSymbol(oldSymbol);
        step--;
        oldState = HALT_STATE;
    }

    public boolean isMaxStepsReached() {
        return step >= maxSteps;
    }

    public boolean isTapeOutOfRange() {
        return tape.isHeadOutOfRange();
    }

    public boolean isHaltState() {
        return state == HALT_STATE;
    }

    public TMStateN copy() {
        return new TMStateN(tmProgram.copy(), tape.copy(), maxSteps, step, state);
    }

    public TMStateN fillFrom(TMStateN t) {
        this.tmProgram.fillFrom(t.getTmProgram());
        this.tape.fillFrom(t.tape);
        this.step = t.step;
        this.state = t.state;
        this.oldState = t.oldState;
        this.lastDirection = t.lastDirection;
        this.oldSymbol = t.oldSymbol;
        return this;
    }
}
