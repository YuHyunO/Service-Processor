package lab.processor.core;

import lombok.Getter;

@Getter
public class Instruction {
    private final String instructionId;
    private String processId;
    private boolean active = true;

    public Instruction(String instructionId) {
        this.instructionId = instructionId;
    }

}
