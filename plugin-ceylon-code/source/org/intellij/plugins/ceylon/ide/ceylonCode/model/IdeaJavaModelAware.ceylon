import com.redhat.ceylon.ide.common.model {
    IJavaModelAware
}
import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.psi {
    PsiClass
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration
}
import com.redhat.ceylon.ide.common.util {
    BaseProgressMonitor
}

shared interface IdeaJavaModelAware
    satisfies IJavaModelAware<Module, PsiClass, PsiClass> {

        shared actual Module javaClassRootToNativeProject(PsiClass javaClassRoot)
            => nothing;

        shared actual PsiClass? toJavaElement(Declaration ceylonDeclaration, BaseProgressMonitor? monitor)
            => null; // TODO
}
