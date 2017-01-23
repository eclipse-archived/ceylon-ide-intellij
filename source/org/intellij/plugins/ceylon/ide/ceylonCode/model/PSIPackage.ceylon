import com.intellij.psi {
    PsiClass,
    PsiJavaFile,
    SmartPsiElementPointer,
    PsiFile
}
import com.redhat.ceylon.ide.common.platform {
    platformUtils,
    Status
}
import com.redhat.ceylon.model.loader.mirror {
    PackageMirror
}

class PSIPackage(SmartPsiElementPointer<PsiClass> psiPointer)
    satisfies PackageMirror {

    PsiClass psi {
        "The PSI element should still exist"
        assert(exists el = psiPointer.element);
        return el;
    }
    
    variable String? name = null;

    "This is needed when a PsiClass is removed from the index, and the model loader
     tries to unload the corresponding mirror. When that happens, we still need to access
     the qualified name although the PSI has been invalidated."
    variable String cachedQualifiedName = "";

    shared actual String qualifiedName { 
        if (!name exists) {
            try {
                name = concurrencyManager.needReadAccess(() {
                    PsiFile? f = psi.containingFile;
                    if (is PsiJavaFile f) {
                        return f.packageName;
                    }
                    platformUtils.log(Status._WARNING,
                        "No qualified name for file ``f else "<null>"``");
                    return "";
                });
            } catch (AssertionError e) {
                return cachedQualifiedName;
            }
        }

        cachedQualifiedName = name else "";
        return cachedQualifiedName;
    }

    cachedQualifiedName = qualifiedName;
}