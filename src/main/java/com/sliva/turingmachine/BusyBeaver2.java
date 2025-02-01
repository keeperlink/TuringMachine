package com.sliva.turingmachine;

import static com.sliva.turingmachine.Transition.SYMBOL_ONE;
import static com.sliva.turingmachine.Transition.SYMBOL_ZERO;
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

/**
 *
 * @author whost
 */
public class BusyBeaver2 {

    private static final int STEP_LIMIT = 20000;
    private static final int TAPE_LIMIT = 200;
    private static final int MAX_WINNERS = 6;
    private static final Duration MAX_RUNTIME = Duration.ofMinutes(1000);
    private static final long startTimestamp = System.currentTimeMillis();

    private static final int numStates = 5;

    private static final List<Transition>[] ALL_TRANSITIONS = generateAllTransitions();
    private static final TMState[] TM_STATE_BUFFER = new TMState[numStates * 2];
    private static final AtomicInteger maxSteps = new AtomicInteger();
    private static final AtomicLong executed = new AtomicLong();
    private static final AtomicLong infLoops = new AtomicLong();
    private static final Map<Integer, AtomicLong> countPerSteps = new TreeMap<>();
    private static final List<TMProgram> winningPrograms = Collections.synchronizedList(new ArrayList<>());
    private static final NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);

    public static void main(String[] args) {
        for (int i = 0; i < TM_STATE_BUFFER.length; i++) {
            TM_STATE_BUFFER[i] = new TMState(numStates, STEP_LIMIT, TAPE_LIMIT);
        }
        TMState tmState = TM_STATE_BUFFER[0];
        for (byte newSymbol = SYMBOL_ZERO; newSymbol <= SYMBOL_ONE; newSymbol++) {
            tmState.setNextTransition(new Transition(newSymbol, Direction.RIGHT, 2));
            tmState.applyTransition();
            recursiveTuringMachine(tmState, 1);
            tmState.reverseTransition();
        }

        System.out.println("executed = " + nf.format(executed.get()) + ", infLoops = " + nf.format(infLoops.get()));

        System.out.println();
        countPerSteps.forEach((k, v) -> System.out.println(k + ": " + nf.format(v)));

        printWinners();

        System.out.println();
        System.out.println("BB(" + numStates + ") = " + nf.format(maxSteps.get()));
    }

    private static void recursiveTuringMachine(TMState tmState, int nTransitions) {
        TMState _tmState = TM_STATE_BUFFER[nTransitions].fillFrom(tmState);
        if (_tmState.hasNextTransition()) {
            do {
                _tmState.applyTransition();
                if (checkFinished(_tmState)) {
                    return;
                }
            } while (_tmState.hasNextTransition());
        }
        loopInitNewTransition(_tmState, nTransitions);
    }

    private static void loopInitNewTransition(TMState tmState, int nTransitions) {
        if (nTransitions == numStates * 2 - 1) {
            //if we are down to last undefined transition, then it must be a HALT.
            tmState.setNextTransition(Transition.HALT);
            tmState.applyTransition();
            checkFinished(tmState);
        } else {
            tmState.setNextTransition(Transition.HALT); //just need to make current state non-null, simce it will affect on getLastState() response
            int loopMaxState = Math.min(tmState.getLastState() + 1, numStates);
            for (int nextState = 1; nextState <= loopMaxState; nextState++) {
                for (Transition t : ALL_TRANSITIONS[nextState]) {
                    tmState.setNextTransition(t);
                    tmState.applyTransition();
                    recursiveTuringMachine(tmState, nTransitions + 1);
                    tmState.reverseTransition();
                }
            }
        }
    }

    private static boolean checkFinished(TMState tmState) {
        if (tmState.isMaxStepsReached()) {
            //inf loop detected
            executed.incrementAndGet();
            infLoops.incrementAndGet();
            return true;
        } else if (tmState.isHaltState()) {
            executed.incrementAndGet();
            checkStats(tmState);
            return true;
        } else {
            return false;
        }
    }

    private static void checkStats(TMState tmState) {
        countPerSteps.computeIfAbsent(tmState.getStep(), k -> new AtomicLong()).incrementAndGet();
        if (tmState.getStep() > maxSteps.get()) {
            maxSteps.set(tmState.getStep());
            winningPrograms.clear();
            System.out.println("  maxSteps = " + maxSteps.get() + ". executed = " + nf.format(executed.get()) + ", infLoops = " + nf.format(infLoops.get()));
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
            for (byte newSymbol = SYMBOL_ZERO; newSymbol <= SYMBOL_ONE; newSymbol++) { // All possible writes
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
            PrintUtils.runAndPrint(new TMState(t, maxSteps.get(), maxSteps.get()), System.out, null);
        });

//        System.out.println("---");
//        TuringMachineWithHistory tm = new TuringMachineWithHistory(STEP_LIMIT);
//        tm.run(winningPrograms.get(0).toTransitionsArray());
//        tm.printHistory();
    }
}
