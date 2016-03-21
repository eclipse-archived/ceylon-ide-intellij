import com.intellij.psi {
    PsiClass,
    PsiJavaFile
}
import com.redhat.ceylon.model.loader.mirror {
    PackageMirror
}
import com.redhat.ceylon.ide.common.platform {
    platformUtils,
    Status
}

class PSIPackage(PsiClass psi) 
    satisfies PackageMirror {
    
    variable String? _name = null;
    
    shared actual String qualifiedName { 
        if (!_name exists) {
            _name = doWithLock(() {
                if (is PsiJavaFile f = psi.containingFile) {
                    return f.packageName;
                }
                platformUtils.log(Status._WARNING, 
                    "No qualified name for file ``className(psi.containingFile)``");
                return "";
            });
        }
        
        return _name else "";
    }
}