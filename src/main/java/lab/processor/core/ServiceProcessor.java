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

        String operationId = contextData.getOperationId();
        String processId = contextData.getInstruction().getProcessId();

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

            } finally {
                sw.split();
                log.debug("[{}]End the service '{}'", operationId, service.getClass());
            }
        }
        sw.stop();

        return contextData;
    }


}
