package lab.processor.provider;

import lab.processor.core.ErrorHandler;
import lab.processor.core.Instruction;
import lab.processor.core.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SplittableRandom;
import java.util.random.RandomGenerator;

@Slf4j
public class ResourceProvider {

    private static ResourceProvider instance;
    private Map<String, Instruction> instructions;
    private Map<String, List<Service>> services;
    private Map<String, List<ErrorHandler>> errorHandlers;
    public RandomGenerator randomGenerator = new SplittableRandom();

    private ResourceProvider(){
        if (ResourceProvider.instance == null) {
            instance = this;
            instance.instructions = new HashMap<>();
            instance.services = new HashMap<>();
            instance.errorHandlers = new HashMap<>();
            log.info("StorageManager instantiated");
        } else {
            log.info("StorageManager not instantiated");
        }
    }

    public static ResourceProvider access(){
        if (ResourceProvider.instance == null) {
            new ResourceProvider();
        }
        return ResourceProvider.instance;
    }

    public void setInstructions(List<Instruction> instructions){
        for(Instruction instruction : instructions){
            String instId = instruction.getInstructionId();
            this.instructions.put(instId, instruction);
        }
    }

    public void setServices(Map<String, List<Service>> services){
        for(String id : services.keySet()){
            List<Service> serviceList = services.get(id);
            if (serviceList.isEmpty())
                throw new IllegalArgumentException("Service list for service id '" + id + "' is empty");
            this.services.put(id, serviceList);
        }
    }

    public void setErrorHandlers(Map<String, List<ErrorHandler>> errorHandlers){
        for(String id : errorHandlers.keySet()){
            List<ErrorHandler> errorHandlerList = errorHandlers.get(id);
            if (errorHandlerList.isEmpty())
                throw new IllegalArgumentException("Service list for service id '" + id + "' is empty");
            this.errorHandlers.put(id, errorHandlerList);
        }
    }

    public Instruction getInstruction(String instructionId){
        return instructions.get(instructionId);
    }

    public List<Service> getServices(String serviceId){
        return services.get(serviceId);
    }

    public List<ErrorHandler> getErrorHandlers(String errorHandlerId){
        return errorHandlers.get(errorHandlerId);
    }

    public List<Service> getServicesByInstrId(String instructionId){
        return services.get(getInstruction(instructionId).getInstructionId());
    }

    public List<ErrorHandler> getErrorHandlersByInstrId(String instructionId){
        return errorHandlers.get(getInstruction(instructionId).getInstructionId());
    }

    public void setRandomGenerator(RandomGenerator randomGenerator){
        if (randomGenerator == null)
            throw new IllegalArgumentException("Random generator cannot be null");
        this.randomGenerator = randomGenerator;
    }

    public RandomGenerator getRandomGenerator() {
        return randomGenerator;
    }

}
