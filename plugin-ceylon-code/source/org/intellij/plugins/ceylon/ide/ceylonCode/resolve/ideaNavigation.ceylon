import ceylon.interop.java {
    javaClass
}

import com.intellij.openapi.project {
    Project
}
import com.intellij.openapi.vfs {
    VirtualFileManager,
    VirtualFile
}
import com.intellij.psi {
    PsiManager,
    PsiNameIdentifierOwner,
    PsiFile,
    JavaPsiFacade
}
import com.intellij.psi.search {
    GlobalSearchScope
}
import com.intellij.psi.util {
    PsiTreeUtil
}
import com.redhat.ceylon.ide.common.open {
    AbstractNavigation
}
import com.redhat.ceylon.ide.common.util {
    Path
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration
}

import java.lang {
    Integer
}

import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    doWithIndex
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}

shared class IdeaNavigation(Project project) 
        extends AbstractNavigation<PsiNameIdentifierOwner,VirtualFile>() {
    
    shared actual Path filePath(VirtualFile file) => Path(file.path);
    
    shared actual PsiNameIdentifierOwner? gotoFile(VirtualFile file, 
        Integer offset, Integer length) {
        
        assert(is PsiFile psiFile = PsiManager.getInstance(project).findFile(file));
        return PsiTreeUtil.findElementOfClassAtOffset(psiFile, offset.intValue(),
            javaClass<PsiNameIdentifierOwner>(), false);
    }
    
    shared actual PsiNameIdentifierOwner? gotoJavaNode(Declaration declaration) {
        value qn = declaration.qualifiedNameString.replace("::", ".");

        return doWithIndex(project, () {
            value facade = JavaPsiFacade.getInstance(project);
            value scope = GlobalSearchScope.allScope(project);

            if (exists psi = facade.findClass(qn, scope)) {
                return psi;
            }
            return null;
        });
    }
    
    shared actual PsiNameIdentifierOwner? gotoLocation(Path? path, 
        Integer offset, Integer length) {
        
        if (exists strPath = path?.string,
            exists file = VirtualFileManager.instance.findFileByUrl(
                (strPath.contains("!/") then "jar" else "file") + "://" + strPath),
            is CeylonFile psiFile = PsiManager.getInstance(project).findFile(file)) {

            return PsiTreeUtil.findElementOfClassAtOffset(psiFile, offset.intValue(),
                javaClass<PsiNameIdentifierOwner>(), false);
        }
        
        return null;
    }
}
