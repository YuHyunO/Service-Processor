package lab.processor.core;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Instruction {
    private final String instructionId;
    private String processId;
    private String errorHandlerId;
    @Setter private boolean ignoreError;
    private boolean active = true;

    public Instruction(String instructionId) {
        this.instructionId = instructionId;
    }

    public void setProcessId(String processId) {
        this.processId = processId.trim();
    }

    public void setErrorHandlerId(String errorHandlerId) {
        this.errorHandlerId = errorHandlerId.trim();
    }

}
