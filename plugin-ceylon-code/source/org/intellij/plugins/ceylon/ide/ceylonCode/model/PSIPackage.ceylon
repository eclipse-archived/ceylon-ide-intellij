import com.intellij.psi {
    PsiClass,
    PsiJavaFile
}
import com.redhat.ceylon.ide.common.platform {
    platformUtils,
    Status
}
import com.redhat.ceylon.model.loader.mirror {
    PackageMirror
}

class PSIPackage(PsiClass psi) 
    satisfies PackageMirror {
    
    variable String? name = null;
    
    shared actual String qualifiedName { 
        if (!name exists) {
            name = concurrencyManager.needReadAccess(() {
                if (is PsiJavaFile f = psi.containingFile) {
                    return f.packageName;
                }
                platformUtils.log(Status._WARNING, 
                    "No qualified name for file ``className(psi.containingFile)``");
                return "";
            });
        }
        
        return name else "";
    }
}