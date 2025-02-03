package com.sliva.turingmachine;

import static com.sliva.turingmachine.PrintUtils.getFinalTape;
import static com.sliva.turingmachine.PrintUtils.stateSymbolToString;
import static com.sliva.turingmachine.PrintUtils.stateToLetter;
import static com.sliva.turingmachine.PrintUtils.toStringTape;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

public class RunTuringMachine {

    private static final int STEP_LIMIT = 100_000_000;
    private static final int TAPE_LIMIT = 100_000;

    public static void main(String[] args) throws Exception {
        String bb4 = """
                      \tA\tB\tC\tD
                      0\t1RB\t1LA\t1RH\t1RD
                      1\t1LB\t0LC\t1LD\t0RA
                      """;
        String bb5 = """
                     \tA\tB\tC\tD\tE
                     0\t1RB\t1RC\t1RD\t1LA\t1RH
                     1\t1LC\t1RB\t0LE\t1LD\t0LA
                     """;
        String bb2_3 = """
                      \tA\tB
                      0\t1RB\t2LA
                      1\t2LB\t2RB
                      2\t1RH\t1LB
                      """;
        String bb2_4 = """
                      \tA\tB
                      0\t1RB\t1LB
                      1\t2LA\t1LA
                      2\t1RA\t3RB
                      3\t1RA\t1RH
                      """;
        TMProgramN tmProgram = ParsingUtils.readProgram(bb2_4);
        String outputFile = "output.txt";
        //String outputFile = null;

        PrintUtils.printTransisitons(tmProgram, System.out);

        TMStateN tmState = new TMStateN(tmProgram, STEP_LIMIT, TAPE_LIMIT);

        try ( var out = StringUtils.isBlank(outputFile) ? System.out : new PrintStream(new FileOutputStream(outputFile))) {
            PrintUtils.printTransisitonsTable(tmProgram, out);
            runAndPrintResultTape(tmState.copy(), out);
            List<Range> repeatRanges = TMUtls.getRepeatingRanges(tmState.copy(), 1);
            System.out.println("repeatRanges=" + repeatRanges);
            List<PatternRangeToExclude> exGroups = TMUtls.getRepeatingGroups(tmState.copy(), 1).stream()
                    .map(g -> new PatternRangeToExclude(g.getProgramPosGroup(),
                    g.getSize() / g.getGroupSize(),
                    g.getRangeList().get(g.getStart() + g.getGroupSize()).getStart(),
                    g.getRangeList().get(g.getEnd() - g.getGroupSize()).getEnd(),
                    new Range(g.getRangeList().get(g.getStart()).getStart(), g.getRangeList().get(g.getEnd()).getEnd())))
                    .toList();
            System.out.println("exGroups=" + exGroups);

            Tape finalTape = getFinalTape(tmState.copy());
            out.println("finalTape: " + finalTape);
            out.println();
            out.println("    step trans   group    " + StringUtils.rightPad("tape", finalTape.getUsedSize() * 3) + "  next");
            while (!tmState.isFinshed()) {
                Transition t = tmState.applyTransition();
                Optional<PatternRangeToExclude> grp = exGroups.stream().filter(r -> r.inRange(tmState.getStep()-1)).findFirst();
                if (grp.isPresent()) {
                    PatternRangeToExclude exg = grp.get();
                    if (!exg.isPrinted()) {
                        System.out.println("exg="+exg);
                        out.println("    Group#" + exg.getUniqueGroupId() + " (" + exg.getProgramPosGroup().stream().map(pp -> progPosToString(pp, tmProgram.getNumSymbols())).collect(Collectors.joining(",")) + ") x " + exg.getRepeats());
                        exg.setPrinted(true);
                    }
                } else if (!repeatRanges.stream().anyMatch(r -> r.inRange(tmState.getStep()-1))) {
                    out.println(StringUtils.leftPad(Integer.toString(tmState.getStep()), 8) + " "
                            + t.toShortString() + " "
                            + stateSymbolToString(tmState.getOldState(), tmState.getOldSymbol()) + "  "
                            + StringUtils.rightPad(exGroups.stream().filter(r -> r.getFull().inRange(tmState.getStep()-1)).findFirst().map(r -> "Group#" + r.getUniqueGroupId()).orElse(""), 9)
                            + toStringTape(tmState.getTape(), finalTape.getMinPos(), finalTape.getMaxPos()) + "  "
                            + (tmState.getState() == 0 ? "" : tmState.getNextTransition().toShortString() + " "
                            + stateSymbolToString(tmState.getState(), tmState.getTape().getSymbol())));
                }
            }
        }
    }

    private static String progPosToString(int pp, int numSymbols) {
        return String.valueOf(stateToLetter(pp / numSymbols + 1)) + (pp % numSymbols);
    }

    private static void runAndPrintResultTape(TMStateN tms, PrintStream out) {
        tms.runLoop(null);
        int steps = tms.getStep();
        Tape finalTape = tms.getTape();
        out.println("Steps=" + nf.format(steps) + "; Tape: " + finalTape);
        out.println("Final Tape Content:");
        out.println(PrintUtils.toStringTape(finalTape));
    }

    @Getter
    @ToString(callSuper = true)
    public static class PatternRangeToExclude extends Range {

        private final List<Integer> programPosGroup;
        private final int uniqueGroupId;
        private final int repeats;
        private final Range full;
        @Setter
        private boolean printed;

        public PatternRangeToExclude(List<Integer> programPosGroup, int repeats, int start, int end, Range full) {
            super(start, end);
            this.programPosGroup = programPosGroup;
            this.uniqueGroupId = uniqueGroups.computeIfAbsent(programPosGroup, t -> uniqueGroups.size());
            this.repeats = repeats;
            this.full = full;
        }
        private static final Map<List<Integer>, Integer> uniqueGroups = new HashMap<>();
    }
    private static final NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
}
