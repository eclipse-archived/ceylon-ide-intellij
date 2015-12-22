import com.intellij.psi {
    PsiClass,
    PsiJavaFile
}
import com.redhat.ceylon.model.loader.mirror {
    PackageMirror
}

class PSIPackageMirror(PsiClass psi) 
    satisfies PackageMirror {
    
    variable String? _name = null;
    
    shared actual String qualifiedName { 
        if (!_name exists) {
            _name = doWithLock(() {
                assert(is PsiJavaFile f = psi.containingFile);
                return f.packageName; 
            });
        }
        
        return _name else "";
    }
}