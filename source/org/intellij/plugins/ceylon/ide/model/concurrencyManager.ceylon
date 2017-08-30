import com.intellij.openapi.application {
    ApplicationAdapter
}
import com.intellij.openapi.application.ex {
    ApplicationManagerEx {
        application=applicationEx
    }
}
import com.intellij.openapi.progress {
    ProgressManager {
        progressManager=instance
    },
    EmptyProgressIndicator,
    ProcessCanceledException
}
import com.intellij.openapi.project {
    Project,
    DumbService {
        dumbService=getInstance
    }
}
import com.intellij.openapi.util {
    Ref
}

import java.lang {
    Thread,
    InterruptedException,
    ThreadLocal,
    System
}

import org.intellij.plugins.ceylon.ide.util {
    CeylonLogger
}

shared class NoIndexStrategy
        of useAlternateResolution |
        waitForIndexes |
        ousideDumbMode {
    String str;
    shared new useAlternateResolution {
        str = "useAlternateResolution";
    }
    shared new waitForIndexes {
        str = "waitForIndexes";
    }
    shared new ousideDumbMode {
        str = "ousideDumbMode";
    }
    string => str;
}

shared abstract class ConcurrencyError(String? description=null, Throwable? cause=null)
        extends Exception(description, cause) {}

shared class CannotWaitForIndexesInReadAccessError()
        extends ConcurrencyError("Waiting for up-to-date indexes inside a read-allowed section is dead-lock-prone.") {}

shared class DumbModeNotSupported()
        extends ConcurrencyError("This code should never be called while Dumb mode is on.") {}

String noIndexStrategyMessage
        = """Entering in a section that would need indexes, but no strategy has been specified.
             The strategy used when indexes are unavailable can be specified by one of the following methods:
             - concurrencyManager.withUpToDateIndexes()
             - concurrencyManager.withAlternateResolution()
             - concurrencyManager.outsideDumbMode()""";

shared class IndexNeededWithNoIndexStrategy()
        extends ConcurrencyError(noIndexStrategyMessage) {}

shared object concurrencyManager {
    value logger = CeylonLogger<\IconcurrencyManager>();

    shared Integer deadLockDetectionTimeout = 30000;

    value noIndexStrategy_ = ThreadLocal<NoIndexStrategy?>();

    shared NoIndexStrategy? noIndexStrategy => noIndexStrategy_.get();

    shared Return|ProcessCanceledException tryReadAccess<Return>(Return func()) {
        try {
            return needReadAccess(func, 0);
        } catch(ProcessCanceledException e) {
            return e;
        }
    }

    shared Return dontCancel<Return>(Return func()) {
        value ref = Ref<Return>();
        ProgressManager.instance.executeNonCancelableSection(() => ref.set(func()));
        return ref.get();
    }

    shared Return needReadAccess<Return>(Return func(), Integer timeout = deadLockDetectionTimeout) {
        value ref = Ref<Return>();

        function funcRunnable() {
            value restoreCurrentPriority = withOriginalModelUpdatePriority();
            try {
                return ref.set(func());
            } finally {
                restoreCurrentPriority();
            }
        }

        if (application.readAccessAllowed) {
            ProgressManager.instance.executeNonCancelableSection(funcRunnable);
            return ref.get();
        } else {
            "This method is copied on
             [[com.intellij.openapi.progress.util::ProgressIndicatorUtils.runInReadActionWithWriteActionPriority]]
             but doesn't fail when a write action is only pending, since this would lead to deadlocks when another read action is
             preventing a pending write action to acquire its lock"
            Boolean runInReadActionWithWriteActionPriority(void action()) {
                value progressIndicator = object extends EmptyProgressIndicator() {
                    // hashCode() seems to be quite slow when used in CoreProgressManager.threadsUnderIndicator
                    hash => 42;
                };
                value listener = object extends ApplicationAdapter() {
                    shared actual void beforeWriteActionStart(Object action) {
                        if (!progressIndicator.canceled) {
                            progressIndicator.cancel();
                        }
                    }
                };
                value succeededWithAddingListener =
                        application.tryRunReadAction(()
                            => application.addApplicationListener(listener));
                if (!succeededWithAddingListener) { // second catch: writeLock.lock() acquisition is in progress or already acquired
                    if (!progressIndicator.canceled) {
                        progressIndicator.cancel();
                    }
                    return false;
                }
                value wasCancelled = Ref<Boolean>();
                try {
                    progressManager.runProcess(() {
                        try {
                            wasCancelled.set(!application.tryRunReadAction(action));
                        }
                        catch (ProcessCanceledException ignore) {
                            wasCancelled.set(true);
                        }
                    }, progressIndicator);
                }
                finally {
                    application.removeApplicationListener(listener);
                }
                Boolean cancelled = wasCancelled.get();
                return ! cancelled;
            }

            value allowedWaitingTime = System.currentTimeMillis() + timeout;

            while(!runInReadActionWithWriteActionPriority(funcRunnable)) {
                try {
                    if (System.currentTimeMillis() > allowedWaitingTime) {
                        if (timeout == deadLockDetectionTimeout) {
                            logger.error(()=>"Stopped waiting for read access to avoid a deadlock", 15);
                        }
                        throw ProcessCanceledException();
                    }
                    Thread.sleep(50);
                } catch(InterruptedException ie) {
                    if (application.disposeInProgress) {
                        throw ProcessCanceledException(ie);
                    } else {
                        try {
                            Thread.sleep(200);
                        } catch(InterruptedException ie2) {
                            throw ProcessCanceledException(ie2);
                        }
                    }
                }
                logger.debug(()=>"Retrying the read action after a write action");
            }
            return ref.get();
        }
    }

    Return withIndexStrategy<Return>(NoIndexStrategy s, Return() func) {
        NoIndexStrategy? previousStrategy;
        if (exists currentStrategy = noIndexStrategy_.get()) {
            if (currentStrategy != s && s != NoIndexStrategy.ousideDumbMode) {
                logger.info(()=>"The current strategy (``currentStrategy``) when indexes are unavailable should overriden by a new one (``s``) with care", 10);
            }
            previousStrategy = currentStrategy;
        } else {
            previousStrategy = null;
        }
        try {
            if (s == NoIndexStrategy.waitForIndexes
            && application.readAccessAllowed) {
                throw CannotWaitForIndexesInReadAccessError();
            }
            noIndexStrategy_.set(s);
            return func();
        } finally {
            noIndexStrategy_.set(previousStrategy);
        }
    }


    "With this strategy applied to [[func()]], all the calls to [[needIndexes()]] 
     (in descendant functions in the same thread), *while Dumb mode is on*,
     will *use the alternate resolution* method."
    see (`function DumbService.withAlternativeResolveEnabled`)
    shared Return withAlternateResolution<Return>(Return func())
            => withIndexStrategy(NoIndexStrategy.useAlternateResolution, func);

    "With this strategy applied to [[func()]], all the calls to [[needIndexes()]] 
     (in descendant functions in the same thread), *while Dumb mode is on*,
     will *wait for up-to-date indices* (the end of the Dumb mode), 
     unless already in a read-access section (to avoid deadlocks), 
     in which case a [[CannotWaitForIndexesInReadAccessError]] will be thrown."
    see (`function DumbService.repeatUntilPassesInSmartMode`)
    shared Return withUpToDateIndexes<Return>(Return func())
            => withIndexStrategy(NoIndexStrategy.waitForIndexes, func);

    "With this strategy applied to [[func()]], all the calls to [[needIndexes()]] 
     (in descendant functions in the same thread), are *expected to be called 
     outside Dumb mode*.
     If a [[needIndexes()]] is made during dumb mode, this will throw 
     will wait for up-to-date indices (the end of the Dumb mode), 
     unless already in a read-access section (to avoid deadlocks), 
     in which case a [[CannotWaitForIndexesInReadAccessError]] will be thrown."
    shared Return outsideDumbMode<Return>(Return func())
            => withIndexStrategy(NoIndexStrategy.ousideDumbMode, func);

    shared Return needIndexes<Return>(Project p, Return func()) {
        value ds = dumbService(p);

        value ref = Ref<Return>();
        function runnable() => ref.set(func());

        switch(indexStragey = noIndexStrategy_.get())
        case(NoIndexStrategy.useAlternateResolution) {
            // we are probably in the completion, or navigation, or local typechecking
            // so don't wait for indices, and if dumb mode is on, the alternate
            // resolution will be used.
            ds.withAlternativeResolveEnabled(runnable);
            return ref.get();
        }
        case(NoIndexStrategy.waitForIndexes) {
            if (application.readAccessAllowed) {
                throw CannotWaitForIndexesInReadAccessError();
            }
            ds.runReadActionInSmartMode(runnable);
            return ref.get();
        }
        case (NoIndexStrategy.ousideDumbMode) {
            if (ds.dumb) {
                value exception = DumbModeNotSupported();
                logger.errorThrowable(exception);
                throw exception;
            } else {
                return func();
            }
        }
        case(null) {
            if (ds.dumb) {
                value exception = IndexNeededWithNoIndexStrategy();
                logger.errorThrowable(exception);
                throw exception;
            } else {
                logger.debug(() => noIndexStrategyMessage, 20);
                return func();
            }
        }
    }
}

//object concurrencyManagerForJava {
//    shared Anything needReadAccess(JCallable<Anything> func, Integer timeout)
//            => concurrencyManager.needReadAccess(func.call, timeout);
//
//    shared Anything withAlternateResolution(JCallable<Anything> func)
//            => concurrencyManager.withAlternateResolution(func.call);
//
//    shared Anything withUpToDateIndexes(JCallable<Anything> func)
//            => concurrencyManager.withUpToDateIndexes(func.call);
//
//    shared Anything outsideDumbMode(JCallable<Anything> func)
//            => concurrencyManager.outsideDumbMode(func.call);
//}
//
