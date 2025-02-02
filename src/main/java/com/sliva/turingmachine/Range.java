package com.sliva.turingmachine;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class Range {

    private final int start;
    private final int end;

    public boolean inRange(int n) {
        return n >= start && n <= end;
    }

    public int getSize() {
        return end - start + 1;
    }
}
