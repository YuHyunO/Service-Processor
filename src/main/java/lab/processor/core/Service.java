package lab.processor.core;

import lab.processor.context.ContextData;

public interface Service {

    public void process(ContextData contextData) throws Throwable;

}
