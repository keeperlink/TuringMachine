package com.sliva.turingmachine;

import static com.sliva.turingmachine.Transition.STARTING_STATE;
import static com.sliva.turingmachine.Transition.SYMBOL_ONE;
import static com.sliva.turingmachine.Transition.SYMBOL_ZERO;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;

public class BusyBeaver {

    private static final int STEP_LIMIT = 200;
    private static final int MAX_WINNING_TRANSITIONS = 20;
    private static final NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
    private static final Map<Integer, AtomicLong> countPerSteps = new ConcurrentHashMap<>();
    private static final List<Transition[]> winningTransitions = Collections.synchronizedList(new ArrayList<>());

    private static final int numStates = 4;

    private static final AtomicInteger maxSteps = new AtomicInteger();
    private static final AtomicLong executed = new AtomicLong();
    private static final AtomicLong skipped = new AtomicLong();
    private static final AtomicLong infLoops = new AtomicLong();

    public static void main(String[] args) {

        // Generate all transitions for each (state, readSymbol) pair
        List<Transition> allTrans = generateAllTransitions(false);
        List<Transition> allTransWithHalt = generateAllTransitions(true);
        System.out.println("allTrans.size=" + allTrans.size() + ", allTransWithHalt.size=" + allTransWithHalt.size());

        // Iterate through all possible Turing machines (transition tables)
        _execOneMultiThreaded(allTrans, allTransWithHalt);

        System.out.println("executed = " + nf.format(executed.get())
                + ", skipped = " + nf.format(skipped.get())
                + ", total = " + nf.format(executed.get() + skipped.get())
                + ", infLoops = " + nf.format(infLoops.get()));

        System.out.println();
        countPerSteps.forEach((k, v) -> System.out.println(k + ": " + nf.format(v)));

        System.out.println();
        System.out.println("winningTransitions: " + winningTransitions.size());
        TuringMachineWithHistory tm = new TuringMachineWithHistory(STEP_LIMIT);
        winningTransitions.forEach(t -> {
            System.out.println();
            printTransisitons(t);
            System.out.println();
            tm.run(t);
            tm.printHistory();
        });

        System.out.println("---");
        PrintUtils.runAndPrint(new TMState(new TMProgram(winningTransitions.get(0)), maxSteps.get(), maxSteps.get()));

        System.out.println();
        System.out.println("BB(" + numStates + ") = " + nf.format(maxSteps.get()));
    }

    private static void _execOneMultiThreaded(List<Transition> allTrans, List<Transition> allTransWithHalt) {
        final int threads = Runtime.getRuntime().availableProcessors();
        System.out.println("threads=" + threads);
        final ExecutorService executor = Executors.newFixedThreadPool(threads);
        final ExecutionContext context = new ExecutionContext(allTrans, allTransWithHalt);

        // Submit all tasks to thread pool
        allTrans.stream()
                //Filter out cases: 1. 1:0 stage cannot halt; 2. should transition to either stage 2 or last(halting) stage; 3. head goes to right (since left move will be a mirror)
                .filter(sZero -> (sZero.getNextState() == STARTING_STATE + 1 || sZero.getNextState() == numStates) && sZero.getDirection() == Direction.RIGHT)
                .forEach(sZero -> allTransWithHalt.stream()
                //Exclude jump to last stage if it's not a halting stage
                .filter(sOne -> numStates <= 2 || !sOne.isHalt() || sZero.getNextState() != numStates)
                .forEach(sOne -> executor.execute(() -> context.execute(sZero, sOne)))
                );

        // Graceful shutdown with error handling
        executor.shutdown();
        try {
            if (!executor.awaitTermination(1, TimeUnit.HOURS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException ex) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private static class ExecutionContext {

        private final ThreadLocal<LoopExecutor> loopExecutorThreadLocal;

        public ExecutionContext(List<Transition> allTrans, List<Transition> allTransWithHalt) {
            loopExecutorThreadLocal = ThreadLocal.withInitial(() -> new LoopExecutor(allTrans, allTransWithHalt));
        }

        public void execute(Transition sZero, Transition sOne) {
            LoopExecutor loopExecutor = loopExecutorThreadLocal.get();
            loopExecutor.run(sZero, sOne);
        }
    }

    @RequiredArgsConstructor
    private static class LoopExecutor {

        private final TuringMachine turingMachine = new TuringMachine(STEP_LIMIT);
        private final Transition[] transitions = new Transition[numStates * 2];
        private final List<Transition> allTrans;
        private final List<Transition> allTransWithHalt;

        public void run(Transition sZero, Transition sOne) {
            transitions[0] = sZero;
            transitions[1] = sOne;
            _execute(2);
        }

        private void _execute(int state) {
            List<Transition> _allTrans = state < numStates || transitions[1].isHalt() ? allTrans : allTransWithHalt;
            for (Transition sZero : _allTrans) {
                transitions[state * 2 - 2] = sZero;
                for (Transition sOne : _allTrans) {
                    transitions[state * 2 - 1] = sOne;
                    if (state < numStates) {
                        _execute(state + 1);
                    } else {
                        if (!transitions[1].isHalt() && (sZero.isHalt() == sOne.isHalt())) {
                            //last transition should have one halt
                            //skipped.incrementAndGet();
                        } else {
                            runMachine();
                        }
                    }
                }
            }
        }

        private void runMachine() {
            int steps = turingMachine.run(transitions);

            countPerSteps.computeIfAbsent(steps, k -> new AtomicLong()).incrementAndGet();

            if (steps == -1) {
                infLoops.incrementAndGet();
            } else if (steps > maxSteps.get()) {
                maxSteps.set(steps);
                winningTransitions.clear();
                winningTransitions.add(Arrays.copyOf(transitions, transitions.length));
                System.out.println("   maxSteps = " + nf.format(maxSteps.get())
                        + ", executed = " + nf.format(executed.get())
                        + ", skipped = " + nf.format(skipped.get())
                        + ", total = " + nf.format(executed.get() + skipped.get())
                        + ", infLoops = " + nf.format(infLoops.get()));
            } else if (steps == maxSteps.get()) {
                if (winningTransitions.size() < MAX_WINNING_TRANSITIONS) {
                    winningTransitions.add(Arrays.copyOf(transitions, transitions.length));
                }
            }
            executed.incrementAndGet();
        }
    }

    private static List<Transition> generateAllTransitions(boolean withHaltState) {
        List<Transition> list = new ArrayList<>();
        // Generate all possible transitions for (currentState, readSymbol)
        for (byte newSymbol = SYMBOL_ZERO; newSymbol <= SYMBOL_ONE; newSymbol++) { // All possible writes
            for (Direction dir : Direction.values()) { // All directions
                for (int nextState = 1; nextState <= numStates; nextState++) { // All next states
                    list.add(new Transition(newSymbol, dir, nextState));
                }
            }
        }
        if (withHaltState) {
            list.add(Transition.HALT);
        }
        return list;
    }

    private static void printTransisitons(Transition[] transitions) {
        for (int state = 1; state <= transitions.length / 2; state++) {
            for (int symbol = 0; symbol <= 1; symbol++) {
                System.out.println("" + state + ":" + symbol + "  " + transitions[state * 2 - 2 + symbol]);
            }
        }
    }
}
