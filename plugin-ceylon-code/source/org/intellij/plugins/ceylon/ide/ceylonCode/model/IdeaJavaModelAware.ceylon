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
    Declaration,
    Value
}
import com.redhat.ceylon.ide.common.util {
    BaseProgressMonitor
}
import com.redhat.ceylon.model.loader.model {
    LazyClass,
    JavaMethod,
    LazyInterface,
    AnnotationProxyClass
}

shared interface IdeaJavaModelAware
    satisfies IJavaModelAware<Module, PsiClass, PsiClass|PsiMethod> {

        javaClassRootToNativeProject(PsiClass javaClassRoot)
            => nothing; // TODO : this has to be implemented

        toJavaElement(Declaration ceylonDeclaration, BaseProgressMonitor? monitor)
            => if (is LazyClass cls = ceylonDeclaration,
                   is PSIClass mirror = cls.classMirror)
               then mirror.psi
               else if (is LazyInterface cls = ceylonDeclaration,
                   is PSIClass mirror = cls.classMirror)
               then mirror.psi
               else if (is JavaMethod meth = ceylonDeclaration,
                        is PSIMethod mirror = meth.mirror)
               then mirror.psi
               else if (is Value val = ceylonDeclaration,
                        is AnnotationProxyClass cls = val.container,
                        is PSIClass mirror = cls.iface.classMirror,
                        exists meth = mirror.psi.findMethodsByName(val.name, false).array.first)
               then meth
               else null;
}
