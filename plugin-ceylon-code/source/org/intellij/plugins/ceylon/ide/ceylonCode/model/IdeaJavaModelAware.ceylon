import com.redhat.ceylon.ide.common.model {
    IJavaModelAware
}
import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.psi {
    PsiClass,
    PsiMethod
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration
}
import com.redhat.ceylon.ide.common.util {
    BaseProgressMonitor
}
import com.redhat.ceylon.model.loader.model {
    LazyClass,
    JavaMethod
}

shared interface IdeaJavaModelAware
    satisfies IJavaModelAware<Module, PsiClass, PsiClass|PsiMethod> {

        javaClassRootToNativeProject(PsiClass javaClassRoot)
            => nothing; // TODO : this has to be implemented

        toJavaElement(Declaration ceylonDeclaration, BaseProgressMonitor? monitor)
            => if (is LazyClass cls = ceylonDeclaration,
                   is PSIClass mirror = cls.classMirror)
               then mirror.psi
               else if (is JavaMethod meth = ceylonDeclaration,
                        is PSIMethod mirror = meth.mirror)
               then mirror.psi
               else null;
}
