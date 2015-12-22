import com.redhat.ceylon.ide.common.model {
    CeylonBinaryUnit
}
import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.psi {
    PsiClass
}
import com.redhat.ceylon.model.typechecker.model {
    Package,
    Declaration
}
import com.redhat.ceylon.ide.common.util {
    BaseProgressMonitor
}

shared class IdeaCeylonBinaryUnit(
    PsiClass cls,
    String filename,
    String relativePath,
    String fullPath,
    Package pkg)
        extends CeylonBinaryUnit<Module,PsiClass,PsiClass>(
    cls, filename, relativePath, fullPath, pkg) {
    
    shared actual Module javaClassRootToNativeProject(PsiClass javaClassRoot) => nothing;
    
    shared actual PsiClass? toJavaElement(Declaration ceylonDeclaration, BaseProgressMonitor? monitor) => nothing;
    
}
