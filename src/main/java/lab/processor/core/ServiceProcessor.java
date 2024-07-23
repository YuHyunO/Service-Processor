package lab.processor.core;

import lab.processor.context.ServiceContextData;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ServiceProcessor {

    public static ServiceContextData unfoldServices(ServiceContextData serviceContextData) {

        List<Service> services = null;
        for (Service service : services) {
            service.process(serviceContextData);
        }

        return serviceContextData;
    }


}
