package com.sliva.turingmachine;

import java.util.function.Consumer;
import org.apache.commons.lang3.NotImplementedException;

public class TMStateEmulator extends TMStateN {

    private final int[] posArray;
    private int step;

    public TMStateEmulator(int[] posArray) {
        super(1, 1, 1, 1);
        this.posArray = posArray;
        step = 0;
    }

    @Override
    public Direction getLastDirection() {
        throw new NotImplementedException();
    }

    @Override
    public int getOldState() {
        throw new NotImplementedException();
    }

    @Override
    public byte getOldSymbol() {
        throw new NotImplementedException();
    }

    @Override
    public int getState() {
        throw new NotImplementedException();
    }

    @Override
    public int getStep() {
        return step;
    }

    @Override
    public int getMaxSteps() {
        throw new NotImplementedException();
    }

    @Override
    public Tape getTape() {
        throw new NotImplementedException();
    }

    @Override
    public TMProgramN getTmProgram() {
        throw new NotImplementedException();
    }

    @Override
    public TMStateN fillFrom(TMStateN t) {
        throw new NotImplementedException();
    }

    @Override
    public TMStateN copy() {
        return new TMStateEmulator(posArray);
    }

    @Override
    public boolean isHaltState() {
        return step >= posArray.length;
    }

    @Override
    public boolean isTapeOutOfRange() {
        return false;
    }

    @Override
    public boolean isMaxStepsReached() {
        return false;
    }

    @Override
    public void reverseTransition() {
        throw new NotImplementedException();
    }

    @Override
    public void runLoop(Consumer<Transition> consumer) {
        super.runLoop(consumer);
    }

    @Override
    public Transition applyTransition() {
        step++;
        return null;
    }

    @Override
    public int getTransitionsCount() {
        throw new NotImplementedException();
    }

    @Override
    public int getLastState() {
        throw new NotImplementedException();
    }

    @Override
    public boolean hasNextTransition() {
        throw new NotImplementedException();
    }

    @Override
    public int getProgramPos() {
        return isHaltState() ? -1 : posArray[step];
    }

    @Override
    public void setNextTransition(Transition t) {
        throw new NotImplementedException();
    }

    @Override
    public Transition getNextTransition() {
        throw new NotImplementedException();
    }

}
