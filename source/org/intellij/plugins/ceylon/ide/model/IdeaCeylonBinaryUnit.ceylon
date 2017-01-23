import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.psi {
    PsiClass,
    PsiMethod
}
import com.redhat.ceylon.ide.common.model {
    CeylonBinaryUnit
}
import com.redhat.ceylon.model.typechecker.model {
    Package
}

shared class IdeaCeylonBinaryUnit(
    PsiClass cls,
    String filename,
    String relativePath,
    String fullPath,
    Package pkg)
        extends CeylonBinaryUnit<Module,PsiClass,PsiClass|PsiMethod>(
    cls, filename, relativePath, fullPath, pkg)
        satisfies IdeaJavaModelAware {
    
}
