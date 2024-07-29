package lab.processor.context;

import lab.processor.core.Instruction;
import lab.processor.core.Service;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ContextData {
    private final Instruction instruction;
    @Setter
    private boolean processOn = true;
    private Throwable throwable;
    private List<Class<? extends Service>> serviceTrace;

    public ContextData(Instruction instruction) {
        this.instruction = instruction;
        serviceTrace = new ArrayList<>();
    }


    public void addServiceTrace(Class<? extends Service> service) throws IllegalArgumentException {
        serviceTrace.add(service);
    }

}
