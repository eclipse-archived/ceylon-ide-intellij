import com.intellij.openapi.diagnostic {
    Logger
}
import com.intellij.openapi.progress {
    ProcessCanceledException
}
import com.intellij.openapi.project {
    IndexNotReadyException
}
import com.redhat.ceylon.ide.common.platform {
    Status,
    IdeUtils
}
import com.redhat.ceylon.model.loader {
    ModelResolutionException
}

import java.lang {
    Types
}

import org.intellij.plugins.ceylon.ide.model {
    ConcurrencyError
}

shared object ideaPlatformUtils satisfies IdeUtils {
    
    value logger = Logger.getInstance("ideaPlatformUtils");
    
    shared actual void log(Status status, String message, Exception? e) {
        if (is ProcessCanceledException e) {
            // to avoid "Control-flow exceptions (like Xyz) should never be logged"
            log(status, "``message`` (``e.string``)");
            return;
        }

        if (is ModelResolutionException e) {
            // We can certainly recover from ModelResolutionException so we don't need to show
            // them to the user. The IDE will likely reindex JARs later and a model reset will fix them.
            log(Status._WARNING, "``message`` (``e.string``)");
            return;
        }

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
            => ProcessCanceledException(OperationCanceledException(message));

    isOperationCanceledException(Exception exception)
            => exception is ProcessCanceledException;

    isExceptionToPropagateInVisitors(Exception exception)
            => exception is IndexNotReadyException | ConcurrencyError;

    class OperationCanceledException(String message)
            extends Exception(message) {
    }
    
    pluginClassLoader => Types.classForInstance(this).classLoader;
    
}
