import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.psi {
    PsiClass,
    PsiMethod
}
import com.redhat.ceylon.ide.common.model {
    IJavaModelAware
}
import com.redhat.ceylon.ide.common.util {
    BaseProgressMonitor
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration
}

import org.intellij.plugins.ceylon.ide.resolve {
    declarationToPsi
}

shared interface IdeaJavaModelAware
    satisfies IJavaModelAware<Module, PsiClass, PsiClass|PsiMethod> {

        javaClassRootToNativeProject(PsiClass javaClassRoot)
            => nothing; // TODO : this has to be implemented

        toJavaElement(Declaration ceylonDeclaration, BaseProgressMonitor? monitor)
            => if (is PsiClass|PsiMethod psi = declarationToPsi(ceylonDeclaration))
                then psi
                else null;
}
