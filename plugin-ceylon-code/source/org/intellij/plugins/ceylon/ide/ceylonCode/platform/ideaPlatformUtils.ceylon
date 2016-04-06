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
    
    newOperationCanceledException(String message)
            => OperationCanceledException(message);
    
    isOperationCanceledException(Exception exception)
            => exception is OperationCanceledException;
    
    class OperationCanceledException(String message)
            extends RuntimeException(message) {
    }
}
