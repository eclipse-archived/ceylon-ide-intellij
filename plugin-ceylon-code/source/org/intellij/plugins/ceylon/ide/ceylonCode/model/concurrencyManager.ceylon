import ceylon.interop.java {
    JavaRunnable
}

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
import com.redhat.ceylon.ide.common.platform {
    platformUtils,
    Status
}

import java.lang {
    Thread {
        currentThread
    },
    InterruptedException,
    ThreadLocal,
    Runnable,
    System
}
import java.util.concurrent { 
    JCallable = Callable
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

shared abstract class ConcurrencyError(String? description=null, Throwable? cause=null) extends Exception(description, cause) {}

shared class CannotWaitForIndexesInReadAccessError() 
        extends ConcurrencyError("Waiting for up-to-date indexes inside a read-allowed section is dead-lock-prone.") {}

shared class DumbModeNotSupported() 
        extends ConcurrencyError("This code should never be called while Dumb mode is on.") {}

String noIndexStrategyMessage = """Entering in a section that would need indexes, but no strategy has been specified.
                                     The stragtegy used when indexes are unavailable can be specificied by one of the following methods:
                                     - concurrencyManager.withUpToDateIndexes()
                                     - concurrencyManager.withAlternateResolution()
                                     - concurrencyManager.outsideDumbMode()""";

shared class IndexNeededWithNoIndexStrategy() 
        extends ConcurrencyError(noIndexStrategyMessage) {}

shared object concurrencyManager {
    value deadLockDetectionTimeoutSeconds = 30;
    
    value noIndexStrategy_ = ThreadLocal<NoIndexStrategy?>();

    shared Return needReadAccess<Return>(Return() func) {
        if (application.readAccessAllowed) {
            value ref = Ref<Return>();
            ProgressManager.instance.executeNonCancelableSection(JavaRunnable(void () {
                ref.set(func());
            }));
            return ref.get();
        } else {
            "This method is copied on 
             [[com.intellij.openapi.progress.util::ProgressIndicatorUtils.runInReadActionWithWriteActionPriority]]
             but doesn't fail when a write action is only pending, since this would lead to deadlocks when another read action is
             preventing a pending write action to acquire its lock"
            Boolean runInReadActionWithWriteActionPriority(Runnable action) {
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
                value succeededWithAddingListener = application.tryRunReadAction(JavaRunnable {
                    run() => application.addApplicationListener(listener);
                });
                if (!succeededWithAddingListener) { // second catch: writeLock.lock() acquisition is in progress or already acquired
                    if (!progressIndicator.canceled) {
                        progressIndicator.cancel();
                    }
                    return false;
                }
                value wasCancelled = Ref<Boolean>();
                try {
                    progressManager.runProcess(JavaRunnable {
                        void run() {
                            try {
                                wasCancelled.set(!application.tryRunReadAction(action));
                            }
                            catch (ProcessCanceledException ignore) {
                                wasCancelled.set(true);
                            }
                        }
                    }, progressIndicator);
                }
                finally {
                    application.removeApplicationListener(listener);
                }
                Boolean cancelled = wasCancelled.get();
                return ! cancelled;
            }
            
            value ref = Ref<Return>();
            
            value allowedWaitingTime = System.currentTimeMillis() + deadLockDetectionTimeoutSeconds * 1000;
            while(!runInReadActionWithWriteActionPriority(JavaRunnable {
                run() => ref.set(func());
            })) {
                try {
                    if (System.currentTimeMillis() > allowedWaitingTime) {
                        platformUtils.log(Status._ERROR, "Stopped waiting for read access to avoid a deadlock");
                        throw ProcessCanceledException();
                    }
                    Thread.sleep(200);
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
                platformUtils.log(Status._DEBUG, "Retrying the read action after a write action");
            }
            return ref.get();
        }
    }
    
    Return withIndexStrategy<Return>(NoIndexStrategy s, Return() func) {
        NoIndexStrategy? previousStrategy;
        if (exists currentStrategy = noIndexStrategy_.get()) {
            platformUtils.log(Status._WARNING, "The current strategy (``currentStrategy``) when indexes are unavailable should not be overriden by a new one (``s``)");
            previousStrategy = currentStrategy;
        } else {
            previousStrategy = null;
        }
        try {
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
        value runnable = JavaRunnable {
            run() => ref.set(func());
        };
        
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
            ds.repeatUntilPassesInSmartMode(runnable);
            return ref.get();
        }
        case (NoIndexStrategy.ousideDumbMode) {
            if (ds.dumb) {
                throw DumbModeNotSupported();
            } else {
                return func();
            }
        }
        case(null) {
            if (ds.dumb) {
                throw IndexNeededWithNoIndexStrategy();
            } else {
                value message = "\n".join { 
                    noIndexStrategyMessage,
                    "  Stacktrace: ", *currentThread().stackTrace.array.coalesced.map((stackTraceElement) =>
                    "    ``stackTraceElement``") };
                platformUtils.log(Status._WARNING, message);
                return func();
            }
        }
    }
}

shared Return doWithIndex<Return>(Project p, Return() func) => concurrencyManager.needIndexes(p, func);
shared Return doWithLock<Return>(Return() func) => concurrencyManager.needReadAccess(func);

object concurrencyManagerForJava {
    shared Anything withAlternateResolution(JCallable<Anything> func)
            => concurrencyManager.withAlternateResolution(func.call);
    
    shared Anything withUpToDateIndexes(JCallable<Anything> func)
            => concurrencyManager.withUpToDateIndexes(func.call);
    
    shared Anything outsideDumbMode(JCallable<Anything> func)
            => concurrencyManager.outsideDumbMode(func.call);
}