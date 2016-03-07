import com.intellij.openapi.actionSystem {
    AnAction,
    AnActionEvent,
    CommonDataKeys
}
import org.intellij.plugins.ceylon.ide.ceylonCode.correct {
    RefineEqualsHashIntention
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}
import com.intellij.openapi.command {
    WriteCommandAction
}
import com.intellij.openapi.application {
    Result
}

shared class RefineEqualsHashAction() extends AnAction() {
    
    shared actual void actionPerformed(AnActionEvent evt) {
        if (exists project = evt.project,
            exists editor = evt.getData(CommonDataKeys.\iEDITOR),
            is CeylonFile psiFile = evt.getData(CommonDataKeys.\iPSI_FILE)) {
            
            value intention = RefineEqualsHashIntention();

            if (intention.isAvailable(project, editor, psiFile)) {
                value p = project;

                object extends WriteCommandAction<Nothing>(p, "Refine equals()/hash") {
                    shared actual void run(Result<Nothing> result) {
                        intention.invoke(project, editor, psiFile);
                    }
                }.execute();
            }
        }
    }
}
