package lab.processor.core;

import lab.processor.context.ContextData;
import lab.processor.provider.ResourceProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;

import java.util.Date;
import java.util.List;

@Slf4j
public class ServiceProcessor {

    public static ContextData unfoldServices(ContextData contextData) {
        if (contextData == null)
            throw new IllegalArgumentException("ContextData is null");

        //* Get a process instruction from ContextData.
        Instruction instruction = contextData.getInstruction();
        String operationId = contextData.getOperationId();
        String instructionId = instruction.getInstructionId();
        String processId = instruction.getProcessId();
        String errorHandlerId = instruction.getErrorHandlerId();

        //* Instruction object has only the ID of the services(service strategy) to be processed
        //* ResourceProvider provides the access to a services(service strategy) through a service ID(process ID)
        List<Service> services = ResourceProvider.access().getServices(processId);
        if (services == null || services.isEmpty()) {
            log.warn("[{}]No services found for process id {}", operationId, processId);
            return contextData;
        }

        StopWatch sw = StopWatch.createStarted();
        int serviceCount = services.size();
        int c1 = 0;
        for (Service service : services) {
            Class serviceClass = service.getClass();
            try {
                //* For convenience of debugging, records the service trace of the strategy.
                contextData.addServiceTrace(serviceClass);
                //* If the processOn flag of the context data set to false, the strategy cease the process immediately.
                //* Else it continues all the processes.
                if (contextData.isProcessOn()) {
                    ++c1;
                    log.debug("[{}]Start the service '{}'({}/{})", operationId, serviceClass, c1, serviceCount);
                    service.process(contextData);
                } else {
                    log.warn("[{}]Service chain is broken. An error may be exist.({}/{})\nService trace: {}", operationId, c1, serviceCount, contextData.getServiceTraceMessage());
                    break;
                }
            } catch (Throwable t) {
                //* When an exception occurred, add an error trace and its class.
                contextData.addErrorTrace(serviceClass, t);
                //* Continue the process even when error occurred, if an instruction's ignore error option is true.
                if (instruction.isIgnoreError()) {
                    log.warn("[{}]An error occurred at the service '{}' but ignored.({}/{})", operationId, serviceClass, c1, serviceCount);
                    continue;
                }
                //* Resource provider also provides the access of the retrieval of error handling strategies.
                List<ErrorHandler> errorHandlers = ResourceProvider.access().getErrorHandlers(errorHandlerId);
                if (errorHandlers == null || errorHandlers.isEmpty()) {
                    log.warn("[{}]Error handler for instruction '{}' is not exist. Skip error handling", operationId, instructionId);
                    break;
                }
                int errorHandlerCount = errorHandlers.size();
                int c2 = 0;
                for (ErrorHandler errorHandler : errorHandlers) {
                    ++c2;
                    Class errorHandlerClass = errorHandler.getClass();
                    try {
                        log.warn("[{}]Start the error handler '{}'({}/{})", operationId, errorHandlerClass, c2, errorHandlerCount);
                        errorHandler.handleError(contextData);
                    } catch (Throwable it) {
                        log.error("[" + operationId + "]An error occurred when handling error.(" + c2 + "/" + errorHandlerCount
                                + ") Error handler id:'" + errorHandlerId + "', Error handler class: '" + errorHandlerClass + "'"
                                + "Continue error handling process.", it);
                        //* For process another error handling process, error occurred during the error handling is ignored.
                    }
                }
            } finally {
                sw.split();
                //* Set the end time at each end of service
                contextData.stampEndTime();
                log.debug("[{}]End the service '{}'", operationId, service.getClass());
            }
        }

        sw.stop();

        return contextData;
    }


}
