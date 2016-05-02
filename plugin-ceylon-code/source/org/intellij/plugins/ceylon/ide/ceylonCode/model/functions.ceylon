import com.intellij.openapi.application {
    ApplicationManager
}

import com.intellij.openapi.progress {
    ProgressManager
}
import java.lang {
    Runnable
}
import com.intellij.openapi.util {
    Ref
}
import com.intellij.openapi.project {
    Project,
    DumbService
}

Return doWithLock<Return>(Return() callback) {
    value lock = ApplicationManager.application.acquireReadActionLock();
    try {
        value ref = Ref<Return>();

        ProgressManager.instance.executeNonCancelableSection(object satisfies Runnable {
            shared actual void run() {
                ref.set(callback());
            }
        });

        return ref.get();
    } finally {
        lock.finish();
    }
}

shared Return doWithIndex<Return>(Project p, Return() callback) {

    value ref = Ref<Return>();
    value runnable = object satisfies Runnable {
        shared actual void run() => ref.set(callback());
    };

    DumbService.getInstance(p).runReadActionInSmartMode(runnable);

    return ref.get();
}