import com.intellij.openapi.application {
    ApplicationInfo
}
import com.intellij.openapi.diagnostic {
    Logger
}

import java.lang {
    Thread {
        currentThread
    },
    Types
}

shared class CeylonLogger<Type>() 
    given Type satisfies Object {
    value internalLogger = Logger.getInstance(Types.classForType<Type>());
    
    function prepareMessage(Integer stackTraceDepth, String() message) {
        value stackTrace = currentThread().stackTrace;
        String realMessage = 
            if (stackTraceDepth > 0) 
            then "\n".join { 
                        "
                         ===================================================",
                        message(),
                        "  Stacktrace: ", *stackTrace.array.coalesced.skip(4).take(stackTraceDepth).map((stackTraceElement) =>
                            "    ``stackTraceElement``")
                        .chain {
                        "==================================================="}}
            else message();
        return realMessage;
    }
    
    shared void error(String message(), Integer stackTraceDepth = 0) 
        => internalLogger.error(prepareMessage(stackTraceDepth, message));
    
    shared void errorThrowable(Throwable throwable, String message() => "")
        => internalLogger.error(message(), throwable);

    shared void warn(String message(), Integer stackTraceDepth = 0) 
            => internalLogger.warn(prepareMessage(stackTraceDepth, message));
    
    shared void warnThrowable(Throwable throwable, String message() => "")
            => internalLogger.warn(message(), throwable);

    shared void info(String message(), Integer stackTraceDepth = 0) 
            => internalLogger.info(prepareMessage(stackTraceDepth, message));
    
    shared void infoThrowable(Throwable throwable, String message() => "")
            => internalLogger.info(message(), throwable);
    
    shared void debug(String message(), Integer stackTraceDepth = 0) {
        if (internalLogger.debugEnabled) {
            internalLogger.debug(prepareMessage(stackTraceDepth, message));
        }
    }
    
    shared void debugThrowable(Throwable throwable, String message() => "") {
        if (internalLogger.debugEnabled) {
            internalLogger.debug(message(), throwable);
        }
    }
    
    shared void trace(String message(), Integer stackTraceDepth = 0) {
        if (ApplicationInfo.instance.build.baselineVersion >= 145,
            internalLogger.traceEnabled) {

            internalLogger.trace(prepareMessage(stackTraceDepth, message));
        } else if (internalLogger.debugEnabled) {
            internalLogger.debug(prepareMessage(stackTraceDepth, message));
        }
    }
}