import com.intellij.psi {
    PsiClass,
    PsiJavaFile,
    SmartPsiElementPointer
}
import com.redhat.ceylon.ide.common.platform {
    platformUtils,
    Status
}
import com.redhat.ceylon.model.loader.mirror {
    PackageMirror
}

import org.intellij.plugins.ceylon.ide.model {
    concurrencyManager {
        needReadAccess
    }
}

class PSIPackage(SmartPsiElementPointer<PsiClass> psiPointer)
    satisfies PackageMirror {

    "This is needed when a PsiClass is removed from the index,
     and the model loader tries to unload the corresponding
     mirror. When that happens, we still need to access the
     qualified name although the PSI has been invalidated."
    variable String cachedQualifiedName = "";

    shared actual String qualifiedName { 
        try {
            cachedQualifiedName = needReadAccess(() {
                value file = get(psiPointer).containingFile;
                if (is PsiJavaFile file) {
                    return file.packageName;
                }
                platformUtils.log(Status._WARNING,
                    "No qualified name for file ``file else "<null>"``");
                return "";
            });
        } catch (PsiElementGoneException e) {}

        return cachedQualifiedName;
    }

    noop(qualifiedName); //initialize it on construction

}