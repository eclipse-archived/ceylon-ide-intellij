import com.intellij.openapi.diagnostic {
    Logger
}
import com.redhat.ceylon.ide.common.platform {
    Status,
    IdeUtils
}

import java.lang {
    RuntimeException
}
import com.intellij.openapi.progress {
    ProcessCanceledException
}
import ceylon.interop.java {
    javaClassFromInstance
}

shared object ideaPlatformUtils satisfies IdeUtils {
    
    value logger = Logger.getInstance("ideaPlatformUtils");
    
    shared actual void log(Status status, String message, Exception? e) {
        switch (status)
        case (Status._OK) {
            logger.debug(message, e);
        }
        case (Status._INFO) {
            logger.info(message, e);
        }
        case (Status._DEBUG) {
            logger.debug(message, e);
        }
        case (Status._ERROR) {
            logger.error(message, e);
        }
        case (Status._WARNING) {
            logger.warn(message, e);
        }
    }
    
    newOperationCanceledException(String message) => ProcessCanceledException(OperationCanceledException(message));
    
    isOperationCanceledException(Exception exception)
            => exception is ProcessCanceledException;
    
    class OperationCanceledException(String message)
            extends RuntimeException(message) {
    }
    
    pluginClassLoader => javaClassFromInstance(this).classLoader;
    
}
