package com.sliva.turingmachine;

import java.text.NumberFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class BusyBeaverN {

    private static final int STEP_LIMIT = 4_000_000;
    private static final int TAPE_LIMIT = 2100;
    private static final int MAX_WINNERS = 6;
    private static final Duration MAX_RUNTIME = Duration.ofHours(1000);
    private static final long startTimestamp = System.currentTimeMillis();

    private static final int numStates = 2;
    private static final int numSymbols = 4;

    private static final int numTransitions = numStates * numSymbols;
    private static final List<Transition>[] ALL_TRANSITIONS = generateAllTransitions();
    private static final TMStateN[] TM_STATE_BUFFER = new TMStateN[numTransitions];
    private static final AtomicInteger maxSteps = new AtomicInteger();
    private static final AtomicLong executed = new AtomicLong();
    private static final AtomicLong stepsLimitReached = new AtomicLong();
    private static final AtomicLong stepsLimitReachedTimer = new AtomicLong();
    private static final AtomicLong tapeOutOfRange = new AtomicLong();
    private static final AtomicLong tapeOutOfRangeTimer = new AtomicLong();
    private static final Map<Integer, AtomicLong> countPerSteps = new TreeMap<>();
    private static final List<TMProgramN> winningPrograms = Collections.synchronizedList(new ArrayList<>());
    private static final NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);

    public static void main(String[] args) {
        System.out.println("Searching BB(" + numStates + "," + numSymbols + ")...");
        for (int i = 0; i < TM_STATE_BUFFER.length; i++) {
            TM_STATE_BUFFER[i] = new TMStateN(numStates, numSymbols, STEP_LIMIT, TAPE_LIMIT);
        }
        TMStateN tmState = TM_STATE_BUFFER[0];
        for (byte newSymbol = 0; newSymbol < 2; newSymbol++) {
            tmState.setNextTransition(new Transition(newSymbol, Direction.RIGHT, 2));
            tmState.applyTransition();
            recursiveTuringMachine(tmState, 1);
            tmState.reverseTransition();
        }

        System.out.println("executed = " + nf.format(executed.get())
                + ", stepsLimitReached = " + nf.format(stepsLimitReached.get())
                + " (time: " + Duration.ofMillis(stepsLimitReachedTimer.get()) + ")"
                + ", tapeOutOfRange = " + nf.format(tapeOutOfRange.get())
                + " (time: " + Duration.ofMillis(tapeOutOfRangeTimer.get()) + ")");

        System.out.println();
        countPerSteps.forEach((k, v) -> System.out.println(k + ": " + nf.format(v)));

        printWinners();

        System.out.println();
        System.out.println("BB(" + numStates + "," + numSymbols + ") = " + nf.format(maxSteps.get()));
    }

    private static void recursiveTuringMachine(TMStateN tmState, int nTransitions) {
        TMStateN _tmState = TM_STATE_BUFFER[nTransitions].fillFrom(tmState);
        if (_tmState.hasNextTransition()) {
            long timestamp = System.currentTimeMillis();
            do {
                _tmState.applyTransition();
                if (checkFinished(_tmState, timestamp)) {
                    return;
                }
            } while (_tmState.hasNextTransition());
        }
        loopInitNewTransition(_tmState, nTransitions);
    }

    private static void loopInitNewTransition(TMStateN tmState, int nTransitions) {
        if (nTransitions == numTransitions - 1) {
            //if we are down to last undefined transition, then it must be a HALT.
            tmState.setNextTransition(Transition.HALT);
            tmState.applyTransition();
            checkFinished(tmState, System.currentTimeMillis());
        } else {
            tmState.setNextTransition(Transition.HALT); //just need to make current state non-null, simce it will affect on getLastState() response
            int loopMaxState = Math.min(tmState.getLastState() + 1, numStates);
            for (int nextState = 1; nextState <= loopMaxState; nextState++) {
                for (Transition t : ALL_TRANSITIONS[nextState]) {
                    if (t.getNewSymbol() > nTransitions + 1) {
                        //
                        continue;
                    }
                    tmState.setNextTransition(t);
                    tmState.applyTransition();
                    recursiveTuringMachine(tmState, nTransitions + 1);
                    tmState.reverseTransition();
                }
            }
        }
    }

    private static boolean checkFinished(TMStateN tmState, long timeLoopStarts) {
        if (tmState.isMaxStepsReached()) {
            executed.incrementAndGet();
            stepsLimitReached.incrementAndGet();
            stepsLimitReachedTimer.addAndGet(System.currentTimeMillis() - timeLoopStarts);
            return true;
        } else if (tmState.isTapeOutOfRange()) {
            executed.incrementAndGet();
            tapeOutOfRange.incrementAndGet();
            tapeOutOfRangeTimer.addAndGet(System.currentTimeMillis() - timeLoopStarts);
            return true;
        } else if (tmState.isHaltState()) {
            executed.incrementAndGet();
            checkStats(tmState);
            return true;
        } else {
            return false;
        }
    }

    private static void checkStats(TMStateN tmState) {
        countPerSteps.computeIfAbsent(tmState.getStep(), k -> new AtomicLong()).incrementAndGet();
        if (tmState.getStep() > maxSteps.get()) {
            maxSteps.set(tmState.getStep());
            winningPrograms.clear();
            System.out.println("  maxSteps = " + nf.format(maxSteps.get())
                    + ". executed = " + nf.format(executed.get())
                    + ", stepsLimitReached = " + nf.format(stepsLimitReached.get())
                    + " (time: " + Duration.ofMillis(stepsLimitReachedTimer.get()) + ")"
                    + ", tapeOutOfRange = " + nf.format(tapeOutOfRange.get())
                    + " (time: " + Duration.ofMillis(tapeOutOfRangeTimer.get()) + ")"
                    + ". Total runtime: " + Duration.ofMillis(System.currentTimeMillis() - startTimestamp) + ".");
        }
        if (tmState.getStep() == maxSteps.get() && winningPrograms.size() < MAX_WINNERS) {
            winningPrograms.add(tmState.getTmProgram().copy());
        }
        if (System.currentTimeMillis() - startTimestamp > MAX_RUNTIME.toMillis()) {
            System.out.println("Max time reached: " + MAX_RUNTIME);
            System.exit(0);
        }
    }

    private static List<Transition>[] generateAllTransitions() {
        List<Transition>[] result = new List[numStates + 1];
        for (int nextState = 1; nextState <= numStates; nextState++) { // All next states
            List<Transition> list = new ArrayList<>();
            for (byte newSymbol = 0; newSymbol < numSymbols; newSymbol++) { // All possible writes
                for (Direction dir : Direction.values()) { // All directions
                    list.add(new Transition(newSymbol, dir, nextState));
                }
            }
            result[nextState] = list;
        }
        return result;
    }

    private static void printWinners() {
        System.out.println();
        System.out.println("winningPrograms: " + winningPrograms.size());
        winningPrograms.forEach(t -> {
            System.out.println();
            PrintUtils.printTransisitons(t, System.out);
            System.out.println();
            PrintUtils.runAndPrint(new TMStateN(t, maxSteps.get(), maxSteps.get()), System.out, null);
            System.out.println("^^^");
            PrintUtils.printTransisitons(t, System.out);
            System.out.println();
        });
    }
}
