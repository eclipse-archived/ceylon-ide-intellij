import com.intellij.openapi.actionSystem {
    AnAction,
    AnActionEvent,
    CommonDataKeys
}
import com.intellij.openapi.application {
    Result
}
import com.intellij.openapi.command {
    WriteCommandAction
}

import org.intellij.plugins.ceylon.ide.correct {
    AbstractIntention
}
import org.intellij.plugins.ceylon.ide.psi {
    CeylonFile
}

"An action that wraps an [[AbstractIntention]]."
shared abstract class AbstractIntentionAction() extends AnAction() {
    
    shared actual void update(AnActionEvent evt) {
        evt.presentation.enabled = false;

        if (exists project = evt.project,
            exists editor = evt.getData(CommonDataKeys.editor),
            is CeylonFile psiFile = evt.getData(CommonDataKeys.psiFile)) {

            evt.presentation.enabled = createIntention().isAvailable(project, editor, psiFile);
        }
    }
    
    shared actual void actionPerformed(AnActionEvent evt) {
        if (exists project = evt.project,
            exists editor = evt.getData(CommonDataKeys.editor),
            is CeylonFile file = evt.getData(CommonDataKeys.psiFile)) {
            
            value intention = createIntention();
            if (intention.isAvailable(project, editor, file)) {
                value p = project;
                value cn = commandName;
                object extends WriteCommandAction<Nothing>(p, cn, file) {
                    run(Result<Nothing> result)
                            => intention.invoke(project, editor, file);
                }.execute();
            }
        }
    }
    
    shared formal AbstractIntention createIntention();
    
    shared formal String commandName;
}