import com.redhat.ceylon.ide.common.platform {
    IdePlatformUtils,
    Status
}
import java.lang {
    RuntimeException
}
import com.intellij.openapi.diagnostic {
    Logger
}

shared object ideaPlatformUtils satisfies IdePlatformUtils {
    
    value logger = Logger.getInstance("ideaPlatformUtils");

    shared actual void log(Status status, String message, Exception? e) {
        switch(status)
        case(Status._OK) {
            logger.debug(message, e);
        }
        case(Status._INFO) {
            logger.info(message, e);
        }
        case(Status._ERROR) {
            logger.error(message, e);
        }
        case(Status._WARNING) {
            logger.warn(message, e);
        }
    }
    
    shared actual RuntimeException newOperationCanceledException(String message)
            => OperationCanceledException(message);
    
    shared actual Boolean isOperationCanceledException(Exception exception)
            => exception is OperationCanceledException;

    class OperationCanceledException(String message)
            extends RuntimeException(message) {
    }
}