import org.intellij.plugins.ceylon.ide.ceylonCode.correct {
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
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
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
    
    shared actual void invoke(Project project, Editor editor, PsiFile psiFile) {
        value intention = createIntention();
        if (intention.isAvailable(project, editor, psiFile)) {
            value p = project;
            value cn = commandName;
            object extends WriteCommandAction<Nothing>(p, cn) {
                run(Result<Nothing> result) 
                        => intention.invoke(project, editor, psiFile);
            }.execute();
        }
    }
    
    isValidFor(Editor editor, PsiFile psiFile) => psiFile is CeylonFile;
    
    startInWriteAction() => false;
    
}
