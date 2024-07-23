package lab.processor.context;

import lombok.Data;

@Data
public class ServiceContextData {
    private final String serviceName;

    public ServiceContextData(String serviceName) {
        this.serviceName = serviceName;
    }

}
