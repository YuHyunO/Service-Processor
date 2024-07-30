package lab.processor.core;

import lab.processor.context.ContextData;
import lab.processor.provider.ResourceProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;

import java.util.List;

@Slf4j
public class ServiceProcessor {

    public static ContextData unfoldServices(ContextData contextData) {
        if (contextData == null)
            throw new IllegalArgumentException("ContextData is null");

        Instruction instruction = contextData.getInstruction();
        String operationId = contextData.getOperationId();
        String instructionId = instruction.getInstructionId();
        String processId = instruction.getProcessId();
        String errorHandlerId = instruction.getErrorHandlerId();

        List<Service> services = ResourceProvider.access().getServices(processId);
        if (services == null || services.isEmpty()) {
            log.warn("[{}]No services found for process id {}", operationId, processId);
            return contextData;
        }

        StopWatch sw = StopWatch.createStarted();
        for (Service service : services) {
            try {
                contextData.addServiceTrace(service.getClass());
                if (contextData.isProcessOn()) {
                    log.debug("[{}]Start the service '{}'", operationId, service.getClass());
                    service.process(contextData);
                } else {
                    log.warn("[{}]Service chain is broken. An error may be exist.\nService trace: {}", operationId, contextData.getServiceTraceMessage());
                    break;
                }
            } catch (Throwable t) {
                contextData.addErrorTrace(service.getClass(), t);
                if (instruction.isIgnoreError()) {
                    log.warn("[{}]An error occurred at the service '{}' but ignored.", operationId, service.getClass());
                    continue;
                }
                List<ErrorHandler> errorHandlers = ResourceProvider.access().getErrorHandlers(errorHandlerId);
                if (errorHandlers == null || errorHandlers.isEmpty()) {
                    log.warn("[{}]Error handler for instruction '{}' is not exist. Skip error handling", operationId, instructionId);
                    break;
                }
                for (ErrorHandler errorHandler : errorHandlers) {
                    try {
                        errorHandler.handleError(contextData);
                    } catch (Throwable it) {
                        log.error("[" + operationId + "]An error occurred when handling error. Error handler id:'" + errorHandlerId + "', Error handler class: '" + errorHandler.getClass() + "'"
                                + "Continue error handling process.", it);
                    }
                }

            } finally {
                sw.split();
                log.debug("[{}]End the service '{}'", operationId, service.getClass());
            }
        }

        sw.stop();

        return contextData;
    }


}
