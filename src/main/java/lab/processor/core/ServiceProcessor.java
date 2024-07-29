package lab.processor.core;

import lab.processor.context.ContextData;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ServiceProcessor {

    public static ContextData unfoldServices(ContextData contextData) {
        if (contextData == null)
            throw new IllegalArgumentException("ContextData is null");

        List<Service> services = null;
        for (Service service : services) {
            contextData.addServiceTrace(service.getClass());
            if (contextData.isProcessOn()) {
                service.process(contextData);
            } else {
                log.warn("");
            }
        }

        return contextData;
    }


}
