package lab.processor.storage;

import lab.processor.core.Instruction;
import lab.processor.core.Service;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class StorageManager {

    private Map<String, Instruction> instructions;
    private Map<String, List<Service>> services;
    private static StorageManager instance;

    private StorageManager(){
        if (StorageManager.instance == null) {
            instance = this;
            instance.instructions = new HashMap<>();
            instance.services = new HashMap<>();
            log.info("StorageManager instantiated");
        } else {
            log.info("StorageManager not instantiated");
        }
    }

    public static StorageManager getInstance(){
        if (StorageManager.instance == null) {
            instance = new StorageManager();
            instance.instructions = new HashMap<>();
            instance.services = new HashMap<>();
        }
        return StorageManager.instance;
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

    public Instruction getInstruction(String instructionId){
        return instructions.get(instructionId);
    }

    public List<Service> getServices(String serviceId){
        return services.get(serviceId);
    }

}
