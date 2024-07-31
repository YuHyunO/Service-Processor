package lab.processor.service;

import lab.processor.context.ContextData;
import lab.processor.core.ErrorHandler;
import lab.processor.core.Instruction;
import lab.processor.core.Service;
import lab.processor.provider.ResourceProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;

import java.util.List;

@Slf4j
public class InnerAsyncServiceProcessor implements Service {

    private List<Service> services;
    private List<ErrorHandler> errorHandlers;

    @Override
    public void process(ContextData contextData) {
        log.debug("Start the inner asynchronous service processing");
        Thread.startVirtualThread(new InnerProcessWorker(contextData, services, errorHandlers));
    }


    private class InnerProcessWorker implements Runnable {
        private final List<Service> services;
        private final List<ErrorHandler> errorHandlers;
        private final ContextData contextData;

        private InnerProcessWorker(ContextData contextData, List<Service> services, List<ErrorHandler> errorHandlers) {
            this.services = services;
            this.errorHandlers = errorHandlers;
            this.contextData = contextData;
        }

        @Override
        public void run() {
            doAsyncWork(contextData, services, errorHandlers);
        }

        static void doAsyncWork(ContextData contextData, List<Service> services, List<ErrorHandler> errorHandlers) {
            if (contextData == null)
                throw new IllegalArgumentException("ContextData is null");

            Instruction instruction = contextData.getInstruction();
            String operationId = contextData.getOperationId();
            String instructionId = instruction.getInstructionId();

            if (services == null || services.isEmpty()) {
                log.warn("[InnerAsync][{}]No services found for inner process", operationId);
                return;
            }

            int serviceCount = services.size();
            int c1 = 0;
            for (Service service : services) {
                Class serviceClass = service.getClass();
                try {
                    contextData.addServiceTrace(serviceClass);
                    if (contextData.isProcessOn()) {
                        ++c1;
                        log.debug("[InnerAsync][{}]Start the service '{}'({}/{})", operationId, serviceClass, c1, serviceCount);
                        service.process(contextData);
                    } else {
                        log.warn("[InnerAsync][{}]Service chain is broken. An error may be exist.({}/{})\nService trace: {}", operationId, c1, serviceCount, contextData.getServiceTraceMessage());
                        break;
                    }
                } catch (Throwable t) {
                    contextData.addErrorTrace(serviceClass, t);
                    if (instruction.isIgnoreError()) {
                        log.warn("[InnerAsync][{}]An error occurred at the service '{}' but ignored.({}/{})", operationId, serviceClass, c1, serviceCount);
                        continue;
                    }
                    if (errorHandlers == null || errorHandlers.isEmpty()) {
                        log.warn("[InnerAsync][{}]Error handler for instruction '{}' is not exist. Skip error handling", operationId, instructionId);
                        break;
                    }
                    int errorHandlerCount = errorHandlers.size();
                    int c2 = 0;
                    for (ErrorHandler errorHandler : errorHandlers) {
                        ++c2;
                        Class errorHandlerClass = errorHandler.getClass();
                        try {
                            log.warn("[InnerAsync][{}]Start the error handler '{}'({}/{})", operationId, errorHandlerClass, c2, errorHandlerCount);
                            errorHandler.handleError(contextData);
                        } catch (Throwable it) {
                            log.error("[InnerAsync][" + operationId + "]An error occurred when handling error.(" + c2 + "/" + errorHandlerCount
                                    + ") Continue error handling process.", it);
                            //* For process another error handling process, error occurred during error handling is ignored.
                        }
                    }
                } finally {
                    contextData.stampEndTime();
                    log.debug("[InnerAsync][{}]End the service '{}'", operationId, service.getClass());
                }
            }
        }

    }

}
