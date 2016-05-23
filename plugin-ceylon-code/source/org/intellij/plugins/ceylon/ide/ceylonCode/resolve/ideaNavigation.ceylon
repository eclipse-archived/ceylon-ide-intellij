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
    JavaPsiFacade,
    PsiClass
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
import com.redhat.ceylon.ide.common.platform {
    platformUtils,
    Status
}
import com.redhat.ceylon.ide.common.util {
    Path
}
import com.redhat.ceylon.model.loader.model {
    JavaBeanValue,
    LazyClass,
    LazyInterface,
    JavaMethod
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration,
    ClassOrInterface,
    Value
}

import java.lang {
    Integer
}

import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    PSIClass,
    PSIMethod,
    concurrencyManager
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}

shared class IdeaNavigation(Project project) 
        extends AbstractNavigation<PsiNameIdentifierOwner,VirtualFile>() {
    
    shared actual Path filePath(VirtualFile file) => Path(file.path);
    
    shared actual PsiNameIdentifierOwner? gotoFile(VirtualFile file, 
        Integer offset, Integer length) {
        
        if (is PsiFile psiFile = PsiManager.getInstance(project).findFile(file)) {
            return PsiTreeUtil.findElementOfClassAtOffset(psiFile, offset.intValue(),
               javaClass<PsiNameIdentifierOwner>(), false);
        }
        platformUtils.log(Status._WARNING, "Can't navigate to file " + file.canonicalPath);
        return null;
    }
    
    shared actual PsiNameIdentifierOwner? gotoJavaNode(Declaration declaration) {
        if (is LazyClass declaration) {
            if (is PSIMethod meth = declaration.constructor) {
                return meth.psi;
            }
            if (is PSIClass cls = declaration.classMirror) {
                return cls.psi;
            }
        } else if (is LazyInterface declaration, is PSIClass cls = declaration.classMirror) {
            return cls.psi;
        } else if (is JavaMethod declaration, is PSIMethod meth = declaration.mirror) {
            return meth.psi;
        } else if (is JavaBeanValue declaration, is PSIMethod meth = declaration.mirror) {
            return meth.psi;
        } else if (is Value declaration) {
            if (is ClassOrInterface container = declaration.container,
                exists cls = getJavaClass(container)) {

                return cls.findFieldByName(declaration.name, true);
            }
        }
        
        return null;
    }
    
    PsiClass? getJavaClass(ClassOrInterface cls) {
        value qn = cls.qualifiedNameString.replace("::", ".");

        return concurrencyManager.withAlternateResolution {
            func() => concurrencyManager.needIndexes(project, () {
                value facade = JavaPsiFacade.getInstance(project);
                value scope = GlobalSearchScope.allScope(project);
    
                if (exists psi = facade.findClass(qn, scope)) {
                    return psi;
                }
                return null;
            });
        };
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
