import ceylon.interop.java {
    JavaRunnable
}

import com.intellij.openapi.application {
    ApplicationManager {
        application
    }
}
import com.intellij.openapi.progress {
    ProgressManager {
        progressManager=instance
    }
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

shared Return doWithLock<Return>(Return() callback) {
    value lock = application.acquireReadActionLock();
    try {
        value ref = Ref<Return>();

        progressManager.executeNonCancelableSection(JavaRunnable{
            run() => ref.set(callback());
        });

        return ref.get();
    } finally {
        lock.finish();
    }
}

shared Return doWithIndex<Return>(Project p, Return() callback) {

    value ref = Ref<Return>();
    value runnable = JavaRunnable {
        run() => ref.set(callback());
    };
    dumbService(p).runReadActionInSmartMode(runnable);

    return ref.get();
}