package lab.processor.context;

import lab.processor.core.Instruction;
import lab.processor.core.Service;
import lab.processor.provider.ResourceProvider;
import lab.processor.util.MessageUtil;
import lab.processor.util.TimeUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
public class ContextData {
    private final Instruction instruction;
    @Setter private boolean processOn = true;
    private String operationId;
    private Date startTime;
    private Date endTime;
    @Setter private Throwable throwable;
    private List<Class<? extends Service>> serviceTrace;
    private Map<Class<? extends Service>, Throwable> errorTrace;
    private Map<String, Objects> contextParams;

    public ContextData(Instruction instruction) {
        this.instruction = instruction;
        startTime = new Date();
        serviceTrace = new ArrayList<>();
        errorTrace = new LinkedHashMap<>();
        contextParams = new HashMap<>();
    }

    public void addServiceTrace(Class<? extends Service> service) {
        serviceTrace.add(service);
    }

    public String getServiceTraceMessage() {
        StringBuilder sb = new StringBuilder();
        for (Class service : serviceTrace) {
            sb.append(service.getName());
            sb.append("→");
        }
        if (!sb.isEmpty()) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    public void addErrorTrace(Class<? extends Service> service, Throwable throwable) {
        errorTrace.put(service, throwable);
    }

    public String getErrorTraceMessage() {
        StringBuilder sb = new StringBuilder();
        for (Class service : errorTrace.keySet()) {
            Throwable throwable = errorTrace.get(service);
            sb.append("Error class: ");
            sb.append(service.getName());
            sb.append("\n");
            sb.append("Error trace: ");
            sb.append(MessageUtil.toStringBuf(throwable));
            sb.append("\n");
        }
        if (!sb.isEmpty())
            sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    public void stampEndTime() {
        endTime = new Date();
    }

    public String getOperationId() {
        if (operationId == null) {
            return getOperationId();
        }
        return operationId;
    }

    private String generateOperationId(Instruction instruction, Date startTime) {
        StringBuilder operIdBd = new StringBuilder();
        StringBuilder randomBd = new StringBuilder();
        int randomNum = ResourceProvider.RANDOM_SUPPORTER.nextInt( 999);
        if (randomNum < 0)
            randomNum *= -1;
        if (randomNum < 100) {
            if (randomNum < 10) {
                randomBd.append("00")
                        .append(randomNum);
            } else {
                randomBd.append("0")
                        .append(randomNum);
            }
        } else {
            randomBd.append(randomNum);
        }
        return operIdBd.append(instruction.getProcessId())
                .append("$")
                .append(instruction.getInstructionId())
                .append("_")
                .append(TimeUtil.getFormattedTime(startTime, TimeUtil.YYYYMMDDHHMMSSFFF))
                .append(randomBd).toString();
    }

}
