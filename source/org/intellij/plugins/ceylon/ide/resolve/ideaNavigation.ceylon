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
    PsiClass,
    PsiElement,
    PsiLiteralExpression
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
    JavaMethod,
    FieldValue,
    AnnotationProxyClass
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration,
    ClassOrInterface,
    Referenceable,
    Function,
    Value,
    ModelUtil
}

import java.lang {
    Integer
}

import org.intellij.plugins.ceylon.ide.model {
    PSIClass,
    PSIMethod,
    concurrencyManager
}
import org.intellij.plugins.ceylon.ide.psi {
    CeylonFile
}
import ceylon.interop.java {
    javaString
}

shared PsiNameIdentifierOwner? declarationToPsi(Declaration rawDeclaration) {
    PsiClass? getJavaClass(ClassOrInterface cls) {
        if (is LazyClass cls, is PSIClass mirror = cls.classMirror) {
            return mirror.psi;
        }
        else if (is LazyInterface cls, is PSIClass mirror = cls.classMirror) {
            return mirror.psi;
        }
        return null;
//        value qn = cls.qualifiedNameString.replace("::", ".");
//
//        return concurrencyManager.withAlternateResolution {
//            func() => concurrencyManager.needIndexes(project, () {
//                value facade = JavaPsiFacade.getInstance(project);
//                value scope = GlobalSearchScope.allScope(project);
//
//                if (exists psi = facade.findClass(qn, scope)) {
//                    return psi;
//                }
//                return null;
//            });
//        };
    }

    value declaration = if (is Function rawDeclaration, rawDeclaration.annotation)
            then rawDeclaration.typeDeclaration
            else rawDeclaration;
    if (is LazyClass declaration) {
        if (is PSIMethod meth = declaration.constructor,
            meth.psi.canNavigate()) {
            return meth.psi;
        }
        if (is PSIClass cls = declaration.classMirror) {
            return cls.psi;
        }
    } else if (is LazyInterface declaration,
        is PSIClass cls = declaration.classMirror) {
        return cls.psi;
    } else if (is JavaMethod declaration,
        is PSIMethod meth = declaration.mirror) {
        return meth.psi;
    } else if (is JavaBeanValue declaration,
        is PSIMethod meth = declaration.mirror) {
        return meth.psi;
    } else if (is FieldValue declaration) {
        if (is ClassOrInterface container = declaration.container,
            exists cls = getJavaClass(container)) {

            return cls.findFieldByName(declaration.realName, true);
        }
    } else if (is Value declaration,
        is AnnotationProxyClass container = declaration.container,
        is PSIClass cls = container.iface.classMirror) {

        return cls.psi.findMethodsByName(declaration.name, false).array.first;
    } else if (ModelUtil.isConstructor(declaration),
        is LazyClass container = declaration.container,
        is PSIClass cls = container.classMirror) {

        for (ctor in cls.psi.constructors) {
            if (exists ann = ctor.modifierList.findAnnotation("com.redhat.ceylon.compiler.java.metadata.Name"),
                is PsiLiteralExpression name = ann.findAttributeValue("value"),
                (name.\ivalue else "") == javaString(declaration.name)) {

                return ctor;
            }
        }
        return cls.psi.findMethodsByName(declaration.name, false).array.first;
    }
    return null;
}

shared class IdeaNavigation(Project project)
        extends AbstractNavigation<PsiElement,VirtualFile>() {
    
    filePath(VirtualFile file) => Path(file.path);
    
    gotoDeclaration(Referenceable? model) =>
            concurrencyManager.withAlternateResolution(() => super.gotoDeclaration(model));

    shared actual PsiElement? gotoFile(VirtualFile file,
        Integer offset, Integer length) {
        
        if (is PsiFile psiFile = PsiManager.getInstance(project).findFile(file)) {
            return PsiTreeUtil.findElementOfClassAtOffset(psiFile, offset.intValue(),
               `PsiElement`, false);
        }
        platformUtils.log(Status._WARNING, "Can't navigate to file " + file.path);
        return null;
    }
    
    shared actual PsiNameIdentifierOwner? gotoJavaNode(Declaration rawDeclaration) {
        return declarationToPsi(rawDeclaration);
    }

    shared actual PsiElement? gotoLocation(Path? path,
        Integer offset, Integer length) {
        
        if (exists strPath = path?.string,
            exists file = VirtualFileManager.instance.findFileByUrl(
                ("!/" in strPath then "jar" else "file") + "://" + strPath),
            is CeylonFile psiFile = PsiManager.getInstance(project).findFile(file)) {

            return PsiTreeUtil.findElementOfClassAtOffset(psiFile, offset.intValue(),
                `PsiElement`, false);
        }
        
        return null;
    }
}
