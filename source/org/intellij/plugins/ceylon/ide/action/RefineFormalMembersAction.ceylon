import org.intellij.plugins.ceylon.ide.correct {
    RefineFormalMembersIntention
}
import com.intellij.lang {
    LanguageCodeInsightActionHandler
}
import com.intellij.openapi.editor {
    Editor
}
import com.intellij.psi {
    PsiFile
}
import com.intellij.openapi.project {
    Project
}
import org.intellij.plugins.ceylon.ide.psi {
    CeylonFile
}
import com.intellij.openapi.command {
    WriteCommandAction
}
import com.intellij.openapi.application {
    Result
}

shared class RefineFormalMembersAction() extends AbstractIntentionAction() 
        satisfies LanguageCodeInsightActionHandler {
    
    commandName => "Refine formal members";
    
    createIntention() => RefineFormalMembersIntention();
    
    shared actual void invoke(Project project, Editor editor, PsiFile file) {
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
    
    isValidFor(Editor editor, PsiFile psiFile) => psiFile is CeylonFile;
    
    startInWriteAction() => false;
    
}
