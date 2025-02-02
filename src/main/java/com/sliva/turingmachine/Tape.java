package com.sliva.turingmachine;

import java.util.Arrays;
import lombok.Getter;

/**
 *
 * @author whost
 */
@Getter
public class Tape {

    private final byte[] tape;
    @Getter
    private int head;
    @Getter
    private int minPos;
    @Getter
    private int maxPos;

    public Tape(int maxTapeShift) {
        this.tape = new byte[maxTapeShift * 2];
        this.head = maxTapeShift;
        this.minPos = maxTapeShift;
        this.maxPos = maxTapeShift;
    }

    private Tape(byte[] tape, int head, int minPos, int maxPos) {
        this.tape = tape;
        this.head = head;
        this.minPos = minPos;
        this.maxPos = maxPos;
    }

    public byte getSymbol() {
        return tape[head];
    }

    public void setSymbol(byte symbol) {
        tape[head] = symbol;
    }

    public void moveHead(Direction d) {
        if (d == Direction.LEFT) {
            head--;
            if (head >= 0 && head < minPos) {
                minPos = head;
            }
        } else {
            head++;
            if (head > maxPos && head < tape.length) {
                maxPos = head;
            }
        }
    }

    public boolean isHeadOutOfRange() {
        return head < 0 || head >= tape.length;
    }

    public Tape copy() {
        return new Tape(Arrays.copyOf(tape, tape.length), head, minPos, maxPos);
    }

    public Tape fillFrom(Tape t) {
        System.arraycopy(t.tape, 0, this.tape, 0, t.tape.length);
        this.head = t.head;
        this.minPos = t.minPos;
        this.maxPos = t.maxPos;
        return this;
    }
}
