package lab.processor.core;

import lab.processor.context.ContextData;

public interface ErrorHandler {

    public void handleError(ContextData contextData) throws Throwable;

}
