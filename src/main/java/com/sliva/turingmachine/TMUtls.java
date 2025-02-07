package com.sliva.turingmachine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public final class TMUtls {

    public static List<Range> getRepeatingRanges(TMStateN tms, int includeRepeats) {
        List<Range> result = new ArrayList<>();
        AtomicInteger prevProgramPos = new AtomicInteger(tms.getProgramPos());
        AtomicInteger startRepeating = new AtomicInteger(tms.getStep());
        tms.runLoop(t -> {
            if (tms.getProgramPos() != prevProgramPos.get()) {
                if (tms.getStep() - startRepeating.get() > includeRepeats * 3) {
                    result.add(new Range(startRepeating.get() + includeRepeats, tms.getStep() - includeRepeats - 1));
                }
                prevProgramPos.set(tms.getProgramPos());
                startRepeating.set(tms.getStep());
            }
        });
        return result;
    }

    public static List<ProgramPosGroups> getRepeatingGroups(TMStateN tms, int includeRepeats) {
        List<ProgramPosGroups> result = new ArrayList<>();
        List<ProgramPosWithRange> hist = getCompactHistory(tms);

        for (int groupSize = 2; groupSize < 15; groupSize++) {
            LinkedList<Integer> prevProgramPos = new LinkedList<>(Collections.nCopies(groupSize, -1));
            int startRepeating = 0;
            for (int h = 0; h < hist.size(); h++) {
                ProgramPosWithRange pp = hist.get(h);
                int hh = h;
                if (pp.getProgramPos() != prevProgramPos.get(0)) {
                    if (h - startRepeating > groupSize * (includeRepeats * 2 + 1)
                            && result.stream().noneMatch(p -> p.inRange(hh - 2))) {
                        int groupStart = startRepeating - groupSize + 1;
                        int groupEnd = h - 1;
                        int size = groupEnd - groupStart + 1;
                        for (int i = 0; i < size % groupSize; i++) {
                            groupEnd--;
                            prevProgramPos.addFirst(prevProgramPos.removeLast());
                        }
                        result.add(new ProgramPosGroups(new ArrayList<>(prevProgramPos),
                                groupStart, groupEnd, hist));
                        Collections.fill(prevProgramPos, -1);
                    }
                    startRepeating = h;
                }
                prevProgramPos.addLast(pp.getProgramPos());
                prevProgramPos.removeFirst();
            }
        }
        return result;
    }

    public static List<ProgramPosWithRange> getCompactHistory(TMStateN tms) {
        List<ProgramPosWithRange> result = new ArrayList<>();
        AtomicInteger prevProgramPos = new AtomicInteger(tms.getProgramPos());
        AtomicInteger startRepeating = new AtomicInteger(tms.getStep());
        tms.runLoop(t -> {
            if (tms.getProgramPos() != prevProgramPos.get()) {
                result.add(new ProgramPosWithRange(prevProgramPos.get(), startRepeating.get(), tms.getStep() - 1));
                prevProgramPos.set(tms.getProgramPos());
                startRepeating.set(tms.getStep());
            }
        });
        return result;
    }

    @Getter
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = true)
    public static class ProgramPosWithRange extends Range {

        private final int programPos;

        public ProgramPosWithRange(int programPos, int start, int end) {
            super(start, end);
            this.programPos = programPos;
        }

    }

    @Getter
    @ToString(callSuper = true, exclude = "rangeList")
    @EqualsAndHashCode(callSuper = true)
    public static class ProgramPosGroups extends Range {

        private final List<Integer> programPosGroup;
        private final List<ProgramPosWithRange> rangeList;

        public ProgramPosGroups(List<Integer> programPosGroup, int start, int end, List<ProgramPosWithRange> rangeList) {
            super(start, end);
            this.programPosGroup = programPosGroup;
            this.rangeList = rangeList;
        }

        public int getGroupSize() {
            return programPosGroup.size();
        }
    }
}
