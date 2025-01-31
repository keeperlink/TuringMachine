package com.sliva.turingmachine;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author whost
 */
@AllArgsConstructor
@Getter
public class TuringMachineState {

    private final int step;
    private final byte currentState;
    private final int headPosition;
    private final byte[] tape;

    @Override
    public String toString() {
        return StringUtils.leftPad(Integer.toString(step), 5) + ": currentState=" + currentState + ", headPosition=" + headPosition + ", tape=" + tape + '}';
    }

}
