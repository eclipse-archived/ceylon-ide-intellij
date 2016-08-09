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
                value f = psi.containingFile else null;
                if (is PsiJavaFile f) {
                    return f.packageName;
                }
                platformUtils.log(Status._WARNING, 
                    "No qualified name for file ``f else "<null>"``");
                return "";
            });
        }
        
        return name else "";
    }
}